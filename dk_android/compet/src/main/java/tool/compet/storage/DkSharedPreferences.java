/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import tool.compet.json4j.DkJsonConverter;
import tool.compet.core.DkLogcats;
import tool.compet.core4j.DkMaths;

/**
 * This works as memory-cache (after first time of retriving data from system file).
 * For back compability, this stores all value as `String` since if we store with
 * other types (int, double...) then we will get an exception when load them with other type.
 */
@SuppressLint("ApplySharedPref")
public class DkSharedPreferences {
	protected final SharedPreferences preferences;

	public DkSharedPreferences(Context context, String prefName) {
		this.preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
	}

	public DkSharedPreferences(Context context, String prefName, int prefMode) {
		this.preferences = context.getSharedPreferences(prefName, prefMode);
	}

	public boolean exists(String key) {
		return preferences.contains(key);
	}

	//
	// Integer
	//

	public void putInt(String key, int value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
	}

	public int getInt(String key) {
		return DkMaths.parseInt(getString(key));
	}

	public int getInt(String key, int defautValue) {
		String value = getString(key);
		return value == null ? defautValue : DkMaths.parseInt(value);
	}

	public void storeInt(String key, int value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	//
	// Float
	//

	public void putFloat(String key, float value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
	}

	public float getFloat(String key) {
		return DkMaths.parseFloat(getString(key));
	}

	public float getFloat(String key, float defaultValue) {
		String value = getString(key);
		return value == null ? defaultValue : DkMaths.parseFloat(value);
	}

	public void storeFloat(String key, float value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	//
	// Double
	//

	public void putDouble(String key, double value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
	}

	public double getDouble(String key) {
		return DkMaths.parseDouble(getString(key));
	}

	public double getDouble(String key, double defaultValue) {
		String value = getString(key);
		return value == null ? defaultValue : DkMaths.parseDouble(value);
	}

	public void storeDouble(String key, double value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	//
	// Boolean
	//

	public void putBoolean(String key, boolean value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
	}

	public boolean getBoolean(String key) {
		return DkMaths.parseBoolean(getString(key));
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		String value = getString(key);
		return value == null ? defaultValue : DkMaths.parseBoolean(value);
	}

	public void storeBoolean(String key, boolean value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	//
	// Long
	//

	public void putLong(String key, long value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
	}

	public long getLong(String key) {
		return DkMaths.parseLong(getString(key));
	}

	public long getLong(String key, long defaultValue) {
		String value = getString(key);
		return value == null ? defaultValue : DkMaths.parseLong(value);
	}

	public void storeLong(String key, long value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	//
	// String
	//

	public void putString(String key, String value) {
		preferences.edit().putString(key, value).apply();
	}

	public String getString(String key) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preferences.getString(key, null);
		}
		catch (Exception e) {
			DkLogcats.error(this, e);
		}
		return null;
	}

	public String getString(String key, String defaultValue) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preferences.getString(key, defaultValue);
		}
		catch (Exception e) {
			DkLogcats.error(this, e);
		}
		return defaultValue;
	}

	public void storeString(String key, String value) {
		preferences.edit().putString(key, value).commit();
	}

	//
	// String set
	//

	public void putStringSet(String key, Set<String> values) {
		preferences.edit().putStringSet(key, values).apply();
	}

	public Set<String> getStringSet(String key) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preferences.getStringSet(key, null);
		}
		catch (Exception e) {
			DkLogcats.error(this, e);
		}
		return null;
	}

	public Set<String> getStringSet(String key, Set<String> defaultValue) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preferences.getStringSet(key, defaultValue);
		}
		catch (Exception e) {
			DkLogcats.error(this, e);
		}
		return defaultValue;
	}

	public void storeStringSet(String key, Set<String> values) {
		preferences.edit().putStringSet(key, values).commit();
	}

	//
	// Json object
	//

	public void putJsonObject(String key, Object value) {
		putString(key, DkJsonConverter.getIns().obj2json(value));
	}

	public <T> T getJsonObject(String key, Class<T> resClass) {
		return DkJsonConverter.getIns().json2obj(getString(key), resClass);
	}

	public void storeJsonObject(String key, Object value) {
		storeString(key, DkJsonConverter.getIns().obj2json(value));
	}

	//
	// CRUD
	//

	public SharedPreferences.Editor edit() {
		return preferences.edit();
	}

	public void deleteAsync(String key) {
		preferences.edit().remove(key).apply();
	}

	public void delete(String key) {
		preferences.edit().remove(key).commit();
	}

	public void clearAsync() {
		preferences.edit().clear().apply();
	}

	public void clear() {
		preferences.edit().clear().commit();
	}
}
