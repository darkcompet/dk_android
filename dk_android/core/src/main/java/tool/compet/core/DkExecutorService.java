/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This provides task-schduling on overall app by handle `ScheduledThreadPoolExecutor` class.
 * This uses 2~4 threads, and by default, each thread has timeout 1 minute.
 */
public class DkExecutorService {
	private static DkExecutorService INS;
	private final ScheduledThreadPoolExecutor executor;

	public DkExecutorService(int corePoolSize, int maxPoolSize, long aliveTime, TimeUnit unit) {
		ScheduledThreadPoolExecutor executor = this.executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(corePoolSize);
		executor.setMaximumPoolSize(maxPoolSize);
		executor.setKeepAliveTime(aliveTime, unit);
	}

	public static void install() {
		// By default, we need 2 to 4 processors
		int corePoolSize = Math.max(2, Math.min(4, Runtime.getRuntime().availableProcessors() - 1));
		install(corePoolSize, 1 + (corePoolSize << 1), 1, TimeUnit.MINUTES);
	}

	public static void install(int corePoolSize) {
		install(corePoolSize, 1 + (corePoolSize << 1), 1, TimeUnit.MINUTES);
	}

	public static void install(int corePoolSize, int maxPoolSize) {
		install(corePoolSize, maxPoolSize, 1, TimeUnit.MINUTES);
	}

	public static void install(int corePoolSize, int maxPoolSize, long aliveTime, TimeUnit unit) {
		if (INS == null) {
			INS = new DkExecutorService(corePoolSize, maxPoolSize, aliveTime, unit);
		}
	}

	public static ScheduledThreadPoolExecutor getExecutor() {
		if (INS == null) {
			throw new RuntimeException("Must call `install()` first");
		}
		return INS.executor;
	}
}
