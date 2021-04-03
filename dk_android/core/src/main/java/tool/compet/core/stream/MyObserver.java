/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.BuildConfig;
import tool.compet.core.DkLogs;

class MyObserver<T> implements DkObserver<T> {
	protected final DkObserver<T> child;

	public MyObserver(DkObserver<T> child) {
		this.child = child;
	}

	@Override
	public void onSubscribe(DkControllable controllable) throws Exception {
		child.onSubscribe(controllable);
	}

	@Override
	public void onNext(T result) throws Exception {
		child.onNext(result);
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

		if (BuildConfig.DEBUG) {
			DkLogs.warning(this, "Call onFinal()");

			if (++__testFinalCount > 1) {
				DkLogs.warning(this, "Wrong implementation of #onFinal. Please review code !");
			}
		}
	}

	private int __testFinalCount;
	private long __startTime;
}
