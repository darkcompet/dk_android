/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkRunnable1;

/**
 * This node just execute `Runnable` from caller and pass the result from above-node to under-node.
 */
class MyOnNextObservable<T> extends DkObservable<T> {
	private final DkObservable<T> parent;
	private final DkRunnable1<T> action;

	MyOnNextObservable(DkObservable<T> parent, DkRunnable1<T> action) {
		this.parent = parent;
		this.action = action;
	}

	@Override
	protected void subscribeActual(DkObserver<T> observer) {
		// Just send to parent own wrapped observer
		parent.subscribe(new OnNextObserver<>(observer, action));
	}

	// Own wrapped observer which holds child observer
	static class OnNextObserver<T> extends MyObserver<T> {
		final DkRunnable1<T> action;

		OnNextObserver(DkObserver<T> child, DkRunnable1<T> action) {
			super(child);
			this.action = action;
		}

		@Override
		public void onNext(T result) throws Exception {
			// Execute action and Pass result to child node
			// Note that, God node will handle error if raised
			action.run(result);
			child.onNext(result);
		}
	}
}
