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
    // clientOwner Like activity/fragment

    public DkTopicProvider(ViewModelStoreOwner hostOwner) {
        if (hostOwner == null) {
            throw new RuntimeException("Host must be present");
        }
        this.hostOwner = hostOwner;
    }

    // Get or Create a topic from host, also make client listen to the topic
    public <M> M register(ViewModelStoreOwner clientOwner, String topicId, String modelKey, Class<M> modelType) {
        if (clientOwner == null) {
            throw new RuntimeException("Client must be present");
        }
        try {
            TheHost host = obtainTheHostFromHostOwner();
            TheClient client = obtainTheClientFromClientOwner(clientOwner);
            return host.register(client, topicId, modelKey, modelType);
        }
        catch (Exception e) {
            DkLogs.error(DkTopicProvider.class, e);
            throw new RuntimeException(e);
        }
    }

    // Remove client from topic
    public void unregister(ViewModelStoreOwner clientOwner, String topicId) {
        TheHost host = obtainTheHostFromHostOwner();
        TheClient client = obtainTheClientFromClientOwner(clientOwner);
        host.unregister(client, topicId);
    }

    /**
     * Host is an instance which be held by hostOwner
     * Note that, hostOwners which have same type will share same TheHost object.
     *
     * Normally, host is long-live than client.
     */
    private TheHost obtainTheHostFromHostOwner() {
        return new ViewModelProvider(hostOwner).get(TheHost.class);
    }

    /**
     * Client is an instance which be held by clientOwner
     * Note that, clientOwners which have same type will share same TheClient object.
     *
     * Normally, client is short-live than host.
     */
    private TheClient obtainTheClientFromClientOwner(ViewModelStoreOwner clientOwner) {
        return new ViewModelProvider(clientOwner).get(TheClient.class);
    }
}
