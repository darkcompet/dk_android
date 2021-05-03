/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.DkCollections;
import tool.compet.core.DkStrings;

public class ThePreferenceManager {
	private final Context context;
	private final List<DkPreference> preferences = new ArrayList<>();
	private final DkPreferenceStorage storage;
	private final DkPreferenceListener listener;

	ThePreferenceManager(Context context, DkPreferenceStorage storage, DkPreferenceListener listener) {
		this.context = context;
		this.storage = storage;
		this.listener = listener;
	}

	/**
	 * Call this when have changes at preference list.
	 */
	public void notifyDataSetChanged() {
		listener.notifyDataSetChanged();
	}

	public int count() {
		return preferences.size();
	}

	public void clear() {
		preferences.clear();
	}

	public DkPreference findPreference(String key) {
		int index = DkCollections.findIndex(preferences, pref -> DkStrings.isEquals(key, pref.key()));
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
		int index = DkCollections.findIndex(preferences, pref -> DkStrings.isEquals(key, pref.key()));
		if (index >= 0) {
			preferences.remove(index);
		}
	}
}
