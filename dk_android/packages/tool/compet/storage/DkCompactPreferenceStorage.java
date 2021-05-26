/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import tool.compet.core.DkJsonHelper;
import tool.compet.core.DkLogs;
import tool.compet.core.DkMaths;

/**
 * This works as memory-cache (after first time of retriving data from system file).
 * For back compability, this stores all value as `String` since if we store with
 * other types (int, double...) then we will get an exception when load them with other type.
 */
@SuppressLint("ApplySharedPref")
public class DkCompactPreferenceStorage {
	protected final SharedPreferences preference;

	public DkCompactPreferenceStorage(Context context, String prefName) {
		this.preference = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
	}

	public DkCompactPreferenceStorage(Context context, String prefName, int prefMode) {
		this.preference = context.getSharedPreferences(prefName, prefMode);
	}

	public boolean exists(String key) {
		return preference.contains(key);
	}

	//
	// Store/Load integer
	//

	public void setInt(String key, int value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setIntAsync(String key, int value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	public int getInt(String key) {
		return DkMaths.parseInt(getString(key));
	}

	public int getInt(String key, int defautValue) {
		String value = getString(key);
		return value == null ? defautValue : DkMaths.parseInt(value);
	}

	//
	// Store/Load float
	//

	public void setFloat(String key, float value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setFloatAsync(String key, float value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	public float getFloat(String key) {
		return DkMaths.parseFloat(getString(key));
	}

	public float getFloat(String key, float defaultValue) {
		String value = getString(key);
		return value == null ? defaultValue : DkMaths.parseFloat(value);
	}

	//
	// Store/Load double
	//

	public void setDouble(String key, double value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setDoubleAsync(String key, double value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	public double getDouble(String key) {
		return DkMaths.parseDouble(getString(key));
	}

	public double getDouble(String key, double defaultValue) {
		String value = getString(key);
		return value == null ? defaultValue : DkMaths.parseDouble(value);
	}

	//
	// Store/Load boolean
	//

	public void setBoolean(String key, boolean value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setBooleanAsync(String key, boolean value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	public boolean getBoolean(String key) {
		return DkMaths.parseBoolean(getString(key));
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		String value = getString(key);
		return value == null ? defaultValue : DkMaths.parseBoolean(value);
	}

	//
	// Store/Load long
	//

	public void setLong(String key, long value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setLongAsync(String key, long value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	public long getLong(String key) {
		return DkMaths.parseLong(getString(key));
	}

	public long getLong(String key, long defaultValue) {
		String value = getString(key);
		return value == null ? defaultValue : DkMaths.parseLong(value);
	}

	//
	// Store/Load string
	//

	public void setString(String key, String value) {
		preference.edit().putString(key, value).commit();
	}

	public void setStringAsync(String key, String value) {
		preference.edit().putString(key, value).apply();
	}

	public String getString(String key) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preference.getString(key, null);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return null;
	}

	public String getString(String key, String defaultValue) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preference.getString(key, defaultValue);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return defaultValue;
	}

	//
	// Store/Load string set
	//

	public void setStringSet(String key, Set<String> values) {
		preference.edit().putStringSet(key, values).commit();
	}

	public void setStringSetAsync(String key, Set<String> values) {
		preference.edit().putStringSet(key, values).apply();
	}

	public Set<String> getStringSet(String key) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preference.getStringSet(key, null);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return null;
	}

	public Set<String> getStringSet(String key, Set<String> defaultValue) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preference.getStringSet(key, defaultValue);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return defaultValue;
	}

	//
	// Store/Load json
	//

	public <T> T getJsonObject(String key, Class<T> resClass) {
		return DkJsonHelper.getIns().json2obj(getString(key), resClass);
	}

	public void setJsonObject(String key, Object value) {
		setString(key, DkJsonHelper.getIns().obj2json(value));
	}

	//
	// CRUD
	//

	public void delete(String key) {
		preference.edit().remove(key).commit();
	}

	public void deleteAsync(String key) {
		preference.edit().remove(key).apply();
	}

	public void clear() {
		preference.edit().clear().commit();
	}

	public void clearAsync() {
		preference.edit().clear().apply();
	}

	public void commit() {
		preference.edit().commit();
	}
}