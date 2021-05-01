/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

class MyDelayObservable<T> extends DkObservable<T> {
	private final long delayMillis;

	MyDelayObservable(DkObservable<T> parent, long delayMillis) {
		super(parent);
		this.delayMillis = delayMillis;
	}

	@Override
	protected void subscribeActual(DkObserver<T> observer) {
		parent.subscribe(new DelayObserver<>(observer, delayMillis));
	}

	static class DelayObserver<T> extends MyObserver<T> {
		final long delayMillis;

		DelayObserver(DkObserver<T> child, long delayMillis) {
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
}
