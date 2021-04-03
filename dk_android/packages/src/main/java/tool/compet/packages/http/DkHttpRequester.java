/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.http;

import android.graphics.Bitmap;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import tool.compet.core.DkBitmaps;
import tool.compet.packages.json.DkJsonHelper;
import tool.compet.core.DkLogs;
import tool.compet.core.DkUtils;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * Performs request (GET, POST...) to server. You can specify response type or it will be
 * acted as String, so be converted from Json string to Object.
 * Note that you must do it in IO thread. Here is an example of usage;
 * <code><pre>
 *    DkHttpRequester<Bitmap> httpRequester = new DkHttpRequester<>();
 *    DkHttpResponse<Bitmap> httpResponse = httpRequester.request(imageUrl, Bitmap.class);
 *
 *    int code = httpResponse.code;
 *    String message = httpResponse.message;
 *    Bitmap bitmap = httpResponse.response;
 * </pre></code>
 *
 * @param <T> response class, like String, Bitmap...
 */
@SuppressWarnings("unchecked")
public class DkHttpRequester<T> {
	private final SimpleArrayMap<String, String> headers = new ArrayMap<>();
	private String requestMethod = DkHttpConst.GET;
	private byte[] body;
	private int connectTimeout = 10000;
	private int readTimeout = 30000;

	public DkHttpRequester() {
	}

	public DkHttpRequester<T> setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
		return this;
	}

	public DkHttpRequester<T> addToHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	public DkHttpRequester<T> addAllToHeader(SimpleArrayMap<String, String> map) {
		headers.putAll(map);
		return this;
	}

	public DkHttpRequester<T> setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public DkHttpRequester<T> setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public DkHttpRequester<T> setBody(byte[] body) {
		this.body = body;
		return this;
	}

	public DkHttpResponse<T> request(String link, Class<T> responseClass) throws Exception {
		if (DEBUG) {
			DkLogs.info(this, "Start request, link: %s", link);
		}

		DkHttpResponse httpResponse = new DkHttpResponse();

		URL url = new URL(link);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		try {
			for (int i = headers.size() - 1; i >= 0; --i) {
				conn.setRequestProperty(headers.keyAt(i), headers.valueAt(i));
			}
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			conn.setRequestMethod(requestMethod);
			conn.setDoInput(true);

			switch (requestMethod) {
				case DkHttpConst.GET: {
					doGet(conn);
					break;
				}
				case DkHttpConst.POST: {
					doPost(conn);
					break;
				}
				default: {
					DkLogs.complain(this, "Invalid request method: " + requestMethod);
				}
			}

			if (DEBUG) {
				int code = conn.getResponseCode();

				if (code != 200) {
					DkLogs.info(this, "Error code: %d, message: %s", code, DkUtils.stream2string(conn.getErrorStream()));
				}
			}

			T response = null;

			if (responseClass != null) {
				response = decodeResponse(conn.getInputStream(), responseClass);
			}

			httpResponse.code = conn.getResponseCode();
			httpResponse.message = conn.getResponseMessage();
			httpResponse.response = response;

			if (DEBUG) {
				DkLogs.info(this, "Got response, code: %d, message: %s, respose: " + response, httpResponse.code, httpResponse.message);
			}
		}
		finally {
			conn.disconnect();
		}

		return httpResponse;
	}

	private void doGet(HttpURLConnection conn) throws Exception {
	}

	private void doPost(HttpURLConnection conn) throws Exception {
		if (body != null) {
			conn.setDoOutput(true);

			BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream());
			os.write(body);
			os.close();
		}
	}

	private T decodeResponse(InputStream inputStream, Class<T> responseClass) {
		T response;

		if (Bitmap.class.equals(responseClass)) {
			response = (T) DkBitmaps.load(inputStream);
		}
		else {
			String json = DkUtils.stream2string(inputStream);

			if (DEBUG) {
				DkLogs.info(this, "Got response, json: %s", json);
			}

			response = DkJsonHelper.getIns().json2obj(json, responseClass);
		}

		return response;
	}
}
