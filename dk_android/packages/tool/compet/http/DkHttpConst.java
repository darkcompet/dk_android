/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.http;

public interface DkHttpConst {
	// Http request methods
	String GET = "GET";
	String POST = "POST";
	String HEAD = "HEAD";
	String OPTIONS = "OPTIONS";
	String PUT = "PUT";
	String DELETE = "DELETE";
	String TRACE = "TRACE";

	// Authentication method
	String AUTHORIZATION = "Authorization";
	String BASIC = "Basic ";
	String BEARER = "Bearer ";

	// Content Format
	String CONTENT_TYPE = "Content-Type";
	String APPLICATION_JSON = "application/json";
	String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
}
