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

public interface DkHttpConst {
	// Request method
	String GET = "GET";
	String POST = "POST";

	// Authentication method
	String AUTHORIZATION = "Authorization";
	String BASIC = "Basic ";
	String BEARER = "Bearer ";

	// Content Format
	String CONTENT_TYPE = "Content-Type";
	String APPLICATION_JSON = "application/json";
	String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
}
