/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

class MyFlatMapObservable<T, R> extends MyDownstreamObservable<T, R> {
    private final DkThrowableFunction<T, DkObservable<R>> converter;

    MyFlatMapObservable(DkObservable<T> parent, DkThrowableFunction<T, DkObservable<R>> converter) {
        super(parent);
        this.converter = converter;
    }

    @Override
    protected void performSubscribe(DkObserver<R> child) {
        parent.subscribe(new FlatMapObserver<>(child, converter));
    }

    static class FlatMapObserver<T, R> extends MyAbsFlatMapObserver<T, R> {
        final DkThrowableFunction<T, DkObservable<R>> converter;

        FlatMapObserver(DkObserver<R> child, DkThrowableFunction<T, DkObservable<R>> converter) {
            super(child);
            this.converter = converter;
        }

        @Override
        public void onNext(T result) {
            try {
                DkObservable<R> nextObservable = converter.apply(result);

                // If converter null, we can considere this flatMap is normal map
                if (nextObservable == null) {
                    child.onNext(null);
                    return;
                }

                // Run on same thread with upper node
                nextObservable.subscribe(new MyBenchMarkObserver<>(child));
            }
            catch (Exception e) {
                child.onError(e);
            }
        }
    }
}
