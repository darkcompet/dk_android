/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import androidx.lifecycle.Observer;

/**
 * This wrapper is alway active, user must remove it manually
 *
 * @param <M> Data (value) type.
 */
class MyAlwaysActiveObserverWrapper<M> extends MyObserverWrapper<M> {
	MyAlwaysActiveObserverWrapper(DkLiveData<M> host, TheOptions options, Observer<? super M> observer) {
		super(host, options, observer);
	}

	@Override
	public boolean considerAsActive() {
		// Always active for this wrapper
		return true;
	}
}