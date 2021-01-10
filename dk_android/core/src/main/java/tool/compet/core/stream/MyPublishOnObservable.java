/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.TimeUnit;

import tool.compet.core.log.DkLogs;

class MyPublishOnObservable<T> extends DkObservable<T> {
    private final DkScheduler<T> scheduler;
    private final DkThrowableCallback<T> action;
    private final long delay;
    private final TimeUnit unit;
    private final boolean isSerial;

    MyPublishOnObservable(DkObservable<T> parent, DkScheduler<T> scheduler, DkThrowableCallback<T> action,
                        long delay, TimeUnit unit, boolean isSerial) {

        super(parent);
        this.scheduler = scheduler;
        this.action = action;
        this.delay = delay;
        this.unit = unit;
        this.isSerial = isSerial;
    }

    @Override
    protected void performSubscribe(DkObserver<T> child) {
        parent.subscribe(new PublishOnObserver<>(child, scheduler, action, delay, unit, isSerial));
    }

    static class PublishOnObserver<T> extends MyObserver<T> {
        final DkScheduler<T> scheduler;
        final DkThrowableCallback<T> action;
        final long delay;
        final TimeUnit unit;
        final boolean isSerial;

        PublishOnObserver(DkObserver<T> child, DkScheduler<T> scheduler, DkThrowableCallback<T> action,
                          long delay, TimeUnit unit, boolean isSerial) {

            super(child);
            this.scheduler = scheduler;
            this.action = action;
            this.delay = delay;
            this.unit = unit;
            this.isSerial = isSerial;
        }

        @Override
        public void onNext(T item) {
            try {
                scheduler.schedule(() -> {
                    try {
                        action.call(item);
                        child.onNext(item);
                    }
                    catch (Exception e) {
                        child.onError(e);
                        DkLogs.error(this, e);
                    }
                }, delay, unit, isSerial);
            }
            catch (Exception e) {
                DkLogs.error(this, e);
                child.onError(e);
            }
        }
    }
}
