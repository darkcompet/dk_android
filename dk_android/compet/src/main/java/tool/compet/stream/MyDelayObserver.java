/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.stream;

import android.os.Handler;
import android.os.HandlerThread;

import tool.compet.stream4j.DkObserver;
import tool.compet.stream4j.OwnObserver;

class MyDelayObserver<T> extends OwnObserver<T> {
	final long delayMillis;

	MyDelayObserver(DkObserver<T> child, long delayMillis) {
		super(child);
		this.delayMillis = delayMillis;
	}

	@Override
	public void onNext(T result) {
		HandlerThread handlerThread = new HandlerThread(MyDelayObservable.class.getName());
		handlerThread.start();

		Handler handler = new Handler(handlerThread.getLooper());
		handler.postDelayed(() -> {
			try {
				child.onNext(result);
			}
			catch (Exception e) {
				child.onError(e);
			}
			finally {
				handlerThread.quit();
			}
		}, delayMillis);
	}
}