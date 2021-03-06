/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.storage;

import android.graphics.Bitmap;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import tool.compet.core.graphics.DkBitmaps;

/**
 * Thread-safe memory cache (LruCache).
 * Each cache-entry has own priority, expired time.
 */
public class DkMemoryCache {
	public interface Listener {
		void onRemoved(String key, @Nullable Snapshot snapshot);
	}

	private static DkMemoryCache INS;

	private long size;
	private long maxSize;
	private final TreeMap<String, Snapshot> cache;
	private final ArrayList<Listener> listeners = new ArrayList<>();

	private DkMemoryCache() {
		maxSize = Runtime.getRuntime().maxMemory() >> 2;
		cache = new TreeMap<>();
	}

	public static DkMemoryCache getIns() {
		if (INS == null) {
			synchronized (DkMemoryCache.class) {
				if (INS == null) {
					INS = new DkMemoryCache();
				}
			}
		}
		return INS;
	}

	public DkMemoryCache setMaxSize(long maxSize) {
		if (maxSize <= 0) {
			maxSize = 1;
		}
		this.maxSize = maxSize;
		return this;
	}

	public Snapshot newSnapshot(Object target) {
		return new Snapshot(target);
	}

	public void put(String key, Bitmap value) {
		put(key, new Snapshot(value, DkBitmaps.size(value)));
	}

	public synchronized void put(String key, Snapshot snapshot) {
		if (key == null || snapshot == null) {
			throw new RuntimeException("Cannot put null-key or null-snapshot");
		}
		long more = snapshot.size;
		removeExpiredObjects();

		if (size + more >= maxSize) {
			trimToSize(maxSize - more);
		}

		size += more;
		cache.put(key, snapshot);
	}

	public synchronized void remove(String key) {
		Snapshot snapshot = cache.get(key);

		if (snapshot != null) {
			size -= snapshot.size;
		}

		cache.remove(key);

		for (Listener listener : listeners) {
			listener.onRemoved(key, snapshot);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T get(String key) {
		Snapshot snapshot = cache.get(key);

		if (snapshot != null) {
			return (T) snapshot.target;
		}

		return null;
	}

	/**
	 * 優先度の昇順でnewSizeに下がるまでオブジェクトを削除していきます。
	 */
	public synchronized void trimToSize(long newSize) {
		if (newSize < 0) {
			newSize = 0;
		}

		long curSize = size;

		// Remove low priority and older objects from start to end
		while (curSize > newSize) {
			Map.Entry<String, Snapshot> entry = cache.pollFirstEntry();
			Snapshot snapshot = entry.getValue();
			curSize -= snapshot.size;

			for (Listener listener : listeners) {
				listener.onRemoved(entry.getKey(), snapshot);
			}
		}

		size = curSize < 0 ? 0 : curSize;
	}

	/**
	 * 期限切れたオブジェクトを全て削除します。
	 */
	public synchronized void removeExpiredObjects() {
		long curSize = size;
		long now = SystemClock.uptimeMillis();
		Iterator<Map.Entry<String, Snapshot>> it = cache.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Snapshot> entry = it.next();
			Snapshot snapshot = entry.getValue();

			if (snapshot.expiredTime >= now) {
				curSize -= snapshot.size;
				it.remove();

				for (Listener listener : listeners) {
					listener.onRemoved(entry.getKey(), snapshot);
				}
			}
		}

		size = curSize < 0 ? 0 : curSize;
	}

	public void register(Listener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void unregister(Listener listener) {
		listeners.remove(listener);
	}

	public static class Snapshot {
		// 定まったメモリ量を超えた場合、優先度の低いものから削除していきます。
		// 基本的に昇順で0から10までの数字で十分だと思います。
		int priority;

		// SystemClock.uptimeMillis()の時間、デフォルト値は無限値です。
		// 期限切れたものはキャッシュから削除されます
		long expiredTime;

		// キャッシュ対象オブジェクト
		Object target;
		long size;

		public Snapshot() {
		}

		public Snapshot(Object target) {
			this.target = target;
		}

		public Snapshot(Object target, long size) {
			this.target = target;
			this.size = size < 1 ? 1 : size;
		}

		public Snapshot setPriority(int priority) {
			this.priority = priority;
			return this;
		}

		public Snapshot setExpiredTime(long uptimeMillis) {
			this.expiredTime = uptimeMillis;
			return this;
		}

		public Snapshot setExpiredTime(long duration, TimeUnit timeUnit) {
			this.expiredTime = SystemClock.uptimeMillis() + timeUnit.toNanos(duration);
			return this;
		}

		public Snapshot setSize(long size) {
			this.size = size;
			return this;
		}

		public Snapshot setTarget(Object target) {
			this.target = target;
			return this;
		}
	}
}
