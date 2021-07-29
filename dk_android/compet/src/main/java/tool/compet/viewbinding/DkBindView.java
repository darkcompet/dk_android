/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.viewbinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @deprecated Use DataBinding instead.
 */
@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
public @interface DkBindView {
	/**
	 * @return id of view.
	 */
	int value();
}
