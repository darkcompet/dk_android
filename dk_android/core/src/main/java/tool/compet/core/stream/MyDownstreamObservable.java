/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * Extends this class to switch from upper-stream to lower-stream.
 *
 * @param <T> type of upper stream
 * @param <R> type of lower stream
 */
abstract class MyDownstreamObservable<T, R> extends DkObservable<R> {
    protected DkObservable<T> parent;

    MyDownstreamObservable(DkObservable<T> parent) {
        this.parent = parent;
    }
}
