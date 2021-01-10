/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DkServer {
	@Expose
	@SerializedName("baseUrl")
	public String baseUrl;

	@Expose
	@SerializedName("username")
	public String username;

	@Expose
	@SerializedName("password")
	public String password;

	@Expose
	@SerializedName("connectTimeoutMillis")
	public int connectTimeoutMillis = -1;

	@Expose
	@SerializedName("readTimeoutMillis")
	public int readTimeoutMillis = -1;
}
