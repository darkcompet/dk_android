/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.core.stream.scheduler;

import android.os.Handler;
import android.os.Looper;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import tool.compet.core.util.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

public class DkAndroidMainScheduler<T> implements DkScheduler<T> {
	private Handler handler;
	private ConcurrentHashMap<Callable<T>, Runnable> pendingCommands;

	DkAndroidMainScheduler() {
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
				DkLogs.logex(this, e);
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
			DkLogs.log(this, "Cancelled task %s, result: %b",
				task.toString(), command != null);
		}

		if (command != null) {
			handler.removeCallbacks(command);
			pendingCommands.remove(task);
			return true;
		}

		return false;
	}
}
