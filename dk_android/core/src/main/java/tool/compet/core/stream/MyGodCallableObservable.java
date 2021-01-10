/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.Callable;

/**
 * God observable node.
 */
class MyGodCallableObservable<T> extends DkObservable<T> {
    private final Callable<T> execution;

    MyGodCallableObservable(Callable<T> execution) {
        this.execution = execution;
    }

    @Override
    protected void performSubscribe(DkObserver<T> child) {
        CallableObserver<T> wrapper = new CallableObserver<>(child, execution);
        wrapper.start();
    }

    static class CallableObserver<T> extends DkControllable<T> {
        final Callable<T> execution;

        CallableObserver(DkObserver<T> child, Callable<T> execution) {
            super(child);
            this.execution = execution;
        }

        void start() {
            try {
                onSubscribe(this);

                if (isCancel) {
                    isCanceled = true;
                    return;
                }

                onNext(execution.call());
                onComplete();
            }
            catch (Exception e) {
                onError(e);
            }
            finally {
                onFinal();
            }
        }

        @Override
        public void onSubscribe(DkControllable controllable) {
            parent = null;
            child.onSubscribe(controllable);
        }
    }
}
