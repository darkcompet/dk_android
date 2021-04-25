/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelStoreOwner;

public class TheFragmentTopicController extends TheBaseTopicController {
	public TheFragmentTopicController(String topicId, FragmentActivity host, ViewModelStoreOwner clientOwner) {
		super(topicId, host, clientOwner);

		this.scope = SCOPE_HOST;
	}

	public TheBaseTopicController atHostScope() {
		return atScope(SCOPE_HOST);
	}
}
