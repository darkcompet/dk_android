/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import java.util.Set;

public interface DkPreferenceStorage {
	boolean exists(String key);

	boolean getBoolean(String key);

	void setBoolean(String key, boolean value);

	int getInt(String key);

	void setInt(String key, int value);

	long getLong(String key);

	void setLong(String key, long value);

	float getFloat(String key);

	void setFloat(String key, float value);

	double getDouble(String key);

	void setDouble(String key, double value);

	String getString(String key);

	void setString(String key, String value);

	Set<String> getStringSet(String key);

	void setStringSet(String key, Set<String> values);

	<T> T getJsonObject(String key, Class<T> resClass);

	void setJsonObject(String key, Object value);

	void delete(String key);

	void clear();
}
