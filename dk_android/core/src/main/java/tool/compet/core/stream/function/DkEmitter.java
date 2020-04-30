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

package tool.compet.core.stream.function;

import tool.compet.core.stream.observer.DkControllable;
import tool.compet.core.stream.observer.DkObserver;

/**
 * This class is developed since some cases you wanna full-customize logic of emitting events,
 * so we just invoke {@link DkEmitter#call(DkObserver)} without try/catch block to give you full-control.
 * Note for the implementation time:
 * <p></p>
 * You must handle all event-methods
 * {@link DkObserver#onSubscribe(DkControllable)},
 * {@link DkObserver#onNext(Object)},
 * {@link DkObserver#onError(Throwable)},
 * {@link DkObserver#onComplete()},
 * {@link DkObserver#onFinal()}.
 * Normally, just use try-catch with finally statement to notify child observer.
 */
public interface DkEmitter<T> {
	void call(DkObserver<T> observer);
}
