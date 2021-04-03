/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.simple;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelStoreOwner;

public class TheFragmentTopicRegistry extends TheBaseTopicRegistry {
	public TheFragmentTopicRegistry(String topicId, FragmentActivity host, ViewModelStoreOwner clientOwner) {
		super(topicId, host, clientOwner);

		this.scope = SCOPE_HOST;
	}

	public TheBaseTopicRegistry atHostScope() {
		return atScope(SCOPE_HOST);
	}
}
