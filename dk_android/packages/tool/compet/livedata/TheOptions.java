/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

public class TheOptions {
	public static final int THREAD_MODE_MAIN = 1;
	public static final int THREAD_MODE_POSTER = 2;

	// Indicates host need dispatch data to this observer at observing (register) time.
	// User must take care of turing off it to false, since data on observer maybe out of date,
	// so if new data was dispatched before register observer, then observer will not receive that data.
	public boolean notifyWhenObserve = true;

	// Specify which thread will be executed when observer receive data
	public int threadMode = THREAD_MODE_MAIN;

	public TheOptions() {
	}

	public TheOptions threadMode(int threadMode) {
		this.threadMode = threadMode;
		return this;
	}

	public TheOptions notifyWhenObserve(boolean notifyWhenObserve) {
		this.notifyWhenObserve = notifyWhenObserve;
		return this;
	}
}
