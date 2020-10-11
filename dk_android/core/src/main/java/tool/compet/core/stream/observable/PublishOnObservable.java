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

package tool.compet.core.stream.observable;

import java.util.concurrent.TimeUnit;

import tool.compet.core.stream.function.DkThrowableCallback;
import tool.compet.core.stream.observer.DkObserver;
import tool.compet.core.stream.observer.Observer;
import tool.compet.core.stream.scheduler.DkScheduler;
import tool.compet.core.util.DkLogs;

public class PublishOnObservable<T> extends DkObservable<T> {
	private final DkScheduler<T> scheduler;
	private final DkThrowableCallback<T> action;
	private final long delay;
	private final TimeUnit unit;
	private final boolean isSerial;

	PublishOnObservable(DkObservable<T> parent, DkScheduler<T> scheduler, DkThrowableCallback<T> action,
		long delay, TimeUnit unit, boolean isSerial) {

		super(parent);
		this.scheduler = scheduler;
		this.action = action;
		this.delay = delay;
		this.unit = unit;
		this.isSerial = isSerial;
	}

	@Override
	protected void performSubscribe(DkObserver<T> child) {
		parent.subscribe(new PublishOnObserver<>(child, scheduler, action, delay, unit, isSerial));
	}

	static class PublishOnObserver<T> extends Observer<T> {
		final DkScheduler<T> scheduler;
		final DkThrowableCallback<T> action;
		final long delay;
		final TimeUnit unit;
		final boolean isSerial;

		PublishOnObserver(DkObserver<T> child, DkScheduler<T> scheduler, DkThrowableCallback<T> action,
			long delay, TimeUnit unit, boolean isSerial) {

			super(child);
			this.scheduler = scheduler;
			this.action = action;
			this.delay = delay;
			this.unit = unit;
			this.isSerial = isSerial;
		}

		@Override
		public void onNext(T item) {
			try {
				scheduler.schedule(() -> {
					try {
						action.call(item);
						child.onNext(item);
					}
					catch (Exception e) {
						child.onError(e);
						DkLogs.error(this, e);
					}
				}, delay, unit, isSerial);
			}
			catch (Exception e) {
				DkLogs.error(this, e);
				child.onError(e);
			}
		}
	}
}
