/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DkServer {
	@Expose
	@SerializedName("baseUrl")
	public String baseUrl;

	@Expose
	@SerializedName("basicAuthUsername")
	public String basicAuthUsername;

	@Expose
	@SerializedName("basicAuthPassword")
	public String basicAuthPassword;

	@Expose
	@SerializedName("connectTimeoutMillis")
	public int connectTimeoutMillis = -1;

	@Expose
	@SerializedName("readTimeoutMillis")
	public int readTimeoutMillis = -1;
}
