/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.topic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.core.DkLogcats;

/**
 * Provide topics.
 */
public class DkTopicsProvider {
	// Topic owner, for eg,. app/activity
	private final ViewModelStoreOwner owner;

	public DkTopicsProvider(@NonNull ViewModelStoreOwner owner) {
		this.owner = owner;
	}

	// Get or Create a topic from host, also make client listen to the topic
	public DkTopicsProvider registerClient(String topicId, ViewModelStoreOwner client) {
		try {
			theHost().registerClient(topicId, theClient(client));
			return this;
		}
		catch (Exception e) {
			DkLogcats.error(DkTopicsProvider.class, e);
			throw new RuntimeException(e);
		}
	}

	// Get or Create a topic from host, also make client listen to the topic
	public <M> M obtainModel(String topicId, String modelKey, Class<M> modelType) {
		try {
			return theHost().obtainModel(topicId, modelKey, modelType);
		}
		catch (Exception e) {
			DkLogcats.error(DkTopicsProvider.class, e);
			throw new RuntimeException(e);
		}
	}

	// Remove client from topic
	public DkTopicsProvider unregisterClient(String topicId, ViewModelStoreOwner client) {
		theHost().unregisterClient(topicId, theClient(client));
		return this;
	}

	public DkTopicsProvider removeTopic(String topicId) {
		theHost().removeTopic(topicId);
		return this;
	}

	public DkTopicsProvider cleanupTopic(String topicId) {
		theHost().cleanupTopic(topicId);
		return this;
	}

	/**
	 * Host is an instance which be held by `hostOwner`
	 * Note that, `hostOwners` which have same type will share same TheHost object.
	 * <p>
	 * Normally, host is long-live than client.
	 */
	protected TheHost theHost() {
		return new ViewModelProvider(owner).get(TheHost.class);
	}

	/**
	 * Client is an instance which be held by `clientOwner`/
	 * Note that, `clientOwners` which have same type will share same TheClient object.
	 * <p>
	 * Normally, client is short-live than host.
	 */
	protected TheClient theClient(ViewModelStoreOwner client) {
		return new ViewModelProvider(client).get(TheClient.class);
	}
}
