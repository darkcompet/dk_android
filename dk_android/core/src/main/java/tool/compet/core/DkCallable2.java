/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

/**
 * Extra version of `Callable` with 2 inputs.
 */
public interface DkCallable2<A, B, R> {
	/**
	 * Computes a result, throws an exception if unable to do so.
	 *
	 * @return Result.
	 * @throws Exception When unable to compute result.
	 */
	R call(A a, B b) throws Exception;
}
