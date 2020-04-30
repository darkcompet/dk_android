/*
 * Copyright (c) 2019 DarkCompet. All rights reserved.
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

import java.util.concurrent.Callable;

import tool.compet.core.util.DkLogs;

/**
 * タスクをキャンセル・一時停止・再生できるものです。
 * 親Controllableが設定されれば連続リストとしてシステムを支配できような仕組みとなっています。
 */
public class DkControllable<T> extends AbsControllable implements Callable<T>, DkObserver<T> {
	protected final DkObserver<T> child;

	public DkControllable(DkObserver<T> child) {
		this.child = child;
	}

	@Override
	public T call() {
		throw new RuntimeException("Must implement this method");
	}

	@Override
	public void onSubscribe(DkControllable controllable) {
		if (controllable == this) {
			DkLogs.complain(this, "Wrong implementation ! God observer must be parentless");
		}
		this.parent = controllable;
		child.onSubscribe(controllable);
	}

	@Override
	public void onNext(T result) {
		child.onNext(result);
	}

	@Override
	public void onError(Throwable e) {
		child.onError(e);
	}

	@Override
	public void onComplete() {
		child.onComplete();
	}

	@Override
	public void onFinal() {
		child.onFinal();
	}
}
