/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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

	public static <T> Set<T> asSet(@Nullable T[] objs) {
		if (objs == null || objs.length == 0) {
			return new HashSet<>();
		}
		Set<T> set = new HashSet<>(objs.length);
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

	public static void moveItem(@NonNull List<?> list, int fromPos, int toPos) {
		if (fromPos < toPos) {
			for (int index = fromPos; index < toPos; ++index) {
				Collections.swap(list, index, index + 1);
			}
		}
		else if (fromPos > toPos) {
			for (int index = fromPos; index > toPos; --index) {
				Collections.swap(list, index, index - 1);
			}
		}
	}

	/**
	 * Only use it if you don't care about order of items in result list.
	 */
	public static <T> void fastRemove(T item, @NonNull List<T> list) {
		fastRemove(list.indexOf(item), list);
	}

	/**
	 * Only use it if you don't care about order of items in result list.
	 */
	public static <T> void fastRemove(int index, @NonNull List<T> list) {
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

	public static <M> int sizeOf(@Nullable List<M> list) {
		return list == null ? 0 : list.size();
	}
}
