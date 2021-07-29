/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;

/**
 * Network activity util.
 */
public class DkNetworks {
	@Nullable
	public static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			return cm.getActiveNetworkInfo();
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
