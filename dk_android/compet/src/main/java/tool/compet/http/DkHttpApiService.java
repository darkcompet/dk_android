/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.http;

import android.content.Context;
import android.util.Base64;

import tool.compet.core.DkUtils;
import tool.compet.http4j.DkHttpServerConfig;
import tool.compet.json4j.DkJsonConverter;

/**
 * It is convenience class. It will create new instance of http api requester,
 * called as ServiceApi, so caller can use it to request to server.
 * Usage example:
 * <pre>
 *    // Create new instance of UserApi
 *    UserApi userApi = DkApiService.newIns()
 *       .configWith(App.getContext(), "server/server_coresystem.json")
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
public class DkHttpApiService extends tool.compet.http4j.DkHttpApiService<DkHttpApiService> {
	/**
	 * For convenience, this provides setup with json config file.
	 *
	 * @param filename Json file under `asset` folder.
	 */
	public DkHttpApiService configWithJson(Context context, String filename) {
		String json = DkUtils.asset2string(context, filename);
		DkHttpServerConfig serverConfig = DkJsonConverter.getIns().json2obj(json, DkHttpServerConfig.class);
		if (serverConfig == null) {
			DkUtils.complainAt(this, "Failed to parse server config asset-file: %s", filename);
			return this;
		}

		if (serverConfig.baseUrl != null) {
			baseUrl = serverConfig.baseUrl;
		}
		if (serverConfig.basicAuthUsername != null || serverConfig.basicAuthPassword != null) {
			String pair = serverConfig.basicAuthUsername + ":" + serverConfig.basicAuthPassword;
			String credential = new String(Base64.encode(pair.getBytes(), Base64.NO_WRAP));
			setBasicCredential(credential);
		}
		if (serverConfig.connectTimeoutMillis != 0) {
			connectTimeoutMillis = serverConfig.connectTimeoutMillis;
		}
		if (serverConfig.readTimeoutMillis != 0) {
			readTimeoutMillis = serverConfig.readTimeoutMillis;
		}

		return this;
	}
}
