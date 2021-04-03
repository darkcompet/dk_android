/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.eventbus;

import android.os.Looper;
import android.util.SparseArray;

import androidx.collection.ArrayMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import tool.compet.core.DkLogs;
import tool.compet.core.reflection.DkReflectionFinder;

/**
 * This library supports communication between objects (for eg,. Android Activity/Fragment).
 * You can post an object value to all consumers which interest your event, so consumer will
 * handle that event in the thread that consumer specified.
 * It also support #postSticky() when consumer was absent, but consumer must remove sticky event
 * manually when it consumes the event.
 * <p></p>
 * Here is usage example:
 * <pre>
 *    // In publisher: Post value to a topic with specified id
 *    DkEventBus.getIns().post(id, obj);
 *
 *    // In subscriber: Listen event at a topic which has own id. Note that, param must Not be primitive.
 *    #@DkSubscribe(id = XXX, allowNullParam = false)
 *    public void onEvent(Object param) {
 *    }
 * </pre>
 * <p></p>
 * <p>
 * Refer：https://github.com/greenrobot/EventBus
 */
public class DkEventBus {
	private static DkEventBus INS;

	// Store subscriptions for each ID
	private final SparseArray<CopyOnWriteArrayList<MySubscription>> subscriptions;

	// Cache subscription methods for each Class to improve performance
	private final ArrayMap<Class<?>, List<MySubscriptionMethod>> subscriptionMethodCache;

	// Store sticky events for each ID. Note that, it not be remove even the Class is unregistered.
	// So you should manually remove it via removeStickyEvents().
	private final SparseArray<List<Object>> stickyEvents;

	private MyMainPoster mainPoster;
	private MyAsyncPoster asyncPoster;

	private DkEventBus() {
		this.subscriptions = new SparseArray<>(64);
		this.subscriptionMethodCache = new ArrayMap<>(64);
		this.stickyEvents = new SparseArray<>(64);
	}

	public static DkEventBus getIns() {
		if (INS == null) {
			synchronized (DkEventBus.class) {
				if (INS == null) {
					INS = new DkEventBus();
				}
			}
		}
		return INS;
	}

