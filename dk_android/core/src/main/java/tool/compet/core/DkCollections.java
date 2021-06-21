/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import androidx.annotation.Nullable;
import androidx.collection.ArraySet;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This class, provides common basic operations on a collection.
 */
public class DkCollections {
	// Check emptiness of List, Set ... which is subclass of Collection
	public static boolean empty(Collection<?> collection) {
		return collection == null || collection.size() == 0;
	}

	public static <M> int sizeOf(@Nullable List<M> list) {
		return list == null ? 0 : list.size();
	}

	public static <T> Set<T> asSet(@Nullable T[] objs) {
		if (objs == null || objs.length == 0) {
			return new ArraySet<>();
		}
		Set<T> set = new ArraySet<>(objs.length);
		set.addAll(Arrays.asList(objs));
		return set;
	}

	public static <T> boolean contains(T target, Iterable<T> iterable) {
		if (iterable != null) {
			for (T item : iterable) {
				if (target.equals(item)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Only use it if you don't care about order of items in result list.
	 */
	public static <T> void fastRemove(T item, List<T> list) {
		fastRemove(list.indexOf(item), list);
	}

	/**
	 * Only use it if you don't care about order of items in result list.
	 */
	public static <T> void fastRemove(int index, List<T> list) {
		int lastIndex = list.size() - 1;

		if (index >= 0 && index <= lastIndex) {
			if (index < lastIndex) {
				list.set(index, list.get(lastIndex));
			}
			list.remove(lastIndex);
		}
	}

	public static <T> void addIfAbsent(List<T> list, T obj) {
		if (!list.contains(obj)) {
			list.add(obj);
		}
	}

	public static <T> int findIndex(List<T> list, DkCaller1<T, Boolean> condition) {
		for (int index = list.size() - 1; index >= 0; --index) {
			if (condition.call(list.get(index))) {
				return index;
			}
		}
		return -1;
	}
}
