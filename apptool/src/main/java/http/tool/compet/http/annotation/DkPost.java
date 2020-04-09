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

package tool.compet.http.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import tool.compet.http.DkHttpConst;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use this for POST request method.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface DkPost {
	/**
	 * @return relative url of api.
	 */
	String value();

	/**
	 * @return output format.
	 */
	String responseFormat() default DkHttpConst.APPLICATION_JSON;
}
