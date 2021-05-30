/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

import java.util.Set;

public interface DkPreferenceStorage {
	boolean exists(String key);

	boolean getBoolean(String key);

	void storeBoolean(String key, boolean value);

	void applyBoolean(String key, boolean value);

	int getInt(String key);

	void storeInt(String key, int value);

	long getLong(String key);

	void storeLong(String key, long value);

	float getFloat(String key);

	void storeFloat(String key, float value);

	double getDouble(String key);

	void storeDouble(String key, double value);

	String getString(String key);

	void storeString(String key, String value);

	void applyString(String key, String value);

	Set<String> getStringSet(String key);

	void storeStringSet(String key, Set<String> values);

	<T> T getJsonObject(String key, Class<T> resClass);

	void storeJsonObject(String key, Object value);

	void delete(String key);

	void clear();
}
