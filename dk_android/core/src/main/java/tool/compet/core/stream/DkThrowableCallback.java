/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * Callbacked with a generic type. It is an support version along with Runnable or Callable.
 * If you want a callback that can return a value, use #DtFunction.
 */
public interface DkThrowableCallback<T> {
    void call(T input) throws Exception;
}
