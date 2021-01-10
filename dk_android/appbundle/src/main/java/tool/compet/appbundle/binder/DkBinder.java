/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.binder;

import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.log.DkLogs;

/**
 * Binds resources into fields, methods of an obj via reflection approach.
 * This uses DkReflectionFinder class to improve reflection performance.
 */
public class DkBinder {
    /**
     * Init view-field for the target. Only DkBindView annotation is supported.
     */
    public static void bindViews(Object target, View rootView) {
        List<Field> fields = DkReflectionFinder.getInstalledIns()
            .findFields(target.getClass(), DkBindView.class, true, false);

        for (Field field : fields) {
            View childView = rootView.findViewById((field.getAnnotation(DkBindView.class)).value());

            if (childView != null) {
                try {
                    field.setAccessible(true);
                    field.set(target, childView);
                }
                catch (Exception e) {
                    DkLogs.error(DkBinder.class, e);
                    DkLogs.complain(DkBinder.class, "Could not initialize field %s inside class %s",
                        field.getName(), target.getClass().getName());
                }
            }
        }
    }

    /**
     * Binds views-click into methods of target. Only DkBindClick annotation is supported.
     */
    public static void bindClicks(Object target, View rootView) {
        List<Method> methods = DkReflectionFinder.getInstalledIns()
            .findMethods(target.getClass(), DkBindClick.class, true, false);

        for (Method method : methods) {
            View childView = rootView.findViewById((method.getAnnotation(DkBindClick.class)).value());

            if (childView != null) {
                childView.setOnClickListener((v) -> {
                    try {
                        method.setAccessible(true);
                        method.invoke(target, v);
                    }
                    catch (Exception e) {
                        DkLogs.error(DkBinder.class, e);
                        DkLogs.complain(DkBinder.class, "Could not invoke no-arg method %s inside class %s",
                            method.getName(), target.getClass().getName());
                    }
                });
            }
        }
    }
}
