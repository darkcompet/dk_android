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

import java.lang.reflect.Method;

import tool.compet.core.util.DkLogs;

class SubscriptionMethod {
	final Method method;
	Class<?> paramType;
	final int id;
	final int priority;
	final int threadMode;
	final boolean sticky;
	final boolean allowNullableParam;

	SubscriptionMethod(Method method) {
		Class[] paramTypes = method.getParameterTypes();

		if (paramTypes.length > 0) {
			this.paramType = (Class<?>) paramTypes[0];
		}

		if (paramType == null || paramType.isPrimitive()) {
			DkLogs.complain(this, "Require at least one non-primitive parameter.");
		}

		DkSubscribe subscription = method.getAnnotation(DkSubscribe.class);
		this.method = method;
		this.id = subscription.id();
		this.priority = subscription.priority();
		this.threadMode = subscription.threadMode();
		this.sticky = subscription.sticky();
		this.allowNullableParam = subscription.allowNullParam();
	}
}
