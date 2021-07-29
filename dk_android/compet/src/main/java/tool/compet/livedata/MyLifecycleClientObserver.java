/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

/**
 * This contains client and observer, which aware of lifespan events from lifecycle owner.
 *
 * @param <M> Data (value) type.
 */
class MyLifecycleClientObserver<M> extends MyClientObserver<M> implements LifecycleEventObserver {
	protected final LifecycleOwner owner;

	MyLifecycleClientObserver(DkLiveData<M> liveData, @NonNull LifecycleOwner owner, TheOptions options, Observer<? super M> observer) {
		super(liveData, options, observer);
		this.owner = owner;
	}

	/**
	 * Called when the host has added it successful.
	 */
	@Override
	public void onRegistered() {
		super.onRegistered();
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

		// At this time, we will update active state which should follow up state of lifecycle owner.
		// If observer has changed state inactive -> active, we should make host known it
		Lifecycle.State prevState = null;
		while (prevState != curState) {
			prevState = curState;

			boolean newActive = isInActiveState();
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
		super.onUnregistered();
		// Stop listen to lifespan of lifecycle owner
		owner.getLifecycle().removeObserver(this);
	}

	/**
	 * Check whether lifecycle owner is still in active state,
	 * so we should follow up that active state.
	 *
	 * @return True if lifecycle owner is in active state. Otherwise False.
	 */
	protected boolean isInActiveState() {
		// Activity, Fragment are at onStart()
		return owner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
	}
}