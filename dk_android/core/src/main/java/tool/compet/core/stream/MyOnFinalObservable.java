/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkLogs;
import tool.compet.core.DkRunnable;

class MyOnFinalObservable<T> extends DkObservable<T> {
	private final DkRunnable action;

	MyOnFinalObservable(DkObservable<T> parent, DkRunnable action) {
		super(parent);
		this.action = action;
	}

	@Override
	protected void subscribeActual(DkObserver<T> child) {
		parent.subscribe(new OnFinalObserver<>(child, action));
	}

	static class OnFinalObserver<R> extends MyObserver<R> {
		final DkRunnable action;

		OnFinalObserver(DkObserver<R> child, DkRunnable action) {
			super(child);
			this.action = action;
		}

		@Override
		public void onFinal() {
			// Run action and pass final-event to child node
			// We will ignore any error since this is last event
			try {
				action.run();
			}
			catch (Exception e) {
				DkLogs.error(this, e);
			}
			finally {
				child.onFinal();
			}
		}
	}
}
