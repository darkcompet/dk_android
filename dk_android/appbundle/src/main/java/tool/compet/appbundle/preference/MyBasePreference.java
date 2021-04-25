/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

abstract class MyBasePreference<P> {
	abstract View createView(Context context, ViewGroup parent);

	abstract void decorateView(View view);

	String key; // preference key in storage
	Context context; // to get resource value
	DkPreferenceStorage storage; // to store setting
	MyPreferenceListener listener; // to callback when something changed

	MyBasePreference(String key) {
		this.key = key;
	}

	void init(Context context, DkPreferenceStorage storage, MyPreferenceListener listener) {
		this.context = context;
		this.storage = storage;
		this.listener = listener;
	}

	/**
	 * Call this to update item view when some data changed.
	 */
	void notifyDataChanged() {
		// For now, just update all items
		listener.notifyDataSetChanged();
	}
}
