/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.Iterator;
import java.util.Map;

import tool.compet.BuildConfig;
import tool.compet.core.DkLogcats;
import tool.compet.core4j.DkCaller;
import tool.compet.core4j.DkConst;

@SuppressWarnings("unchecked")
public class DkLiveData<M> {
	protected final MySafeIterableMap<Observer<? super M>, MyClientObserver<M>> observers = new MySafeIterableMap<>();

	// Count of observer which is in active state
	protected int activeCount = 0;

	// To handle active/inactive reentry, we guard with this boolean
	protected boolean isUpdatingActiveState;

	protected static final int DATA_START_VERSION = -1;
	protected static final Object DATA_NOT_SET = DkConst.UID_OBJ;
	// Lock to sync data
	protected final Object dataLock = new Object();
	// Value to send
	protected volatile Object data;
	// When setData is called, we set the pending data and actual data swap happens on the main thread
	protected volatile Object pendingData = DATA_NOT_SET;
	// Increment when set new value
	protected int version;
	protected boolean dispatchingData;
	protected boolean dispatchInvalidated;
	protected final Runnable postDataAction = () -> {
		Object newValue;
		synchronized (dataLock) {
			newValue = pendingData;
			pendingData = DATA_NOT_SET;
		}
		setValue((M) newValue);
	};

	/**
	 * Creates a LiveData initialized with the given {@code value}.
	 *
	 * @param value initial value
	 */
	public DkLiveData(M value) {
		data = value;
		version = DATA_START_VERSION + 1;
	}

	/**
	 * Creates a LiveData with no value assigned to it.
	 */
	public DkLiveData() {
		data = DATA_NOT_SET;
		version = DATA_START_VERSION;
	}

