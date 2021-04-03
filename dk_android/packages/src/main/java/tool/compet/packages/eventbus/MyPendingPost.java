/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.eventbus;

public class MyPendingPost {
	MySubscription subscription;
	Object event;

	MyPendingPost next;

	MyPendingPost(MySubscription subscription, Object event) {
		this.subscription = subscription;
		this.event = event;
	}
}
