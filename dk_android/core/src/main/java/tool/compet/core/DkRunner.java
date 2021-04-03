/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

/**
 * This is like with `DkRunnable`, but take care when run since no exception is declared.
 */
public interface DkRunner {
	/**
	 * No param is passed to caller.
	 */
	void run();
}
