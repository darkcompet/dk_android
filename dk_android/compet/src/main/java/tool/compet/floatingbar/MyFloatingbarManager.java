/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.floatingbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;

class MyFloatingbarManager {
	private static final int DISMISS_TIMEOUT = 1;
	private static final int DISMISS_FORCE = 2;

	private boolean isRunning;
	private final ArrayDeque<Order> orders = new ArrayDeque<>();

	private final Handler dismissHandler = new Handler(Looper.getMainLooper(), msg -> {
		if (msg.what == DISMISS_TIMEOUT) {
			dismissInternal((Order) msg.obj, DISMISS_TIMEOUT);
		}
		return false;
	});

	// Show bar by enqueue it first, then callback when it is ready to display
	void show(long duration, Callback callback) {
		Order tail = orders.peekLast();

		// Update duration if same record
		if (tail != null && callback == tail.callback.get()) {
			tail.duration = duration;
		}
		// Enqueue new record
		else {
			orders.addLast(new Order(duration, callback));
		}

		if (! isRunning) {
			isRunning = true;
			showNext();
		}
	}

	// Dismiss the bar
	void dismiss(Callback callback) {
		Order order = findOrderFromQueue(callback);
		if (order != null) {
			dismissInternal(order, DISMISS_FORCE);
		}
	}

	// Dismiss all current bars immediately
	void dismissAllImmediate() {
		// Cancel all events
		dismissHandler.removeCallbacksAndMessages(null);

		// Tell orders dismiss all bars
		for (Order order : orders) {
			if (! order.dismissNow()) {
				// When order cannot dismiss, we help it
				onDismissed(order);
			}
		}
	}

	void onViewShown(Callback callback) {
		Order order = findOrderFromQueue(callback);

		// Schedule dismiss this bar after duration (~2s)
		if (order != null) {
			dismissHandler.removeCallbacksAndMessages(order);
			dismissHandler.sendMessageDelayed(Message.obtain(dismissHandler, DISMISS_TIMEOUT, order), order.duration);
		}
	}

	void onViewDismissed(@Nullable Callback callback) {
		// Jump to final phase
		onDismissed(findOrderFromQueue(callback));
	}

	/**
	 * Try to show next until some record (bar) is shown successfully.
	 */
	private void showNext() {
		Order nextOrder = orders.peekFirst();

		// No order to do, waiting for user action
		if (nextOrder == null) {
			isRunning = false;
			return;
		}
		// Show next bar, if cannot, jump to final phase (remove order and show next)
		if (! nextOrder.show()) {
			onDismissed(nextOrder);
		}
	}

	private void dismissInternal(Order order, int type) {
		switch (type) {
			case DISMISS_TIMEOUT: {
				// We don't dismiss bar for infinite-duration-timeout type
				if (order.duration == DkFloatingbar.INFINITE_DURATION) {
					return;
				}
				break;
			}
			case DISMISS_FORCE: {
				// Remove timeout since this record is forced to dismiss
				dismissHandler.removeCallbacksAndMessages(order);
				break;
			}
			default: {
				throw new RuntimeException("Invalid type");
			}
		}

		// Tell bar dismiss itself
		if (! order.dismiss()) {
			// Jump to final phase if we cannot dismiss bar
			onDismissed(order);
		}
	}

	/**
	 * This is last phase when a bar is dismissed.
	 * We remove this order, then show next bar.
	 */
	private void onDismissed(Order order) {
		if (order != null) {
			orders.removeFirstOccurrence(order);
		}
		showNext();
	}

	private Order findOrderFromQueue(Callback callback) {
		for (Order order : orders) {
			if (callback == order.callback.get()) {
				return order;
			}
		}
		return null;
	}

	// Callback to tell bar show/dismiss view
	interface Callback {
		void show();

		void dismiss();

		void dismissNow();
	}

	// Order to display a bar
	static class Order {
		long duration;
		WeakReference<Callback> callback;

		Order(long duration, Callback callback) {
			this.duration = duration;
			this.callback = new WeakReference<>(callback);
		}

		boolean show() {
			Callback cb = callback.get();
			if (cb != null) {
				cb.show();
				return true;
			}
			return false;
		}

		boolean dismiss() {
			Callback cb = callback.get();
			if (cb != null) {
				cb.dismiss();
				return true;
			}
			return false;
		}

		boolean dismissNow() {
			Callback cb = callback.get();
			if (cb != null) {
				cb.dismissNow();
				return true;
			}
			return false;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Order && callback.get() == ((Order) other).callback.get();
		}

		@Override
		public String toString() {
			return "Order{" + "callback: " + callback.get().toString() + ", duration: " + duration + '}';
		}
	}
}
