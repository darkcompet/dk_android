/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * This will switch input T and output R.
 */
abstract class MyMapObserver<T, R> implements DkObserver<T> {
	protected final DkObserver<R> child;

	public MyMapObserver(DkObserver<R> child) {
		this.child = child;
	}

	@Override
	public void onSubscribe(DkControllable controllable) throws Exception {
		child.onSubscribe(controllable);
	}

	@Override
	public void onError(Throwable e) {
		child.onError(e);
	}

	@Override
	public void onComplete() throws Exception {
		child.onComplete();
	}

	@Override
	public void onFinal() {
		child.onFinal();
	}
}
