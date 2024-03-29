/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.viewbinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @deprecated Use DataBinding instead.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface DkBindClick {
	/**
	 * @return id of view.
	 */
	int value();
}
