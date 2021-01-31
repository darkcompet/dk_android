/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.http;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use it for body in parameters of the method. The body is url encoded form data,
 * as key-value pairs, will be converted to bytes of k1=v1&k2=v2.... before sent to server.
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface DkUrlEncoded {
	String value();
}
