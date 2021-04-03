/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.eventbus;

import java.util.concurrent.ScheduledExecutorService;

import tool.compet.core.DkExecutorService;
import tool.compet.core.DkLogs;

class MyAsyncPoster {
	private final SerialExecutor serialExecutor;

	MyAsyncPoster(DkEventBus eventbus) {
		serialExecutor = new SerialExecutor(DkExecutorService.getIns(), eventbus);
	}

	void post(DkEventBus eventbus, MySubscription subscription, Object event) {
		DkExecutorService.getIns().execute(() -> {
			eventbus.invokeSubscriber(subscription, event);
		});
	}

	void enqueue(MySubscription subscription, Object eventData) {
		serialExecutor.execute(new MyPendingPost(subscription, eventData));
	}

	static class SerialExecutor {
		final ScheduledExecutorService service;
		final MyPendingPostQueue queue;
		final DkEventBus eventbus;
		MyPendingPost active;

		SerialExecutor(ScheduledExecutorService executor, DkEventBus eventbus) {
			this.service = executor;
			this.queue = new MyPendingPostQueue();
			this.eventbus = eventbus;
		}

		synchronized void execute(MyPendingPost pp) {
			queue.enqueue(pp);
			// start schedule if have not active task
			if (active == null) {
				executeNext();
			}
		}

		synchronized void executeNext() {
			if ((active = queue.dequeue()) == null) {
				return;
			}

			service.execute(() -> {
				try {
					eventbus.invokeSubscriber(active);
				}
				catch (Exception e) {
					DkLogs.warning(this, "Error occured when run task on serial-executor: " + active);
					DkLogs.error(this, e);
				}
				finally {
					executeNext();
				}
			});
		}
	}
}
