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
