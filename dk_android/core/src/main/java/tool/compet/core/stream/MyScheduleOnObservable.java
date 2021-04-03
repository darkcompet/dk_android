/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import tool.compet.core.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * Normally, switch thread, and run code of upper nodes in IO (background) thread.
 */
class MyScheduleOnObservable<T> extends DkObservable<T> {
	private final DkScheduler<T> scheduler;
	private final long delay;
	private final TimeUnit timeUnit;
	private final boolean isSerial;

	MyScheduleOnObservable(DkObservable<T> parent, DkScheduler<T> scheduler, long delay, TimeUnit unit, boolean isSerial) {
		super(parent);
		this.scheduler = scheduler;
		this.delay = delay;
		this.timeUnit = unit;
		this.isSerial = isSerial;
	}

	@Override
	protected void subscribeActual(DkObserver<T> child) throws Exception {
		SchedulerOnObserver<T> myObserver = new SchedulerOnObserver<>(child, this.scheduler);
		// From now, code will be scheduled in other thread
		myObserver.start(parent, delay, timeUnit, isSerial);
	}

	static class SchedulerOnObserver<T> extends DkControllable<T> {
		final DkScheduler<T> scheduler;
		Callable<T> task;

		SchedulerOnObserver(DkObserver<T> child, DkScheduler<T> scheduler) {
			super(child);
			this.scheduler = scheduler;
		}

		void start(DkObservable<T> parent, long delay, TimeUnit timeUnit, boolean isSerial) throws Exception {
			// Give to children a chance to cancel scheduling in other thread
			child.onSubscribe(this);
			if (isCancel) {
				isCanceled = true;
				onFinal();
				return;
			}

			// Task in the service (IO thread...)
			task = () -> {
				// maybe it takes long time to schedule, so check again cancel event from user
				if (isCancel) {
					isCanceled = true;
					onFinal();
				}
				else {
					parent.subscribe(SchedulerOnObserver.this);
				}
				return null;
			};

			// Start subscribe in other thread
			scheduler.schedule(task, delay, timeUnit, isSerial);
		}

		@Override
		public boolean cancel(boolean mayInterruptThread) {
			boolean ok = super.cancel(mayInterruptThread);

			if (task != null) {
				ok |= scheduler.cancel(task, mayInterruptThread);
			}

			isCanceled = ok;

			if (DEBUG) {
				DkLogs.info(this, "Cancel task: " + task + ", ok: " + ok);
			}

			return ok;
		}
	}
}
