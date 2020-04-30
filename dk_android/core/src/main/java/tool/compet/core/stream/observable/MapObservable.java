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
import tool.compet.core.stream.observer.AbsMapObserver;
import tool.compet.core.stream.observer.DkObserver;

public class MapObservable<T, R> extends DownstreamObservable<T, R> {
	private final DkThrowableFunction<T, R> converter;

	MapObservable(DkObservable<T> parent, DkThrowableFunction<T, R> converter) {
		super(parent);
		this.converter = converter;
	}

	@Override
	protected void performSubscribe(DkObserver<R> observer) {
		parent.subscribe(new MapObserver<>(observer, converter));
	}

	static class MapObserver<T, R> extends AbsMapObserver<T, R> {
		final DkThrowableFunction<T, R> converter;

		MapObserver(DkObserver<R> child, DkThrowableFunction<T, R> converter) {
			super(child);
			this.converter = converter;
		}

		@Override
		public void onNext(T result) {
			try {
				child.onNext(converter.apply(result));
			}
			catch (Exception e) {
				onError(e);
			}
		}
	}
}
