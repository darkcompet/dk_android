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
import tool.compet.core.stream.observer.Observer;
import tool.compet.core.util.DkLogs;

public class OnErrorObservable<T> extends DkObservable<T> {
	private final DkThrowableCallback<Throwable> action;

	OnErrorObservable(DkObservable<T> parent, DkThrowableCallback<Throwable> action) {
		super(parent);
		this.action = action;
	}

	@Override
	protected void performSubscribe(DkObserver<T> observer) {
		parent.subscribe(new OnErrorObserver<>(observer, action));
	}

	static class OnErrorObserver<T> extends Observer<T> {
		final DkThrowableCallback<Throwable> action;

		OnErrorObserver(DkObserver<T> child, DkThrowableCallback<Throwable> action) {
			super(child);
			this.action = action;
		}

		@Override
		public void onError(Throwable throwable) {
			try {
				action.call(throwable);
			}
			catch (Exception e) {
				DkLogs.error(this, e);
			}
			finally {
				child.onError(throwable);
			}
		}
	}
}
