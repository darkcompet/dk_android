/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tool.compet.core4j.DkRunner1;

/**
 * Example of usage:
 * <pre>
 *    DkNetworkChangeReceiver networkChangeReceiver = new DkNetworkChangeReceiver();
 *    networkChangeReceiver.setNetworkChangeListener(status -> {
 * 	      DkEventBus.getIns().post(Eventbus$.NETWORK_CHANGED, status);
 *    });
 *    IntentFilter networkChangeIntentFilter = new IntentFilter();
 *    networkChangeIntentFilter.addAction(Dk$.INTENT_CONNECTIVITY_CHANGE);
 * </pre>
 * <p>
 * And register/unregister listener at cycle-pair like #onResume()/#onPause():
 * <pre>
 *    host.registerReceiver(networkChangeReceiver, networkChangeIntentFilter);
 *    host.unregisterReceiver(networkChangeReceiver);
 * </pre>
 */
public class DkNetworkChangeReceiver extends BroadcastReceiver {
	private DkRunner1<Integer> listener;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (listener != null) {
			listener.run(DkNetworks.getConnectionStatus(context));
		}
	}

	/**
	 * @param statusListener Callback with network status (see `DkNetworkStatus.XXX`)
	 */
	public void setNetworkChangeListener(DkRunner1<Integer> statusListener) {
		this.listener = statusListener;
	}
}
