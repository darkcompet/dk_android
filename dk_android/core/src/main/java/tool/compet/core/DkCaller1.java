/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

/**
 * It computes output from 1 input.
 * Note: it is like with `DkCallable1`, but take care when call since no exception is declared.
 */
public interface DkCaller1<A, R> {
	/**
	 * Computes result from an input without exception thrown.
	 *
	 * @param a Input.
	 * @return Result without exception declared.
	 */
	R call(A a);
}
