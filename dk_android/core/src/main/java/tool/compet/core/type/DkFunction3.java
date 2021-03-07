/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * It computes output from 3 inputs.
 * Note: it is like with `DkCallable3`, but take care when call since no exception is declared.
 */
public interface DkFunction3<A, B, C, R> {
    /**
     * Computes result from an input without exception thrown.
     * @param a Input 1.
     * @param b Input 2.
     * @param c Input 3.
     * @return Result without exception declared.
     */
    R call(A a, B b, C c);
}
