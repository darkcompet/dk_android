/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * IoSchedulerとAndroidMainSchedulerの代表的サブクラスがこのインターフェースを実装しています。
 * サブクラス内では、HandlerやExecutorServiceを利用し、タスクをスケジューリングします。
 */
public interface DkScheduler<T> {
    /**
     * Schedule runnable task, default will be run on serial executor.
     */
    void scheduleNow(Runnable task) throws Exception;

    void scheduleNow(Runnable task, boolean isSerial) throws Exception;

    void schedule(Runnable task, long delay, TimeUnit unit, boolean isSerial) throws Exception;

    /**
     * Schedule callable task, default will be run on serial executor.
     */
    void scheduleNow(Callable<T> task) throws Exception;

    void scheduleNow(Callable<T> task, boolean isSerial) throws Exception;

    void schedule(Callable<T> task, long delay, TimeUnit unit, boolean isSerial) throws Exception;

    /**
     * Just try to cancel, not serious way to cancel a task.
     * To cancel a task completely, lets implement cancel in Controllable.
     */
    boolean cancel(Callable<T> task, boolean mayInterruptThread);
}
