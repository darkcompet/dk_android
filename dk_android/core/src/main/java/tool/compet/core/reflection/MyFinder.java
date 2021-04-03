/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.reflection;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Find fields, methods... with reflection approach.
 */
public class MyFinder {
	/**
	 * Search fields of given class (and super class if optioned) by reflection.
	 *
	 * @param clazz          target class.
	 * @param annotations    if a field has one of this annotations, then target it.
	 * @param upSuper        search super class if true.
	 * @param searchPackages packages to search, skip search if a class is outside of it.
	 * @return map of each annotation with list of methods which annotated with that annotation.
	 */
	@NonNull
	Map<Class<? extends Annotation>, List<Field>> findFields
	(Class clazz, Iterable<Class<? extends Annotation>> annotations, boolean upSuper, String[] searchPackages) {
		Map<Class<? extends Annotation>, List<Field>> result = new ArrayMap<>();

		if (clazz != null && isValidTarget(clazz, searchPackages)) {
			collectFieldsOf(clazz, annotations, result);

			if (upSuper) {
				Map<Class<? extends Annotation>, List<Field>> superResult = findFields(
					clazz.getSuperclass(),
					annotations,
					true,
					searchPackages);

				mergeWithSuperResult(annotations, superResult, result);
			}
		}

		return result;
	}

	/**
	 * Search methods of given class (and super class if optioned) by reflection.
	 *
	 * @param clazz          target class.
	 * @param annotations    if a method has one of this annotations, then target it.
	 * @param upSuper        search super class if true.
	 * @param searchPackages packages to search, skip search if a class is outside of it.
	 * @return map of each annotation with list of methods which annotated with that annotation.
	 */
	@NonNull
	Map<Class<? extends Annotation>, List<Method>> findMethods
	(Class clazz, Iterable<Class<? extends Annotation>> annotations, boolean upSuper, String[] searchPackages) {
		Map<Class<? extends Annotation>, List<Method>> result = new ArrayMap<>();

		if (clazz != null && isValidTarget(clazz, searchPackages)) {
			collectMethodsOf(clazz, annotations, result);

			if (upSuper) {
				Map<Class<? extends Annotation>, List<Method>> superResult = findMethods(
					clazz.getSuperclass(),
					annotations,
					true,
					searchPackages);

				mergeWithSuperResult(annotations, superResult, result);
			}
		}

		return result;
	}

	/**
	 * In given class, collect fields which be annotated with one of given annotations.
	 */
	private void collectFieldsOf
	(Class clazz, Iterable<Class<? extends Annotation>> annotations, Map<Class<? extends Annotation>, List<Field>> result) {
		for (Class<? extends Annotation> annotation : annotations) {
			List<Field> fields = result.get(annotation);

			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotation)) {
					if (fields == null) {
						fields = new ArrayList<>();
						result.put(annotation, fields);
					}
					fields.add(field);
				}
			}
		}
	}

	/**
	 * In given class, collect methods which be annotated with one of given annotations.
	 */
	private void collectMethodsOf
	(Class clazz, Iterable<Class<? extends Annotation>> annotations, Map<Class<? extends Annotation>, List<Method>> result) {
		for (Class<? extends Annotation> annotation : annotations) {
			List<Method> methods = result.get(annotation);

			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(annotation)) {
					if (methods == null) {
						methods = new ArrayList<>();
						result.put(annotation, methods);
					}
					methods.add(method);
				}
			}
		}
	}

	/**
	 * Check whether #clazz is inside #packages.
	 */
	private boolean isValidTarget(Class clazz, String[] packages) {
		if (packages != null) {
			String name = clazz.getName();

			for (String pkg : packages) {
				if (name.startsWith(pkg)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Merge #superResult to #result.
	 */
	private static <T> void mergeWithSuperResult
	(Iterable<Class<? extends Annotation>> annotations, Map<Class<? extends Annotation>, List<T>> superResult, Map<Class<? extends Annotation>, List<T>> result) {
		for (Class<? extends Annotation> annotation : annotations) {
			List<T> superItems = superResult.get(annotation);

			if (superItems != null) {
				List<T> items = result.get(annotation);

				if (items == null) {
					items = new ArrayList<>();
					result.put(annotation, items);
				}

				items.addAll(superItems);
			}
		}
	}
}
