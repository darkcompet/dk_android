/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.network;

public interface DkNetworkStatus {
	int NOT_CONNECTED = -1;
	int UNKNOWN_PROVIDER = 0;
	int WIFI = 1;
	int MOBILE_3G = 2;
	int MOBILE_4G = 3;
}
