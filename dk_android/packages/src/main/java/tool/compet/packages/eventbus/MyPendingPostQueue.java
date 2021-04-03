/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.eventbus;

class MyPendingPostQueue {
	private MyPendingPost head;
	private MyPendingPost tail;

	void enqueue(MyPendingPost pp) {
		if (tail != null) {
			tail.next = tail = pp;
		}
		else if (head == null) {
			head = tail = pp;
		}
		else {
			throw new RuntimeException("Invalid state, head: " + head + ", tail: " + tail);
		}
	}

	MyPendingPost dequeue() {
		MyPendingPost h = head;

		if (h != null) {
			MyPendingPost next = h.next;
			head = next;

			h.next = null;

			if (next == null) {
				tail = null;
			}
		}

		return h;
	}
}
