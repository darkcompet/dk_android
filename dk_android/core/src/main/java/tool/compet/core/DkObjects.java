/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class, provides common basic operations on object.
 */
public class DkObjects {
	public static void requireNonNull(Object obj) {
		if (obj == null) throw new RuntimeException("Must be non-null");
	}

	public static void requireNonNull(Object obj, String format, Object... args) {
		if (obj == null) throw new RuntimeException(DkStrings.format(format, args));
	}

	/**
	 * Check null or zero-size.
	 *
	 * @param obj Object Any
	 * @return boolean true if given object is null, empty string, 0-size array, list...
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) return true;
		if (obj instanceof CharSequence) return ((CharSequence) obj).length() == 0;
		if (obj instanceof Collection) return ((Collection) obj).size() == 0;
		if (obj instanceof Map) return ((Map) obj).size() == 0;
		if (obj.getClass().isArray()) return Array.getLength(obj) == 0;

		return false;
	}

	public static boolean equals(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}
}
