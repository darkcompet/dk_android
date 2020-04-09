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

import tool.compet.core.helper.DkExecutorService;

public class DkSchedulers {
	private static DkScheduler IO;
	private static DkScheduler MAIN;

	@SuppressWarnings("unchecked")
	public static <T> DkScheduler<T> io() {
		if (IO == null) {
			synchronized (DkSchedulers.class) {
				if (IO == null) {
					IO = new DkIoScheduler<>(DkExecutorService.getIns());
				}
			}
		}
		return (DkScheduler<T>) IO;
	}

	@SuppressWarnings("unchecked")
	public static <T> DkScheduler<T> main() {
		if (MAIN == null) {
			synchronized (DkSchedulers.class) {
				if (MAIN == null) {
					MAIN = new DkAndroidMainScheduler<>();
				}
			}
		}
		return (DkScheduler<T>) MAIN;
	}
}
