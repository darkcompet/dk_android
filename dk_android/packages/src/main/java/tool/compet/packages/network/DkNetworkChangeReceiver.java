/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tool.compet.core.type.DkCallback;

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
 *
 * And register/unregister listener at cycle-pair like #onResume()/#onPause():
 * <pre>
 *    host.registerReceiver(networkChangeReceiver, networkChangeIntentFilter);
 *    host.unregisterReceiver(networkChangeReceiver);
 * </pre>
 */
public class DkNetworkChangeReceiver extends BroadcastReceiver {
	private DkCallback<Integer> listener;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (listener != null) {
			listener.call(DkNetworks.getConnectionStatus(context));
		}
	}

	/**
	 * @param statusListener will callback status of network, #see DkNetworkStatus.XXX
	 */
	public void setNetworkChangeListener(DkCallback<Integer> statusListener) {
		this.listener = statusListener;
	}
}
