/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * Callbacked with a generic type. It is an support version along with Runnable or Callable.
 * If you want a callback that can return a value, use #DkFunction.
 */
public interface DkCallback<T> {
    void call(T input);
}
