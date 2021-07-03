/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.http;

import java.net.HttpURLConnection;
import java.net.URL;

import tool.compet.core.DkLogcats;
import tool.compet.core4j.DkBuildConfig;
import tool.compet.core4j.DkUtils;
import tool.compet.http4j.DkHttpConst;

/**
 * Performs request (GET, POST...) to server. You can specify response type or it will be
 * acted as String, so be converted from Json string to Object.
 * Note that you must do it in IO thread. Here is an example of usage;
 * <code><pre>
 *    DkHttpClient<Bitmap> client = new DkHttpClient<>();
 *    DkHttpResponse<Bitmap> httpResponse = client.execute(imageUrl, Bitmap.class);
 *
 *    int code = httpResponse.code;
 *    String message = httpResponse.message;
 *    Bitmap bitmap = httpResponse.body().bitmap();
 * </pre></code>
 */
public class DkHttpClient extends tool.compet.http4j.DkHttpClient<DkHttpClient> {
	public DkHttpClient() {
	}

	public DkHttpClient(String link) {
		super(link);
	}

	@Override
	public TheHttpResponse execute() throws Exception {
		if (link == null) {
			throw new RuntimeException("Must provide url");
		}
		if (DkBuildConfig.DEBUG) {
			DkLogcats.info(this, "Start request with link: %s", link);
		}
		final URL url = new URL(link);
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		final TheHttpResponse response = new TheHttpResponse(connection);

		for (int index = headers.size() - 1; index >= 0; --index) {
			connection.setRequestProperty(headers.keyAt(index), headers.valueAt(index));
		}
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);
		connection.setRequestMethod(requestMethod);
		connection.setDoInput(true);

		if (DkHttpConst.GET.equals(requestMethod)) {
			doGet(connection);
		}
		else if (DkHttpConst.POST.equals(requestMethod)) {
			doPost(connection);
		}
		else {
			DkUtils.complainAt(this, "Invalid request method: " + requestMethod);
		}

		return response;
	}
}
