/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.topic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * This wraps topic-provider to handle specific topic.
 */
public class DkTopicOwner {
	protected String topicId;
	protected DkTopicProvider topicProvider;

	public DkTopicOwner(String topicId, ViewModelStoreOwner owner) {
		this.topicId = topicId;
		this.topicProvider = new DkTopicProvider(owner);
	}

	/**
	 * Use this to switch topic.
	 */
	public void setTopicId(String newTopicId) {
		this.topicId = newTopicId;
	}

	/**
	 * Use this to switch owner.
	 */
	public DkTopicOwner setOwner(@NonNull ViewModelStoreOwner newOwner) {
		this.topicProvider = new DkTopicProvider(newOwner);
		return this;
	}

	/**
	 * Make the client as topic-owner.
	 */
	public DkTopicOwner registerClient(ViewModelStoreOwner client) {
		topicProvider.registerClient(topicId, client);
		return this;
	}

	/**
	 * Remove a client from the topic.
	 */
	public void unregisterClient(ViewModelStoreOwner client) {
		topicProvider.unregisterClient(topicId, client);
	}
	
	public <M> M obtain(Class<M> modelType) {
		return obtain(modelType.getName(), modelType);
	}

	/**
	 * Obtain a model from given topic.
	 * If not exist the topic, then create new topic.
	 * If not exist the model in the topic, then create and register new model inside the topic.
	 */
	public <M> M obtain(String modelKey, Class<M> modelType) {
		return topicProvider.obtainModel(topicId, modelKey, modelType);
	}

	/**
	 * Clear all materials which be held by the topic (such as: ViewModels, Clients...).
	 */
	public DkTopicOwner clear() {
		topicProvider.removeTopic(topicId);
		return this;
	}
}
