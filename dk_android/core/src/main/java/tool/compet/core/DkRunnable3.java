/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

/**
 * Extra version of `Runnable` with 3 inputs.
 */
public interface DkRunnable3<A, B, C> {
	/**
	 * Run (execute) instructions, throws exception when unable to run.
	 *
	 * @throws Exception Unable to run.
	 */
	void run(A a, B b, C c) throws Exception;
}
