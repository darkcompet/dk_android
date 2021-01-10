/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.Callable;

import tool.compet.core.log.DkLogs;

/**
 * タスクをキャンセル・一時停止・再生できるものです。
 * 親Controllableが設定されれば連続リストとしてシステムを支配できような仕組みとなっています。
 */
public class DkControllable<T> extends MyAbsControllable implements Callable<T>, DkObserver<T> {
    protected final DkObserver<T> child;

    public DkControllable(DkObserver<T> child) {
        this.child = child;
    }

    @Override
    public T call() {
        throw new RuntimeException("Must implement this method");
    }

    @Override
    public void onSubscribe(DkControllable controllable) {
        if (controllable == this) {
            DkLogs.complain(this, "Wrong implementation ! God observer must be parentless");
        }
        this.parent = controllable;
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
    }
}
