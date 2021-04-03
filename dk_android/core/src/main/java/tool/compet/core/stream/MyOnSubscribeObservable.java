/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkRunnable1;

class MyOnSubscribeObservable<T> extends DkObservable<T> {
	private final DkRunnable1<DkControllable> action;

	MyOnSubscribeObservable(DkObservable<T> parent, DkRunnable1<DkControllable> action) {
		super(parent);
		this.action = action;
	}

	@Override
	protected void subscribeActual(DkObserver<T> observer) {
		parent.subscribe(new OnSubscribeObserver<>(observer, action));
	}

	static class OnSubscribeObserver<T> extends MyObserver<T> {
		final DkRunnable1<DkControllable> action;

		OnSubscribeObserver(DkObserver<T> child, DkRunnable1<DkControllable> action) {
			super(child);
			this.action = action;
		}

		@Override
		public void onSubscribe(DkControllable controllable) throws Exception {
			// Run incoming task and pass controllable to child node
			action.run(controllable);
			child.onSubscribe(controllable);
		}
	}
}
