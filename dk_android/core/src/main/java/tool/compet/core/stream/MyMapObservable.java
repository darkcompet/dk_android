/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkCallable1;

class MyMapObservable<T, R> extends DkObservable<R> {
	private final DkObservable<T> parent;
	private final DkCallable1<T, R> converter;

	MyMapObservable(DkObservable<T> parent, DkCallable1<T, R> converter) {
		this.parent = parent;
		this.converter = converter;
	}

	@Override
	protected void subscribeActual(DkObserver<R> observer) {
		parent.subscribe(new MapObserver<>(observer, converter));
	}

	static class MapObserver<T, R> extends MyMapObserver<T, R> {
		final DkObserver<R> child;
		final DkCallable1<T, R> converter;

		MapObserver(DkObserver<R> child, DkCallable1<T, R> converter) {
			super(child);
			this.child = child;
			this.converter = converter;
		}

		@Override
		public void onNext(T result) throws Exception {
			child.onNext(converter.call(result));
		}
	}
}
