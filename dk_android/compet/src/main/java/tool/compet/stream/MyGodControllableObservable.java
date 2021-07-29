/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.stream;

import tool.compet.stream4j.DkControllable;
import tool.compet.stream4j.DkObserver;
import tool.compet.stream4j.OwnGodControllableObserver;

/**
 * God observable node.
 */
class MyGodControllableObservable<M> extends DkObservable<M> {
	private final DkControllable<M> controllable;

	MyGodControllableObservable(DkControllable<M> controllable) {
		this.tail = this;
		this.controllable = controllable;
	}

	@Override
	public void subscribeActual(DkObserver<M> child) throws Exception {
		OwnGodControllableObserver<M> wrapper = new OwnGodControllableObserver<>(child, controllable);
		wrapper.start();
	}
}
