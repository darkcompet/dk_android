/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.stream;

import tool.compet.core4j.DkCallable;
import tool.compet.stream4j.DkObserver;
import tool.compet.stream4j.OwnGodCallableObserver;

/**
 * God observable node.
 * When start, this run callable and then handle next events.
 */
class MyGodCallableObservable<M> extends DkObservable<M> {
	private final DkCallable<M> execution;

	MyGodCallableObservable(DkCallable<M> execution) {
		this.tail = this;
		this.execution = execution;
	}

	@Override
	public void subscribeActual(DkObserver<M> child) throws Exception {
		OwnGodCallableObserver<M> wrapper = new OwnGodCallableObserver<>(child, execution);
		wrapper.start();
	}
}
