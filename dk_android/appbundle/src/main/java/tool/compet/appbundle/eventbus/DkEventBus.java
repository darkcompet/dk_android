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

import android.os.Looper;
import android.util.SparseArray;

import androidx.collection.ArrayMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.util.DkLogs;

/**
 * This library supports communication between objects, specially Android Activity/Fragment.
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
 * #Original：https://github.com/greenrobot/EventBus
 */
public class DkEventBus {
	private static DkEventBus INS;

	// Store subscriptions for each ID
	private final SparseArray<CopyOnWriteArrayList<Subscription>> subscriptions;

	// Cache subscription methods for each Class to improve performance
	private final ArrayMap<Class<?>, List<SubscriptionMethod>> subscriptionMethodCache;

	// Store sticky events for each ID. Note that, it not be remove even the Class is unregistered.
	// So you should manually remove it via removeStickyEvents().
	private final SparseArray<List<Object>> stickyEvents;

	private MainPoster mainPoster;
	private AsyncPoster asyncPoster;

	private DkEventBus() {
		subscriptions = new SparseArray<>(64);
		subscriptionMethodCache = new ArrayMap<>(64);
		stickyEvents = new SparseArray<>(64);
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
		ArrayMap<Class<?>, List<SubscriptionMethod>> methodCache = subscriptionMethodCache;
		List<SubscriptionMethod> subscriptionMethods;

		synchronized (this.subscriptionMethodCache) {
			subscriptionMethods = methodCache.get(clazz);
		}

		if (subscriptionMethods == null) {
			List<Method> methods = DkReflectionFinder.getIns()
				.findMethods(clazz, DkSubscribe.class, false, false);

			if (methods.size() > 0) {
				subscriptionMethods = new ArrayList<>();

				for (Method method : methods) {
					subscriptionMethods.add(new SubscriptionMethod(method));
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
				CopyOnWriteArrayList<Subscription> subscriptions;

				for (SubscriptionMethod subscriptionMethod : subscriptionMethods) {
					final int id = subscriptionMethod.id;
					subscriptions = this.subscriptions.get(id);

					if (subscriptions == null) {
						subscriptions = new CopyOnWriteArrayList<>();
						this.subscriptions.put(subscriptionMethod.id, subscriptions);
					}

					Subscription subscription = new Subscription(target, subscriptionMethod);
					binaryInsertionLocked(subscription, subscriptions);

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
		SparseArray<CopyOnWriteArrayList<Subscription>> cache = subscriptions;

		for (int index = 0, N = cache.size(); index < N; ++index) {
			List<Subscription> subscriptions = cache.valueAt(index);

			if (subscriptions != null) {
				for (int subIndex = subscriptions.size() - 1; subIndex >= 0; --subIndex) {
					Subscription subscription = subscriptions.get(subIndex);

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
		SparseArray<CopyOnWriteArrayList<Subscription>> cache = subscriptions;

		for (int index = 0, N = cache.size(); index < N; ++index) {
			List<Subscription> subscriptions = cache.valueAt(index);

			if (subscriptions != null) {
				for (int subIndex = subscriptions.size() - 1; subIndex >= 0; --subIndex) {
					Subscription subscription = subscriptions.get(subIndex);

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
		List<SubscriptionMethod> methods;

		synchronized (subscriptionMethodCache) {
			methods = this.subscriptionMethodCache.get(clazz);
		}

		return methods != null && methods.size() > 0;
	}

	/**
	 * Notify all subscriptions that have same id.
	 */
	public <T> void post(int id, T event) {
		CopyOnWriteArrayList<Subscription> subscriptions;

		synchronized (this.subscriptions) {
			subscriptions = this.subscriptions.get(id);
		}

		if (subscriptions != null) {
			boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

			for (Subscription subscription : subscriptions) {
				postToSubscription(subscription, event, isMainThread);
			}
		}
	}

	/**
	 * Post to given target class.
	 */
	public <T> void post(int id, Class target, T event) {
		CopyOnWriteArrayList<Subscription> subscriptions;

		synchronized (this.subscriptions) {
			subscriptions = this.subscriptions.get(id);
		}

		if (subscriptions != null) {
			boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

			for (Subscription subscription : subscriptions) {
				if (target.equals(subscription.subscriber.getClass())) {
					postToSubscription(subscription, event, isMainThread);
					break;
				}
			}
		}
	}

	/**
	 * Post to targets that not be in excepted target classes.
	 */
	public <T> void postExcept(int id, T event, Class... exceptTargetClasses) {
		CopyOnWriteArrayList<Subscription> subscriptions;

		synchronized (this.subscriptions) {
			subscriptions = this.subscriptions.get(id);
		}

		if (subscriptions != null) {
			List<Class> blacklist = Arrays.asList(exceptTargetClasses);
			boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

			for (Subscription subscription : subscriptions) {
				if (!blacklist.contains(subscription.subscriber.getClass())) {
					postToSubscription(subscription, event, isMainThread);
				}
			}
		}
	}

	/**
	 * Post to targets that not be in excepted targets.
	 */
	public <T> void postExcept(int id, T event, Object... exceptTargets) {
		CopyOnWriteArrayList<Subscription> subscriptions;

		synchronized (this.subscriptions) {
			subscriptions = this.subscriptions.get(id);
		}

		if (subscriptions != null) {
			List<Object> blacklist = Arrays.asList(exceptTargets);
			boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

			for (Subscription subscription : subscriptions) {
				if (!blacklist.contains(subscription.subscriber)) {
					postToSubscription(subscription, event, isMainThread);
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

	void invokeSubscriber(PendingPost pp) {
		if (pp != null) {
			invokeSubscriber(pp.subscription, pp.event);
		}
	}

	<T> void invokeSubscriber(Subscription subscription, T event) {
		if (subscription.active) {
			try {
				subscription.invoke(event);
			}
			catch (Exception e) {
				DkLogs.error(this, e);
			}
		}
	}

	private void binaryInsertionLocked(Subscription subscription, List<Subscription> subscriptions) {
		final int N = subscriptions.size();

		if (N == 0) {
			subscriptions.add(subscription);
			return;
		}

		int target = subscription.subscriptionMethod.priority;
		int index = 0;
		int left = 0, right = N;
		int mid, value;

		while (left <= right) {
			mid = (left + right) >> 1;
			value = subscriptions.get(mid).subscriptionMethod.priority;

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

		if (index > N) {
			index = N;
		}

		subscriptions.add(index, subscription);
	}

	private MainPoster getMainPoster() {
		if (mainPoster == null) {
			mainPoster = new MainPoster(this);
		}
		return mainPoster;
	}

	private AsyncPoster getAsyncPoster() {
		if (asyncPoster == null) {
			asyncPoster = new AsyncPoster(this);
		}
		return asyncPoster;
	}

	private <T> void postToSubscription(Subscription subscription, T event, boolean isMainThread) {
		switch (subscription.subscriptionMethod.threadMode) {
			case DkThreadMode.POSTER: {
				invokeSubscriber(subscription, event);
				break;
			}
			case DkThreadMode.MAIN: {
				if (isMainThread) {
					invokeSubscriber(subscription, event);
				}
				else {
					getMainPoster().enqueue(subscription, event);
				}
				break;
			}
			case DkThreadMode.MAIN_ORDERED: {
				getMainPoster().enqueue(subscription, event);
				break;
			}
			case DkThreadMode.ASYNC: {
				getAsyncPoster().post(this, subscription, event);
				break;
			}
			case DkThreadMode.ASYNC_ORDERED: {
				getAsyncPoster().enqueue(subscription, event);
				break;
			}
		}
	}
}
