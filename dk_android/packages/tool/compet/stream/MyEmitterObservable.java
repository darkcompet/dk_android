/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.stream;

import tool.compet.stream4j.DkEmitter;
import tool.compet.stream4j.DkObserver;

/**
 * Observerの関数の呼び出しを自由に管理したい場合、このクラスを利用して頂ければ、
 * Observerを渡しますので、イベントの処理を完全に支配できます。
 */
class MyEmitterObservable<M> extends DkObservable<M> {
	private final DkEmitter<M> emitter;

	MyEmitterObservable(DkEmitter<M> emitter) {
		this.current = this;
		this.emitter = emitter;
	}

	@Override
	public void subscribeActual(DkObserver<M> child) throws Exception {
		emitter.call(child);
	}
}
