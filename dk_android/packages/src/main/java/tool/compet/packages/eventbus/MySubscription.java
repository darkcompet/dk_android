/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.eventbus;

import java.lang.reflect.InvocationTargetException;

class MySubscription {
	final Object subscriber;
	final MySubscriptionMethod subscriptionMethod;

	// Become false if this subscription is unregistered
	boolean active = true;

	MySubscription(Object subscriber, MySubscriptionMethod subscriptionMethod) {
		this.subscriber = subscriber;
		this.subscriptionMethod = subscriptionMethod;
	}

	void invoke(Object arg) throws InvocationTargetException, IllegalAccessException {
		MySubscriptionMethod sm = subscriptionMethod;

		if (arg == null ? sm.allowNullableParam : sm.paramType.isAssignableFrom(arg.getClass())) {
			sm.method.invoke(subscriber, arg);
		}
	}
}
