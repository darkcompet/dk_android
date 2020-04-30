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

import tool.compet.core.stream.observer.DkObserver;
import tool.compet.core.stream.observer.DkControllable;
import tool.compet.core.stream.observer.Observer;
import tool.compet.core.stream.scheduler.DkScheduler;
import tool.compet.core.util.DkLogs;

public class ObserveOnObservable<T> extends DkObservable<T> {
	private final DkScheduler<T> scheduler;
	private final long delay;
	private final TimeUnit timeUnit;
	private final boolean isSerial;

	ObserveOnObservable(DkObservable<T> parent, DkScheduler<T> scheduler, long delay, TimeUnit timeUnit, boolean isSerial) {
		super(parent);
		this.scheduler = scheduler;
		this.delay = delay;
		this.timeUnit = timeUnit;
		this.isSerial = isSerial;
	}

	@Override
	protected void performSubscribe(DkObserver<T> child) {
		parent.subscribe(new ObserveOnObserver<>(child, scheduler, delay, timeUnit, isSerial));
	}

	static class ObserveOnObserver<T> extends Observer<T> {
		final DkScheduler<T> scheduler;
		final long delay;
		final TimeUnit timeUnit;
		final boolean isSerial;

		ObserveOnObserver(DkObserver<T> child, DkScheduler<T> scheduler, long delay, TimeUnit timeUnit, boolean isSerial) {
			super(child);
			this.scheduler = scheduler;
			this.delay = delay;
			this.timeUnit = timeUnit;
			this.isSerial = isSerial;
		}

		@Override
		public void onSubscribe(DkControllable controllable) {
			try {
				scheduler.schedule(() -> child.onSubscribe(controllable), delay, timeUnit, isSerial);
			}
			catch (Exception e) {
				DkLogs.logex(this, e);
			}
		}

		@Override
		public void onNext(T result) {
			try {
				scheduler.schedule(() -> child.onNext(result), delay, timeUnit, isSerial);
			}
			catch (Exception e) {
				DkLogs.logex(this, e);
			}
		}

		@Override
		public void onError(Throwable throwable) {
			try {
				scheduler.scheduleNow(() -> child.onError(throwable), isSerial);
			}
			catch (Exception e) {
				DkLogs.logex(this, e);
			}
		}

		@Override
		public void onComplete() {
			try {
				scheduler.scheduleNow(child::onComplete, isSerial);
			}
			catch (Exception e) {
				DkLogs.logex(this, e);
			}
		}
	}
}
