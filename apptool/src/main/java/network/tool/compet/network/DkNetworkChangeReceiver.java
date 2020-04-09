/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tool.compet.core.type.DkCallback;

/**
 * Example of usage:
 * <pre>
 *    DkNetworkChangeReceiver networkChangeReceiver = new DkNetworkChangeReceiver();
 *    networkChangeReceiver.setNetworkChangeListener(status -> {
 * 	   DkEventBus.getIns().post(Eventbus$.NETWORK_CHANGED, status);
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
