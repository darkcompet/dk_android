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

import tool.compet.core.stream.observer.DkObserver;
import tool.compet.core.stream.observer.DkControllable;

/**
 * God observable node.
 */
public class GodIterableObservable<T> extends DkObservable<T> {
	private final Iterable<T> items;

	GodIterableObservable(Iterable<T> items) {
		this.items = items;
	}

	@Override
	protected void performSubscribe(DkObserver<T> child) {
		DkIterableObserver<T> wrapper = new DkIterableObserver<>(child);
		wrapper.start(items);
	}

	static class DkIterableObserver<T> extends DkControllable<T> implements DkObserver<T> {
		DkIterableObserver(DkObserver<T> child) {
			super(child);
		}

		void start(Iterable<T> items) {
			try {
				child.onSubscribe(this);

				if (isCancel) {
					isCanceled = true;
					return;
				}

				for (T item : items) {
					if (isCancel) {
						isCanceled = true;
						break;
					}
					child.onNext(item);
				}

				child.onComplete();
			}
			catch (Exception e) {
				onError(e);
			}
			finally {
				onFinal();
			}
		}

		@Override
		public void onSubscribe(DkControllable controllable) {
			parent = null;
			child.onSubscribe(controllable);
		}
	}
}
