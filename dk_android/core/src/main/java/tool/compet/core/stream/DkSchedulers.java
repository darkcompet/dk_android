/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.DkExecutorService;

@SuppressWarnings("unchecked")
public class DkSchedulers {
	private static DkScheduler ioScheduler;
	private static DkScheduler mainScheduler;

	// Background thread scheduler
	public static <T> DkScheduler<T> io() {
		if (ioScheduler == null) {
			synchronized (DkSchedulers.class) {
				if (ioScheduler == null) {
					ioScheduler = new MyIoScheduler<>(DkExecutorService.getIns());
				}
			}
		}
		return (DkScheduler<T>) ioScheduler;
	}

	// Android main thread scheduler
	public static <T> DkScheduler<T> main() {
		if (mainScheduler == null) {
			synchronized (DkSchedulers.class) {
				if (mainScheduler == null) {
					mainScheduler = new MyMainScheduler<>();
				}
			}
		}
		return (DkScheduler<T>) mainScheduler;
	}
}
