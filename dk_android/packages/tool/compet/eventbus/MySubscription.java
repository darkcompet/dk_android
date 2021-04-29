/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.eventbus;

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

	void invoke(Object param) throws InvocationTargetException, IllegalAccessException {
		MySubscriptionMethod sm = subscriptionMethod;

		if (param == null ? sm.allowNullableParam : sm.paramType.isAssignableFrom(param.getClass())) {
			sm.method.invoke(subscriber, param);
		}
	}
}
