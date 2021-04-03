/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkRunnable;

class MyOnCompleteObservable<T> extends DkObservable<T> {
	private final DkRunnable action;

	MyOnCompleteObservable(DkObservable<T> parent, DkRunnable action) {
		super(parent);
		this.action = action;
	}

	@Override
	protected void subscribeActual(DkObserver<T> child) {
		parent.subscribe(new OnCompleteObserver<>(child, action));
	}

	static class OnCompleteObserver<R> extends MyObserver<R> {
		final DkRunnable action;

		OnCompleteObserver(DkObserver<R> child, DkRunnable action) {
			super(child);
			this.action = action;
		}

		@Override
		public void onComplete() throws Exception {
			// Run action and pass complete-event to child node
			action.run();
			child.onComplete();
		}
	}
}
