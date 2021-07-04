/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.topic;

import androidx.collection.ArrayMap;
import androidx.collection.ArraySet;

import java.util.Set;

/**
 * Each Topic provides storage to hold multiple types of model.
 */
@SuppressWarnings("unchecked")
public class OwnTopic {
	// Unique topic id
	public final String id;

	// List of clients which listening this topic
	public final Set<TheClient> clients = new ArraySet<>();

	// List of models inside this topic
	public final ArrayMap<String, Object> models = new ArrayMap<>();

	public OwnTopic(String id) {
		this.id = id;
	}

	/**
	 * Get or Create new model instance which associate with given #modelClass.
	 */
	public <M> M getOrCreateModel(String modelKey, Class<M> modelType) throws Exception {
		M model = (M) models.get(modelKey);

		if (model == null) {
			model = modelType.newInstance();
			models.put(modelKey, model);
		}

		return model;
	}

	public void registerClient(TheClient client) {
		clients.add(client);
	}

	public void unregisterClient(TheClient client) {
		clients.remove(client);
	}

	public int clientCount() {
		return clients.size();
	}

	public boolean removeClient(TheClient client) {
		return clients.remove(client);
	}

	public void clear() {
		clients.clear();
		models.clear();
	}
}
