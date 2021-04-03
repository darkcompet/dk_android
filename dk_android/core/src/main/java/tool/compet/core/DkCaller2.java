/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

/**
 * It computes output from 2 inputs.
 * Note: it is like with `DkCallable2`, but take care when call since no exception is declared.
 */
public interface DkCaller2<A, B, R> {
	/**
	 * Computes result from an input without exception thrown.
	 *
	 * @param a Input 1.
	 * @param b Input 2.
	 * @return Result without exception declared.
	 */
	R call(A a, B b);
}
