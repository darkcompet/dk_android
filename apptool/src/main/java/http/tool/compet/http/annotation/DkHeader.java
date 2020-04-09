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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Value assigned to this annotation will be added to header.
 * Note that, if you use this in a parameter of a method, then value will be content of parameter,
 * so content of #value() will be ignored.
 */
@Target({METHOD, PARAMETER})
@Retention(RUNTIME)
public @interface DkHeader {
	/**
	 * @return key of property in header.
	 */
	String key();

	/**
	 * @return value of property in header, unused for case of parameter.
	 */
	String value() default "";
}
