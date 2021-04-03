/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.reflection;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.List;

import tool.compet.core.DkCaller1;
import tool.compet.core.DkCaller2;
import tool.compet.core.DkLogs;
import tool.compet.core.DkStrings;

public class DkFieldCopier {
	public static void copy(Object src, Object dst) {
		copy(src, dst, true, null, null);
	}

	public static void copy(Object src, Object dst, boolean upSuper) {
		copy(src, dst, upSuper, null, null);
	}

	public static void copy(Object src, Object dst, boolean upSuper, @Nullable DkCaller1<String, Boolean> filterCallback) {
		copy(src, dst, upSuper, filterCallback, null);
	}

	/**
	 * Copy all fields which be annotated with {@link SerializedName} from an object to other object.
	 *
	 * @param src               From object.
	 * @param dst               To object.
	 * @param upSuper           True to include super fields, False to exclude super fields.
	 * @param filterCallback	Before set, we tell caller know the field will be ignored for copy
	 * @param setCallback 		Determine on field values whether we should set value from srcField to dstField.
	 */
	public static void copy(Object src, Object dst, boolean upSuper,
		@Nullable DkCaller1<String, Boolean> filterCallback,
		@Nullable DkCaller2<Object, Object, Boolean> setCallback) {

		DkReflectionFinder finder = DkReflectionFinder.getIns();
		List<Field> srcFields = finder.findFields(src.getClass(), SerializedName.class, upSuper);
		List<Field> dstFields = finder.findFields(dst.getClass(), SerializedName.class, upSuper);

		ArrayMap<String, Field> srcFieldMap = new ArrayMap<>();
		ArrayMap<String, Field> dstFieldMap = new ArrayMap<>();

		for (Field f : srcFields) {
			srcFieldMap.put(f.getName(), f);
		}
		for (Field f : dstFields) {
			dstFieldMap.put(f.getName(), f);
		}

		final int N = srcFieldMap.size();

		if (N != dstFieldMap.size()) {
			DkLogs.warning(DkFieldCopier.class, "Different number of copy fields between %s(%d) vs %s(%d)",
				src.getClass().getName(), srcFields.size(), dst.getClass().getName(), dstFields.size());
		}

		for (int index = N - 1; index >= 0; --index) {
			String fieldName = srcFieldMap.keyAt(index);

			if (filterCallback != null && filterCallback.call(fieldName)) {
				continue;
			}

			Field srcField = srcFieldMap.valueAt(index);
			Field dstField = dstFieldMap.get(fieldName);

			if (dstField != null) {
				try {
					srcField.setAccessible(true);
					dstField.setAccessible(true);

					if (setCallback == null) {
						dstField.set(dst, srcField.get(src));
					}
					else {
						Object srcFieldValue = srcField.get(src);

						if (setCallback.call(srcFieldValue, dstField.get(dst))) {
							dstField.set(dst, srcFieldValue);
						}
					}
				}
				catch (Exception e) {
					DkLogs.error(DkFieldCopier.class, e);
					throw new RuntimeException(DkStrings.format("Could not copy field: %s.%s -> %s.%s",
						src.getClass().getName(), srcField.getName(), dst.getClass().getName(), dstField.getName()));
				}
			}
		}
	}
}
