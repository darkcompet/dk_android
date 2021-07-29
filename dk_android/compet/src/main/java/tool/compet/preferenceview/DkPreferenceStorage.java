/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.preferenceview;

import java.util.Set;

public interface DkPreferenceStorage {
	boolean exists(String key);

	boolean getBoolean(String key);

	void putBoolean(String key, boolean value);

	int getInt(String key);

	void putInt(String key, int value);

	long getLong(String key);

	void putLong(String key, long value);

	float getFloat(String key);

	void putFloat(String key, float value);

	double getDouble(String key);

	void putDouble(String key, double value);

	String getString(String key);

	void putString(String key, String value);

	Set<String> getStringSet(String key);

	void putStringSet(String key, Set<String> values);

	<T> T getJsonObject(String key, Class<T> resClass);

	void putJsonObject(String key, Object value);

	void delete(String key);

	void clear();
}
