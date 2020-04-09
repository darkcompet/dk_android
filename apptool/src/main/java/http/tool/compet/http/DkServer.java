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
