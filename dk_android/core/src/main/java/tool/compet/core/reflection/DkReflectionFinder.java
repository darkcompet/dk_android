/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.reflection;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Find fields or methods which annotated with specified annotations in class.
 * Note that, this class supports cache optimization.
 */
public class DkReflectionFinder {
    private static DkReflectionFinder INS;

    private final Map<String, List<Field>> fieldCache;
    private final Map<String, List<Method>> methodCache;

    // For reset search packages at runtime
    private final String[] searchPackages;

    private DkReflectionFinder(String... searchPackages) {
        if (searchPackages == null) {
            searchPackages = new String[] {"tool.compet"};
        }
        this.searchPackages = searchPackages;
        this.fieldCache = new ArrayMap<>();
        this.methodCache = new ArrayMap<>();
    }

    public static void install(String... searchPackages) {
        if (INS == null) {
            synchronized (DkReflectionFinder.class) {
                if (INS == null) {
                    INS = new DkReflectionFinder(searchPackages);
                }
            }
        }
    }

    public static DkReflectionFinder getIns() {
        if (INS == null) {
            throw new RuntimeException("Must call install() first");
        }
        return INS;
    }

    /**
     * Calculate cache-key for a annotation of given class.
     */
    private static String keyOf(Class clazz, Class<? extends Annotation> annotation) {
        return clazz.getName() + "_" + annotation.getName();
    }

    /**
     * From #fieldsMap of #clazz, get field-list of #annotation.
     */
    @NonNull
    public static List<Field> extractFields(Class<? extends Annotation> annotation, Class clazz, ArrayMap<String, List<Field>> fieldsMap) {
        String key = keyOf(clazz, annotation);
        List<Field> fields = fieldsMap.get(key);

        return fields != null ? fields : Collections.emptyList();
    }

    /**
     * From #methodsMap of #clazz, get method-list of #annotation.
     */
    @NonNull
    public static List<Method> extractMethods(Class<? extends Annotation> annotation, Class clazz, ArrayMap<String, List<Method>> methodsMap) {
        String key = keyOf(clazz, annotation);
        List<Method> methods = methodsMap.get(key);

        return methods != null ? methods : Collections.emptyList();
    }

    /**
     * Find fields which be annotated with given #annotation inside a class.
     * By default, it also look up super class fields, and does not cache result.
     */
    @NonNull
    public List<Field> findFields(Class clazz, Class<? extends Annotation> annotation) {
        return findFields(clazz, annotation, true, false);
    }

    /**
     * Find fields which be annotated with given #annotation inside a class.
     */
    @NonNull
    public List<Field> findFields(Class clazz, Class<? extends Annotation> annotation, boolean upSuper, boolean cache) {
        // Lookup cache first
        String key = keyOf(clazz, annotation);
        List<Field> fields = fieldCache.get(key);

        if (fields != null) {
            return fields;
        }

        // Not found in cache, start search and cache
        fields = new MyFinder()
            .findFields(clazz, Collections.singletonList(annotation), upSuper, searchPackages)
            .get(annotation);

        if (fields == null) {
            fields = Collections.emptyList();
        }
        if (cache) {
            fieldCache.put(key, fields);
        }

        return fields;
    }

    /**
     * @return map which {@code key} is {@link DkReflectionFinder#keyOf(Class, Class)} and
     * {@code value} is field list of that annoClass. To get fields of a annoClass, consider use
     * {@link DkReflectionFinder#extractFields(Class, Class, ArrayMap)}.
     */
    @NonNull
    public ArrayMap<String, List<Field>> findFields(Class clazz, Iterable<Class<? extends Annotation>> annotations, boolean upSuper, boolean cache) {
        ArrayMap<String, List<Field>> result = new ArrayMap<>();

        for (Class<? extends Annotation> annoClass : annotations) {
            // Lookup cache for this annotation first
            String key = keyOf(clazz, annoClass);
            List<Field> fields = fieldCache.get(key);

            // Not found cache, start find
            if (fields == null) {
                fields = findFields(clazz, annoClass, upSuper, cache);
            }

            result.put(key, fields);
        }

        return result;
    }

    /**
     * Find methods which be annotated with given #annotation inside a class.
     */
    @NonNull
    public List<Method> findMethods(Class clazz, Class<? extends Annotation> annotation, boolean upSuper, boolean cache) {
        // Lookup cache first
        String key = keyOf(clazz, annotation);
        List<Method> methods = methodCache.get(key);

        if (methods != null) {
            return methods;
        }

        // Not found in cache, start search
        methods = new MyFinder()
            .findMethods(clazz, Collections.singletonList(annotation), upSuper, searchPackages)
            .get(annotation);

        if (methods == null) {
            methods = Collections.emptyList();
        }
        if (cache) {
            methodCache.put(key, methods);
        }

        return methods;
    }

    /**
     * @return map which {@code key} is {@link DkReflectionFinder#keyOf(Class, Class)} and
     * {@code value} is field list of that annoClass. To get methods of a annoClass, consider use
     * {@link DkReflectionFinder#extractMethods(Class, Class, ArrayMap)}.
     */
    @NonNull
    public ArrayMap<String, List<Method>> findMethods(Class clazz, Iterable<Class<? extends Annotation>> annotations, boolean upSuper, boolean cache) {
        ArrayMap<String, List<Method>> result = new ArrayMap<>();

        for (Class<? extends Annotation> annoClass : annotations) {
            // Lookup cache for this annotation
            String key = keyOf(clazz, annoClass);
            List<Method> methods = methodCache.get(key);

            // Not found cache, start find
            if (methods == null) {
                methods = findMethods(clazz, annoClass, upSuper, cache);
            }

            result.put(key, methods);
        }

        return result;
    }
}
