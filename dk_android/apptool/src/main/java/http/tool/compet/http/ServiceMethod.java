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

import android.graphics.Bitmap;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import tool.compet.core.graphic.DkBitmaps;
import tool.compet.core.helper.DkJsonHelper;
import tool.compet.core.reflect.DkReflects;
import tool.compet.core.util.DkLogs;
import tool.compet.core.util.DkStrings;
import tool.compet.http.annotation.DkBody;
import tool.compet.http.annotation.DkGet;
import tool.compet.http.annotation.DkHeader;
import tool.compet.http.annotation.DkPost;
import tool.compet.http.annotation.DkQuery;
import tool.compet.http.annotation.DkUrlReplacement;
import tool.compet.http.annotation.DkUrlEncoded;

class ServiceMethod<T> {
	// Header key-value pairs
	final SimpleArrayMap<String, String> headers;

	// Response class: Bitmap, String, Object...
	final Class<T> responseClass;
	
	// Request method: GET, POST...
	String requestMethod;
	
	// Completely url
	String url;
	
	// Post body
	byte[] body;

	// Building process temporary data
	private String __baseUrl;
	private String __relativeUrl;
	private StringBuilder __formData;
	
	ServiceMethod(String baseUrl, Method method) {
		this.__baseUrl = baseUrl;
		this.headers = new ArrayMap<>();
		this.responseClass = DkReflects.getLastGenericReturnClass(method);

		parseOnMethod(method);
	}

	/**
	 * Parse info from given method. Note that, it is parsed only one time.
	 */
	private void parseOnMethod(Method method) {
		Annotation[] methodAnnotations = method.getDeclaredAnnotations();

		if (methodAnnotations.length == 0) {
			DkLogs.complain(this, "Must annotate each method with One of @DkGet, @DkPost...");
		}

		for (Annotation annotation : methodAnnotations) {
			if (annotation instanceof DkHeader) {
				parseOnMethod((DkHeader) annotation);
			}
			else if (annotation instanceof DkGet) {
				parseOnMethod((DkGet) annotation);
			}
			else if (annotation instanceof DkPost) {
				parseOnMethod((DkPost) annotation);
			}
		}

		if (requestMethod == null) {
			DkLogs.complain(this, "Missing request method annotation on the method: " + method);
		}
		if (DkStrings.isWhite(__relativeUrl)) {
			DkLogs.complain(this, "Invalid relative url: " + __relativeUrl);
		}

		__relativeUrl = DkStrings.trimExtras(__relativeUrl, '/');
	}

	private void parseOnMethod(DkHeader headerInfo) {
		headers.put(headerInfo.key(), headerInfo.value());
	}

	private void parseOnMethod(DkGet getInfo) {
		if (requestMethod != null) {
			DkLogs.complain(this, "Can specify only one request method");
		}

		requestMethod = DkHttpConst.GET;
		__relativeUrl = getInfo.value();

		switch (getInfo.responseFormat()) {
			case DkHttpConst.APPLICATION_JSON: {
				headers.put(DkHttpConst.CONTENT_TYPE, DkHttpConst.APPLICATION_JSON);
				break;
			}
			case DkHttpConst.X_WWW_FORM_URLENCODED: {
				headers.put(DkHttpConst.CONTENT_TYPE, DkHttpConst.X_WWW_FORM_URLENCODED);
				break;
			}
		}
	}

	private void parseOnMethod(DkPost postInfo) {
		if (requestMethod != null) {
			DkLogs.complain(this, "Can specify only one request method");
		}

		requestMethod = DkHttpConst.POST;
		__relativeUrl = postInfo.value();

		switch (postInfo.responseFormat()) {
			case DkHttpConst.APPLICATION_JSON: {
				headers.put(DkHttpConst.CONTENT_TYPE, DkHttpConst.APPLICATION_JSON);
				break;
			}
			case DkHttpConst.X_WWW_FORM_URLENCODED: {
				headers.put(DkHttpConst.CONTENT_TYPE, DkHttpConst.X_WWW_FORM_URLENCODED);
				break;
			}
		}
	}

	void build(Method method, Object[] methodParams) {
		// Reset dynamic fields before building
		url = null;
		body = null;

		// Parse method parameter annotations
		parseOnParams(method, methodParams);

		url = __baseUrl + __relativeUrl;

		if (__formData != null) {
			try {
				body = URLEncoder.encode(__formData.toString(), "UTF-8").getBytes(Charset.forName("UTF-8"));
			}
			catch (Exception e) {
				DkLogs.error(this, e);
			}
		}
	}

	private void parseOnParams(Method method, Object[] methodParams) {
		Annotation[][] paramAnnotations = method.getParameterAnnotations();
		StringBuilder query = new StringBuilder();

		for (int i = paramAnnotations.length - 1; i >= 0; --i) {
			for (Annotation annotation : paramAnnotations[i]) {
				if (annotation instanceof DkUrlReplacement) {
					parseOnParams((DkUrlReplacement) annotation, methodParams[i]);
				}
				else if (annotation instanceof DkHeader) {
					parseOnParams((DkHeader) annotation, methodParams[i]);
				}
				else if (annotation instanceof DkQuery) {
					parseOnParams(query, (DkQuery) annotation, methodParams[i]);
				}
				else if (annotation instanceof DkBody) {
					parseOnParams((DkBody) annotation, methodParams[i]);
				}
				else if (annotation instanceof DkUrlEncoded) {
					parseOnParams((DkUrlEncoded) annotation, methodParams[i]);
				}
			}
		}

		if (query.length() > 0) {
			__relativeUrl += "?" + query;
		}
	}

	private void parseOnParams(DkUrlReplacement replaceUrlInfo, Object paramValue) {
		String nodeName = replaceUrlInfo.value();
		String value = paramValue instanceof String ? (String) paramValue : String.valueOf(paramValue);

		while (true) {
			int index = __relativeUrl.indexOf(nodeName);

			if (index < 0) {
				break;
			}

			__relativeUrl = __relativeUrl.replace(nodeName, value);
		}
	}

	private void parseOnParams(DkHeader headerInfo, Object paramValue) {
		String key = headerInfo.key();
		String value = String.valueOf(paramValue);

		if (!DkStrings.isEmpty(headerInfo.value())) {
			DkLogs.complain(this, "Don't use #value() in #DkHeader for params");
		}

		headers.put(key, value);
	}

	private void parseOnParams(StringBuilder query, DkQuery queryInfo, Object paramValue) {
		if (query.length() > 0) {
			query.append('&');
		}
		query.append(queryInfo.value()).append("=").append(paramValue);
	}

	private void parseOnParams(DkBody bodyInfo, Object paramValue) {
		if (paramValue instanceof String) {
			body = ((String) paramValue).getBytes();
		}
		else if (paramValue.getClass().isPrimitive()) {
			body = String.valueOf(paramValue).getBytes();
		}
		else if (paramValue instanceof Bitmap) {
			body = DkBitmaps.toByteArray((Bitmap) paramValue);
		}
		else {
			body = DkJsonHelper.getIns().obj2json(paramValue).getBytes();
		}
	}

	private void parseOnParams(DkUrlEncoded bodyInfo, Object paramValue) {
		if (__formData == null) {
			__formData = new StringBuilder(256);
		}
		if (__formData.length() > 0) {
			__formData.append('&');
		}
		__formData.append(bodyInfo.value()).append("=").append(paramValue);
	}
}
