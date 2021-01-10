/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.log.DkLogs;

class MyOnFinalObservable<T> extends DkObservable<T> {
    private final Runnable action;

    MyOnFinalObservable(DkObservable<T> parent, Runnable action) {
        super(parent);
        this.action = action;
    }

    @Override
    protected void performSubscribe(DkObserver<T> child) {
        parent.subscribe(new OnFinalObserver<>(child, action));
    }

    static class OnFinalObserver<R> extends MyObserver<R> {
        final Runnable action;

        OnFinalObserver(DkObserver<R> child, Runnable action) {
            super(child);
            this.action = action;
        }

        @Override
        public void onFinal() {
            try {
                action.run();
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
            finally {
                child.onFinal();
            }
        }
    }
}
