/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.core.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;

/**
 * Reflection utility class.
 */
public class DkReflections {
	/**
	 * Get class object from given class name.
	 *
	 * @param className path to class {@code "package.Class"}, like: {@code "java.lang.String", "tool.core.reflection.DkReflections"}.
	 *
	 * @return class which represents for given name.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClass(String className) {
		try {
			return (Class<T>) Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Get class of generic type of return type of given method.
	 *
	 * @return for eg, given method as {@code List<SparseArray<String>> foo()}, then
	 *    return class will be {@code SparseArray<String>.class}.
	 *
	 * @see tool.compet.core.reflection.DkReflections#getLastGenericReturnClass(Method)
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getGenericReturnClass(Method method) {
		Type type = method.getGenericReturnType();

		if (type instanceof ParameterizedType) {
			Type[] typeArgs = ((ParameterizedType) type).getActualTypeArguments();

			if (typeArgs.length > 0) {
				return (Class<T>) typeArgs[0];
			}
		}

		return null;
	}

	/**
	 * Get last class of generic type of return type of given method.
	 *
	 * @return for eg, given method is {@code List<SparseArray<String>> foo()}, then
	 *    return class will be {@code String.class}.
	 *
	 * @see tool.compet.core.reflection.DkReflections#getGenericReturnClass(Method)
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getLastGenericReturnClass(Method method) {
		Type type = method.getGenericReturnType();

		if (type instanceof ParameterizedType) {
			Type[] typeArgs = ((ParameterizedType) type).getActualTypeArguments();

			while (typeArgs.length > 0) {
				type = typeArgs[0];

				if (type instanceof ParameterizedType) {
					typeArgs = ((ParameterizedType) type).getActualTypeArguments();
				}
				else {
					return (Class<T>) type;
				}
			}
		}

		return null;
	}
}
