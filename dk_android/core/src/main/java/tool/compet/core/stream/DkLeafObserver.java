/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.log.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * This is lowest (leaf) observer, so events come to it will not be sent to down more.
 */
public class DkLeafObserver<T> implements DkObserver<T> {
    private int __testFinalCount;
    private long startTime;

    public DkLeafObserver() {
    }

    @Override
    public void onSubscribe(DkControllable controllable) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onNext(T item) {
    }

    @Override
    public void onError(Throwable e) {
        if (DEBUG) {
            DkLogs.error(this, e, "Stream error after %d (ms)",
                System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public void onComplete() {
        if (DEBUG) {
            DkLogs.info(this, "Stream complete after %d (ms)",
                System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public void onFinal() {
        if (DEBUG) {
            DkLogs.info(this, "Stream final after %d (ms)",
                System.currentTimeMillis() - startTime);
        }
        if (++__testFinalCount > 1) {
            DkLogs.warn(this, "Wrong implementation of #onFinal. Please review code !");
        }
    }
}
