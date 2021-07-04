/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

abstract class MyObserverWrapper<M> {
	protected final DkLiveData<M> liveData;
	final Observer<? super M> mObserver;
	boolean mActive;
	int mLastVersion = DkLiveData.START_VERSION;

	MyObserverWrapper(DkLiveData<M> liveData, Observer<? super M> observer) {
		this.liveData = liveData;
		this.mObserver = observer;
	}

	abstract boolean shouldBeActive();

	boolean isAttachedTo(LifecycleOwner owner) {
		return false;
	}

	void detachObserver() {
	}

	void activeStateChanged(boolean newActive) {
		if (newActive == mActive) {
			return;
		}
		// Immediately set active state, so we'd never dispatch anything to inactive owner
		mActive = newActive;
		liveData.changeActiveCounter(mActive ? 1 : -1);

		if (mActive) {
			liveData.dispatchingValue(this);
		}
	}
}
