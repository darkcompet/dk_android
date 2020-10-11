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

package tool.compet.http;

import android.content.Context;
import android.util.Base64;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import tool.compet.core.helper.DkJsonHelper;
import tool.compet.core.stream.observable.DkObservable;
import tool.compet.core.util.DkLogs;
import tool.compet.core.util.Dks;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * Create new instance of http requester, also called as ServiceApi, then request to server.
 * Here is usage example:
 * <pre>
 *    // Create new instance of UserApi
 *    UserApi userApi = DkHttp.newIns()
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
public class DkHttp {
	private String baseUrl;
	private String credential;
	private int connectTimeoutMillis;
	private int readTimeoutMillis;

	private final SimpleArrayMap<Method, ServiceMethod<?>> serviceMethods;

	private DkHttp() {
		serviceMethods = new ArrayMap<>();
	}

	public static DkHttp newIns() {
		return new DkHttp();
	}

	public DkHttp setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	public DkHttp configWith(Context context, String filename) {
		String json = Dks.asset2string(context, filename);
		DkServer server = DkJsonHelper.getIns().json2obj(json, DkServer.class);

		if (server == null) {
			DkLogs.complain(this, "Failed to parse server config file %s", filename);
			return this;
		}

		if (server.baseUrl != null) {
			baseUrl = server.baseUrl;
		}
		if (server.username != null || server.password != null) {
			setBasicCredential(server.username, server.password);
		}
		if (server.connectTimeoutMillis != -1) {
			connectTimeoutMillis = server.connectTimeoutMillis;
		}
		if (server.readTimeoutMillis != -1) {
			readTimeoutMillis = server.readTimeoutMillis;
		}

		return this;
	}

	/**
	 * Static set basic credential to authenticate with server.
	 * Note that, dynamic adding should be performed in Service's methods.
	 */
	public DkHttp setBasicCredential(String username, String password) {
		String auth = username + ":" + password;
		this.credential = DkHttpConst.BASIC + new String(Base64.encode(auth.getBytes(), Base64.NO_WRAP));
		return this;
	}

	/**
	 * Static set bearer credential to authenticate with server.
	 * Note that, dynamic adding should be performed in Service's methods.
	 */
	public DkHttp setBearerCredential(String auth) {
		this.credential = DkHttpConst.BEARER + auth;
		return this;
	}

	public DkHttp setConnectTimeoutMillis(int connectTimeoutSecond) {
		this.connectTimeoutMillis = connectTimeoutSecond;
		return this;
	}

	public DkHttp setReadTimeoutMillis(int readTimeoutSecond) {
		this.readTimeoutMillis = readTimeoutSecond;
		return this;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getCredential() {
		return credential;
	}

	public long getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}

	public long getReadTimeoutMillis() {
		return readTimeoutMillis;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> serviceClass) {
		validateConfig();

		InvocationHandler handler = (Object proxy, Method method, Object[] args) -> {
			// Don't handle method which is not in service class
			if (!method.getDeclaringClass().equals(serviceClass)) {
				return method.invoke(proxy, args);
			}

			return createReturnValue(method, args);
		};

		return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, handler);
	}

	private DkObservable<DkHttpResponse<?>> createReturnValue(Method method, Object[] args) {
		// Create and cache service method
		ServiceMethod<?> sm;

		synchronized (serviceMethods) {
			sm = serviceMethods.get(method);
		}

		if (sm == null) {
			sm = new ServiceMethod<>(baseUrl, method);

			synchronized (serviceMethods) {
				serviceMethods.put(method, sm);
			}
		}

		final ServiceMethod<?> serviceMethod = sm;

		return DkObservable.fromExecution(() -> {
			// Rebuild arguments of service method since args are dynamic
			String requestMethod;
			byte[] body;
			ArrayMap<String, String> headers = new ArrayMap<>();
			String url;
			Class<?> responseClass;

			synchronized (serviceMethod) {
				serviceMethod.build(method, args);

				requestMethod = serviceMethod.requestMethod;
				body = serviceMethod.body;
				headers.putAll(serviceMethod.headers);
				url = serviceMethod.url;
				responseClass = serviceMethod.responseClass;
			}

			// Start request to server with parsed info
			return startRequest(requestMethod, body, headers, url, responseClass);
		});
	}

	private void validateConfig() {
		if (baseUrl == null) {
			DkLogs.complain(this, "Must specify non-null baseUrl");
		}
		if (!baseUrl.endsWith("/")) {
			baseUrl += '/';
		}
	}

	private <R> DkHttpResponse<R> startRequest(String requestMethod, byte[] body,
		SimpleArrayMap<String, String> headers, String url, Class<R> responseClass) throws Exception {

		DkHttpRequester<R> requester = DkHttpRequester.<R>newIns()
			.setReadTimeout(readTimeoutMillis)
			.setConnectTimeout(connectTimeoutMillis)
			.setRequestMethod(requestMethod)
			.setBody(body);

		if (credential != null) {
			requester.addToHeader(DkHttpConst.AUTHORIZATION, credential);
		}

		requester.addAllToHeader(headers);

		if (DEBUG) {
			DkLogs.info(this, "Network request in thread: %s", Thread.currentThread().toString());
		}

		return requester.request(url, responseClass);
	}
}
