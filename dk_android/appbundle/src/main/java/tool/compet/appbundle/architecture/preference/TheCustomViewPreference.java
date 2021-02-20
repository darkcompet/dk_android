/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;
import android.view.View;

import tool.compet.core.storage.DkPreferenceStorage;

public class TheCustomViewPreference extends DkPreference<TheCustomViewPreference> {
    int layoutResId;

    public TheCustomViewPreference(Context context, DkPreferenceStorage storage, MyPreferenceListener listener) {
        super(context, storage, listener);
    }

    @Override
    protected View createView(Context context) {
        return View.inflate(context, layoutResId, null);
    }

    @Override
    protected void decorateView(View view) {
    }
}
