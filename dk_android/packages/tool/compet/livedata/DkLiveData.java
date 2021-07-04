/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.Iterator;
import java.util.Map;

/**
 * @param <M> Model type.
 */
public class DkLiveData<M> {
	@SuppressWarnings("WeakerAccess")
	protected final Object mDataLock = new Object();
	protected static final int START_VERSION = -1;
	@SuppressWarnings("WeakerAccess")
	protected static final Object VALUE_NOT_SET = new Object();

	protected MySafeIterableMap<Observer<? super M>, MyObserverWrapper> mObservers = new MySafeIterableMap<>();

	// how many observers are in active state
	@SuppressWarnings("WeakerAccess")
	protected int mActiveCount = 0;
	// to handle active/inactive reentry, we guard with this boolean
	protected boolean mChangingActiveState;
	protected volatile Object mData;
	// When setData is called, we set the pending data and actual data swap happens on the main thread
	@SuppressWarnings("WeakerAccess")
	protected volatile Object mPendingData = VALUE_NOT_SET;
	protected int mVersion;

	protected boolean mDispatchingValue;
	@SuppressWarnings("FieldCanBeLocal")
	protected boolean mDispatchInvalidated;
	@SuppressWarnings("unchecked")
	protected final Runnable mPostValueRunnable = () -> {
		Object newValue;
		synchronized (mDataLock) {
			newValue = mPendingData;
			mPendingData = VALUE_NOT_SET;
		}
		setValue((M) newValue);
	};

	/**
	 * Creates a LiveData initialized with the given {@code value}.
	 *
	 * @param value initial value
	 */
	public DkLiveData(M value) {
		mData = value;
		mVersion = START_VERSION + 1;
	}

	/**
	 * Creates a LiveData with no value assigned to it.
	 */
	public DkLiveData() {
		mData = VALUE_NOT_SET;
		mVersion = START_VERSION;
	}

	@SuppressWarnings("unchecked")
	private void considerNotify(MyObserverWrapper observer) {
		if (! observer.mActive) {
			return;
		}
		// Check latest state b4 dispatch. Maybe it changed state but we didn't get the event yet.
		//
		// we still first check observer.active to keep it as the entrance for events. So even if
		// the observer moved to an active state, if we've not received that event, we better not
		// notify for a more predictable notification order.
		if (! observer.shouldBeActive()) {
			observer.activeStateChanged(false);
			return;
		}
		if (observer.mLastVersion >= mVersion) {
			return;
		}
		observer.mLastVersion = mVersion;
		observer.mObserver.onChanged((M) mData);
	}

	@SuppressWarnings("WeakerAccess")
	protected void dispatchingValue(@Nullable MyObserverWrapper initiator) {
		if (mDispatchingValue) {
			mDispatchInvalidated = true;
			return;
		}
		mDispatchingValue = true;

		do {
			mDispatchInvalidated = false;
			if (initiator != null) {
				considerNotify(initiator);
				initiator = null;
			}
			else {
				for (Iterator<Map.Entry<Observer<? super M>, MyObserverWrapper>> iterator = mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
					considerNotify(iterator.next().getValue());

					if (mDispatchInvalidated) {
						break;
					}
				}
			}
		} while (mDispatchInvalidated);

		mDispatchingValue = false;
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
	 * @param owner    The LifecycleOwner which controls the observer
	 * @param observer The observer that will receive the events
	 */
	@MainThread
	public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super M> observer) {
		assertMainThread("observe");
		if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
			return; // ignore
		}
		MyLifecycleBoundObserver<M> wrapper = new MyLifecycleBoundObserver<>(this, owner, observer);
		MyObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
		if (existing != null && !existing.isAttachedTo(owner)) {
			throw new IllegalArgumentException("Cannot add the same observer" + " with different lifecycles");
		}
		if (existing != null) {
			return;
		}
		owner.getLifecycle().addObserver(wrapper);
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
	public void observeForever(@NonNull Observer<? super M> observer) {
		assertMainThread("observeForever");
		MyAlwaysActiveObserver<M> wrapper = new MyAlwaysActiveObserver<>(this, observer);
		MyObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
		if (existing instanceof MyLifecycleBoundObserver) {
			throw new IllegalArgumentException("Cannot add the same observer with different lifecycles");
		}
		if (existing != null) {
			return;
		}
		wrapper.activeStateChanged(true);
	}

