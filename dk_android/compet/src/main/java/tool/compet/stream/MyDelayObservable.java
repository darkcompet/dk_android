/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.stream;

import tool.compet.stream4j.DkObservableSource;
import tool.compet.stream4j.DkObserver;

class MyDelayObservable<M> extends DkObservable<M> {
	private final long delayMillis;

	MyDelayObservable(DkObservableSource<M> parent, long delayMillis) {
		super(parent);
		this.delayMillis = delayMillis;
	}

	@Override
	public void subscribeActual(DkObserver<M> observer) throws Exception {
		parent.subscribe(new MyDelayObserver<>(observer, delayMillis));
	}
}
