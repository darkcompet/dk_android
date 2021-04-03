/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

/**
 * Extra version of `Callable` with 3 inputs.
 */
public interface DkCallable3<A, B, C, R> {
	/**
	 * Computes a result, throws an exception if unable to do so.
	 *
	 * @return Result.
	 * @throws Exception When unable to compute result.
	 */
	R call(A a, B b, C c) throws Exception;
}
