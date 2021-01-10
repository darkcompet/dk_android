/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.TimeUnit;

import tool.compet.core.log.DkLogs;

class MyObserveOnObservable<T> extends DkObservable<T> {
    private final DkScheduler<T> scheduler;
    private final long delay;
    private final TimeUnit timeUnit;
    private final boolean isSerial;

    MyObserveOnObservable(DkObservable<T> parent, DkScheduler<T> scheduler, long delay, TimeUnit timeUnit, boolean isSerial) {
        super(parent);
        this.scheduler = scheduler;
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.isSerial = isSerial;
    }

    @Override
    protected void performSubscribe(DkObserver<T> child) {
        parent.subscribe(new ObserveOnObserver<>(child, scheduler, delay, timeUnit, isSerial));
    }

    static class ObserveOnObserver<T> extends MyObserver<T> {
        final DkScheduler<T> scheduler;
        final long delay;
        final TimeUnit timeUnit;
        final boolean isSerial;

        ObserveOnObserver(DkObserver<T> child, DkScheduler<T> scheduler, long delay, TimeUnit timeUnit, boolean isSerial) {
            super(child);
            this.scheduler = scheduler;
            this.delay = delay;
            this.timeUnit = timeUnit;
            this.isSerial = isSerial;
        }

        @Override
        public void onSubscribe(DkControllable controllable) {
            try {
                scheduler.schedule(() -> child.onSubscribe(controllable), delay, timeUnit, isSerial);
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
        }

        @Override
        public void onNext(T result) {
            try {
                scheduler.schedule(() -> child.onNext(result), delay, timeUnit, isSerial);
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            try {
                scheduler.scheduleNow(() -> child.onError(throwable), isSerial);
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
        }

        @Override
        public void onComplete() {
            try {
                scheduler.scheduleNow(child::onComplete, isSerial);
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
        }
    }
}
