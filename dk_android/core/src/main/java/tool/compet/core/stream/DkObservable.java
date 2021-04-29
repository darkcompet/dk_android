/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import tool.compet.core.DkCallable;
import tool.compet.core.DkCallable1;
import tool.compet.core.DkRunnable;
import tool.compet.core.DkRunnable1;

/**
 * Refer: https://github.com/ReactiveX/RxJava/
 */
public abstract class DkObservable<T> {
	/**
	 * Note for implementation time
	 * <ul>
	 *    <li>
	 *       For God node: implement logic of emitting events (#onNext, #onError, #onFinal...) to under node.
	 *       The code should be blocked by try-catch to call #onFinal event.
	 *    </li>
	 *    <li>
	 *       For Godless node: just wrap given child observer and send to the upper node.
	 *    </li>
	 * </ul>
	 * The remain work to do is, write code in event-methods of Godless node.
	 * This job is same with implementation logic of God node. Mainly job is writing logic of #onNext,
	 * and if sometimes exception raised, you can call #onError to notify to lower node.
	 *
	 * @throws Exception When unable to subscribe at this node.
	 */
	protected abstract void subscribeActual(DkObserver<T> observer) throws Exception;

	protected DkObservable<T> parent;

	protected DkObservable() {
	}

	protected DkObservable(DkObservable<T> parent) {
		this.parent = parent;
	}

	/**
	 * Its useful if you wanna customize emitting-logic like onNext(), onError()... in #DkObserver to children.
	 * Note that, you must implement logic to call #onFinal() in observer.
	 */
	public static <T> DkObservable<T> fromEmitter(DkEmitter<T> emitter) {
		return new MyEmitterObservable<>(emitter);
	}

	/**
	 * Make an execution without input, then pass result to lower node. Note that, you can cancel
	 * execution of running thread but cannot control (cancel, pause, resume...) it deeply.
	 * To overcome this, just use #withControllable() instead.
	 */
	public static <T> DkObservable<T> fromExecution(DkCallable<T> execution) {
		return new MyGodCallableObservable<>(execution);
	}

	/**
	 * Its useful if you wanna control (pause, resume, cancel...) state of the task.
	 */
	public static <T> DkObservable<T> fromControllable(DkControllable<T> task) {
		return new MyGodControllableObservable<>(task);
	}

	/**
	 * Use it if you just wanna send item to children.
	 */
	public static <T> DkObservable<T> from(T item) {
		return new MyGodArrayObservable<>(item);
	}

	/**
	 * Use it if you just wanna send item to children.
	 */
	public static <T> DkObservable<T> from(T[] items) {
		return new MyGodArrayObservable<>(items);
	}

	/**
	 * Use it if you just wanna send item to children.
	 */
	public static <T> DkObservable<T> from(Iterable<T> items) {
		return new MyGodIterableObservable<>(items);
	}

	/**
	 * Receive an input T from Upper node and after converting inside other function,
	 * pass result R to lower node.
	 */
	public <R> DkObservable<R> map(DkCallable1<T, R> function) {
		return new MyMapObservable<>(this, function);
	}

	/**
	 * When some exception occured in upper node, instead of calling #onError(), it call #onNext with
	 * null-result to lower node. So even though succeed or fail, stream will be switched to #onNext() at this node.
	 * <p>
	 * Note:
	 * - If child node throws exception at `onNext()` then it will call `onError()` of child node instead.
	 * - Even this ignores error from upper node, but upper node will NOT call `onComplete()` since error occured
	 * during stream. So caller should call `doOnFinal()` to listen `onFinal()` event instead.
	 */
	public DkObservable<T> ignoreError() {
		return new MyIgnoreErrorObservable<>(this);
	}

	/**
	 * This is same as #map() but it accepts observable parameter, after get an input T from
	 * Upper node, it converts and pass result R to lower node.
	 * <p></p>
	 * Note that, null observable got from given #function.call() will be ok, but since nothing
	 * was converted in this node, then process will jump to next lower node with null-result.
	 */
	public <R> DkObservable<R> flatMap(DkCallable1<T, DkObservable<R>> function) {
		return new MyFlatMapObservable<>(this, function);
	}

	public DkObservable<T> delay(long duration, TimeUnit unit) {
		return new MyDelayObservable<>(this, unit.toMillis(duration));
	}

	public DkObservable<T> scheduleInBackground() {
		return scheduleIn(DkSchedulers.io(), 0, TimeUnit.MILLISECONDS, false);
	}

	public DkObservable<T> observeOnMainThread() {
		return observeOn(DkSchedulers.androidMain(), 0L, TimeUnit.MILLISECONDS, true);
	}

