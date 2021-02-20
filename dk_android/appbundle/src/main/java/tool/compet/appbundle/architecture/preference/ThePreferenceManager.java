/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.storage.DkPreferenceStorage;
import tool.compet.core.util.DkCollections;

public class ThePreferenceManager {
    private final Context context;
    private final List<DkPreference> preferences = new ArrayList<>();
    private final DkPreferenceStorage storage;
    private final MyPreferenceListener listener;

    ThePreferenceManager(Context context, DkPreferenceStorage storage, MyPreferenceListener listener) {
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
        int index = DkCollections.findIndex(preferences, pref -> key.equals(((DkPreference) pref).key));
        return index < 0 ? null : preferences.get(index);
    }

    public List<DkPreference> getPreferences() {
        return preferences;
    }

    public void removePreference(String key) {
        int index = DkCollections.findIndex(preferences, pref -> key.equals(((DkPreference) pref).key));
        if (index >= 0) {
            preferences.remove(index);
        }
    }

    public TheCheckBoxPreference checkbox(String key) {
        TheCheckBoxPreference pref = new TheCheckBoxPreference(context, storage, key, listener);
        preferences.add(pref);
        return pref;
    }

    public TheSelectboxPreference selectbox(String key) {
        TheSelectboxPreference pref = new TheSelectboxPreference(context, storage, key, listener);
        preferences.add(pref);
        return pref;
    }

    public TheCustomViewPreference view(int layoutResId) {
        TheCustomViewPreference pref = new TheCustomViewPreference(context, storage, listener);
        pref.layoutResId = layoutResId;
        preferences.add(pref);
        return pref;
    }
}
