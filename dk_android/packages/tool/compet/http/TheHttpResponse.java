/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.http;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Response of a request. It extracts some basic information from response
 * as code, message, body...
 */
public class TheHttpResponse {
	// Connection for read stream, must close after decode input
	private final HttpURLConnection connection;

	// Http status code: HttpURLConnection.*
	private int code = -1;

	// Http Message
	private String message;

	// Content from server
	private TheResponseBody body;

	TheHttpResponse(HttpURLConnection connection) {
		this.connection = connection;
	}

	/**
	 * For more detail information, use this.
	 */
	public HttpURLConnection connection() {
		return connection;
	}

	public int code() throws IOException {
		return code != -1 ? code : (code = connection.getResponseCode());
	}

	public String message() throws IOException {
		return message != null ? message : (message = connection.getResponseMessage());
	}

	public TheResponseBody body() {
		return body != null ? body : (body = new TheResponseBody(connection));
	}
}
