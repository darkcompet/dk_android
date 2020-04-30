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

import tool.compet.core.stream.function.DkThrowableCallback;
import tool.compet.core.stream.observer.DkObserver;
import tool.compet.core.stream.observer.DkControllable;
import tool.compet.core.stream.observer.Observer;
import tool.compet.core.util.DkLogs;

public class OnSubscribeObservable<T> extends DkObservable<T> {
	private final DkThrowableCallback<DkControllable> action;

	OnSubscribeObservable(DkObservable<T> parent, DkThrowableCallback<DkControllable> action) {
		super(parent);
		this.action = action;
	}

	@Override
	protected void performSubscribe(DkObserver<T> observer) {
		parent.subscribe(new OnSubscribeObserver<>(observer, action));
	}

	static class OnSubscribeObserver<T> extends Observer<T> {
		final DkThrowableCallback<DkControllable> action;

		OnSubscribeObserver(DkObserver<T> child, DkThrowableCallback<DkControllable> action) {
			super(child);
			this.action = action;
		}

		@Override
		public void onSubscribe(DkControllable controllable) {
			try {
				action.call(controllable);
			}
			catch (Exception e) {
				DkLogs.logex(this, e);
			}
			finally {
				child.onSubscribe(controllable);
			}
		}
	}
}
