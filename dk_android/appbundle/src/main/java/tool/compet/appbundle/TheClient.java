/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle;

import androidx.collection.ArraySet;
import androidx.lifecycle.ViewModel;

import java.util.Set;

/**
 * Client is an instance of ViewModel got from a store of client View (Activity, Fragment...),
 * so this can aware when the View is created (connect) or destroyed (disconnect).
 * <p></p>
 * When a client View get topic (connect) to a Host (server), then the Host will register
 * that client, so can unregister when it disconect later.
 * <p></p>
 * Each client maybe connect to different hosts. So it will hold list of
 * topic to release resource when disconnect.
 */
public class TheClient extends ViewModel {
	public interface Listener {
		void onClientDisconnect(TheClient client);
	}

	private final Set<Listener> listeners = new ArraySet<>();

	@Override
	protected void onCleared() {
		super.onCleared();

		for (Listener listener : listeners) {
			listener.onClientDisconnect(this);
		}

		listeners.clear();
	}

	void addListener(Listener listener) {
		listeners.add(listener);
	}
}
