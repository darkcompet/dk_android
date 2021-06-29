/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.topic;

import androidx.collection.ArrayMap;
import androidx.lifecycle.ViewModel;

/**
 * This is subclass of ViewModel, is stored in a {@link androidx.lifecycle.ViewModelStoreOwner} object
 * (like {@link androidx.fragment.app.FragmentActivity}, {@link androidx.fragment.app.Fragment}...).
 *
 * <ul>
 *    <li> Each host is middle part to communicate with coming-clients, and provide topics
 *    for request of a clients.
 *    <li> Relationship betwwen Topic-Host-Client is N-1-N. Diagram of them can be interpreted as
 *    [Topics <==> Host <==> Clients]. Note that, each client can register with multiple topics.
 * </ul>
 * <p>
 */
public class TheHost extends ViewModel implements TheClient.Listener {
	// All topics (topicId vs topic)
	private final ArrayMap<String, MyTopic> allTopics = new ArrayMap<>();

	/**
	 * Obtain a model from the topic.
	 *
	 * @param topicId   Topic unique id
	 * @param modelKey  Model name to separate models in the topic.
	 * @param modelType Model type which be held in the topic
	 * @return Model object inside the topic
	 */

	<M> M obtainModel(String topicId, String modelKey, Class<M> modelType) throws Exception {
		// Get or Create topic
		MyTopic topic = allTopics.get(topicId);
		if (topic == null) {
			topic = new MyTopic(topicId);
			allTopics.put(topicId, topic);
		}

		// Get or Create model from topic
		return topic.getOrCreateModel(modelKey, modelType);
	}

	/**
	 * Make the client become topic-owner.
	 * When all owners of the topic were left, the topic and its material will be cleared.
	 *
	 * @param client For eg,. activity or fragment...
	 */
	void registerClient(String topicId, TheClient client) {
		// Get or Create topic
		MyTopic topic = allTopics.get(topicId);
		if (topic == null) {
			topic = new MyTopic(topicId);
			allTopics.put(topicId, topic);
		}

		// Listen leave-event of this client and Make topic remember this owner (client)
		client.addListener(this);
		topic.registerClient(client);
	}

	/**
	 * Remove the client from topic. If no client listening the topic,
	 * then host will remove the topic from itself.
	 */
	void unregisterClient(String topicId, TheClient client) {
		MyTopic topic = allTopics.get(topicId);

		if (topic != null) {
			topic.removeClient(client);

			if (topic.clientCount() == 0) {
				topic.clear();
				allTopics.remove(topic);
			}
		}
	}

	// Called when this host was destroyed completely
	@Override
	protected void onCleared() {
		super.onCleared();

		// Cleanup all topics
		for (int index = allTopics.size() - 1; index >= 0; --index) {
			allTopics.valueAt(index).clear();
		}
		allTopics.clear();
	}

	// Called when a client was destroyed completely (so it will disconnect host)
	@Override
	public void onClientDisconnect(TheClient client) {
		for (int index = allTopics.size() - 1; index >= 0; --index) {
			MyTopic topic = allTopics.valueAt(index);

			// Remove client from this topic
			topic.unregisterClient(client);

			// Forget topic which is no more listened by client
			if (topic.clientCount() == 0) {
				topic.clear();
				allTopics.removeAt(index);
			}
		}
	}

	public void removeTopic(String topicId) {
		MyTopic topic = allTopics.get(topicId);

		if (topic != null) {
			topic.clear();
			allTopics.remove(topicId);
		}
	}
}
