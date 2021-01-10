/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * Like Java Function, It accepts 1 param, returns output to caller.
 */
public interface DkFunction<I, O> {
    O apply(I input);
}
