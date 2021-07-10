/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.topic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * This wraps topic-provider to handle a specific topic.
 */
public class DkTopicOwner {
	protected String topicId;
	protected DkTopicsProvider topicsProvider;

	public DkTopicOwner(String topicId, ViewModelStoreOwner owner) {
		this.topicId = topicId;
		this.topicsProvider = new DkTopicsProvider(owner);
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
		this.topicsProvider = new DkTopicsProvider(newOwner);
		return this;
	}

	/**
	 * Make the client as topic-owner.
	 */
	public DkTopicOwner registerClient(ViewModelStoreOwner client) {
		topicsProvider.registerClient(topicId, client);
		return this;
	}

	/**
	 * Remove a client from the topic.
	 */
	public void unregisterClient(ViewModelStoreOwner client) {
		topicsProvider.unregisterClient(topicId, client);
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
		return topicsProvider.obtainModel(topicId, modelKey, modelType);
	}

	/**
	 * Just cleanup its materials without close (remove from topics provider) this topic.
	 * So its resource can be used if some where holds this topic instance.
	 */
	public DkTopicOwner close() {
		topicsProvider.removeTopic(topicId);
		return this;
	}

	/**
	 * Close (remove from topics provider) this topic and Cleanup its materials.
	 * So its resource will not be used even some where holds this topic instance.
	 */
	public DkTopicOwner cleanup() {
		topicsProvider.cleanupTopic(topicId);
		return this;
	}
}
