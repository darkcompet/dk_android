/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.topic;

import androidx.collection.ArrayMap;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.log.DkLogs;

import static tool.compet.appbundle.BuildConfig.DEBUG;

/**
 * This is subclass of ViewModel, is stored in a {@link androidx.lifecycle.ViewModelStoreOwner} object
 * (like {@link androidx.fragment.app.FragmentActivity}, {@link androidx.fragment.app.Fragment}...).
 *
 * <ul>
 *    <li> Each host is middle part to communicate with coming-clients, and provide topics
 *    for request of a clients.
 *    <li> Relationship betwwen Topic-Host-Client is N-1-N. Diagram of them can be interpreted as
 *    [Topics <==> Host <==> Clients]. Note that, each client can register with multiple topics.
 * </ul>
 * <p>
 */
public class TheHost extends ViewModel implements TheClient.Listener {
    // All topics
    private final ArrayMap<String, MyTopic> topics = new ArrayMap<>();

    // Topic with Clients which listening that topic
    private final ArrayMap<String, List<TheClient>> topic2clients = new ArrayMap<>();

    /**
     * It does: get or create topic -> get or create model of topic -> make client listen the topic.
     * Note that, each topic contains multiple models which have different type each other.
     *
     * @param topicId topic unique id
     * @param modelClass model type which be held in the topic
     * @param client for eg,. activity or fragment...
     * @param listen true if make client listen the topic, otherwise just get model from topic
     * @return model object inside the topic
     */
    <M> M getOrCreateModelAtTopic(String topicId, Class<M> modelClass, TheClient client, boolean listen) throws Exception {
        // Register topic in the host
        MyTopic topic = topics.get(topicId);
        if (topic == null) {
            topic = new MyTopic(topicId);
            topics.put(topicId, topic);
        }

        if (listen) {
            // Listen un-register event from this client (for eg,. onCleared() was called)
            client.addListener(this);

            // Register this client in this topic
            List<TheClient> clientsListenTopic = topic2clients.get(topicId);
            if (clientsListenTopic == null) {
                clientsListenTopic = new ArrayList<>();
                topic2clients.put(topicId, clientsListenTopic);
            }
            if (! clientsListenTopic.contains(client)) {
                clientsListenTopic.add(client);
            }
        }

        // Get or Create model from topic
        return topic.getOrCreateModel(modelClass);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (topics.size() > 0 || topic2clients.size() > 0) {
            DkLogs.warn(this, "Host %s is cleared before Clients !!!", toString());
        }
        else if (DEBUG) {
            DkLogs.info(this, "Host %s is cleared after Clients", toString());
        }

        topics.clear();
        topic2clients.clear();
    }

    @Override
    public void onClientDisconnect(TheClient client) {
        for (int index = topic2clients.size() - 1; index >= 0; --index) {
            List<TheClient> listeningClients = topic2clients.valueAt(index);

            listeningClients.remove(client);

            if (DEBUG) {
                DkLogs.info(this, "Client %s has left topic %s under host %s.",
                    client.toString(), topic2clients.keyAt(index), toString());
            }

            // delete topic which is no more listened by clients.
            if (listeningClients.size() == 0) {
                String topicId = topic2clients.keyAt(index);
                topics.remove(topicId);
                topic2clients.removeAt(index);

                if (DEBUG) {
                    DkLogs.info(this, "Topic %s was removed from host %s since no client listen.", topicId, toString());
                }
            }
        }
    }
}
