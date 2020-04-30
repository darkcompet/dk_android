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
public class GodControllableObservable<T> extends DkObservable<T> {
	private final DkControllable<T> controllable;

	GodControllableObservable(DkControllable<T> controllable) {
		this.controllable = controllable;
	}

	@Override
	protected void performSubscribe(DkObserver<T> child) {
		ControllableObserver<T> wrapper = new ControllableObserver<>(child, controllable);
		wrapper.start();
	}

	static class ControllableObserver<T> extends DkControllable<T> implements DkObserver<T> {
		private final DkControllable<T> controllable;

		ControllableObserver(DkObserver<T> child, DkControllable<T> controllable) {
			super(child);
			this.controllable = controllable;
		}

		public void start() {
			try {
				onSubscribe(this);

				if (isCancel) {
					isCanceled = true;
					return;
				}

				onNext(controllable.call());
				onComplete();
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
