/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * Observerの関数の呼び出しを自由に管理したい場合、このクラスを利用して頂ければ、
 * Observerを渡しますので、イベントの処理を完全に支配できます。
 */
class MyEmitterObservable<T> extends DkObservable<T> {
    private final DkEmitter<T> emitter;

    MyEmitterObservable(DkEmitter<T> emitter) {
        this.emitter = emitter;
    }

    @Override
    protected void performSubscribe(DkObserver<T> child) {
        emitter.call(child);
    }
}
