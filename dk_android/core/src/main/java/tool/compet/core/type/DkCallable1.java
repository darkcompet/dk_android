/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * Extra version of `Callable` with an input.
 */
public interface DkCallable1<A, R> {
    /**
     * Computes a result, throws an exception if unable to do so.
     * @return Result.
     * @throws Exception When unable to compute result.
     */
    R call(A a) throws Exception;
}
