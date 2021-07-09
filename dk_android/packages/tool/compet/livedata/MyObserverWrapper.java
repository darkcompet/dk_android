/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.livedata;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

/**
 * This is wrapper of observer (callback from client).
 * Normally, subclass contains lifecycle owner, so this wrapper can be considered as a client.
 * If lifecycle owner is available at it, then it can observe changes of lifespan from lifecycle owner.
 *
 * @param <M> Data type.
 */
abstract class MyObserverWrapper<M> {
	// Live data
	protected final DkLiveData<M> host;

	// Callback from client
	protected final Observer<? super M> observer;

	// Current active state of this observer (client)
	// True (active): it is ready to handle incoming dispatched data
	// False (inactive): does NOT ready to handle incoming dispatched data
	protected boolean active;

	// Version at last dispatched data from host
	// To avoid multiple invocation, we only accept newer version from host
	protected int lastVersion = DkLiveData.DATA_START_VERSION;

	// Make this observer (client) become more flexible for usage
	protected final TheOptions options;

	protected MyObserverWrapper(DkLiveData<M> host, TheOptions options, Observer<? super M> observer) {
		this.host = host;
		this.options = options;
		this.observer = observer;
	}

	/**
	 * Called after this observer was added to host (live data).
	 */
	protected abstract void onRegistered();

	/**
	 * Called after this observer was removed from host (live data).
	 */
	protected abstract void onUnregistered();

	/**
	 * Check whether or not this wrapper is associated with a lifecycle owner.
	 */
	protected boolean isAttachedTo(LifecycleOwner owner) {
		return false;
	}
}
