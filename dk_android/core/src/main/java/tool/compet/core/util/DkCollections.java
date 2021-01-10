/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.util;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * This class, provides common basic operations on a collection.
 */
public class DkCollections {
    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
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
}
