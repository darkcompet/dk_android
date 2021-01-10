/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.log.DkLogs;

class MyOnErrorObservable<T> extends DkObservable<T> {
    private final DkThrowableCallback<Throwable> action;

    MyOnErrorObservable(DkObservable<T> parent, DkThrowableCallback<Throwable> action) {
        super(parent);
        this.action = action;
    }

    @Override
    protected void performSubscribe(DkObserver<T> observer) {
        parent.subscribe(new OnErrorObserver<>(observer, action));
    }

    static class OnErrorObserver<T> extends MyObserver<T> {
        final DkThrowableCallback<Throwable> action;

        OnErrorObserver(DkObserver<T> child, DkThrowableCallback<Throwable> action) {
            super(child);
            this.action = action;
        }

        @Override
        public void onError(Throwable throwable) {
            try {
                action.call(throwable);
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
            finally {
                child.onError(throwable);
            }
        }
    }
}
