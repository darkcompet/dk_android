/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.floatingbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;

import tool.compet.core.DkLogs;

public class MyFloatingbarManager {
	private static final int TIMEOUT = 1;
	private static final int FORCE = 2;

	private boolean isRunning;
	private ArrayDeque<Record> queue = new ArrayDeque<>();

	private Handler handler = new Handler(Looper.getMainLooper(), msg -> {
		if (msg.what == TIMEOUT) {
			dismissInternal((Record) msg.obj, TIMEOUT);
		}
		return false;
	});

	void show(long duration, Callback callback) {
		Record tail = queue.peekLast();

		if (tail != null && callback == tail.callback.get()) {
			// just update duration for same record
			tail.duration = duration;
		}
		else {
			// enqueue new record
			queue.addLast(new Record(duration, callback));
		}

		if (!isRunning) {
			isRunning = true;
			showNext();
		}
	}

	void dismiss(Callback callback) {
		Record record = findRecordFromQueue(callback);
		if (record != null) {
			dismissInternal(record, FORCE);
		}
	}

	void dismissAll() {
		for (Record record : queue) {
			dismissInternal(record, FORCE);
		}
	}

	void onViewShown(Callback callback) {
		Record record = findRecordFromQueue(callback);
		if (record != null) {
			// start display-timeout for this bar
			handler.removeCallbacksAndMessages(record);
			handler.sendMessageDelayed(Message.obtain(handler, TIMEOUT, record), record.duration);
		}
	}

	void onViewDismissed(@Nullable Callback callback) {
		onDismissed(findRecordFromQueue(callback));
	}

	/**
	 * Try to show next until some record (bar) is shown successfully.
	 */
	private void showNext() {
		Record next = queue.peekFirst();

		if (next == null) {
			isRunning = false;
			return;
		}
		if (!next.show()) {
			// consider this bar is dismissed if failed to show
			onDismissed(next);
		}
	}

	private void dismissInternal(Record record, int type) {
		switch (type) {
			case TIMEOUT: {
				// we don't dismiss bar for infinite-duration-timeout type
				if (record.duration == DkFloatingbar.INFINITE_DURATION) {
					return;
				}
				break;
			}
			case FORCE: {
				// remove timeout since this record is forced to dismiss
				handler.removeCallbacksAndMessages(record);
				break;
			}
			default: {
				throw new RuntimeException("Invalid type");
			}
		}

		if (!record.dismiss()) {
			onDismissed(record);
		}
	}

	/**
	 * This is last phase when a bar is dismissed. We cleanup this record first.
	 * Then show next bar even thought this record is not existed.
	 * <p></p>
	 * In worst case, if this method is not called at sometime after bar is shown,
	 * then next bar will not be shown, so user need to call dismiss() from
	 * current showing bar to manual dismiss it.
	 */
	private void onDismissed(Record record) {
		if (record != null) {
			queue.removeFirstOccurrence(record);
		}
		showNext();
	}

	private Record findRecordFromQueue(Callback callback) {
		for (Record record : queue) {
			if (callback == record.callback.get()) {
				return record;
			}
		}

		DkLogs.warning(this, "Not found record for callback: " + callback);

		return null;
	}

	interface Callback {
		void show();

		void dismiss();
	}

	static class Record {
		long duration;
		WeakReference<Callback> callback;

		Record(long duration, Callback callback) {
			this.duration = duration;
			this.callback = new WeakReference<>(callback);
		}

		boolean show() {
			Callback cb = callback.get();
			if (cb != null) {
				cb.show();
				return true;
			}
			return false;
		}

		boolean dismiss() {
			Callback cb = callback.get();
			if (cb != null) {
				cb.dismiss();
				return true;
			}
			return false;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Record && callback.get() == ((Record) obj).callback.get();
		}
	}
}
