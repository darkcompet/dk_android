/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import tool.compet.core.log.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

class MyScheduleOnObservable<T> extends DkObservable<T> {
    private final DkScheduler<T> scheduler;
    private final long delay;
    private final TimeUnit timeUnit;
    private final boolean isSerial;

    MyScheduleOnObservable(DkObservable<T> parent, DkScheduler<T> scheduler, long delay, TimeUnit unit, boolean isSerial) {
        super(parent);
        this.scheduler = scheduler;
        this.delay = delay;
        this.timeUnit = unit;
        this.isSerial = isSerial;
    }

    @Override
    protected void performSubscribe(DkObserver<T> child) {
        SchedulerOnObserver<T> wrapper = new SchedulerOnObserver<>(child, this.scheduler);
        wrapper.start(parent, delay, timeUnit, isSerial);
    }

    static class SchedulerOnObserver<T> extends DkControllable<T> {
        final DkScheduler<T> service;
        Callable<T> task;

        SchedulerOnObserver(DkObserver<T> child, DkScheduler<T> service) {
            super(child);
            this.service = service;
        }

        void start(DkObservable<T> parent, long delay, TimeUnit timeUnit, boolean isSerial) {
            // Give to children a chance to cancel scheduling
            child.onSubscribe(this);

            if (isCancel) {
                isCanceled = true;
                onFinal();
                return;
            }

            // Schedule task in the service
            task = () -> {
                // maybe it takes long time to schedule, so check again cancel event from user
                if (isCancel) {
                    isCanceled = true;
                    onFinal();
                }
                else {
                    parent.subscribe(SchedulerOnObserver.this);
                }
                return null;
            };

            try {
                // if schedule to send subscribe event to parent fail, we will consider
                // this node is like God node, and call #onFinal if exception occur.
                service.schedule(task, delay, timeUnit, isSerial);
            }
            catch (Exception e) {
                onError(e);
                onFinal();
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptThread) {
            boolean ok = super.cancel(mayInterruptThread);

            if (task != null) {
                ok |= service.cancel(task, mayInterruptThread);
            }

            isCanceled = ok;

            if (DEBUG) {
                DkLogs.info(this, "Cancel task: " + task + ", ok: " + ok);
            }

            return ok;
        }
    }
}
