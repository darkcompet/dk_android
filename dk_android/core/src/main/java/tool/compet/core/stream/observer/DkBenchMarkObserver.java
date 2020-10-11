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

package tool.compet.core.stream.observer;

import tool.compet.core.util.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

public class DkBenchMarkObserver<T> extends Observer<T> {
	private long startTime;

	public DkBenchMarkObserver(DkObserver<T> child) {
		super(child);
	}

	@Override
	public void onSubscribe(DkControllable controllable) {
		startTime = System.currentTimeMillis();

		super.onSubscribe(controllable);
	}

	@Override
	public void onError(Throwable e) {
		if (DEBUG) {
			DkLogs.error(this, e, "Stream error after %.3f s",
				(System.currentTimeMillis() - startTime) / 1000f);
		}
		super.onError(e);
	}

	@Override
	public void onComplete() {
		if (DEBUG) {
			DkLogs.info(this, "Stream complete after %.3f s",
				(System.currentTimeMillis() - startTime) / 1000f);
		}
		super.onComplete();
	}

	@Override
	public void onFinal() {
		if (DEBUG) {
			DkLogs.info(this, "Stream final after %.3f s",
				(System.currentTimeMillis() - startTime) / 1000f);
		}
		super.onFinal();
	}
}
