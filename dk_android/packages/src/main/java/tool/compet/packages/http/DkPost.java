/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.http;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
