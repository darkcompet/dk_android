/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import tool.compet.core.helper.DkJsonHelper;
import tool.compet.core.log.DkLogs;
import tool.compet.core.math.DkMaths;

/**
 * This works as memory-cache (after first time of retriving data from system file).
 * For back compability, this stores all value as `String` since if we store with
 * other specific types (int, double...) then we will get an exception when load them as other types.
 */
@SuppressLint("ApplySharedPref")
public class DkPreferenceStorage {
    private final SharedPreferences preference;

    private DkPreferenceStorage(Context context, String prefName, int prefMode) {
        this.preference = context.getSharedPreferences(prefName, prefMode);
    }

    //
    // Constructor
    //

    public static DkPreferenceStorage getStorage(Context context, String prefName) {
        return new DkPreferenceStorage(context, prefName, Context.MODE_PRIVATE);
    }

    public static DkPreferenceStorage getStorage(Context context, String prefName, int prefMode) {
        return new DkPreferenceStorage(context, prefName, prefMode);
    }

    public boolean exists(String key) {
        return preference.contains(key);
    }

    //
    // Store/Load integer
    //

    public void storeInt(String key, int value) {
        preference.edit().putString(key, String.valueOf(value)).commit();
    }

    public int loadInt(String key) {
        return DkMaths.parseInt(loadString(key));
    }

    //
    // Store/Load float
    //

    public void storeFloat(String key, float value) {
        preference.edit().putString(key, String.valueOf(value)).commit();
    }

    public float loadFloat(String key) {
        return DkMaths.parseFloat(loadString(key));
    }

    //
    // Store/Load double
    //

    public void storeDouble(String key, double value) {
        preference.edit().putString(key, String.valueOf(value)).commit();
    }

    public double loadDouble(String key) {
        return DkMaths.parseDouble(loadString(key));
    }

    //
    // Store/Load boolean
    //

    public void storeBoolean(String key, boolean value) {
        preference.edit().putString(key, String.valueOf(value)).commit();
    }

    public boolean loadBoolean(String key) {
        return DkMaths.parseBoolean(loadString(key));
    }

    //
    // Store/Load long
    //

    public void storeLong(String key, long value) {
        preference.edit().putString(key, String.valueOf(value)).commit();
    }

    public long loadLong(String key) {
        return DkMaths.parseLong(loadString(key));
    }

    //
    // Store/Load string
    //

    public void storeString(String key, String value) {
        preference.edit().putString(key, value).commit();
    }

    public String loadString(String key) {
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

    public void storeStringSet(String key, Set<String> values) {
        preference.edit().putStringSet(key, values).commit();
    }

    public Set<String> loadStringSet(String key) {
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
    // Store/Load json object (annotated with @Expose and @SerializedName)
    //

    /**
     * @param value Should contain @Expose and @SerializedName annotations
     */
    public void storeJsonObject(String key, Object value) {
        preference.edit().putString(key, DkJsonHelper.getIns().obj2json(value)).commit();
    }

    /**
     * @param resClass Should contain @Expose and @SerializedName annotations
     */
    public <T> T loadJsonObject(String key, Class<T> resClass) {
        return DkJsonHelper.getIns().json2obj(loadString(key), resClass);
    }

    //
    // CRUD
    //

    public void delete(String key) {
        preference.edit().remove(key).commit();
    }

    public void clear() {
        preference.edit().clear().commit();
    }
}
