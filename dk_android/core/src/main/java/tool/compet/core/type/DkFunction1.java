/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * It computes output from 1 input.
 * Note: it is like with `DkCallable1`, but take care when call since no exception is declared.
 */
public interface DkFunction1<A, R> {
    /**
     * Computes result from an input without exception thrown.
     * @param a Input.
     * @return Result without exception declared.
     */
    R call(A a);
}
