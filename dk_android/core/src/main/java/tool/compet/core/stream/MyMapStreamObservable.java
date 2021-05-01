/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.atomic.AtomicInteger;

import tool.compet.core.DkCallable1;

/**
 * Like as normal map, this also maps result from parent node to other type at child node.
 * But this is mapping other stream, so it is convenient method for join streams and run
 * them as straight chain.
 *
 * @param <T> Parent node type.
 * @param <R> Child node type.
 */
class MyMapStreamObservable<T, R> extends DkObservable<R> {
	private final DkObservable<T> parent;
	private final DkCallable1<T, DkObservable<R>> converter;

	MyMapStreamObservable(DkObservable<T> parent, DkCallable1<T, DkObservable<R>> converter) {
		this.parent = parent;
		this.converter = converter;
	}

	@Override
	protected void subscribeActual(DkObserver<R> child) {
		parent.subscribe(new MapStreamObserver<>(child, converter));
	}

	// This observer is flat map which can cancel, pause, resume stream
	private static class MapStreamObserver<T, R> extends MyMapStreamObserver<T, R> {
		final DkCallable1<T, DkObservable<R>> converter;
		WaitObserver<R> myObserver;

		MapStreamObserver(DkObserver<R> child, DkCallable1<T, DkObservable<R>> converter) {
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
			DkObservable<R> myObservable;
			try {
				myObservable = converter.call(result);
			}
			catch (Exception e) {
				myObservable = null;
			}

			// If we got null flat observable, just consider this stream-map is normal map
			if (myObservable == null) {
				child.onNext(null);
				return;
			}

			// Consider given observable is next stream,
			// Run it at same thread with upper node
			myObserver = new WaitObserver<>(child);
			myObservable.subscribe(myObserver);
		}

		@Override
		public void onComplete() throws Exception {
			if (myObserver != null) {
				myObserver.onComplete();
			}
		}

		@Override
		public void onError(Throwable e) {
			if (myObserver != null) {
				myObserver.onError(e);
			}
		}

		@Override
		public void onFinal() {
			if (myObserver != null) {
				myObserver.onFinal();
			}
		}
	}

	private static class WaitObserver<T> extends MyObserver<T> {
		AtomicInteger onCompleteCount = new AtomicInteger(0);
		AtomicInteger onErrorCount = new AtomicInteger(0);
		AtomicInteger onFinalCount = new AtomicInteger(0);

		WaitObserver(DkObserver<T> child) {
			super(child);
		}

		@Override
		public void onSubscribe(DkControllable controllable) throws Exception {
			child.onSubscribe(controllable);
		}

		@Override
		public void onNext(T result) throws Exception {
			child.onNext(result);
		}

		@Override
		public void onComplete() throws Exception {
			if (onCompleteCount.incrementAndGet() == 2) {
				notifyComplete();
			}
		}

		@Override
		public void onError(Throwable e) {
			if (onErrorCount.incrementAndGet() == 2) {
				notifyError(e);
			}
		}

		@Override
		public void onFinal() {
			if (onFinalCount.incrementAndGet() == 2) {
				notifyFinal();
			}
		}

		private void notifyComplete() throws Exception {
			child.onComplete();
		}

		private void notifyError(Throwable e) {
			child.onError(e);
		}

		private void notifyFinal() {
			child.onFinal();
		}
	}
}
