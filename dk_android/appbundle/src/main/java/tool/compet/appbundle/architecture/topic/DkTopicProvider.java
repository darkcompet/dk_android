/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.topic;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.core.log.DkLogs;

public class DkTopicProvider {
    // Like app/activity
    private final ViewModelStoreOwner hostOwner;
    // Like activity/fragment
    private final ViewModelStoreOwner clientOwner;

    public DkTopicProvider(ViewModelStoreOwner hostOwner, ViewModelStoreOwner clientOwner) {
        if (hostOwner == null || clientOwner == null) {
            throw new RuntimeException("Host and Client must be present");
        }
        this.hostOwner = hostOwner;
        this.clientOwner = clientOwner;
    }

    public <M> M getOrCreateModelAtTopic(String topicId, Class<M> modelType, boolean listen) {
        try {
            // Normally, host is long-live than client
            TheHost host = new ViewModelProvider(hostOwner).get(TheHost.class);
            // Normally, client is short-live than host
            TheClient client = new ViewModelProvider(clientOwner).get(TheClient.class);
            
            return host.getOrCreateModelAtTopic(topicId, modelType, client, listen);
        }
        catch (Exception e) {
            DkLogs.error(this, e);
            throw new RuntimeException(e);
        }
    }
}
