/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.eventbus;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Subscribe on EventBus.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface DkSubscribe {
	int id();

	int priority() default DkPriority.MIN;

	boolean sticky() default false;

	int threadMode() default DkThreadMode.ANDROID_MAIN;

	boolean allowNullParam() default true;
}
