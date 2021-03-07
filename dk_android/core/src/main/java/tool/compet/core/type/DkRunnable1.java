/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * Extra version of `Runnable` with an input.
 */
public interface DkRunnable1<T> {
    /**
     * Run (execute) instructions, throws exception when unable to run.
     * @throws Exception Unable to run.
     */
    void run(T input) throws Exception;
}
