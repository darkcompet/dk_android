/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.topic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.core.DkLogs;

public class DkTopicProvider {
	// Topic owner, for eg,. app/activity
	private final ViewModelStoreOwner owner;

	public DkTopicProvider(@NonNull ViewModelStoreOwner owner) {
		this.owner = owner;
	}

	// Get or Create a topic from host, also make client listen to the topic
	public DkTopicProvider registerClient(String topicId, ViewModelStoreOwner client) {
		try {
			theHost().registerClient(topicId, theClient(client));
			return this;
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
	public DkTopicProvider unregisterClient(String topicId, ViewModelStoreOwner client) {
		theHost().unregisterClient(topicId, theClient(client));
		return this;
	}

	public DkTopicProvider removeTopic(String topicId) {
		theHost().removeTopic(topicId);
		return this;
	}

	/**
	 * Host is an instance which be held by `hostOwner`
	 * Note that, `hostOwners` which have same type will share same TheHost object.
	 * <p>
	 * Normally, host is long-live than client.
	 */
	private TheHost theHost() {
		return new ViewModelProvider(owner).get(TheHost.class);
	}

	/**
	 * Client is an instance which be held by `clientOwner`/
	 * Note that, `clientOwners` which have same type will share same TheClient object.
	 * <p>
	 * Normally, client is short-live than host.
	 */
	private TheClient theClient(ViewModelStoreOwner client) {
		return new ViewModelProvider(client).get(TheClient.class);
	}
}