	/**
	 * Adds the given observer to the observers list within the lifespan of the given
	 * owner. The events are dispatched on the main thread. If LiveData already has data
	 * set, it will be delivered to the observer.
	 * <p>
	 * The observer will only receive events if the owner is in {@link Lifecycle.State#STARTED}
	 * or {@link Lifecycle.State#RESUMED} state (active).
	 * <p>
	 * If the owner moves to the {@link Lifecycle.State#DESTROYED} state, the observer will
	 * automatically be removed.
	 * <p>
	 * When data changes while the {@code owner} is not active, it will not receive any updates.
	 * If it becomes active again, it will receive the last available data automatically.
	 * <p>
	 * LiveData keeps a strong reference to the observer and the owner as long as the
	 * given LifecycleOwner is not destroyed. When it is destroyed, LiveData removes references to
	 * the observer &amp; the owner.
	 * <p>
	 * If the given owner is already in {@link Lifecycle.State#DESTROYED} state, LiveData
	 * ignores the call.
	 * <p>
	 * If the given owner, observer tuple is already in the list, the call is ignored.
	 * If the observer is already in the list with another owner, LiveData throws an
	 * {@link IllegalArgumentException}.
	 *
	 * @param owner The LifecycleOwner which controls the observer, for eg,. Activity, Fragment...
	 * @param observer The observer that will receive the events, for eg,. just a callback.
	 */
	@MainThread
	public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super M> observer) {
		observe(owner, new TheOptions(), observer);
	}

	@MainThread
	public void observe(@NonNull LifecycleOwner owner, @NonNull TheOptions options, @NonNull Observer<? super M> observer) {
		if (BuildConfig.DEBUG) {
			assertMainThread("observe");
		}
		if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
			DkLogcats.warning(this, "Ignore obsere when lifecycle owner goto destroyed state");
			return; // ignore
		}
		MyClientObserver<M> client = new MyLifecycleClientObserver<>(this, owner, options, observer);
		MyClientObserver<M> prevClient = observers.putIfAbsent(observer, client);
		if (prevClient == null) {
			// Tell client that is was registered
			client.onRegistered();

			// We need update something when active state of the observer changed
			if (client.active) {
				updateActiveState(1);

				// Client can get newest data if out of date
				if (client.options.notifyWhenObserve) {
					dispatchValue(client);
				}
			}
		}
		else {
			DkLogcats.warning(this, "Not allowed to add the same observer");
		}
	}

	/**
	 * Adds the given observer to the observers list. This call is similar to
	 * {@link DkLiveData#observe(LifecycleOwner, Observer)} with a LifecycleOwner, which
	 * is always active. This means that the given observer will receive all events and will never
	 * be automatically removed. You should manually call {@link #removeObserver(Observer)} to stop
	 * observing this LiveData.
	 * While LiveData has one of such observers, it will be considered
	 * as active.
	 * <p>
	 * If the observer was already added with an owner to this LiveData, LiveData throws an
	 * {@link IllegalArgumentException}.
	 *
	 * @param observer The observer that will receive the events
	 */
	@MainThread
	public void observeForever(@NonNull DkCaller<TheOptions> options, @NonNull Observer<? super M> observer) {
		if (BuildConfig.DEBUG) {
			assertMainThread("observeForever");
		}
		MyAlwaysActiveClientObserver<M> client = new MyAlwaysActiveClientObserver<>(this, options.call(), observer);
		MyClientObserver<M> prevWrapper = observers.putIfAbsent(observer, client);
		if (prevWrapper != null) {
			throw new IllegalArgumentException("Not allowed to add same observer");
		}

		// Tell client that it was registered
		client.onRegistered();

		// Maybe client will be active at this time, check to dispatch event if required
		if (client.active) {
			updateActiveState(1);

			if (client.options.notifyWhenObserve) {
				dispatchValue(client);
			}
		}
	}

	/**
	 * Removes the given observer from the observers list.
	 *
	 * @param observer The Observer to receive events.
	 */
	@MainThread
	public void removeObserver(@NonNull final Observer<? super M> observer) {
		if (BuildConfig.DEBUG) {
			assertMainThread("removeObserver");
		}
		MyClientObserver<M> client = observers.remove(observer);
		if (client != null) {
			// Tell client that it was unregistered
			client.onUnregistered();

			updateActiveState(-1);
		}
	}

	/**
	 * Returns true if this LiveData has observers.
	 *
	 * @return true if this LiveData has observers
	 */
	public boolean hasObservers() {
		return observers.size() > 0;
	}

	/**
	 * Returns true if this LiveData has active observers.
	 *
	 * @return true if this LiveData has active observers
	 */
	public boolean hasActiveObservers() {
		return activeCount > 0;
	}

	/**
	 * It is convenience method, like as EventBus, send an event to subscriber at specified thread.
	 * So it combines `setValue()`, `postValue()`... to match with client options.
	 */
	public void sendValue(M value) {
		final Iterator<Map.Entry<Observer<? super M>, MyClientObserver<M>>> it = observers.iteratorWithAdditions();
		final boolean isMainThread = (Thread.currentThread() == Looper.getMainLooper().getThread());

		while (it.hasNext()) {
			MyClientObserver<M> client = it.next().getValue();

			if (client.options.threadMode == TheOptions.THREAD_MODE_MAIN) {
				if (isMainThread) {
					setValue(value);
				}
				else {
					postValue(value);
				}
			}
			else if (client.options.threadMode == TheOptions.THREAD_MODE_POSTER) {
				setValue(value);
			}
			else {
				throw new RuntimeException("Not yet support or invalid thread mode: " + client.options.threadMode);
			}
		}
	}

	/**
	 * Sets the value. If there are active observers, the value will be dispatched to them.
	 * <p>
	 * This method must be called from the main thread. If you need set a value from a background
	 * thread, you can use {@link #postValue(Object)}
	 *
	 * @param value The new value
	 */
	@MainThread
	public void setValue(M value) {
		if (BuildConfig.DEBUG) {
			assertMainThread("setValue");
		}
		data = value;
		version++;
		dispatchValue(null);
	}

	/**
	 * Unset current value to `DATA_NOT_SET` which does not equal to any value.
	 * Call this to make all observers don't get notification until new value is dispatched.
	 */
	@MainThread
	public void unsetValue() {
		if (BuildConfig.DEBUG) {
			assertMainThread("unsetValue");
		}
		data = DATA_NOT_SET;
		version = DATA_START_VERSION;

		// Also reset last version for observers (clients)
		Iterator<Map.Entry<Observer<? super M>, MyClientObserver<M>>> iterator = observers.iteratorWithAdditions();
		while (iterator.hasNext()) {
			MyClientObserver<M> client = iterator.next().getValue();
			client.lastVersion = DATA_START_VERSION;
		}
	}

	/**
	 * Posts a task to a main thread to set the given value. So if you have a following code
	 * executed in the main thread:
	 * <pre class="prettyprint">
	 * liveData.postValue("a");
	 * liveData.setValue("b");
	 * </pre>
	 * The value "b" would be set at first and later the main thread would override it with
	 * the value "a".
	 * <p>
	 * If you called this method multiple times before a main thread executed a posted task, only
	 * the last value would be dispatched.
	 *
	 * @param value The new value
	 */
	public void postValue(M value) {
		boolean shouldPost;
		synchronized (dataLock) {
			shouldPost = (pendingData == DATA_NOT_SET);
			pendingData = value;
		}
		if (shouldPost) {
			MyArchTaskExecutor.getInstance().postToMainThread(postDataAction);
		}
	}

	@MainThread
	protected void updateActiveState(int change) {
		int prevActiveCount = activeCount;
		activeCount += change;

		if (! isUpdatingActiveState) {
			try {
				isUpdatingActiveState = true;

				while (prevActiveCount != activeCount) {
					boolean needToCallActive = (prevActiveCount == 0 && activeCount > 0);
					boolean needToCallInactive = (prevActiveCount > 0 && activeCount == 0);

					prevActiveCount = activeCount;

					// Maybe activeCount is changed at this time,
					// so we need perform while until prevActiveCount equals to activeCount
					if (needToCallActive) {
						onActive();
					}
					else if (needToCallInactive) {
						onInactive();
					}
				}
			}
			finally {
				isUpdatingActiveState = false;
			}
		}
	}

	/**
	 * Called when the data was changed (updated). This will notify the change to all active observers.
	 *
	 * @param client Null to dispatch to all observers. Otherwise dispatch to target observer.
	 */
	@MainThread
	protected void dispatchValue(@Nullable MyClientObserver<M> client) {
		if (dispatchingData) {
			dispatchInvalidated = true;
			return;
		}

		dispatchingData = true;

		do {
			dispatchInvalidated = false;

			// Notify to all observers
			if (client == null) {
				Iterator<Map.Entry<Observer<? super M>, MyClientObserver<M>>> it = observers.iteratorWithAdditions();

				while (it.hasNext()) {
					attemptNotify(it.next().getValue());

					if (dispatchInvalidated) {
						break;
					}
				}
			}
			// Notify to target observer
			else {
				attemptNotify(client);
				client = null;
			}
		} while (dispatchInvalidated);

		dispatchingData = false;
	}

	@MainThread
	protected void attemptNotify(MyClientObserver<M> client) {
		if (BuildConfig.DEBUG) {
			DkLogcats.debug(client, "Should notify to client? data set (%b), client active (%b), newer data (%b)", data != DATA_NOT_SET, client.active, client.lastVersion < version);
		}
		// Perform notify data changed to given observer (client) iff:
		// - data was set
		// - client is active
		// - client data is out of date (last data version of client < current data version)
		if (data != DATA_NOT_SET && client.active && client.lastVersion < version) {
			client.lastVersion = version;
			client.observer.onChanged((M) data);
		}
	}

	/**
	 * Called when the number of active observers change from 0 to 1.
	 * <p>
	 * This callback can be used to know that this LiveData is being used thus should be kept
	 * up to date.
	 */
	@MainThread
	protected void onActive() {
	}

	/**
	 * Called when the number of active observers change from 1 to 0.
	 * <p>
	 * This does not mean that there are no observers left, there may still be observers but their
	 * lifecycle states aren't {@link Lifecycle.State#STARTED} or {@link Lifecycle.State#RESUMED}
	 * (like an Activity in the back stack).
	 * <p>
	 * You can check if there are observers via {@link #hasObservers()}.
	 */
	@MainThread
	protected void onInactive() {
	}

	@MainThread
	protected void onObserverActiveStateChanged(MyClientObserver<M> client, boolean active) {
		updateActiveState(active ? 1 : -1);
		if (! active) {
			removeObserver(client.observer);
		}
		else if (client.options.notifyWhenObserve) {
			dispatchValue(client);
		}
	}

	private static void assertMainThread(String methodName) {
		if (! MyArchTaskExecutor.getInstance().isMainThread()) {
			throw new IllegalStateException("Cannot invoke " + methodName + " on a background thread");
		}
	}

	// region Get/Set

	/**
	 * Returns the current value.
	 * Note that calling this method on a background thread does not guarantee that the latest
	 * value set will be received.
	 *
	 * @return the current value
	 */
	@Nullable
	public M getValue() {
		return (data != DATA_NOT_SET) ? (M) data : null;
	}

	/**
	 * Get version of current this data, it is just count of `setValue()` invocation.
	 */
	public int getVersion() {
		return version;
	}

	// endregion Get/Set
}
