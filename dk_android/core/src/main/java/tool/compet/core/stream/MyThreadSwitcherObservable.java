/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import tool.compet.core.DkLogs;
import tool.compet.core.DkRunnable1;

/**
 * Normally, switch thread, run in main thread instead of IO thread.
 */
class MyThreadSwitcherObservable<T> extends DkObservable<T> {
	private final DkScheduler<T> scheduler;
	@Nullable
	private final DkRunnable1<T> action;
	private final long delay;
	private final TimeUnit timeUnit;
	private final boolean isSerial;

	MyThreadSwitcherObservable(DkObservable<T> parent, DkScheduler<T> scheduler, @Nullable DkRunnable1<T> action,
		long delay, TimeUnit timeUnit, boolean isSerial) {
		// Call super first
		super(parent);
		this.scheduler = scheduler;
		this.action = action;
		this.delay = delay;
		this.timeUnit = timeUnit;
		this.isSerial = isSerial;
	}

	@Override
	protected void subscribeActual(DkObserver<T> child) throws Exception {
		parent.subscribe(new ThreadSwitcherObserver<>(child, scheduler, action, delay, timeUnit, isSerial));
	}

	static class ThreadSwitcherObserver<T> extends MyObserver<T> {
		final DkScheduler<T> scheduler;
		@Nullable
		final DkRunnable1<T> action;
		final long delay;
		final TimeUnit timeUnit;
		final boolean isSerial;

		// Error when pass event to child node in other (main) thread
		private Throwable eventCallException;

		ThreadSwitcherObserver(DkObserver<T> child, DkScheduler<T> scheduler, @Nullable DkRunnable1<T> action,
			long delay, TimeUnit timeUnit, boolean isSerial) {
			// Call super first
			super(child);
			this.scheduler = scheduler;
			this.action = action;
			this.delay = delay;
			this.timeUnit = timeUnit;
			this.isSerial = isSerial;
		}

		// Normally, this is run in IO thread
		@Override
		public void onSubscribe(DkControllable controllable) throws Exception {
			scheduler.schedule(() -> {
				try {
					child.onSubscribe(controllable);
				}
				catch (Exception e) {
					// Remember this error and pass to child node at next event
					eventCallException = e;
				}
			}, delay, timeUnit, isSerial);
		}

		// Normally, this is run in IO thread
		@Override
		public void onNext(T result) throws Exception {
			scheduler.schedule(() -> {
				// If no error occured, then pass result to child node
				if (eventCallException == null) {
					try {
						// Run action and pass result to child node
						if (action != null) {
							action.run(result);
						}
						child.onNext(result);
					}
					catch (Exception e) {
						// Remember this error and handle it at `onComplete()` event
						eventCallException = e;
					}
				}
				// If some exception was raised previously, just ignore call `onError()` at this time
				// -> We will handle it at `onError()` or `onComplete()` event instead.
				else {
					DkLogs.error(this, eventCallException);
				}
			}, delay, timeUnit, isSerial);
		}

		// Normally, this is run in IO thread
		@Override
		public void onComplete() throws Exception {
			scheduler.scheduleNow(() -> {
				if (eventCallException == null) {
					try {
						// No previous error -> just pass complete event to child node
						child.onComplete();
					}
					catch (Exception e) {
						// Some exception was thrown at lower node.
						// -> We act as God node, switch to error event
						eventCallException = e;
						child.onError(e);
					}
				}
				else {
					// Some exception was occured before -> we should pass that exception
					// to child node and then clear exception
					child.onError(eventCallException);
					eventCallException = null;
				}
			}, isSerial);
		}

		// Normally, this is run in IO thread
		@Override
		public void onError(Throwable throwable) {
			try {
				scheduler.scheduleNow(() -> child.onError(throwable), isSerial);
			}
			catch (Exception e) {
				// Unable to switch thread -> just pass error event to child node
				child.onError(e);
			}
		}

		// Normally, this is run in IO thread
		@Override
		public void onFinal() {
			try {
				scheduler.scheduleNow(child::onFinal, isSerial);
			}
			catch (Exception e) {
				// Cannot schedule -> pass final-event to child node and write log
				child.onFinal();
				DkLogs.error(this, e);
			}
		}
	}
}
