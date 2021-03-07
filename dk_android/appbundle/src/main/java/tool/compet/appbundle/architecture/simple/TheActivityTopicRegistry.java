/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.simple;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelStoreOwner;

public class TheActivityTopicRegistry extends TheBaseTopicRegistry {
    public TheActivityTopicRegistry(String topicId, FragmentActivity host, ViewModelStoreOwner clientOwner) {
        super(topicId, host, clientOwner);

        this.scope = SCOPE_APP;
    }
}
