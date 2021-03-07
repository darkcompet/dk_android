/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * It computes output without input.
 * Note: it is like with `DkCallable`, but take care when call since no exception is declared.
 */
public interface DkFunction<R> {
    /**
     * Computes result from an input without exception thrown.
     * @return Result without exception declared.
     */
    R call();
}
