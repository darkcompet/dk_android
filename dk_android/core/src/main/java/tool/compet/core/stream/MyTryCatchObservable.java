/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * Switch to #onNext() if exception occured in upper node.
 */
class MyTryCatchObservable<T> extends DkObservable<T> {
    MyTryCatchObservable(DkObservable<T> parent) {
        super(parent);
    }

    @Override
    protected void performSubscribe(DkObserver<T> observer) {
        parent.subscribe(new TryCatchObserver<>(observer));
    }

    static class TryCatchObserver<T> extends MyObserver<T> {
        TryCatchObserver(DkObserver<T> child) {
            super(child);
        }

        @Override
        public void onError(Throwable e) {
            child.onNext(null);
        }
    }
}
