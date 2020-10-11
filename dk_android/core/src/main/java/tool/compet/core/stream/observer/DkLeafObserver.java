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

/**
 * This is lowest (leaf) observer, so events come to it will not be sent to down more.
 */
public class DkLeafObserver<T> implements DkObserver<T> {
	private int __testFinalCount;
	private long startTime;

	public DkLeafObserver() {
	}

	@Override
	public void onSubscribe(DkControllable controllable) {
		startTime = System.currentTimeMillis();
	}

	@Override
	public void onNext(T item) {
	}

	@Override
	public void onError(Throwable e) {
		if (DEBUG) {
			DkLogs.error(this, e, "Stream error after %d (ms)",
				System.currentTimeMillis() - startTime);
		}
	}

	@Override
	public void onComplete() {
		if (DEBUG) {
			DkLogs.info(this, "Stream complete after %d (ms)",
				System.currentTimeMillis() - startTime);
		}
	}

	@Override
	public void onFinal() {
		if (DEBUG) {
			DkLogs.info(this, "Stream final after %d (ms)",
				System.currentTimeMillis() - startTime);
		}
		if (++__testFinalCount > 1) {
			DkLogs.warn(this, "Wrong implementation of #onFinal. Please review code !");
		}
	}
}
