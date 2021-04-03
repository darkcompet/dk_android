/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * This class is helpful for some cases you wanna full-customize logic of emitting events,
 * so we just invoke {@link DkEmitter#call(DkObserver)} without try/catch block to give you full-control.
 * Note for the implementation time:
 * <p></p>
 * You must handle all below event-methods
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
