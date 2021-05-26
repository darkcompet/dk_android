/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.topic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.core.DkLogs;

public class DkTopicProvider {
	// Long-live view, for eg,. app/activity
	private final ViewModelStoreOwner hostOwner;
	// Short-live view, for eg,. activity/fragment
	private final ViewModelStoreOwner clientOwner;

	public DkTopicProvider(@NonNull ViewModelStoreOwner hostOwner, @NonNull ViewModelStoreOwner clientOwner) {
		this.hostOwner = hostOwner;
		this.clientOwner = clientOwner;
	}

	// Get or Create a topic from host, also make client listen to the topic
	public void registerClient(String topicId) {
		try {
			theHost().registerClient(topicId, theClient());
		}
		catch (Exception e) {
			DkLogs.error(DkTopicProvider.class, e);
			throw new RuntimeException(e);
		}
	}

	// Get or Create a topic from host, also make client listen to the topic
	public <M> M obtainModel(String topicId, String modelKey, Class<M> modelType) {
		try {
			return theHost().obtainModel(topicId, modelKey, modelType);
		}
		catch (Exception e) {
			DkLogs.error(DkTopicProvider.class, e);
			throw new RuntimeException(e);
		}
	}

	// Remove client from topic
	public void unregisterClient(String topicId) {
		theHost().unregisterClient(topicId, theClient());
	}

	public void removeTopic(String topicId) {
		theHost().removeTopic(topicId);
	}

	/**
	 * Host is an instance which be held by `hostOwner`
	 * Note that, `hostOwners` which have same type will share same TheHost object.
	 * <p>
	 * Normally, host is long-live than client.
	 */
	private TheHost theHost() {
		return new ViewModelProvider(hostOwner).get(TheHost.class);
	}

	/**
	 * Client is an instance which be held by `clientOwner`/
	 * Note that, `clientOwners` which have same type will share same TheClient object.
	 * <p>
	 * Normally, client is short-live than host.
	 */
	private TheClient theClient() {
		return new ViewModelProvider(clientOwner).get(TheClient.class);
	}
}
