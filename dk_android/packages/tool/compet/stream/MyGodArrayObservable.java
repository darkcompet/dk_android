/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.stream;

import java.util.Arrays;
import java.util.Collections;

import tool.compet.stream4j.DkObserver;
import tool.compet.stream4j.OwnGodArrayObserver;

/**
 * God observable node.
 */
class MyGodArrayObservable<M> extends DkObservable<M> {
	private final Iterable<M> items;

	MyGodArrayObservable(M item) {
		this.current = this;
		this.items = Collections.singletonList(item);
	}

	MyGodArrayObservable(M[] items) {
		this.items = Arrays.asList(items);
	}

	@Override
	public void subscribeActual(DkObserver<M> child) throws Exception {
		OwnGodArrayObserver<M> wrapper = new OwnGodArrayObserver<>(child);
		wrapper.start(items);
	}
}
