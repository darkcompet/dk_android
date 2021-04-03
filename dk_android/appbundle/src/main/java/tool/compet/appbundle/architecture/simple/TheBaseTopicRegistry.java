/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.simple;

import android.app.Application;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.architecture.topic.DkTopicProvider;

public class TheBaseTopicRegistry {
	protected final int SCOPE_APP = 1;
	protected final int SCOPE_HOST = 2;
	protected final int SCOPE_OWN = 3;

	protected final String topicId;
	protected int scope;
	protected final FragmentActivity host;
	protected final ViewModelStoreOwner clientOwner;

	public TheBaseTopicRegistry(String topicId, FragmentActivity host, ViewModelStoreOwner clientOwner) {
		this.topicId = topicId;
		this.host = host;
		this.clientOwner = clientOwner;
	}

	public TheBaseTopicRegistry atAppScope() {
		return atScope(SCOPE_APP);
	}

	public TheBaseTopicRegistry atOwnScope() {
		return atScope(SCOPE_OWN);
	}

	public <M> M obtain(Class<M> modelType) {
		return obtain(modelType.getName(), modelType);
	}

	public <M> M obtain(String modelKey, Class<M> modelType) {
		return topicProvider().register(clientOwner, topicId, modelKey, modelType);
	}

	void unregisterClient() {
		topicProvider().unregister(clientOwner, topicId);
	}

	protected TheBaseTopicRegistry atScope(int scope) {
		this.scope = scope;
		return this;
	}

	protected DkTopicProvider topicProvider() {
		if (scope == SCOPE_APP) {
			Application app = host.getApplication();

			if (app instanceof ViewModelStoreOwner) {
				return new DkTopicProvider((ViewModelStoreOwner) app);
			}

			throw new RuntimeException("App must be subclass of ViewModelStoreOwner");
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
