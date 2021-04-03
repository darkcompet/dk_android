/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkExecutorService;

@SuppressWarnings("unchecked")
public class DkSchedulers {
	private static DkScheduler IO_SCHEDULER;
	private static DkScheduler ANDROID_MAIN_SCHEDULER;

	// Background thread scheduler
	public static <T> DkScheduler<T> io() {
		if (IO_SCHEDULER == null) {
			synchronized (DkSchedulers.class) {
				if (IO_SCHEDULER == null) {
					IO_SCHEDULER = new MyIoScheduler<>(DkExecutorService.getIns());
				}
			}
		}
		return (DkScheduler<T>) IO_SCHEDULER;
	}

	// Android main thread scheduler
	public static <T> DkScheduler<T> androidMain() {
		if (ANDROID_MAIN_SCHEDULER == null) {
			synchronized (DkSchedulers.class) {
				if (ANDROID_MAIN_SCHEDULER == null) {
					ANDROID_MAIN_SCHEDULER = new MyAndroidMainScheduler<>();
				}
			}
		}
		return (DkScheduler<T>) ANDROID_MAIN_SCHEDULER;
	}
}
