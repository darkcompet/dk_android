/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;
import android.view.View;

import tool.compet.core.storage.DkPreferenceStorage;

@SuppressWarnings("unchecked")
public abstract class DkPreference<P> {
    protected abstract View createView(Context context);
    protected abstract void decorateView(View view);

    protected Context context;
    protected DkPreferenceStorage storage;
    protected String key; // preference key in storage
    protected MyPreferenceListener listener;

    public DkPreference(Context context, DkPreferenceStorage storage, MyPreferenceListener listener) {
        this.context = context;
        this.storage = storage;
        this.listener = listener;
    }

    public DkPreference(Context context, DkPreferenceStorage storage, String key, MyPreferenceListener listener) {
        this(context, storage, listener);
        this.key = key;
    }

    public P key(String key) {
        this.key = key;
        return (P) this;
    }
}
