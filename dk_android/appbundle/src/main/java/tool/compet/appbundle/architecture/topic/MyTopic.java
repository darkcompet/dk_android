/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.topic;

import androidx.collection.ArrayMap;
import androidx.collection.ArraySet;

import java.util.Set;

/**
 * Each Topic provides storage to hold multiple types of model.
 */
@SuppressWarnings("unchecked")
class MyTopic {
	// Unique id of this topic
	final String id;

	// List of clients which listening this topic
	private final Set<TheClient> clients = new ArraySet<>();

	// List of models inside this topic
	private final ArrayMap<String, Object> models = new ArrayMap<>();

	MyTopic(String id) {
		this.id = id;
	}

	/**
	 * Get or Create new model instance which associate with given #modelClass.
	 */
	<M> M getOrCreateModel(String modelKey, Class<M> modelType) throws Exception {
		M model = (M) models.get(modelKey);

		if (model == null) {
			model = modelType.newInstance();
			models.put(modelKey, model);
		}

		return model;
	}

	void registerClient(TheClient client) {
		clients.add(client);
	}

	void unregisterClient(TheClient client) {
		clients.remove(client);
	}

	int clientCount() {
		return clients.size();
	}

	boolean removeClient(TheClient client) {
		return clients.remove(client);
	}

	void clear() {
		clients.clear();
		models.clear();
	}
}
