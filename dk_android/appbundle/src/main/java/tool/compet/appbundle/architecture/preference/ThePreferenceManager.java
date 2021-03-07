/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.storage.DkStorageInf;
import tool.compet.core.util.DkCollections;
import tool.compet.core.util.DkStrings;

public class ThePreferenceManager {
    private final Context context;
    private final List<DkPreference> preferences = new ArrayList<>();
    private final DkStorageInf storage;
    private final MyPreferenceListener listener;

    ThePreferenceManager(Context context, DkStorageInf storage, MyPreferenceListener listener) {
        this.context = context;
        this.storage = storage;
        this.listener = listener;
    }

    public void notifyDataSetChanged() {
        listener.notifyDataSetChanged();
    }

    public int count() {
        return preferences.size();
    }

    public void clear() {
        preferences.clear();
    }

    public DkPreference getPreference(String key) {
        int index = DkCollections.findIndex(preferences, pref -> DkStrings.isEquals(key, ((DkPreference) pref).key));
        return index < 0 ? null : preferences.get(index);
    }

    public List<DkPreference> getPreferences() {
        return preferences;
    }

    public ThePreferenceManager addPreference(DkPreference preference) {
        preference.init(context, storage, listener);
        preferences.add(preference);
        return this;
    }

    public void removePreference(String key) {
        int index = DkCollections.findIndex(preferences, pref -> DkStrings.isEquals(key, ((DkPreference) pref).key));
        if (index >= 0) {
            preferences.remove(index);
        }
    }
}
