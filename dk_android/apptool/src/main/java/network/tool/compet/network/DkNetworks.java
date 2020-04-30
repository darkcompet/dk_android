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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class DkNetworks {
	public static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null) {
				return info;
			}
		}
		return null;
	}

	/**
	 * @return status of network stated in DkNetworkConst.
	 */
	public static int getConnectionStatus(Context context) {
		NetworkInfo info = getActiveNetworkInfo(context);
		if (info == null) {
			return DkNetworkStatus.NOT_CONNECTED;
		}
		//todo deprecation
		switch (info.getType()) {
			case ConnectivityManager.TYPE_WIFI: {
				return DkNetworkStatus.WIFI;
			}
			case ConnectivityManager.TYPE_MOBILE: {
				switch (info.getSubtype()) {
					case TelephonyManager.NETWORK_TYPE_HSPAP: {
						return DkNetworkStatus.MOBILE_3G;
					}
					case TelephonyManager.NETWORK_TYPE_LTE: {
						return DkNetworkStatus.MOBILE_4G;
					}
					default: {
						return DkNetworkStatus.UNKNOWN_PROVIDER;
					}
				}
			}
			default: {
				return DkNetworkStatus.UNKNOWN_PROVIDER;
			}
		}
	}

	public static String getConnectionStatusName(Context context) {
		return getConnectionStatusName(getConnectionStatus(context));
	}

	public static String getConnectionStatusName(int status) {
		switch (status) {
			case DkNetworkStatus.NOT_CONNECTED: {
				return "Not Connected";
			}
			case DkNetworkStatus.WIFI: {
				return "Wifi";
			}
			case DkNetworkStatus.MOBILE_3G: {
				return "Mobile 3G";
			}
			case DkNetworkStatus.MOBILE_4G: {
				return "Mobile 4G";
			}
			default: {
				return "Unknown Network";
			}
		}
	}
}
