/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * Like Java BiFunction, It accepts 2 params, returns output to caller.
 */
public interface DkBiFunction<A, B, R> {
    R apply(A a, B b);
}
