/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;

import tool.compet.core.log.DkLogs;

/**
 * Item builder for custom view. Your view must be subclass of DkBaseItemView and don't forget
 * call setters to setup view properties like: color, radius, useRippleEffect...
 */
public class DkCustomItemBuilder extends DkItemBuilder<DkCustomItemBuilder> {
    private int layoutRes;
    private DkBaseItemView view;

    private DkCustomItemBuilder() {
    }

    public static DkCustomItemBuilder newIns() {
        return new DkCustomItemBuilder();
    }

    @Override
    protected DkBaseItemView getView(Context context) {
        if (view == null) {
            view = super.prepareView(context, layoutRes);

            if (view == null) {
                DkLogs.complain(this, "Must specify view or layoutRes of DkBaseItemView");
            }
        }

        return view;
    }

    public DkCustomItemBuilder setView(DkBaseItemView view) {
        this.view = view;
        return this;
    }

    public DkCustomItemBuilder setView(int layoutRes) {
        this.layoutRes = layoutRes;
        return this;
    }
}
