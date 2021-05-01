/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import androidx.collection.ArrayMap;

/**
 * Classify type of a class.
 * For eg,. when we have `X.class`, we can detect its primitive-type as: int, long, ...
 */
public class DkTypeHelper {
	public static final int TYPE_BOOLEAN_MASKED = 0x1;
	public static final int TYPE_BOOLEAN_OBJECT = 0x11;
	public static final int TYPE_BOOLEAN_PREMITIVE = 0x12;

	public static final int TYPE_SHORT_MASKED = 0x2;
	public static final int TYPE_SHORT_OBJECT = 0x21;
	public static final int TYPE_SHORT_PREMITIVE = 0x22;

	public static final int TYPE_INTEGER_MASKED = 0x3;
	public static final int TYPE_INTEGER_OBJECT = 0x31;
	public static final int TYPE_INTEGER_PREMITIVE = 0x32;

	public static final int TYPE_LONG_MASKED = 0x4;
	public static final int TYPE_LONG_OBJECT = 0x41;
	public static final int TYPE_LONG_PREMITIVE = 0x42;

	public static final int TYPE_FLOAT_MASKED = 0x5;
	public static final int TYPE_FLOAT_OBJECT = 0x51;
	public static final int TYPE_FLOAT_PREMITIVE = 0x52;

	public static final int TYPE_DOUBLE_MASKED = 0x6;
	public static final int TYPE_DOUBLE_OBJECT = 0x61;
	public static final int TYPE_DOUBLE_PREMITIVE = 0x62;

	public static final int TYPE_STRING_MASKED = 0x7;
	public static final int TYPE_STRING_OBJECT = 0x71;

	private static final ArrayMap<Class, Integer> types = new ArrayMap<>();

	static {
		types.put(boolean.class, TYPE_BOOLEAN_PREMITIVE);
		types.put(Boolean.class, TYPE_BOOLEAN_OBJECT);

		types.put(short.class, TYPE_SHORT_PREMITIVE);
		types.put(Short.class, TYPE_SHORT_OBJECT);

		types.put(int.class, TYPE_INTEGER_PREMITIVE);
		types.put(Integer.class, TYPE_INTEGER_OBJECT);

		types.put(long.class, TYPE_LONG_PREMITIVE);
		types.put(Long.class, TYPE_LONG_OBJECT);

		types.put(float.class, TYPE_FLOAT_PREMITIVE);
		types.put(Float.class, TYPE_FLOAT_OBJECT);

		types.put(double.class, TYPE_DOUBLE_PREMITIVE);
		types.put(Double.class, TYPE_DOUBLE_OBJECT);

		types.put(String.class, TYPE_STRING_OBJECT);
	}

	/**
	 * Detect type as primitive or object.
	 * For eg,. `int.class` will result prmitive-integer,
	 * but `Integer.class` will result object-integer.
	 */
	public static int type(Class clazz) {
		Integer val = types.get(clazz);
		return val == null ? -1 : val;
	}

	/**
	 * Detect given `type` without care of primitive or object.
	 * For eg,. both of `int.class` and `Integer.class` will result integer type.
	 */
	public static int typeMasked(Class clazz) {
		Integer val = types.get(clazz);
		return (val == null) ? -1 : val >> 4;
	}
}
