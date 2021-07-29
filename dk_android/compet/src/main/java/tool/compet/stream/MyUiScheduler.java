/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.stream;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import tool.compet.core.DkLogcats;
import tool.compet.stream4j.DkScheduler;

import static tool.compet.BuildConfig.DEBUG;

class MyUiScheduler<T> implements DkScheduler<T> {
	private final Handler handler;
	private final ConcurrentHashMap<Callable<T>, Runnable> pendingCommands;

	MyUiScheduler() {
		handler = new Handler(Looper.getMainLooper());
		pendingCommands = new ConcurrentHashMap<>();
	}

	@Override
	public void scheduleNow(Runnable task) {
		schedule(task, 0, TimeUnit.MILLISECONDS, true);
	}

	@Override
	public void scheduleNow(Runnable task, boolean isSerial) {
		schedule(task, 0, TimeUnit.MILLISECONDS, isSerial);
	}

	@Override
	public void schedule(Runnable task, long delay, TimeUnit unit, boolean isSerial) {
		schedule(() -> {
			task.run();
			return null;
		}, delay, unit, isSerial);
	}

	@Override
	public void scheduleNow(Callable<T> task) {
		schedule(task, 0, TimeUnit.MILLISECONDS, true);
	}

	@Override
	public void scheduleNow(Callable<T> task, boolean isSerial) {
		schedule(task, 0, TimeUnit.MILLISECONDS, isSerial);
	}

	@Override
	public void schedule(Callable<T> task, long delay, TimeUnit unit, boolean isSerial) {
		// Run on IO thread, so must take care about thread-safe
		Runnable command = () -> {
			try {
				task.call();
			}
			catch (Exception e) {
				DkLogcats.error(this, e);
			}
			finally {
				pendingCommands.remove(task);
			}
		};

		pendingCommands.put(task, command);

		handler.postDelayed(command, unit.toMillis(delay));
	}

	// Just try to cancel, not serious way to cancel a task
	@Override
	public boolean cancel(Callable<T> task, boolean mayInterruptThread) {
		Runnable command = pendingCommands.get(task);

		if (DEBUG) {
			DkLogcats.info(this, "Cancelled task %s, result: %b", task.toString(), command != null);
		}

		if (command != null) {
			handler.removeCallbacks(command);
			pendingCommands.remove(task);
			return true;
		}

		return false;
	}
}