	/**
	 * Removes the given observer from the observers list.
	 *
	 * @param observer The Observer to receive events.
	 */
	@MainThread
	public void removeObserver(@NonNull final Observer<? super M> observer) {
		assertMainThread("removeObserver");
		MyObserverWrapper removed = mObservers.remove(observer);
		if (removed == null) {
			return;
		}
		removed.detachObserver();
		removed.activeStateChanged(false);
	}

	/**
	 * Removes all observers that are tied to the given {@link LifecycleOwner}.
	 *
	 * @param owner The {@code LifecycleOwner} scope for the observers to be removed.
	 */
	@SuppressWarnings("WeakerAccess")
	@MainThread
	public void removeObservers(@NonNull final LifecycleOwner owner) {
		assertMainThread("removeObservers");
		for (Map.Entry<Observer<? super M>, MyObserverWrapper> entry : mObservers) {
			if (entry.getValue().isAttachedTo(owner)) {
				removeObserver(entry.getKey());
			}
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
	protected void postValue(M value) {
		boolean postTask;
		synchronized (mDataLock) {
			postTask = mPendingData == VALUE_NOT_SET;
			mPendingData = value;
		}
		if (!postTask) {
			return;
		}
		MyArchTaskExecutor.getInstance().postToMainThread(mPostValueRunnable);
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
	protected void setValue(M value) {
		assertMainThread("setValue");
		mVersion++;
		mData = value;
		dispatchingValue(null);
	}

	/**
	 * Returns the current value.
	 * Note that calling this method on a background thread does not guarantee that the latest
	 * value set will be received.
	 *
	 * @return the current value
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public M getValue() {
		Object data = mData;
		if (data != VALUE_NOT_SET) {
			return (M) data;
		}
		return null;
	}

	int getVersion() {
		return mVersion;
	}

	/**
	 * Called when the number of active observers change from 0 to 1.
	 * <p>
	 * This callback can be used to know that this LiveData is being used thus should be kept
	 * up to date.
	 */
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
	protected void onInactive() {

	}

	/**
	 * Returns true if this LiveData has observers.
	 *
	 * @return true if this LiveData has observers
	 */
	@SuppressWarnings("WeakerAccess")
	public boolean hasObservers() {
		return mObservers.size() > 0;
	}

	/**
	 * Returns true if this LiveData has active observers.
	 *
	 * @return true if this LiveData has active observers
	 */
	@SuppressWarnings("WeakerAccess")
	public boolean hasActiveObservers() {
		return mActiveCount > 0;
	}

	@MainThread
	void changeActiveCounter(int change) {
		int previousActiveCount = mActiveCount;
		mActiveCount += change;
		if (mChangingActiveState) {
			return;
		}
		mChangingActiveState = true;
		try {
			while (previousActiveCount != mActiveCount) {
				boolean needToCallActive = previousActiveCount == 0 && mActiveCount > 0;
				boolean needToCallInactive = previousActiveCount > 0 && mActiveCount == 0;
				previousActiveCount = mActiveCount;
				if (needToCallActive) {
					onActive();
				} else if (needToCallInactive) {
					onInactive();
				}
			}
		} finally {
			mChangingActiveState = false;
		}
	}

	static void assertMainThread(String methodName) {
		if (! MyArchTaskExecutor.getInstance().isMainThread()) {
			throw new IllegalStateException("Cannot invoke " + methodName + " on a background thread");
		}
	}
}
