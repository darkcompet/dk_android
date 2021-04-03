/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkRunnable1;

class MyOnErrorObservable<T> extends DkObservable<T> {
	private final DkRunnable1<Throwable> action;

	MyOnErrorObservable(DkObservable<T> parent, DkRunnable1<Throwable> action) {
		super(parent);
		this.action = action;
	}

	@Override
	protected void subscribeActual(DkObserver<T> observer) {
		parent.subscribe(new OnErrorObserver<>(observer, action));
	}

	static class OnErrorObserver<T> extends MyObserver<T> {
		final DkRunnable1<Throwable> action;

		OnErrorObserver(DkObserver<T> child, DkRunnable1<Throwable> action) {
			super(child);
			this.action = action;
		}

		@Override
		public void onError(Throwable throwable) {
			try {
				action.run(throwable);
				child.onError(throwable);
			}
			catch (Exception e) {
				child.onError(e);
			}
		}
	}
}
