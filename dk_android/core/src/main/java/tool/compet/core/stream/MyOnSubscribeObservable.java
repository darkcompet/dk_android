/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.log.DkLogs;

class MyOnSubscribeObservable<T> extends DkObservable<T> {
    private final DkThrowableCallback<DkControllable> action;

    MyOnSubscribeObservable(DkObservable<T> parent, DkThrowableCallback<DkControllable> action) {
        super(parent);
        this.action = action;
    }

    @Override
    protected void performSubscribe(DkObserver<T> observer) {
        parent.subscribe(new OnSubscribeObserver<>(observer, action));
    }

    static class OnSubscribeObserver<T> extends MyObserver<T> {
        final DkThrowableCallback<DkControllable> action;

        OnSubscribeObserver(DkObserver<T> child, DkThrowableCallback<DkControllable> action) {
            super(child);
            this.action = action;
        }

        @Override
        public void onSubscribe(DkControllable controllable) {
            try {
                action.call(controllable);
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
            finally {
                child.onSubscribe(controllable);
            }
        }
    }
}
