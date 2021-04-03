/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.http;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Value assigned to this annotation will be added to header.
 * If caller use this annotation in a parameter of the method,
 * then value will be content of parameter, and content of `value()` will be ignored.
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
