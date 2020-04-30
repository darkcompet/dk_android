/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.core.stream.scheduler;

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
