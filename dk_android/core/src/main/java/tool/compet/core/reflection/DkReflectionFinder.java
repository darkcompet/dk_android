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
    private final String[] searchPrefixPackages;

    private DkReflectionFinder(String... searchPrefixPackages) {
        if (searchPrefixPackages == null) {
            searchPrefixPackages = new String[] {"tool.compet"};
        }
        this.searchPrefixPackages = searchPrefixPackages;
        this.fieldCache = new ArrayMap<>();
        this.methodCache = new ArrayMap<>();
    }

    public static void install(String... prefixSearchPackages) {
        if (INS == null) {
            INS = new DkReflectionFinder(prefixSearchPackages);
        }
    }

    public static DkReflectionFinder getIns() {
        if (INS == null) {
            throw new RuntimeException("Must call install() first");
        }
        return INS;
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
     * By default, it does not cache result.
     */
    @NonNull
    public List<Field> findFields(Class clazz, Class<? extends Annotation> annotation, boolean upSuper) {
        return findFields(clazz, annotation, upSuper, false);
    }

    /**
     * Find fields which be annotated with given #annotation inside a class.
     */
    @NonNull
    public List<Field> findFields(Class clazz, Class<? extends Annotation> annotation, boolean upSuper, boolean cache) {
        // Lookup cache first
        String key = keyOf(clazz, annotation);
        List<Field> fields = fieldCache.get(key);

        // Since result of this method is not null -> just check existence by != null
        if (fields != null) {
            return fields;
        }

        // Not found in cache, start search and cache
        fields = new MyFinder()
            .findFields(clazz, Collections.singletonList(annotation), upSuper, searchPrefixPackages)
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
     * Find methods which be annotated with given #annotation inside a class.
     * By default, it also look up super class fields, and does not cache result.
     */
    @NonNull
    public List<Method> findMethods(Class clazz, Class<? extends Annotation> annotation) {
        return findMethods(clazz, annotation, true, false);
    }

    /**
     * Find methods which be annotated with given #annotation inside a class.
     * By default, it does not cache result.
     */
    @NonNull
    public List<Method> findMethods(Class clazz, Class<? extends Annotation> annotation, boolean upSuper) {
        return findMethods(clazz, annotation, upSuper, false);
    }

    /**
     * Find methods which be annotated with given #annotation inside a class.
     */
    @NonNull
    public List<Method> findMethods(Class clazz, Class<? extends Annotation> annotation, boolean upSuper, boolean cache) {
        // Lookup cache first
        String key = keyOf(clazz, annotation);
        List<Method> methods = methodCache.get(key);

        // Since result of this method is not null -> just check existence by != null
        if (methods != null) {
            return methods;
        }

        // Not found in cache, start search
        methods = new MyFinder()
            .findMethods(clazz, Collections.singletonList(annotation), upSuper, searchPrefixPackages)
            .get(annotation);

        if (methods == null) {
            methods = Collections.emptyList();
        }
        if (cache) {
            methodCache.put(key, methods);
        }

        return methods;
    }

    //
    // Private region
    //

    /**
     * Calculate cache-key for a annotation of given class.
     */
    private static String keyOf(Class clazz, Class<? extends Annotation> annotation) {
        return clazz.getName() + "_" + annotation.getName();
    }
}
