/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import tool.compet.core.DkLogs;
import tool.compet.core.DkMaths;

/**
 * This works as memory-cache (after first time of retriving data from system file).
 * For back compability, this stores all value as `String` since if we store with
 * other types (int, double...) then we will get an exception when load them with other type.
 */
@SuppressLint("ApplySharedPref")
public abstract class DkPreferenceStorageCompat implements DkStorageInf {
	protected final SharedPreferences preference;

	public DkPreferenceStorageCompat(Context context, String prefName, int prefMode) {
		this.preference = context.getSharedPreferences(prefName, prefMode);
	}

	@Override
	public boolean exists(String key) {
		return preference.contains(key);
	}

	//
	// Store/Load integer
	//

	@Override
	public void setInt(String key, int value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setIntAsync(String key, int value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	@Override
	public int getInt(String key) {
		return DkMaths.parseInt(getString(key));
	}

	//
	// Store/Load float
	//

	@Override
	public void setFloat(String key, float value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setFloatAsync(String key, float value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	@Override
	public float getFloat(String key) {
		return DkMaths.parseFloat(getString(key));
	}

	//
	// Store/Load double
	//

	@Override
	public void setDouble(String key, double value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setDoubleAsync(String key, double value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	@Override
	public double getDouble(String key) {
		return DkMaths.parseDouble(getString(key));
	}

	//
	// Store/Load boolean
	//

	@Override
	public void setBoolean(String key, boolean value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setBooleanAsync(String key, boolean value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	@Override
	public boolean getBoolean(String key) {
		return DkMaths.parseBoolean(getString(key));
	}

	//
	// Store/Load long
	//

	@Override
	public void setLong(String key, long value) {
		preference.edit().putString(key, String.valueOf(value)).commit();
	}

	public void setLongAsync(String key, long value) {
		preference.edit().putString(key, String.valueOf(value)).apply();
	}

	@Override
	public long getLong(String key) {
		return DkMaths.parseLong(getString(key));
	}

	//
	// Store/Load string
	//

	@Override
	public void setString(String key, String value) {
		preference.edit().putString(key, value).commit();
	}

	public void setStringAsync(String key, String value) {
		preference.edit().putString(key, value).apply();
	}

	@Override
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

	//
	// Store/Load string set
	//

	@Override
	public void setStringSet(String key, Set<String> values) {
		preference.edit().putStringSet(key, values).commit();
	}

	public void setStringSetAsync(String key, Set<String> values) {
		preference.edit().putStringSet(key, values).apply();
	}

	@Override
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

	//
	// CRUD
	//

	@Override
	public void delete(String key) {
		preference.edit().remove(key).commit();
	}

	public void deleteAsync(String key) {
		preference.edit().remove(key).apply();
	}

	@Override
	public void clear() {
		preference.edit().clear().commit();
	}

	public void clearAsync() {
		preference.edit().clear().apply();
	}
}
