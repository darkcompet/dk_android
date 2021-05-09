/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

import android.content.Context;

import androidx.annotation.Nullable;

@SuppressWarnings("unchecked")
abstract class MyBasePreference<P> implements DkPreference<P> {
	// Control enabled/disabled state by this
	protected boolean enabled = true;

	protected String groupId; // id of group which contains this preference
	protected String key; // preference key in storage

	// Init below fields when add a preference
	protected Context context; // to get resource value
	protected DkPreferenceStorage storage; // to store setting
	protected DkPreferenceListener listener; // to callback when something changed

	MyBasePreference(@Nullable String key) {
		this.key = key;
	}

	@Override
	public CharSequence key() {
		return this.key;
	}

	/**
	 * Pass more data when add a preference.
	 */
	@Override
	public void init(Context context, DkPreferenceStorage storage, DkPreferenceListener listener) {
		this.context = context;
		this.storage = storage;
		this.listener = listener;
	}

	/**
	 * Indicate this preference is one of given group.
	 */
	public P groupId(String groupId) {
		this.groupId = groupId;
		return (P) this;
	}

	public P setEnabled(boolean enabled) {
		this.enabled = enabled;
		return (P) this;
	}

	/**
	 * Call this to update item view when some data changed.
	 */
	public void notifyDataChanged() {
		// For now, just update all items
		listener.notifyDataSetChanged();
	}
}
