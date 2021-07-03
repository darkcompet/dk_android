/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.stream;

import java.util.concurrent.TimeUnit;

import tool.compet.core4j.DkCallable;
import tool.compet.core4j.DkCallable1;
import tool.compet.stream4j.DkControllable;
import tool.compet.stream4j.DkEmitter;
import tool.compet.stream4j.DkObservableSource;
import tool.compet.stream4j.DkScheduler;
import tool.compet.stream4j.OwnThreadSwitcherObservable;

/**
 * This works like RxJava or Java stream.
 * Refer: https://github.com/ReactiveX/RxJava/
 *
 * M: model which be passed down to child node
 */
@SuppressWarnings("unchecked")
public abstract class DkObservable<M> extends tool.compet.stream4j.DkObservable<M, DkObservable<M>> {
	protected DkObservable() {
	}

	protected DkObservable(DkObservableSource<M> parent) {
		super(parent);
	}

	// region Basic instance creator

	/**
	 * Its useful if you wanna customize emitting-logic like onNext(), onError()... in #DkObserver to children.
	 * Note that, you must implement logic to call #onFinal() in observer.
	 */
	public static <M> DkObservable<M> fromEmitter(DkEmitter<M> emitter) {
		return new MyEmitterObservable<M>(emitter);
	}

	/**
	 * Executes an action (without input from us) and then Emits result to child node.
	 *
	 * Make an execution without input, then pass result to lower node. Note that, you can cancel
	 * execution of running thread but cannot control (cancel, pause, resume...) it deeply.
	 * To overcome this, just use #withControllable() instead.
	 *
	 */
	public static <M> DkObservable<M> fromCallable(DkCallable<M> action) {
		return new MyGodCallableObservable<>(action);
	}

	/**
	 * Executes an action (without input from us) and then Emits result to child node.
	 *
	 * Its useful if you wanna control (pause, resume, cancel...) state of the task.
	 */
	public static <M> DkObservable<M> fromControllable(DkControllable<M> action) {
		return new MyGodControllableObservable<>(action);
	}

	/**
	 * Emits an item to child node.
	 */
	public static <M, T extends DkObservable<M>> DkObservable<M> from(M item) {
		return new MyGodArrayObservable<>(item);
	}

	/**
	 * Emits items to child node.
	 */
	public static <M> DkObservable<M> from(M[] items) {
		return new MyGodArrayObservable<>(items);
	}

	/**
	 * Emits items to child node.
	 */
	public static <M> DkObservable<M> from(Iterable<M> items) {
		return new MyGodIterableObservable<>(items);
	}

	// endregion Basic instance creator

	public DkObservable<M> delay(long duration) {
		return delay(duration, TimeUnit.MILLISECONDS);
	}

	public DkObservable<M> delay(long duration, TimeUnit unit) {
		current = new MyDelayObservable<>(current, unit.toMillis(duration));
		return this;
	}

	public <N> DkObservable<N> map(DkCallable1<M, N> function) {
		return super.jmap(function);
	}

	public <N> DkObservable<N> mapStream(DkCallable1<M, DkObservableSource<N>> function) {
		return super.jmapStream(function);
	}

	public DkObservable<M> scheduleInBackgroundAndObserveOnForeground() {
		return this
			.scheduleIn(DkSchedulers.io(), 0L, TimeUnit.MILLISECONDS, false)
			.observeOn(DkSchedulers.ui(), 0L, TimeUnit.MILLISECONDS, true);
	}

	public DkObservable<M> observeOnForeground() {
		return observeOn(DkSchedulers.ui(), 0L, TimeUnit.MILLISECONDS, true);
	}

	public DkObservable<M> observeOn(DkScheduler<M> scheduler, long delay, TimeUnit unit, boolean isSerial) {
		current = new OwnThreadSwitcherObservable<>(current, scheduler, null, delay, unit, isSerial);
		return this;
	}
}