	public DkObservable<T> scheduleInBackgroundAndObserveOnAndroidMainThread() {
		return this
			.scheduleIn(DkSchedulers.io(), 0, TimeUnit.MILLISECONDS, false)
			.observeOn(DkSchedulers.androidMain(), 0L, TimeUnit.MILLISECONDS, true);
	}

	public DkObservable<T> scheduleIn(DkScheduler<T> scheduler) {
		return scheduleIn(scheduler, 0, TimeUnit.MILLISECONDS, false);
	}

	public DkObservable<T> scheduleIn(DkScheduler<T> scheduler, boolean isSerial) {
		return scheduleIn(scheduler, 0, TimeUnit.MILLISECONDS, isSerial);
	}

	public DkObservable<T> scheduleIn(DkScheduler<T> scheduler, long delay, TimeUnit unit, boolean isSerial) {
		return new MyScheduleOnObservable<>(this, scheduler, delay, unit, isSerial);
	}

	public DkObservable<T> observeOn(DkScheduler<T> scheduler) {
		return observeOn(scheduler, 0L, TimeUnit.MILLISECONDS, true);
	}

	public DkObservable<T> observeOn(DkScheduler<T> scheduler, long delayMillis) {
		return observeOn(scheduler, delayMillis, TimeUnit.MILLISECONDS, true);
	}

	public DkObservable<T> observeOn(DkScheduler<T> scheduler, long delay, TimeUnit unit, boolean isSerial) {
		return new MyThreadSwitcherObservable<>(this, scheduler, null, delay, unit, isSerial);
	}

	public DkObservable<T> switchThread(DkScheduler<T> scheduler, DkRunnable1<T> action) {
		return switchThread(scheduler, action, 0, TimeUnit.MILLISECONDS, true);
	}

	/**
	 * Make flow run in other thread, this is thread-switching.
	 */
	public DkObservable<T> switchThread(DkScheduler<T> scheduler, DkRunnable1<T> action, long delay, TimeUnit unit, boolean isSerial) {
		return new MyThreadSwitcherObservable<>(this, scheduler, action, delay, unit, isSerial);
	}

	/**
	 * Hears subscribe-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	public DkObservable<T> doOnSubscribe(DkRunnable1<DkControllable> action) {
		return new MyOnSubscribeObservable<>(this, action);
	}

	/**
	 * Hears success-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	public DkObservable<T> doOnNext(DkRunnable1<T> action) {
		return new MyOnNextObservable<>(this, action);
	}

	/**
	 * Hears error-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	public DkObservable<T> doOnError(DkRunnable1<Throwable> action) {
		return new MyOnErrorObservable<>(this, action);
	}

	/**
	 * Hears complete-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	public DkObservable<T> doOnComplete(DkRunnable action) {
		return new MyOnCompleteObservable<>(this, action);
	}

	/**
	 * Hears final-event while streaming. Note that, this method is developed to make observe
	 * stream-events easier when subscribing, so equivalent to #subscribe(observer),
	 * this function does not affect flow of current stream even if action throws exception.
	 */
	public DkObservable<T> doOnFinal(DkRunnable action) {
		return new MyOnFinalObservable<>(this, action);
	}

	public DkControllable<T> subscribeForControllable() {
		return subscribeForControllable(new DkControllable<>(new MyLeafObserver<>()));
	}

	/**
	 * Subscribe a observer (listener, callback) to stream, so we can listen what happening in stream.
	 * Differ with aother subscribe() method, this will return Controllable object,
	 * so you can control (dispose, resume, pause...) stream anytime you want.
	 */
	public DkControllable<T> subscribeForControllable(DkObserver<T> observer) {
		DkControllable<T> controllable = new DkControllable<>(observer);
		subscribe(controllable);
		return controllable;
	}

	/**
	 * Subscribe with empty (leaf) observer (listener, callback) to stream.
	 * You can use #doOnSubscribe(), #doOnNext()... to hear events in stream.
	 */
	public void subscribe() {
		subscribe(new MyLeafObserver<>());
	}

	/**
	 * Subscribe a observer (listener, callback) to stream, so we can listen what happening in the stream.
	 *
	 * @param observer For first time, this is object passed from caller. Next, we wrap it to own observer
	 *                 and pass up to parent node.
	 */
	public void subscribe(@NonNull DkObserver<T> observer) {
		try {
			// Go up to send observer (normally wrapped observer) of this node to parent node,
			// by do it, we can make linked list of observers, so parent can pass events to this node later.
			subscribeActual(observer);
		}
		catch (Exception e) {
			// Unable to subscribe (make node-link process), just pass error and then final events
			// to child observer
			observer.onError(e);
			observer.onFinal();
		}
	}
}
