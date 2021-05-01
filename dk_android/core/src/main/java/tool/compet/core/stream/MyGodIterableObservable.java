/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * God observable node.
 */
class MyGodIterableObservable<T> extends DkObservable<T> {
	private final Iterable<T> items;

	MyGodIterableObservable(Iterable<T> items) {
		this.items = items;
	}

	@Override
	protected void subscribeActual(DkObserver<T> child) {
		DkIterableObserver<T> wrapper = new DkIterableObserver<>(child);
		wrapper.start(items);
	}

	static class DkIterableObserver<T> extends DkControllable<T> implements DkObserver<T> {
		DkIterableObserver(DkObserver<T> child) {
			super(child);
		}

		void start(Iterable<T> items) {
			try {
				onSubscribe(this);

				if (isCancel) {
					isCanceled = true;
					return;
				}

				for (T item : items) {
					if (isCancel) {
						isCanceled = true;
						break;
					}
					onNext(item);
				}

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
