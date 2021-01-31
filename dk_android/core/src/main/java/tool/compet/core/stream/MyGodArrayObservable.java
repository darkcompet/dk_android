/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.Arrays;
import java.util.Collections;

/**
 * God observable node.
 */
class MyGodArrayObservable<T> extends DkObservable<T> {
    private final Iterable<T> items;

    MyGodArrayObservable(T single) {
        this.items = Collections.singletonList(single);
    }

    MyGodArrayObservable(T[] items) {
        this.items = Arrays.asList(items);
    }

    @Override
    protected void performSubscribe(DkObserver<T> child) {
        ArrayObserver<T> wrapper = new ArrayObserver<>(child);
        wrapper.start(items);
    }

    static class ArrayObserver<T> extends DkControllable<T> {
        ArrayObserver(DkObserver<T> child) {
            super(child);
        }

        void start(Iterable<T> items) {
            try {
                onSubscribe(this);

                if (isCancel) {
                    isCanceled = true;
                    return;
                }

                for (T item : items) {
                    if (isCancel) {
                        isCanceled = true;
                        return;
                    }
                    onNext(item);
                }

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