/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Makes a field, constructor, parameter... automatic initilization in Dk libraries. Declaration example:
 *
 * <pre>{@code
 *    class HomeFragment extends DkFragment {
 *       @DkAutowired
 *       public Decorator decorator;
 *    }
 * }</pre>
 * <p>
 * #refer: https://github.com/spring-projects/spring-framework/blob/master/spring-beans/src/main/java/org/springframework/beans/factory/annotation/Autowired.java}
 */
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface DkAutowired {
}
