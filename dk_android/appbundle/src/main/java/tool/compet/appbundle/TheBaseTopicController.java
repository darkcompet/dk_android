/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle;

import android.app.Application;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelStoreOwner;

public class TheBaseTopicController {
	protected final int SCOPE_APP = 1;
	protected final int SCOPE_HOST = 2;
	protected final int SCOPE_OWN = 3;

	protected final String topicId;
	protected int scope;
	protected final FragmentActivity host;
	protected final ViewModelStoreOwner clientOwner;

	TheBaseTopicController(String topicId, FragmentActivity host, ViewModelStoreOwner clientOwner) {
		this.topicId = topicId;
		this.host = host;
		this.clientOwner = clientOwner;
	}

	public TheBaseTopicController atAppScope() {
		return atScope(SCOPE_APP);
	}

	public TheBaseTopicController atOwnScope() {
		return atScope(SCOPE_OWN);
	}

	/**
	 * Choose scope to holds the topic.
	 */
	public TheBaseTopicController atScope(int scope) {
		this.scope = scope;
		return this;
	}

	public <M> M obtain(Class<M> modelType) {
		return obtain(modelType.getName(), modelType);
	}

	/**
	 * Register the client to the topic, and obtain model from that topic (create new if not exist).
	 */
	public <M> M obtain(String modelKey, Class<M> modelType) {
		return topicProvider().register(clientOwner, topicId, modelKey, modelType);
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
	public TheBaseTopicController clear() {
		topicProvider().removeTopic(topicId);
		return this;
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
			return new DkTopicProvider(clientOwner);
		}

		throw new RuntimeException("Invalid scope level: " + scope);
	}
}