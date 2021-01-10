/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.log.DkLogs;

class MyOnNextObservable<T> extends DkObservable<T> {
    private final DkObservable<T> parent;
    private final DkThrowableCallback<T> action;

    MyOnNextObservable(DkObservable<T> parent, DkThrowableCallback<T> action) {
        this.parent = parent;
        this.action = action;
    }

    @Override
    protected void performSubscribe(DkObserver<T> observer) {
        parent.subscribe(new OnNextObserver<>(observer, action));
    }

    static class OnNextObserver<T> extends MyObserver<T> {
        final DkThrowableCallback<T> action;

        OnNextObserver(DkObserver<T> child, DkThrowableCallback<T> action) {
            super(child);
            this.action = action;
        }

        @Override
        public void onNext(T result) {
            try {
                action.call(result);
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
            finally {
                child.onNext(result);
            }
        }
    }
}
