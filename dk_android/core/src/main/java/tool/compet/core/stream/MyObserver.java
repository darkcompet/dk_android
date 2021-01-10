/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.log.DkLogs;

class MyObserver<T> implements DkObserver<T> {
    private int __testFinalCount;
    protected final DkObserver<T> child;

    public MyObserver(DkObserver<T> child) {
        this.child = child;
    }

    @Override
    public void onSubscribe(DkControllable controllable) {
        child.onSubscribe(controllable);
    }

    @Override
    public void onNext(T result) {
        child.onNext(result);
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

        if (++__testFinalCount > 1) {
            DkLogs.warn(this, "Wrong implementation of #onFinal. Please review code !");
        }
    }
}
