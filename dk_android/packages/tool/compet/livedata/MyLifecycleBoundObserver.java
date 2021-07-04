/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import static androidx.lifecycle.Lifecycle.State.DESTROYED;
import static androidx.lifecycle.Lifecycle.State.STARTED;

class MyLifecycleBoundObserver<M> extends MyObserverWrapper<M> implements LifecycleEventObserver {
	final LifecycleOwner mOwner;

	MyLifecycleBoundObserver(DkLiveData<M> liveData, @NonNull LifecycleOwner owner, Observer<? super M> observer) {
		super(liveData, observer);
		this.mOwner = owner;
	}

	@Override
	boolean shouldBeActive() {
		return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
	}

	@Override
	public void onStateChanged(@NonNull LifecycleOwner source,
		@NonNull Lifecycle.Event event) {
		Lifecycle.State currentState = mOwner.getLifecycle().getCurrentState();
		if (currentState == DESTROYED) {
			liveData.removeObserver(mObserver);
			return;
		}

		Lifecycle.State prevState = null;

		while (prevState != currentState) {
			prevState = currentState;
			activeStateChanged(shouldBeActive());
			currentState = mOwner.getLifecycle().getCurrentState();
		}
	}

	@Override
	boolean isAttachedTo(LifecycleOwner owner) {
		return mOwner == owner;
	}

	@Override
	void detachObserver() {
		mOwner.getLifecycle().removeObserver(this);
	}
}