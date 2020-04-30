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

package tool.compet.database.helper;

import androidx.collection.ArrayMap;

/**
 * Checks whether a type is supported by Sqlite.
 */
public class DkSqliteSupportedTypes {
	public static final int TYPE_INTEGER = 1;
	public static final int TYPE_STRING = 2;
	public static final int TYPE_REAL = 3;

	private static final ArrayMap<Class, Integer> types = new ArrayMap<>();

	public DkSqliteSupportedTypes() {
		types.put(String.class, TYPE_STRING);

		types.put(boolean.class, TYPE_INTEGER);
		types.put(Boolean.class, TYPE_INTEGER);
		types.put(short.class, TYPE_INTEGER);
		types.put(Short.class, TYPE_INTEGER);
		types.put(int.class, TYPE_INTEGER);
		types.put(Integer.class, TYPE_INTEGER);
		types.put(long.class, TYPE_INTEGER);
		types.put(Long.class, TYPE_INTEGER);

		types.put(float.class, TYPE_REAL);
		types.put(Float.class, TYPE_REAL);
		types.put(double.class, TYPE_REAL);
		types.put(Double.class, TYPE_REAL);
	}

	public int getType(Class type) {
		Integer val = types.get(type);
		return val == null ? -1 : val;
	}
}