	/**
	 * Add newly all subscriptions of this target to bus system.
	 */
	public <T> void register(T target) {
		Class clazz = target.getClass();

		// lookup subscription methods of the target's class
		ArrayMap<Class<?>, List<MySubscriptionMethod>> methodCache = subscriptionMethodCache;
		List<MySubscriptionMethod> subscriptionMethods;

		synchronized (this.subscriptionMethodCache) {
			subscriptionMethods = methodCache.get(clazz);
		}

		if (subscriptionMethods == null) {
			List<Method> methods = DkReflectionFinder.getIns().findMethods(clazz, DkSubscribe.class);

			if (methods.size() > 0) {
				subscriptionMethods = new ArrayList<>();

				for (Method method : methods) {
					subscriptionMethods.add(new MySubscriptionMethod(method));
				}

				synchronized (this.subscriptionMethodCache) {
					if (!methodCache.containsKey(clazz)) {
						methodCache.put(clazz, subscriptionMethods);
					}
				}
			}
		}

		if (subscriptionMethods != null && subscriptionMethods.size() > 0) {
			// add newly subscription to subscriptions for each subscriptionMethod
			synchronized (this) {
				CopyOnWriteArrayList<MySubscription> subscriptions;

				for (MySubscriptionMethod subscriptionMethod : subscriptionMethods) {
					final int id = subscriptionMethod.id;
					subscriptions = this.subscriptions.get(id);

					if (subscriptions == null) {
						subscriptions = new CopyOnWriteArrayList<>();
						this.subscriptions.put(subscriptionMethod.id, subscriptions);
					}

					MySubscription subscription = new MySubscription(target, subscriptionMethod);
					binaryInsertionSynced(subscription, subscriptions);

					// post sticky event to this subscription
					if (subscriptionMethod.sticky) {
						List<Object> stickyEvents = this.stickyEvents.get(id);

						if (stickyEvents != null && stickyEvents.size() > 0) {
							boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

							for (Object event : stickyEvents) {
								postToSubscription(subscription, event, isMainThread);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Remove all subscriptions of this target from bus system
	 */
	public synchronized <T> void unregister(T target) {
		// For Class: remove from subscription methods
		subscriptionMethodCache.remove(target.getClass());

		// For ID: remove from subscriptions
		SparseArray<CopyOnWriteArrayList<MySubscription>> cache = subscriptions;

		for (int index = 0, N = cache.size(); index < N; ++index) {
			List<MySubscription> subscriptions = cache.valueAt(index);

			if (subscriptions != null) {
				for (int subIndex = subscriptions.size() - 1; subIndex >= 0; --subIndex) {
					MySubscription subscription = subscriptions.get(subIndex);

					if (target == subscription.subscriber) {
						// make this subscription unactive
						subscription.active = false;
						subscriptions.remove(subIndex);
					}
				}
			}
		}
	}

	/**
	 * Check whether this subscriber was registered or not.
	 */
	public synchronized <T> boolean isRegistered(T subscriber) {
		SparseArray<CopyOnWriteArrayList<MySubscription>> cache = subscriptions;

		for (int index = 0, N = cache.size(); index < N; ++index) {
			List<MySubscription> subscriptions = cache.valueAt(index);

			if (subscriptions != null) {
				for (int subIndex = subscriptions.size() - 1; subIndex >= 0; --subIndex) {
					MySubscription subscription = subscriptions.get(subIndex);

					if (subscriber == subscription.subscriber) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Check whether this Class was registered or not.
	 */
	public <T> boolean isRegistered(Class<T> clazz) {
		List<MySubscriptionMethod> methods;

		synchronized (subscriptionMethodCache) {
			methods = this.subscriptionMethodCache.get(clazz);
		}

		return methods != null && methods.size() > 0;
	}

	/**
	 * Notify all subscriptions that have same id.
	 */
	public <T> void post(int id, T data) {
		CopyOnWriteArrayList<MySubscription> subscriptions;

		synchronized (this.subscriptions) {
			subscriptions = this.subscriptions.get(id);
		}

		if (subscriptions != null) {
			boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

			for (MySubscription subscription : subscriptions) {
				postToSubscription(subscription, data, isMainThread);
			}
		}
	}

	/**
	 * Post to given target class.
	 */
	public <T> void post(int id, Class target, T data) {
		CopyOnWriteArrayList<MySubscription> subscriptions;

		synchronized (this.subscriptions) {
			subscriptions = this.subscriptions.get(id);
		}

		if (subscriptions != null) {
			boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

			for (MySubscription subscription : subscriptions) {
				if (target.equals(subscription.subscriber.getClass())) {
					postToSubscription(subscription, data, isMainThread);
					break;
				}
			}
		}
	}

	/**
	 * Post to targets that not be in excepted target classes.
	 */
	public <T> void postExcept(int id, T data, Class... exceptTargetClasses) {
		CopyOnWriteArrayList<MySubscription> subscriptions;

		synchronized (this.subscriptions) {
			subscriptions = this.subscriptions.get(id);
		}

		if (subscriptions != null) {
			List<Class> blacklist = Arrays.asList(exceptTargetClasses);
			boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

			for (MySubscription subscription : subscriptions) {
				if (!blacklist.contains(subscription.subscriber.getClass())) {
					postToSubscription(subscription, data, isMainThread);
				}
			}
		}
	}

	/**
	 * Post to targets that not be in excepted targets.
	 */
	public <T> void postExcept(int id, T data, Object... exceptTargets) {
		CopyOnWriteArrayList<MySubscription> subscriptions;

		synchronized (this.subscriptions) {
			subscriptions = this.subscriptions.get(id);
		}

		if (subscriptions != null) {
			List<Object> blacklist = Arrays.asList(exceptTargets);
			boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

			for (MySubscription subscription : subscriptions) {
				if (!blacklist.contains(subscription.subscriber)) {
					postToSubscription(subscription, data, isMainThread);
				}
			}
		}
	}

	/**
	 * Post sticky the event to all subscriptions that has same id.
	 * Note that, subscription should take care of removing this sticky event.
	 */
	public <T> void postSticky(int id, T event) {
		synchronized (stickyEvents) {
			List<Object> events = stickyEvents.get(id);

			if (events == null) {
				stickyEvents.put(id, (events = new ArrayList<>()));
			}

			events.add(event);
		}

		post(id, event);
	}

	/**
	 * @return sticky events that was assigned to this id.
	 */
	public List<Object> getStickyEvents(int id) {
		synchronized (stickyEvents) {
			return stickyEvents.get(id);
		}
	}

	/**
	 * Remove all sticky events that assigned to this id.
	 */
	public void removeStickyEvents(int id) {
		synchronized (stickyEvents) {
			stickyEvents.remove(id);
		}
	}

	/**
	 * Remove a sticky event that assigned to this id.
	 */
	public synchronized void removeStickyEvent(int id, Object event) {
		List<Object> events = stickyEvents.get(id);

		if (events != null) {
			events.remove(event);
		}
	}

	/**
	 * Remove all sticky events in bus system.
	 */
	public void removeAllStickyEvents() {
		synchronized (stickyEvents) {
			stickyEvents.clear();
		}
	}

	void invokeSubscriber(MyPendingPost pp) {
		if (pp != null) {
			invokeSubscriber(pp.subscription, pp.event);
		}
	}

	<T> void invokeSubscriber(MySubscription subscription, T eventData) {
		if (subscription.active) {
			try {
				subscription.invoke(eventData);
			}
			catch (Exception e) {
				DkLogs.error(this, e);
			}
		}
	}

	private void binaryInsertionSynced(MySubscription subscription, List<MySubscription> subscriptions) {
		final int N = subscriptions.size();
		if (N == 0) {
			subscriptions.add(subscription);
			return;
		}

		int target = subscription.subscriptionMethod.priority;
		int index = 0;
		int left = 0, right = N;

		while (left <= right) {
			int mid = (left + right) >> 1;
			int value = subscriptions.get(mid).subscriptionMethod.priority;

			if (target < value) {
				right = mid - 1;
				index = mid;
			}
			else if (target > value) {
				left = index = mid + 1;
			}
			else {
				index = mid;
				break;
			}
		}

		index = Math.min(N, index); // index <= N
		subscriptions.add(index, subscription);
	}

	private MyMainPoster getMainPoster() {
		if (mainPoster == null) {
			mainPoster = new MyMainPoster(this);
		}
		return mainPoster;
	}

	private MyAsyncPoster getAsyncPoster() {
		if (asyncPoster == null) {
			asyncPoster = new MyAsyncPoster(this);
		}
		return asyncPoster;
	}

	private <T> void postToSubscription(MySubscription subscription, T eventData, boolean isMainThread) {
		switch (subscription.subscriptionMethod.threadMode) {
			case DkThreadMode.POSTER: {
				invokeSubscriber(subscription, eventData);
				break;
			}
			case DkThreadMode.ANDROID_MAIN: {
				if (isMainThread) {
					invokeSubscriber(subscription, eventData);
				}
				else {
					getMainPoster().enqueue(subscription, eventData);
				}
				break;
			}
			case DkThreadMode.ANDROID_MAIN_ORDERED: {
				getMainPoster().enqueue(subscription, eventData);
				break;
			}
			case DkThreadMode.ASYNC: {
				getAsyncPoster().post(this, subscription, eventData);
				break;
			}
			case DkThreadMode.ASYNC_ORDERED: {
				getAsyncPoster().enqueue(subscription, eventData);
				break;
			}
		}
	}
}
