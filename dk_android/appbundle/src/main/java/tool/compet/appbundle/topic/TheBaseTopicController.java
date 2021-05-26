/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.topic;

import android.app.Application;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelStoreOwner;

@SuppressWarnings("unchecked")
public class TheBaseTopicController<T> {
	protected final int SCOPE_APP = 1;
	protected final int SCOPE_HOST = 2;
	protected final int SCOPE_OWN = 3;

	protected final String topicId;
	protected int scope;
	protected final FragmentActivity host;
	protected final ViewModelStoreOwner client;
	protected boolean clientIsOwner;

	TheBaseTopicController(String topicId, FragmentActivity host, ViewModelStoreOwner client) {
		this.topicId = topicId;
		this.host = host;
		this.client = client;
	}

	public T atAppScope() {
		return atScope(SCOPE_APP);
	}

	public T atOwnScope() {
		return atScope(SCOPE_OWN);
	}

	/**
	 * Choose scope to holds the topic.
	 */
	public T atScope(int scope) {
		this.scope = scope;
		return (T) this;
	}

	public T setClientIsOwner(boolean clientIsOwner) {
		this.clientIsOwner = clientIsOwner;
		return (T) this;
	}

	public <M> M obtain(Class<M> modelType) {
		return obtain(modelType.getName(), modelType);
	}

	/**
	 * Register the client to the topic, and obtain model from that topic (create new if not exist).
	 */
	public <M> M obtain(String modelKey, Class<M> modelType) {
		return topicProvider().register(client, clientIsOwner, topicId, modelKey, modelType);
	}

	/**
	 * Remove a client from the topic.
	 */
	public void removeClient(ViewModelStoreOwner clientOwner) {
		topicProvider().unregister(clientOwner, topicId);
	}

	/**
	 * Clear all materials which be held by the topic (such as: ViewModels, Clients...).
	 */
	public T clear() {
		topicProvider().removeTopic(topicId);
		return (T) this;
	}

	// Obtain topic provider to handle topics
	protected DkTopicProvider topicProvider() {
		if (scope == SCOPE_APP) {
			Application app = host.getApplication();

			if (app instanceof ViewModelStoreOwner) {
				return new DkTopicProvider((ViewModelStoreOwner) app);
			}

			throw new RuntimeException("App must be subclass of `ViewModelStoreOwner`");
		}
		else if (scope == SCOPE_HOST) {
			return new DkTopicProvider(host);
		}
		else if (scope == SCOPE_OWN) {
			return new DkTopicProvider(client);
		}

		throw new RuntimeException("Invalid scope level: " + scope);
	}
}
