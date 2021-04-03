/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * Imagine that some where in stream has occured exception at some node,
 * but we wanna ignore that error and continue to process it,
 * <p>
 * To resolve this, we introduce this node to switch to `onNext()` at `onError()` event.
 * So child node
 */
class MyIgnoreErrorObservable<T> extends DkObservable<T> {
	MyIgnoreErrorObservable(DkObservable<T> parent) {
		super(parent);
	}

	@Override
	protected void subscribeActual(DkObserver<T> observer) {
		parent.subscribe(new IgnoreErrorObserver<>(observer));
	}

	static class IgnoreErrorObserver<T> extends MyObserver<T> {
		IgnoreErrorObserver(DkObserver<T> child) {
			super(child);
		}

		@Override
		public void onError(Throwable e) {
			try {
				// We don't know what result was passed from us parent node
				// -> Ignore error and try to pass with null-result to child node
				child.onNext(null);
			}
			catch (Exception exception) {
				// Humh, still error occured in child node...
				// -> We have no idea to resolve it
				// -> Just pass to child node exception which was caused from it
				child.onError(exception);
			}
		}
	}
}
