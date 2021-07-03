/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.http;

import java.net.HttpURLConnection;

/**
 * Response of a request. It extracts some basic information from response
 * as code, message, body...
 */
public class TheHttpResponse extends tool.compet.http4j.TheHttpResponse {
	protected TheHttpResponse(HttpURLConnection connection) {
		super(connection);
	}

	@Override
	public TheResponseBody body() {
		return (TheResponseBody) (body != null ? body : (body = new TheResponseBody(connection)));
	}
}
