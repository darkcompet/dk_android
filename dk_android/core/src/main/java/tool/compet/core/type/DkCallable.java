/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * Same version of `Callable` without input.
 */
public interface DkCallable<R> {
    /**
     * Computes a result, throws an exception if unable to do so.
     * @return Result.
     * @throws Exception When unable to compute result.
     */
    R call() throws Exception;
}
