/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkExecutorService;

@SuppressWarnings("unchecked")
public class DkSchedulers {
	private static DkScheduler IO_SCHEDULER;
	private static DkScheduler MAIN_SCHEDULER;

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
	public static <T> DkScheduler<T> main() {
		if (MAIN_SCHEDULER == null) {
			synchronized (DkSchedulers.class) {
				if (MAIN_SCHEDULER == null) {
					MAIN_SCHEDULER = new MyMainScheduler<>();
				}
			}
		}
		return (DkScheduler<T>) MAIN_SCHEDULER;
	}
}
