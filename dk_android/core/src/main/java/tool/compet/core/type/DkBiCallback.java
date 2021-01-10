/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * Callbacked with a generic type without return result.
 * If you want a callback that can return a value, use #DkFunction.
 */
public interface DkBiCallback<A, B> {
    void call(A a, B b);
}
