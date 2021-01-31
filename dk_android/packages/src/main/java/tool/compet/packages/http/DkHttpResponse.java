/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.http;

/**
 * Response for a request. It contains: code, message and response from server.
 */
public class DkHttpResponse<T> {
	// Status code: 200, 400, 404....
	public int code;

	// Message from server for the api
	public String message;

	// Object got from server, was converted from json
	public T response;
}
