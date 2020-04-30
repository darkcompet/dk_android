package tool.compet.appbundle.arch.scopedTopic;

import androidx.collection.ArrayMap;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.util.DkLogs;

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
public class Host extends ViewModel implements Client.Listener {
	// All topics.
	private final ArrayMap<String, Topic> topicMap = new ArrayMap<>();

	// Topic with Clients which listening that topic.
	private final ArrayMap<String, List<Client>> topicClientsMap = new ArrayMap<>();

	/**
	 * Get (register topic if want) a model from a topic.
	 *
	 * @param modelType type of model caller want from the topic.
	 * @param register true if caller wanna register the topic, otherwise just get model.
	 */
	public <M> M getTopic(Client client, String topicId, Class<M> modelType, boolean register) throws Exception {
		// Obtain or Create new topic
		Topic topic = topicMap.get(topicId);

		if (topic == null) {
			topic = new Topic(topicId);
			topicMap.put(topicId, topic);
		}

		if (register) {
			// Add listener to here event from this client
			client.addListener(this);

			// Register this client in this topic
			List<Client> clientsInTopic = topicClientsMap.get(topicId);

			if (clientsInTopic == null) {
				clientsInTopic = new ArrayList<>();
				topicClientsMap.put(topicId, clientsInTopic);
			}
			if (!clientsInTopic.contains(client)) {
				clientsInTopic.add(client);
			}
		}

		return topic.getModel(modelType);
	}

	@Override
	protected void onCleared() {
		super.onCleared();

		if (topicMap.size() > 0 || topicClientsMap.size() > 0) {
			DkLogs.logw(this, "Host %s is cleared before Clients !!!", toString());
		}
		else if (DEBUG) {
			DkLogs.log(this, "Host %s is cleared after Clients", toString());
		}

		topicMap.clear();
		topicClientsMap.clear();
	}

	@Override
	public void onClientDisconnect(Client client) {
		for (int index = topicClientsMap.size() - 1; index >= 0; --index) {
			List<Client> listeningClients = topicClientsMap.valueAt(index);

			listeningClients.remove(client);

			if (DEBUG) {
				DkLogs.log(this, "Client %s has left topic %s at host %s.",
					client.toString(), topicClientsMap.keyAt(index), toString());
			}

			// delete topic which is no more listened by clients.
			if (listeningClients.size() == 0) {
				String topicId = topicClientsMap.keyAt(index);
				topicMap.remove(topicId);
				topicClientsMap.removeAt(index);

				if (DEBUG) {
					DkLogs.log(this, "Topic %s was removed from host %s since no client listen.",
						topicId, toString());
				}
			}
		}
	}
}
