/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * This is lowest (leaf) observer at first subscribe, so all events come to it will not be sent to down more.
 * From that, we can write log to observe events here.
 */
class MyLeafObserver<T> implements DkObserver<T> {
	MyLeafObserver() {
	}

	@Override
	public void onSubscribe(DkControllable controllable) {
		if (DEBUG) {
			__startTime = System.currentTimeMillis();
		}
	}

	@Override
	public void onNext(T item) {
	}

	@Override
	public void onError(Throwable e) {
		if (DEBUG) {
			DkLogs.error(this, e, "Stream error after %d (ms)", System.currentTimeMillis() - __startTime);
		}
	}

	@Override
	public void onComplete() {
		if (DEBUG) {
			DkLogs.info(this, "Stream complete after %d (ms)", System.currentTimeMillis() - __startTime);
		}
	}

	@Override
	public void onFinal() {
		if (DEBUG) {
			DkLogs.info(this, "Stream final after %d (ms)", System.currentTimeMillis() - __startTime);

			if (++__testFinalCount > 1) {
				DkLogs.warning(this, "Wrong implementation of #onFinal. Please review code !");
			}
		}
	}

	private int __testFinalCount;
	private long __startTime;
}
