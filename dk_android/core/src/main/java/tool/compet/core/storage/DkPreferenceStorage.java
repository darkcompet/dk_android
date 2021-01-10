/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

import tool.compet.core.helper.DkJsonHelper;
import tool.compet.core.helper.DkTypeHelper;
import tool.compet.core.math.DkMaths;
import tool.compet.core.log.DkLogs;

@SuppressLint("ApplySharedPref")
@SuppressWarnings("unchecked")
public class DkPreferenceStorage {
    private static DkPreferenceStorage INS;

    private final Context appContext;
    private final String defaultPrefName;
    private SharedPreferences curPref;
    private final SharedPreferences settingPref;

    private DkPreferenceStorage(Context appContext) {
        this.appContext = appContext;
        this.defaultPrefName = appContext.getPackageName().replace('.', '_') + "_dk_default_sp";
        this.settingPref = PreferenceManager.getDefaultSharedPreferences(appContext);

        this.curPref = appContext.getSharedPreferences(defaultPrefName, Context.MODE_PRIVATE);
    }

    public static void install(Context appContext) {
        if (INS == null) {
            INS = new DkPreferenceStorage(appContext);
        }
    }

    public static DkPreferenceStorage getInstalledIns() {
        if (INS == null) {
            DkLogs.complain(DkPreferenceStorage.class, "Must call install() first");
        }
        return INS;
    }

    public void switchToDefaultStorage() {
        this.curPref = appContext.getSharedPreferences(defaultPrefName, Context.MODE_PRIVATE);
    }

    public void switchToStorage(String prefName) {
        this.curPref = appContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public void switchToStorage(String prefName, int mode) {
        this.curPref = appContext.getSharedPreferences(prefName, mode);
    }

    public boolean isExist(String key) {
        return curPref.contains(key);
    }

    public boolean isExistSetting(String key) {
        return settingPref.contains(key);
    }

    public <T> void delete(Class<T> clazz) {
        if (clazz != null) {
            delete(clazz.getName());
        }
    }

    public void delete(String key) {
        curPref.edit()
            .remove(key)
            .commit();
    }

    public void save(Object obj) {
        if (obj != null) {
            save(obj.getClass().getName(), obj);
        }
    }

    /**
     * Make sure your object is convertable to json string !
     */
    public void save(String key, Object value) {
        String json;

        if (value instanceof String) {
            json = (String) value;
        }
        else {
            json = DkJsonHelper.getIns().obj2json(value);
        }

        curPref.edit()
            .putString(key, json)
            .commit();
    }

    public <T> boolean contains(Class<T> clazz) {
        return contains(clazz.getName());
    }

    public <T> boolean contains(String key) {
        return curPref.contains(key);
    }

    public <T> T load(Class<T> clazz) {
        return load(clazz.getName(), clazz);
    }

    /**
     * @param resClass when you wanna return list of object, lets pass it as ArrayOfYourModel[].class.
     */
    public <T> T load(String key, Class<T> resClass) {
        final int type = DkTypeHelper.getTypeMasked(resClass);

        switch (type) {
            case DkTypeHelper.TYPE_STRING_MASKED: {
                return (T) load(key);
            }
            case DkTypeHelper.TYPE_BOOLEAN_MASKED: {
                return (T) (Boolean) DkMaths.parseBoolean(load(key));
            }
            case DkTypeHelper.TYPE_INTEGER_MASKED: {
                return (T) (Integer) DkMaths.parseInt(load(key));
            }
            case DkTypeHelper.TYPE_LONG_MASKED: {
                return (T) (Long) DkMaths.parseLong(load(key));
            }
            case DkTypeHelper.TYPE_FLOAT_MASKED: {
                return (T) (Float) DkMaths.parseFloat(load(key));
            }
            case DkTypeHelper.TYPE_DOUBLE_MASKED: {
                return (T) (Double) DkMaths.parseDouble(load(key));
            }
        }

        return DkJsonHelper.getIns().json2obj(load(key), resClass);
    }

    public String load(String key) {
        return curPref.getString(key, "");
    }

    public void saveSetting(int key, Object value) {
        saveSetting(appContext.getString(key), value);
    }

    /**
     * For backward-compability, this will save setting value in one of 3 types: Boolean, String or Set<String>.
     * So you must only use the above types in your xml files !
     */
    public void saveSetting(String key, Object value) {
        // Check Boolean first since Android framework default doesn't allow convert String to Boolean
        if (value instanceof Boolean) {
            settingPref.edit().putBoolean(key, (Boolean) value).commit();
        }
        else if (value instanceof String) {
            settingPref.edit().putString(key, (String) value).commit();
        }
        else if (value instanceof Set) {
            settingPref.edit().putStringSet(key, (Set<String>) value).commit();
        }
        else {
            settingPref.edit().putString(key, String.valueOf(value)).commit();
        }
    }

    private String loadBooleanOrStringSetting(String key) {
        try {
            // Try load String value first
            return settingPref.getString(key, "");
        }
        catch (Exception ignore) {
            // Next load Boolean since Android framework default doesn't allow load Boolean as String
            return String.valueOf(settingPref.getBoolean(key, false));
        }
    }

    public <T> T loadSetting(int key, Class<T> resClass) {
        return loadSetting(appContext.getString(key), resClass);
    }

    public <T> T loadSetting(String key, Class<T> resClass) {
        final int type = DkTypeHelper.getTypeMasked(resClass);

        switch (type) {
            case DkTypeHelper.TYPE_STRING_MASKED: {
                return (T) loadBooleanOrStringSetting(key);
            }
            case DkTypeHelper.TYPE_BOOLEAN_MASKED: {
                return (T) (Boolean) DkMaths.parseBoolean(loadBooleanOrStringSetting(key));
            }
            case DkTypeHelper.TYPE_INTEGER_MASKED: {
                return (T) (Integer) DkMaths.parseInt(loadBooleanOrStringSetting(key));
            }
            case DkTypeHelper.TYPE_LONG_MASKED: {
                return (T) (Long) DkMaths.parseLong(loadBooleanOrStringSetting(key));
            }
            case DkTypeHelper.TYPE_FLOAT_MASKED: {
                return (T) (Float) DkMaths.parseFloat(loadBooleanOrStringSetting(key));
            }
            case DkTypeHelper.TYPE_DOUBLE_MASKED: {
                return (T) (Double) DkMaths.parseDouble(loadBooleanOrStringSetting(key));
            }
        }

        if (resClass.equals(Set.class)) {
            return (T) settingPref.getStringSet(key, null);
        }

        throw new RuntimeException("Not support load setting for class: " + resClass);
    }
}
