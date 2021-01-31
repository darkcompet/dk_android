/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.reflection;

import androidx.collection.ArrayMap;
import androidx.collection.ArraySet;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import tool.compet.core.log.DkLogs;
import tool.compet.core.util.DkStrings;

public class DkFieldCopier {
    public static void copy(Object src, Object dst) {
        copy(src, dst, true);
    }

    public static void copy(Object src, Object dst, boolean upSuper) {
        copy(src, dst, upSuper, new ArraySet<>());
    }

    /**
     * Copy all fields which be annotated with {@link SerializedName} from an object to other object.
     *
     * @param src From object.
     * @param dst To object.
     * @param upSuper True to include super fields, False to exclude super fields.
     * @param excludeFieldNames Fields should not be copied from src to dst.
     */
    public static void copy(Object src, Object dst, boolean upSuper, Collection<String> excludeFieldNames) {
        excludeFieldNames = new ArraySet<>(excludeFieldNames);

        DkReflectionFinder finder = DkReflectionFinder.getIns();
        List<Field> srcFields = finder.findFields(src.getClass(), SerializedName.class, upSuper, false);
        List<Field> dstFields = finder.findFields(dst.getClass(), SerializedName.class, upSuper, false);

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
            DkLogs.warn(DkFieldCopier.class, "Different number of copy fields between %s(%d) vs %s(%d)",
                src.getClass().getName(), srcFields.size(), dst.getClass().getName(), dstFields.size());
        }

        for (int index = N - 1; index >= 0; --index) {
            String fieldName = srcFieldMap.keyAt(index);

            if (excludeFieldNames.contains(fieldName)) {
                continue;
            }

            Field srcField = srcFieldMap.valueAt(index);
            Field dstField = dstFieldMap.get(fieldName);

            if (dstField != null) {
                try {
                    srcField.setAccessible(true);
                    dstField.setAccessible(true);

                    dstField.set(dst, srcField.get(src));
                }
                catch (Exception e) {
                    DkLogs.error(DkFieldCopier.class, e);
                    throw new RuntimeException(DkStrings.format("Could not copy: %s.%s -> %s.%s",
                        src.getClass().getName(), srcField.getName(), dst.getClass().getName(), dstField.getName()));
                }
            }
        }
    }
}
