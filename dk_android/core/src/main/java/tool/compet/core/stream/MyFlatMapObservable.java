/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkCallable1;

/**
 * This switches result from parent node to other type at child node.
 *
 * @param <T> Parent node type.
 * @param <R> Child node type.
 */
class MyFlatMapObservable<T, R> extends DkObservable<R> {
	private final DkObservable<T> parent;
	private final DkCallable1<T, DkObservable<R>> converter;

	MyFlatMapObservable(DkObservable<T> parent, DkCallable1<T, DkObservable<R>> converter) {
		this.parent = parent;
		this.converter = converter;
	}

	@Override
	protected void subscribeActual(DkObserver<R> child) {
		parent.subscribe(new FlatMapObserver<>(child, converter));
	}

	// This observer is flat map which can cancel, pause, resume stream
	static class FlatMapObserver<T, R> extends MyFlatMapObserver<T, R> {
		final DkCallable1<T, DkObservable<R>> converter;

		FlatMapObserver(DkObserver<R> child, DkCallable1<T, DkObservable<R>> converter) {
			super(child);
			this.converter = converter;
		}

		@Override
		public void onSubscribe(DkControllable controllable) throws Exception {
			super.onSubscribe(controllable);
		}

		@Override
		public void onNext(T result) throws Exception {
			// Pass result to create new observable
			DkObservable<R> flatObservable;
			try {
				flatObservable = converter.call(result);
			}
			catch (Exception e) {
				flatObservable = null;
			}

			// If we got null flat observable, just consider this flatMap is normal map
			if (flatObservable == null) {
				child.onNext(null);
				return;
			}

			//todo consider logic of flatmap before implement

			// Run on same thread with upper node
			flatObservable.subscribe(new MyObserver<>(child));
		}

		@Override
		public void onComplete() throws Exception {
			super.onComplete();
		}

		@Override
		public void onError(Throwable e) {
			super.onError(e);
		}

		@Override
		public void onFinal() {
			super.onFinal();
		}
	}
}
