/*
 * Copyright (c) 2019 DarkCompet. All rights reserved.
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

import tool.compet.core.stream.observer.DkObserver;
import tool.compet.core.stream.observer.Observer;

/**
 * Switch to #onNext() if exception occured in upper node.
 */
public class TryCatchObservable<T> extends DkObservable<T> {
	TryCatchObservable(DkObservable<T> parent) {
		super(parent);
	}

	@Override
	protected void performSubscribe(DkObserver<T> observer) {
		parent.subscribe(new TryCatchObserver<>(observer));
	}

	static class TryCatchObserver<T> extends Observer<T> {
		TryCatchObserver(DkObserver<T> child) {
			super(child);
		}

		@Override
		public void onError(Throwable e) {
			child.onNext(null);
		}
	}
}
