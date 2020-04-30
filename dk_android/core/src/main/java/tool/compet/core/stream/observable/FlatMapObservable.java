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

import tool.compet.core.stream.function.DkThrowableFunction;
import tool.compet.core.stream.observer.AbsFlatMapObserver;
import tool.compet.core.stream.observer.DkObserver;
import tool.compet.core.stream.observer.DkBenchMarkObserver;

public class FlatMapObservable<T, R> extends DownstreamObservable<T, R> {
	private final DkThrowableFunction<T, DkObservable<R>> converter;

	FlatMapObservable(DkObservable<T> parent, DkThrowableFunction<T, DkObservable<R>> converter) {
		super(parent);
		this.converter = converter;
	}

	@Override
	protected void performSubscribe(DkObserver<R> child) {
		parent.subscribe(new FlatMapObserver<>(child, converter));
	}

	static class FlatMapObserver<T, R> extends AbsFlatMapObserver<T, R> {
		final DkThrowableFunction<T, DkObservable<R>> converter;

		FlatMapObserver(DkObserver<R> child, DkThrowableFunction<T, DkObservable<R>> converter) {
			super(child);
			this.converter = converter;
		}

		@Override
		public void onNext(T result) {
			try {
				DkObservable<R> nextObservable = converter.apply(result);

				// If converter null, we can considere this flatMap is normal map
				if (nextObservable == null) {
					child.onNext(null);
					return;
				}

				// Run on same thread with upper node
				nextObservable.subscribe(new DkBenchMarkObserver<>(child));
			}
			catch (Exception e) {
				child.onError(e);
			}
		}
	}
}
