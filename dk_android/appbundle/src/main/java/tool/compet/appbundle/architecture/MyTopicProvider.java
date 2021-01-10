/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.core.log.DkLogs;

public class MyTopicProvider {
    // Like app/activity
    private final ViewModelStoreOwner hostOwner;
    // Like activity/fragment
    private final ViewModelStoreOwner clientOwner;

    public MyTopicProvider(ViewModelStoreOwner hostOwner, ViewModelStoreOwner clientOwner) {
        if (hostOwner == null) {
            throw new RuntimeException("Host must be present");
        }
        if (clientOwner == null) {
            throw new RuntimeException("Client must be present");
        }

        this.hostOwner = hostOwner;
        this.clientOwner = clientOwner;
    }

    /**
     * Get (can register topic) a model from a topic.
     *
     * @param modelType type of model caller want from the topic.
     * @param register  true if caller wanna register the topic, otherwise just get model.
     */
    public <M> M getTopic(String topicId, Class<M> modelType, boolean register) {
        MyHost host = new ViewModelProvider(hostOwner).get(MyHost.class);
        MyClient client = new ViewModelProvider(clientOwner).get(MyClient.class);

        try {
            return host.getTopic(client, topicId, modelType, register);
        }
        catch (Exception e) {
            DkLogs.error(this, e);
        }

        throw new RuntimeException("Could not instantiate topic: " + modelType.getName());
    }
}
