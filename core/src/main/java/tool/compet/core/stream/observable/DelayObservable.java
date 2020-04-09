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

package tool.compet.core.stream.observable;

import android.os.Handler;
import android.os.HandlerThread;

import tool.compet.core.stream.observer.DkObserver;
import tool.compet.core.stream.observer.Observer;

public class DelayObservable<T> extends DkObservable<T> {
	private final long delayMillis;

	DelayObservable(DkObservable<T> parent, long delayMillis) {
		super(parent);
		this.delayMillis = delayMillis;
	}

	@Override
	protected void performSubscribe(DkObserver<T> observer) {
		parent.subscribe(new DelayObserver<>(observer, delayMillis));
	}

	static class DelayObserver<T> extends Observer<T> {
		final long delayMillis;

		DelayObserver(DkObserver<T> child, long delayMillis) {
			super(child);
			this.delayMillis = delayMillis;
		}

		@Override
		public void onNext(T result) {
			//todo fix since not work well
			HandlerThread handlerThread = new HandlerThread("HandlerThread");
			handlerThread.start();
			Handler handler = new Handler(handlerThread.getLooper());
			handler.postDelayed(() -> child.onNext(result), delayMillis);
		}
	}
}
