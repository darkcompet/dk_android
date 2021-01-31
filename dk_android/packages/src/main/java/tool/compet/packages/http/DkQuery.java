/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use this to form url with dynamic params.
 */
@Target(ElementType.PARAMETER)
@Retention(RUNTIME)
public @interface DkQuery {
	/**
	 * @return name of parameter in query
	 */
	String value();
}
