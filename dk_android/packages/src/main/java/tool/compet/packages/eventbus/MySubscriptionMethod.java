/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.eventbus;

import java.lang.reflect.Method;
import java.util.Objects;

import tool.compet.core.DkLogs;
import tool.compet.core.DkStrings;

class MySubscriptionMethod {
	final Method method;
	Class<?> paramType;
	final int id;
	final int priority;
	final int threadMode;
	final boolean sticky;
	final boolean allowNullableParam;

	MySubscriptionMethod(Method method) {
		Class[] paramTypes = method.getParameterTypes();

		if (paramTypes.length > 0) {
			this.paramType = (Class<?>) paramTypes[0];
		}

		if (paramType == null || paramType.isPrimitive()) {
			DkLogs.complain(this, "Require non-primitive parameter.");
		}

		DkSubscribe subscription = method.getAnnotation(DkSubscribe.class);
		if (subscription == null) {
			throw new RuntimeException("Oops !");
		}
		this.method = method;
		this.id = subscription.id();
		this.priority = subscription.priority();
		this.threadMode = subscription.threadMode();
		this.sticky = subscription.sticky();
		this.allowNullableParam = subscription.allowNullParam();
	}
}
