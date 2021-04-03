/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

@SuppressWarnings("unchecked")
public abstract class DkPreference<P> {
	protected abstract View createView(Context context, ViewGroup parent);

	protected abstract void decorateView(View view);

	protected String key; // preference key in storage
	protected Context context; // to get resource value
	protected DkPreferenceStorage storage; // to store setting
	protected MyPreferenceListener listener; // to callback when something changed

	public DkPreference(String key) {
		this.key = key;
	}

	protected void init(Context context, DkPreferenceStorage storage, MyPreferenceListener listener) {
		this.context = context;
		this.storage = storage;
		this.listener = listener;
	}

	public P key(String key) {
		this.key = key;
		return (P) this;
	}

	/**
	 * Call this to update item view when some data changed.
	 */
	protected void notifyDataChanged() {
		// For now, just update all items
		listener.notifyDataSetChanged();
	}
}
