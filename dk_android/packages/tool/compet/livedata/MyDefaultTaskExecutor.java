/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class MyDefaultTaskExecutor extends MyTaskExecutor {
	private final Object mLock = new Object();

	private final ExecutorService mDiskIO = Executors.newFixedThreadPool(4, new ThreadFactory() {
		private static final String THREAD_NAME_STEM = "arch_disk_io_";

		private final AtomicInteger mThreadId = new AtomicInteger(0);

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName(THREAD_NAME_STEM + mThreadId.getAndIncrement());
			return t;
		}
	});

	@Nullable
	private volatile Handler mMainHandler;

	@Override
	public void executeOnDiskIO(@NonNull Runnable runnable) {
		mDiskIO.execute(runnable);
	}

	@Override
	public void postToMainThread(@NonNull Runnable runnable) {
		if (mMainHandler == null) {
			synchronized (mLock) {
				if (mMainHandler == null) {
					mMainHandler = createAsync(Looper.getMainLooper());
				}
			}
		}
		//noinspection ConstantConditions
		mMainHandler.post(runnable);
	}

	@Override
	public boolean isMainThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

	private static Handler createAsync(@NonNull Looper looper) {
		if (Build.VERSION.SDK_INT >= 28) {
			return Handler.createAsync(looper);
		}
		if (Build.VERSION.SDK_INT >= 16) {
			try {
				return Handler.class.getDeclaredConstructor(Looper.class, Handler.Callback.class, boolean.class).newInstance(looper, null, true);
			}
			catch (IllegalAccessException ignored) {
			}
			catch (InstantiationException ignored) {
			}
			catch (NoSuchMethodException ignored) {
			}
			catch (InvocationTargetException e) {
				return new Handler(looper);
			}
		}
		return new Handler(looper);
	}
}
