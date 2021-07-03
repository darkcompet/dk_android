/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.http;

import android.content.Context;
import android.util.Base64;

import tool.compet.core.DkUtils;
import tool.compet.http4j.DkServer;
import tool.compet.json4j.DkJsonConverter;

/**
 * It is convenience class. It will create new instance of http api requester,
 * called as ServiceApi, so caller can use it to request to server.
 * Usage example:
 * <pre>
 *    // Create new instance of UserApi
 *    UserApi userApi = DkApiService.newIns()
 *       .configWith(App.getContext(), "server/server_darkcompet_apps.json")
 *       .create(UserApi.class);
 *
 * 	// Now we can request to server via methods inside userApi
 *    ProfileResponse profileResponse = userApi
 *       .downloadProfile(accessToken)
 *       .map(res -> ResponseValidator.validate(res).response)
 *       .scheduleInBackgroundAndObserveOnMainThread()
 *       .subscribe();
 * </pre>
 */
public class DkApiService extends tool.compet.http4j.DkApiService<DkApiService> {
	/**
	 * For convenience, this provides setup with json config file.
	 *
	 * @param filename Json file under `asset` folder.
	 */
	public DkApiService configWithJson(Context context, String filename) {
		String json = DkUtils.asset2string(context, filename);
		DkServer server = DkJsonConverter.getIns().json2obj(json, DkServer.class);
		if (server == null) {
			DkUtils.complainAt(this, "Failed to parse server config asset-file: %s", filename);
			return this;
		}

		if (server.baseUrl != null) {
			baseUrl = server.baseUrl;
		}
		if (server.basicAuthUsername != null || server.basicAuthPassword != null) {
			String pair = server.basicAuthUsername + ":" + server.basicAuthPassword;
			String credential = new String(Base64.encode(pair.getBytes(), Base64.NO_WRAP));
			setBasicCredential(credential);
		}
		if (server.connectTimeoutMillis != -1) {
			connectTimeoutMillis = server.connectTimeoutMillis;
		}
		if (server.readTimeoutMillis != -1) {
			readTimeoutMillis = server.readTimeoutMillis;
		}

		return this;
	}
}
