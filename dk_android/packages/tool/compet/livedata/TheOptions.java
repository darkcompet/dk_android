/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

public class TheOptions {
	public static final int THREAD_MODE_MAIN = 1;
	public static final int THREAD_MODE_POSTER = 2;

	// True value indicates the host need dispatch data to this observer
	// when state of this observer changed from inactive to active,
	public boolean receiveDataWhenActive;

	public int threadMode = THREAD_MODE_MAIN;
}
