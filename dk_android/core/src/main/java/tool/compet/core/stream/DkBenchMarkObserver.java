/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.log.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

public class DkBenchMarkObserver<T> extends MyObserver<T> {
    private long startTime;

    public DkBenchMarkObserver(DkObserver<T> child) {
        super(child);
    }

    @Override
    public void onSubscribe(DkControllable controllable) {
        startTime = System.currentTimeMillis();

        super.onSubscribe(controllable);
    }

    @Override
    public void onError(Throwable e) {
        if (DEBUG) {
            DkLogs.error(this, e, "Stream error after %.3f s",
                (System.currentTimeMillis() - startTime) / 1000f);
        }
        super.onError(e);
    }

    @Override
    public void onComplete() {
        if (DEBUG) {
            DkLogs.info(this, "Stream complete after %.3f s",
                (System.currentTimeMillis() - startTime) / 1000f);
        }
        super.onComplete();
    }

    @Override
    public void onFinal() {
        if (DEBUG) {
            DkLogs.info(this, "Stream final after %.3f s",
                (System.currentTimeMillis() - startTime) / 1000f);
        }
        super.onFinal();
    }
}
