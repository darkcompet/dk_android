/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.type;

/**
 * Don't pass any param to caller without care of exception.
 * Note: this is like with `DkRunnable`, but take care when run since no exception is declared.
 */
public interface DkCallback {
    /**
     * Don't pass (callback) any param to caller.
     */
    void run();
}
