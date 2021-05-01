/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * God observable node.
 */
class MyGodControllableObservable<T> extends DkObservable<T> {
	private final DkControllable<T> controllable;

	MyGodControllableObservable(DkControllable<T> controllable) {
		this.controllable = controllable;
	}

	@Override
	protected void subscribeActual(DkObserver<T> child) {
		ControllableObserver<T> wrapper = new ControllableObserver<>(child, controllable);
		wrapper.start();
	}

	static class ControllableObserver<T> extends DkControllable<T> implements DkObserver<T> {
		private final DkControllable<T> controllable;

		ControllableObserver(DkObserver<T> child, DkControllable<T> controllable) {
			super(child);
			this.controllable = controllable;
		}

		void start() {
			try {
				onSubscribe(this);

				if (isCancel) {
					isCanceled = true;
					return;
				}

				onNext(controllable.call());
				onComplete();
			}
			catch (Exception e) {
				onError(e);
			}
			finally {
				onFinal();
			}
		}

		@Override
		public void onSubscribe(DkControllable controllable) throws Exception {
			child.onSubscribe(controllable);
		}
	}
}
