/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import androidx.lifecycle.Observer;

class MyAlwaysActiveObserver<M> extends MyObserverWrapper<M> {
	MyAlwaysActiveObserver(DkLiveData<M> liveData, Observer<? super M> observer) {
		super(liveData, observer);
	}

	@Override
	boolean shouldBeActive() {
		return true;
	}
}