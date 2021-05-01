/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.Callable;

/**
 * Extends this class to switch from upper-stream to lower-stream.
 *
 * @param <T> type of upper stream
 * @param <R> type of lower stream
 */
abstract class MyMapStreamObserver<T, R> extends MyControllable implements Callable<R>, DkObserver<T> {
	protected DkObserver<R> child;

	protected MyMapStreamObserver(DkObserver<R> child) {
		this.child = child;
	}

	@Override
	public R call() throws Exception {
		throw new RuntimeException("Must implement this method");
	}

	@Override
	public void onSubscribe(DkControllable controllable) throws Exception {
		this.parent = controllable;
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
