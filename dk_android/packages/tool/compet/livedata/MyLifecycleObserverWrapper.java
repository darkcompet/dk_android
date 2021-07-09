/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import tool.compet.core.DkLogcats;

/**
 * This wrapper of observer, which aware of lifespan events from lifecycle owner.
 *
 * @param <M> Data (value) type.
 */
class MyLifecycleObserverWrapper<M> extends MyObserverWrapper<M> implements LifecycleEventObserver {
	protected final LifecycleOwner owner;

	MyLifecycleObserverWrapper(DkLiveData<M> liveData, @NonNull LifecycleOwner owner, TheOptions options, Observer<? super M> observer) {
		super(liveData, options, observer);
		this.owner = owner;
	}

	/**
	 * Called when the host has added it successful.
	 */
	@Override
	public void onRegistered() {
		if (! active && stillInActiveState()) {
			active = true;
		}
		// Start listen to lifespan of lifecycle owner
		owner.getLifecycle().addObserver(this);
	}

	/**
	 * After observing lifespan of lifecycle owner, at each time lifecycle of source changed,
	 * this will be triggered (for eg,. Activity, Fragment was created, resumed, paused, destroyed...).
	 */
	@Override
	public void onStateChanged(@NonNull LifecycleOwner owner, @NonNull Lifecycle.Event event) {
		Lifecycle.State curState = owner.getLifecycle().getCurrentState();

		DkLogcats.debug(this, "lifecycle state of owner %s was changed to: %s", owner.toString(), curState.toString());

		// Remove observer when owner (activity, fragment) was destroyed
//		if (curState == Lifecycle.State.DESTROYED) {
//			host.removeObserver(observer);
//			return;
//		}

		// When observer has changed state inactive -> active, we should tell host dispatch data
		Lifecycle.State prevState = null;
		while (prevState != curState) {
			prevState = curState;

			boolean newActive = stillInActiveState();
			if (newActive != active) {
				active = newActive;
				host.onObserverActiveStateChanged(this, newActive);
			}

			curState = owner.getLifecycle().getCurrentState();
		}
	}

	/**
	 * Called when the host has removed it successful.
	 */
	@Override
	public void onUnregistered() {
		// After was removed from host, this will be inactive
		if (active && ! stillInActiveState()) {
			active = false;
		}

		// Stop listen to lifespan of lifecycle owner
		owner.getLifecycle().removeObserver(this);
	}

	@Override
	public boolean isAttachedTo(LifecycleOwner owner) {
		return this.owner == owner;
	}

	protected boolean stillInActiveState() {
		// Activity, Fragment are at onStart()
		return owner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
	}
}