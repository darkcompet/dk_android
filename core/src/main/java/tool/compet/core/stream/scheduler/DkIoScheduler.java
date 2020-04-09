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

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import tool.compet.core.util.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

public class DkIoScheduler<T> implements DkScheduler<T> {
	private final ScheduledExecutorService parellelExecutor;
	private final DkSerialExecutor<T> serialExecutor;
	private final ConcurrentHashMap<Callable<T>, ScheduledFuture<?>> schedulingTasks;
	private ConcurrentLinkedQueue<Callable<T>> pendingTasks;

	DkIoScheduler(ScheduledExecutorService service) {
		this.parellelExecutor = service;
		this.schedulingTasks = new ConcurrentHashMap<>();
		this.pendingTasks = new ConcurrentLinkedQueue<>();
		this.serialExecutor = new DkSerialExecutor<>(service, schedulingTasks, pendingTasks);
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
		if (isSerial) {
			// Note that, task will be auto queued to pendingTasks
			serialExecutor.schedule(task, delay, unit);
		}
		else {
			schedulingTasks.put(task, parellelExecutor.schedule(task, delay, unit));
		}
	}

	// Just try to cancel, not serious way to cancel a task
	@Override
	public boolean cancel(Callable<T> task, boolean mayInterruptIfRunning) {
		ScheduledFuture future = schedulingTasks.get(task);

		if (future == null) {
			pendingTasks.remove(task);
			return true;
		}

		boolean ok = future.cancel(mayInterruptIfRunning);

		if (DEBUG) {
			DkLogs.log(this, "Cancelled task %s, result: %b",
				task.toString(), ok);
		}

		if (ok) {
			schedulingTasks.remove(task);
			return true;
		}

		return false;
	}

	static class DkSerialExecutor<T> {
		final ScheduledExecutorService executor;
		ConcurrentLinkedQueue<Callable<T>> pendingTasks;
		Callable<T> active;
		ConcurrentHashMap<Callable<T>, ScheduledFuture<?>> schedulingTasks;

		DkSerialExecutor(ScheduledExecutorService executor,
			ConcurrentHashMap<Callable<T>,
			ScheduledFuture<?>> schedulingTasks,
			ConcurrentLinkedQueue<Callable<T>> pendingTasks) {

			this.executor = executor;
			this.schedulingTasks = schedulingTasks;
			this.pendingTasks = pendingTasks;
		}

		synchronized void schedule(Callable<T> task, long delay, TimeUnit timeUnit) {
			pendingTasks.offer(task);
			// start schedule if have not active task
			if (active == null) {
				scheduleNext(delay, timeUnit);
			}
		}

		synchronized void scheduleNext(long delay, TimeUnit timeUnit) {
			if ((active = pendingTasks.poll()) != null) {
				ScheduledFuture future =  executor.schedule(() -> {
					try {
						active.call();
					}
					catch (Exception e) {
						DkLogs.logw(this, "Error when run task on serial-executor for " + active);
						DkLogs.logex(this, e);
					}
					finally {
						// Cleanup for this task and schedule next
						schedulingTasks.remove(active);
						scheduleNext(delay, timeUnit);
					}
				}, delay, timeUnit);

				// Store this task to handle result or cancel if need
				schedulingTasks.put(active, future);
			}
		}
	}
}
