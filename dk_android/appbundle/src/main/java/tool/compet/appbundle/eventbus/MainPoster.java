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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import tool.compet.core.util.DkLogs;

class MainPoster implements Handler.Callback {
	private final DkEventBus eventbus;
	private final PendingPostQueue queue;
	private final Handler handler;
	private volatile boolean isRunning;

	MainPoster(DkEventBus eventbus) {
		this.eventbus = eventbus;
		this.queue = new PendingPostQueue();
		this.handler = new Handler(Looper.getMainLooper(), this);
	}

	void enqueue(Subscription subscription, Object event) {
		PendingPost pendingPost = new PendingPost(subscription, event);

		synchronized (this) {
			queue.enqueue(pendingPost);

			if (!isRunning) {
				isRunning = true;

				if (!handler.sendMessageDelayed(Message.obtain(handler), 0)) {
					// give a change to try again
					isRunning = false;
					DkLogs.logw(this, "Could not send handler message");
				}
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		long start = System.currentTimeMillis();
		PendingPost pendingPost;

		while (true) {
			synchronized (queue) {
				pendingPost = queue.dequeue();
			}

			if (pendingPost == null) {
				synchronized (this) {
					pendingPost = queue.dequeue();

					if (pendingPost == null) {
						isRunning = false;
						break;
					}
				}
			}

			eventbus.invokeSubscriber(pendingPost.subscription, pendingPost.event);

			// Because Android framework maybe skip frames which coming too close,
			// so we Only request next message if elapsed time over actual frameDelay (10ms)
			if (System.currentTimeMillis() - start > 10) {
				if (!handler.sendMessageDelayed(Message.obtain(handler), 0)) {
					// give a change to try send message again
					isRunning = false;
					DkLogs.logw(this, "Could not send handler message again");
				}
				break;
			}
		}

		return true;
	}
}
