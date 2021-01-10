/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.helper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * システム全体に、タスク実行を担当・管理する唯一のScheduledThreadPoolExecutorです。
 * デフォルトに使用スレッド数は2〜4になります。また、スレッドの生きる時間は1分です。
 */
public class DkExecutorService {
    private static DkExecutorService INS;

    private final ScheduledThreadPoolExecutor executor;

    private DkExecutorService(int corePoolSize, int maxPoolSize, long aliveTime, TimeUnit unit) {
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(corePoolSize);
        executor.setMaximumPoolSize(maxPoolSize);
        executor.setKeepAliveTime(aliveTime, unit);
    }

    public static void install() {
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

    public static ScheduledThreadPoolExecutor getIns() {
        if (INS == null) {
            throw new RuntimeException("Must call #install() first");
        }
        return INS.executor;
    }
}
