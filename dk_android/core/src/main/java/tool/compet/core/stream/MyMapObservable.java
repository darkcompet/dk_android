/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

class MyMapObservable<T, R> extends MyDownstreamObservable<T, R> {
    private final DkThrowableFunction<T, R> converter;

    MyMapObservable(DkObservable<T> parent, DkThrowableFunction<T, R> converter) {
        super(parent);
        this.converter = converter;
    }

    @Override
    protected void performSubscribe(DkObserver<R> observer) {
        parent.subscribe(new MapObserver<>(observer, converter));
    }

    static class MapObserver<T, R> extends MyAbsMapObserver<T, R> {
        final DkThrowableFunction<T, R> converter;

        MapObserver(DkObserver<R> child, DkThrowableFunction<T, R> converter) {
            super(child);
            this.converter = converter;
        }

        @Override
        public void onNext(T result) {
            try {
                child.onNext(converter.apply(result));
            }
            catch (Exception e) {
                onError(e);
            }
        }
    }
}
