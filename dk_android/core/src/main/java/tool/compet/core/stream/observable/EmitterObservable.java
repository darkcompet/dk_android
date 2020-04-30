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

package tool.compet.core.stream.observable;

import tool.compet.core.stream.function.DkEmitter;
import tool.compet.core.stream.observer.DkObserver;

/**
 * Observerの関数の呼び出しを自由に管理したい場合、このクラスを利用して頂ければ、
 * Observerを渡しますので、イベントの処理を完全に支配できます。
 */
public class EmitterObservable<T> extends DkObservable<T> {
	private final DkEmitter<T> emitter;

	EmitterObservable(DkEmitter<T> emitter) {
		this.emitter = emitter;
	}

	@Override
	protected void performSubscribe(DkObserver<T> child) {
		emitter.call(child);
	}
}
