/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * Like Java Function, It accepts 1 param, returns output to caller.
 */
public interface DkThrowableFunction<I, O> {
    O apply(I input) throws Exception;
}
