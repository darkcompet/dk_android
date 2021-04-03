/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import tool.compet.core.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

class MyIoScheduler<T> implements DkScheduler<T> {
	private final ScheduledExecutorService parellelExecutor;
	private final SerialExecutor<T> serialExecutor;
	private final ConcurrentHashMap<Callable<T>, ScheduledFuture<?>> schedulingTasks;
	private final ConcurrentLinkedQueue<Callable<T>> pendingTasks;

	MyIoScheduler(ScheduledExecutorService service) {
		this.parellelExecutor = service;
		this.schedulingTasks = new ConcurrentHashMap<>();
		this.pendingTasks = new ConcurrentLinkedQueue<>();
		this.serialExecutor = new SerialExecutor<>(service, schedulingTasks, pendingTasks);
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
			DkLogs.info(this, "Cancelled task %s, result: %b", task.toString(), ok);
		}
		if (ok) {
			schedulingTasks.remove(task);
			return true;
		}

		return false;
	}

	static class SerialExecutor<T> {
		final ScheduledExecutorService executor;
		ConcurrentLinkedQueue<Callable<T>> pendingTasks;
		Callable<T> active;
		ConcurrentHashMap<Callable<T>, ScheduledFuture<?>> schedulingTasks;

		SerialExecutor(
			ScheduledExecutorService executor,
			ConcurrentHashMap<Callable<T>,
				ScheduledFuture<?>> schedulingTasks,
			ConcurrentLinkedQueue<Callable<T>> pendingTasks) {
			// Just store
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
				ScheduledFuture future = executor.schedule(() -> {
					try {
						active.call();
					}
					catch (Exception e) {
						DkLogs.warning(this, "Error when run on serial-executor for task: " + active);
						DkLogs.error(this, e);
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
