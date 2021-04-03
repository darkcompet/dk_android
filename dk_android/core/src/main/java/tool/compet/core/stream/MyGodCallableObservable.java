/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkLogs;
import tool.compet.core.DkCallable;

/**
 * God observable node.
 * When start, this run callable and then handle next events.
 */
class MyGodCallableObservable<T> extends DkObservable<T> {
	private final DkCallable<T> execution;

	MyGodCallableObservable(DkCallable<T> execution) {
		this.execution = execution;
	}

	@Override
	protected void subscribeActual(DkObserver<T> child) throws Exception {
		CallableObserver<T> wrapper = new CallableObserver<>(child, execution);
		wrapper.start();
	}

	static class CallableObserver<T> extends DkControllable<T> {
		final DkCallable<T> callable;

		CallableObserver(DkObserver<T> child, DkCallable<T> callable) {
			super(child);
			this.callable = callable;
		}

		void start() {
			try {
				onSubscribe(this);

				if (isCancel) {
					isCanceled = true;
					return;
				}

				onNext(callable.call());
				onComplete();
			}
			catch (Exception e) {
				onError(e);
			}
			finally {
				DkLogs.debug(this, "call final at God node");
				onFinal();
			}
		}

		@Override
		public void onSubscribe(DkControllable controllable) throws Exception {
			child.onSubscribe(controllable);
		}
	}
}
