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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import tool.compet.core.stream.function.DkEmitter;
import tool.compet.core.stream.function.DkThrowableCallback;
import tool.compet.core.stream.function.DkThrowableFunction;
import tool.compet.core.stream.observer.DkObserver;
import tool.compet.core.stream.observer.DkControllable;
import tool.compet.core.stream.observer.DkLeafObserver;
import tool.compet.core.stream.scheduler.DkScheduler;
import tool.compet.core.stream.scheduler.DkSchedulers;
import tool.compet.core.util.DkStrings;

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
    * This job is like implementation logic of God node. You mainly write logic of #onNext, and if sometimes
    * exception raised, you can call #onError to notify to lower node.
    */
   protected abstract void performSubscribe(DkObserver<T> observer);

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
      return new EmitterObservable<>(emitter);
   }

   /**
    * Make an execution without input, then pass result to lower node. Note that, you can cancel
    * execution of running thread but cannot control (cancel, pause, resume...) it deeply.
    * To overcome this, just use #withControllable() instead.
    */
   public static <T> DkObservable<T> fromExecution(Callable<T> execution) {
      return new GodCallableObservable<>(execution);
   }

   /**
    * Its useful if you wanna control (pause, resume, cancel...) state of the task.
    */
   public static <T> DkObservable<T> fromControllable(DkControllable<T> task) {
      return new GodControllableObservable<>(task);
   }

   /**
    * Use it if you just wanna send item to children.
    */
   public static <T> DkObservable<T> from(T item) {
      return new GodArrayObservable<>(item);
   }

   /**
    * Use it if you just wanna send item to children.
    */
   public static <T> DkObservable<T> from(T[] items) {
      return new GodArrayObservable<>(items);
   }

   /**
    * Use it if you just wanna send item to children.
    */
   public static <T> DkObservable<T> from(Iterable<T> items) {
      return new GodIterableObservable<>(items);
   }

   /**
    * It always throw runtime exception internal, #onError of under node will be called,
    * so it is useful for validation.
    */
   public static <T> DkObservable<T> rte(String format, Object... args) {
      return new GodCallableObservable<>(() -> {
         throw new RuntimeException(DkStrings.format(format, args));
      });
   }

   /**
    * Receive an input T from Upper node and after converting inside other function,
    * pass result R to lower node.
    */
   public <R> DkObservable<R> map(DkThrowableFunction<T, R> function) {
      return new MapObservable<>(this, function);
   }

   /**
    * When some exception occured in upper node, instead of calling #onError(), it call #onNext with
    * NULL param to lower node. So even though succeed or fail, stream will be switched to #onNext() at this node.
    */
   public DkObservable<T> tryCatch() {
      return new TryCatchObservable<>(this);
   }

   /**
    * This is same as #map() but it accepts observable parameter, after get an input T from
    * Upper node, it converts and pass result R to lower node.
    * <p></p>
    * Note that, null observable got from given #function.call() will be ok, but since nothing
    * was converted in this node, then process will jump to next lower node with null-result.
    */
   public <R> DkObservable<R> flatMap(DkThrowableFunction<T, DkObservable<R>> function) {
      return new FlatMapObservable<>(this, function);
   }

   public DkObservable<T> delay(long duration, TimeUnit unit) {
      return new DelayObservable<>(this, unit.toMillis(duration));
   }

   public DkObservable<T> scheduleInBackground() {
      return scheduleIn(DkSchedulers.io(), 0, TimeUnit.MILLISECONDS, false);
   }

   public DkObservable<T> observeOnMainThread() {
      return observeOn(DkSchedulers.main(), 0L, TimeUnit.MILLISECONDS, true);
   }

   public DkObservable<T> scheduleInBackgroundAndObserveOnMainThread() {
      return this
         .scheduleIn(DkSchedulers.io(), 0, TimeUnit.MILLISECONDS, false)
         .observeOn(DkSchedulers.main(), 0L, TimeUnit.MILLISECONDS, true);
   }

   public DkObservable<T> scheduleIn(DkScheduler<T> scheduler) {
      return scheduleIn(scheduler, 0, TimeUnit.MILLISECONDS, false);
   }

   public DkObservable<T> scheduleIn(DkScheduler<T> scheduler, boolean isSerial) {
      return scheduleIn(scheduler, 0, TimeUnit.MILLISECONDS, isSerial);
   }

   public DkObservable<T> scheduleIn(DkScheduler<T> scheduler, long delay, TimeUnit unit, boolean isSerial) {
      return new ScheduleOnObservable<>(this, scheduler, delay, unit, isSerial);
   }

   public DkObservable<T> observeOn(DkScheduler<T> scheduler) {
      return observeOn(scheduler, 0L, TimeUnit.MILLISECONDS, true);
   }

   public DkObservable<T> observeOn(DkScheduler<T> scheduler, long delayMillis) {
      return observeOn(scheduler, delayMillis, TimeUnit.MILLISECONDS, true);
   }

   public DkObservable<T> observeOn(DkScheduler<T> scheduler, long delay, TimeUnit unit, boolean isSerial) {
      return new ObserveOnObservable<>(this, scheduler, delay, unit, isSerial);
   }

   public DkObservable<T> publishOn(DkScheduler<T> scheduler, DkThrowableCallback<T> action) {
      return publishOn(scheduler, action, 0, TimeUnit.MILLISECONDS, true);
   }

   /**
    * Publish a result on the scheduler during streaming. Note that,
    * given action maybe executed on another thread, so there is no guarantee
    * about execution-order between action and lower node.
    */
   public DkObservable<T> publishOn(DkScheduler<T> scheduler, DkThrowableCallback<T> action, long delay, TimeUnit unit, boolean isSerial) {
      return new PublishOnObservable<>(this, scheduler, action, delay, unit, isSerial);
   }

   /**
    * Hears subscribe-event while streaming. Note that, this method is developed to make observe
    * stream-events easier when subscribing, so equivalent to #subscribe(observer),
    * this function does not affect flow of current stream even if action throws exception.
    */
   public DkObservable<T> doOnSubscribe(DkThrowableCallback<DkControllable> action) {
      return new OnSubscribeObservable<>(this, action);
   }

   /**
    * Hears success-event while streaming. Note that, this method is developed to make observe
    * stream-events easier when subscribing, so equivalent to #subscribe(observer),
    * this function does not affect flow of current stream even if action throws exception.
    */
   public DkObservable<T> doOnNext(DkThrowableCallback<T> action) {
      return new OnNextObservable<>(this, action);
   }

   /**
    * Hears error-event while streaming. Note that, this method is developed to make observe
    * stream-events easier when subscribing, so equivalent to #subscribe(observer),
    * this function does not affect flow of current stream even if action throws exception.
    */
   public DkObservable<T> doOnError(DkThrowableCallback<Throwable> action) {
      return new OnErrorObservable<>(this, action);
   }

   /**
    * Hears complete-event while streaming. Note that, this method is developed to make observe
    * stream-events easier when subscribing, so equivalent to #subscribe(observer),
    * this function does not affect flow of current stream even if action throws exception.
    */
   public DkObservable<T> doOnComplete(Runnable action) {
      return new OnCompleteObservable<>(this, action);
   }

   /**
    * Hears final-event while streaming. Note that, this method is developed to make observe
    * stream-events easier when subscribing, so equivalent to #subscribe(observer),
    * this function does not affect flow of current stream even if action throws exception.
    */
   public DkObservable<T> doOnFinal(Runnable action) {
      return new OnFinalObservable<>(this, action);
   }

   public DkControllable<T> subscribeForControllable() {
      return subscribeForControllable(new DkControllable<>(new DkLeafObserver<>()));
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
    * Subscribe with empty observer (listener, callback) to stream.
    * You can use #doOnSubscribe(), #doOnNext()... to hear events in stream.
    */
   public void subscribe() {
      subscribe(new DkLeafObserver<>());
   }

   /**
    * Subscribe a observer (listener, callback) to stream, so we can listen what happening in stream.
    */
   public void subscribe(DkObserver<T> observer) {
      if (observer == null) {
         throw new RuntimeException("Observer cannot be null");
      }

      performSubscribe(observer);
   }
}
