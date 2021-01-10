/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.log.DkLogs;

class MyOnCompleteObservable<T> extends DkObservable<T> {
    private final Runnable action;

    MyOnCompleteObservable(DkObservable<T> parent, Runnable action) {
        super(parent);
        this.action = action;
    }

    @Override
    protected void performSubscribe(DkObserver<T> child) {
        parent.subscribe(new OnCompleteObserver<>(child, action));
    }

    static class OnCompleteObserver<R> extends MyObserver<R> {
        final Runnable action;

        OnCompleteObserver(DkObserver<R> child, Runnable action) {
            super(child);
            this.action = action;
        }

        @Override
        public void onComplete() {
            try {
                action.run();
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
            finally {
                child.onComplete();
            }
        }
    }
}
