package tool.compet.appbundle.arch.scopedTopic;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Client is an instance of ViewModel got from a store of client View (Activity, Fragment...),
 * so this can aware when the View is created (connect) or destroyed (disconnect).
 * <p></p>
 * When a client View get topic (connect) to a Host (server), then the Host will register
 * that client, so can unregister when it disconect later.
 * <p></p>
 * Each client maybe connect to different hosts. So it will hold list of
 * topic to release resource when disconnect.
 */
public class Client extends ViewModel {
	public interface Listener {
		void onClientDisconnect(Client client);
	}

	private List<Listener> listeners = new ArrayList<>();

	@Override
	protected void onCleared() {
		super.onCleared();

		for (Listener listener : listeners) {
			listener.onClientDisconnect(this);
		}

		listeners.clear();
	}

	public void addListener(Listener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
}
