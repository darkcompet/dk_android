package tool.compet.appbundle.arch.scopedTopic;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.core.util.DkLogs;

public class TopicProvider {
   // Like app/activity
   private final ViewModelStoreOwner hostOwner;
   // Like activity/fragment
   private final ViewModelStoreOwner clientOwner;

   public TopicProvider(ViewModelStoreOwner hostOwner, ViewModelStoreOwner clientOwner) {
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
    * @param register true if caller wanna register the topic, otherwise just get model.
    */
   public <M> M getTopic(String topicId, Class<M> modelType, boolean register) {
      Host host = new ViewModelProvider(hostOwner).get(Host.class);
      Client client = new ViewModelProvider(clientOwner).get(Client.class);

      try {
         return host.getTopic(client, topicId, modelType, register);
      }
      catch (Exception e) {
         DkLogs.logex(this, e);
      }

      throw new RuntimeException("Could not instantiate topic: " + modelType.getName());
   }
}
