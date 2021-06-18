/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.http;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;

import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import tool.compet.core.DkLogs;
import tool.compet.core.DkUtils;

import static tool.compet.core.BuildConfig.DEBUG;

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
 *    Bitmap bitmap = httpResponse.response;
 * </pre></code>
 */
public class DkHttpClient {
	private final SimpleArrayMap<String, String> headers = new ArrayMap<>();
	private String requestMethod = DkHttpConst.GET;
	private String link;
	private byte[] body;
	private int connectTimeout = 15000;
	private int readTimeout = 30000;

	public DkHttpClient() {
	}

	public DkHttpClient(String url) {
		this.link = url;
	}

	public DkHttpClient setUrl(String url) {
		this.link = url;
		return this;
	}

	public DkHttpClient setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
		return this;
	}

	public DkHttpClient addToHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	public DkHttpClient addAllToHeader(SimpleArrayMap<String, String> map) {
		headers.putAll(map);
		return this;
	}

	public DkHttpClient setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public DkHttpClient setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public DkHttpClient setBody(byte[] body) {
		this.body = body;
		return this;
	}

	/**
	 * For GET method, we just connect to remote server without decode stream.
	 * For POST method, we write to remote server and wait for client decode stream.
	 */
	public TheHttpResponse execute() throws Exception {
		if (link == null) {
			throw new RuntimeException("Must provide url");
		}
		if (DEBUG) {
			DkLogs.info(this, "Start request with link: %s", link);
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

	private void doGet(HttpURLConnection conn) throws Exception {
		// nothing to do now
	}

	private void doPost(HttpURLConnection conn) throws Exception {
		if (body != null) {
			conn.setDoOutput(true);

			BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream());
			os.write(body);
			os.close();
		}
	}
}
