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

package tool.compet.appbundle.eventbus;

import java.util.concurrent.ScheduledExecutorService;

import tool.compet.core.helper.DkExecutorService;
import tool.compet.core.util.DkLogs;

class AsyncPoster {
	private final SerialExecutor serialExecutor;

	AsyncPoster(DkEventBus eventbus) {
		serialExecutor = new SerialExecutor(DkExecutorService.getIns(), eventbus);
	}

	void post(DkEventBus eventbus, Subscription subscription, Object event) {
		DkExecutorService.getIns().execute(() -> {
			eventbus.invokeSubscriber(subscription, event);
		});
	}

	void enqueue(Subscription subscription, Object event) {
		serialExecutor.execute(new PendingPost(subscription, event));
	}

	static class SerialExecutor {
		final ScheduledExecutorService service;
		final PendingPostQueue queue;
		final DkEventBus eventbus;
		PendingPost active;

		SerialExecutor(ScheduledExecutorService executor, DkEventBus eventbus) {
			this.service = executor;
			this.queue = new PendingPostQueue();
			this.eventbus = eventbus;
		}

		synchronized void execute(PendingPost pp) {
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
					DkLogs.logw(this, "Error occured when run task on serial-executor: " + active);
					DkLogs.logex(this, e);
				}
				finally {
					executeNext();
				}
			});
		}
	}
}
