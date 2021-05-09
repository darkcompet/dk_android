/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.topic;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelStoreOwner;

public class TheActivityTopicController extends TheBaseTopicController<TheActivityTopicController> {
	public TheActivityTopicController(String topicId, FragmentActivity host, ViewModelStoreOwner clientOwner) {
		super(topicId, host, clientOwner);

		this.scope = SCOPE_APP;
	}
}
