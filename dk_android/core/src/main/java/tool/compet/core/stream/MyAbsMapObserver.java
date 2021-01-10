/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

abstract class MyAbsMapObserver<T, R> implements DkObserver<T> {
    protected final DkObserver<R> child;

    public MyAbsMapObserver(DkObserver<R> child) {
        this.child = child;
    }

    @Override
    public void onSubscribe(DkControllable controllable) {
        child.onSubscribe(controllable);
    }

    @Override
    public void onError(Throwable e) {
        child.onError(e);
    }

    @Override
    public void onComplete() {
        child.onComplete();
    }

    @Override
    public void onFinal() {
        child.onFinal();
    }
}
