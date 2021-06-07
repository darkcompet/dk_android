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
	// Store/Load integer
	//

	public void storeInt(String key, int value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	public void applyInt(String key, int value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
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

	public void storeFloat(String key, float value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	public void applyFloat(String key, float value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
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

	public void storeDouble(String key, double value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	public void applyDouble(String key, double value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
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

	public void storeBoolean(String key, boolean value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	public void applyBoolean(String key, boolean value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
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

	public void storeLong(String key, long value) {
		preferences.edit().putString(key, String.valueOf(value)).commit();
	}

	public void applyLong(String key, long value) {
		preferences.edit().putString(key, String.valueOf(value)).apply();
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

	public void storeString(String key, String value) {
		preferences.edit().putString(key, value).commit();
	}

	public void applyString(String key, String value) {
		preferences.edit().putString(key, value).apply();
	}

	public String getString(String key) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preferences.getString(key, null);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return null;
	}

	public String getString(String key, String defaultValue) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preferences.getString(key, defaultValue);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return defaultValue;
	}

	//
	// Store/Load string set
	//

	public void storeStringSet(String key, Set<String> values) {
		preferences.edit().putStringSet(key, values).commit();
	}

	public void applyStringSet(String key, Set<String> values) {
		preferences.edit().putStringSet(key, values).apply();
	}

	public Set<String> getStringSet(String key) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preferences.getStringSet(key, null);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return null;
	}

	public Set<String> getStringSet(String key, Set<String> defaultValue) {
		try {
			// We perform try/catch to archive back-compability (load other types will cause exception)
			return preferences.getStringSet(key, defaultValue);
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

	public void storeJsonObject(String key, Object value) {
		storeString(key, DkJsonHelper.getIns().obj2json(value));
	}

	public void applyJsonObject(String key, Object value) {
		applyString(key, DkJsonHelper.getIns().obj2json(value));
	}

	//
	// CRUD
	//

	public void delete(String key) {
		preferences.edit().remove(key).commit();
	}

	public void applyDelete(String key) {
		preferences.edit().remove(key).apply();
	}

	public void clear() {
		preferences.edit().clear().commit();
	}

	public void applyClear() {
		preferences.edit().clear().apply();
	}

	public void commit() {
		preferences.edit().commit();
	}
}