/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.appbundle.eventbus;

import java.lang.reflect.InvocationTargetException;

class Subscription {
	final Object subscriber;
	final SubscriptionMethod subscriptionMethod;

	// Become false if this subscription is unregistered
	boolean active = true;

	Subscription(Object subscriber, SubscriptionMethod subscriptionMethod) {
		this.subscriber = subscriber;
		this.subscriptionMethod = subscriptionMethod;
	}

	void invoke(Object arg) throws InvocationTargetException, IllegalAccessException {
		SubscriptionMethod sm = subscriptionMethod;

		if (arg == null ? sm.allowNullableParam : sm.paramType.isAssignableFrom(arg.getClass())) {
			sm.method.invoke(subscriber, arg);
		}
	}
}
