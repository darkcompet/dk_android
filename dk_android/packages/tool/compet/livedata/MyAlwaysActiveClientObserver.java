/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import androidx.lifecycle.Observer;

/**
 * This client observer is alway active, user must remove it manually
 *
 * @param <M> Data (value) type.
 */
class MyAlwaysActiveClientObserver<M> extends MyClientObserver<M> {
	MyAlwaysActiveClientObserver(DkLiveData<M> host, TheOptions options, Observer<? super M> observer) {
		super(host, options, observer);
	}

	@Override
	protected boolean isInActiveState() {
		return true; // always active if client does not remove it manually
	}
}