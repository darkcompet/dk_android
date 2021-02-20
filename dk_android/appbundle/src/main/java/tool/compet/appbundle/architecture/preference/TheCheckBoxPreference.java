/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import tool.compet.appbundle.R;
import tool.compet.core.storage.DkPreferenceStorage;

public class TheCheckBoxPreference extends DkPreference<TheCheckBoxPreference> {
    private boolean checked;
    private String title;
    private String subTitle;
    private View customView;
    private boolean defaultValue;

    TheCheckBoxPreference(Context context, DkPreferenceStorage storage, String key, MyPreferenceListener onChangeListener) {
        super(context, storage, key, onChangeListener);
    }

    @Override
    protected View createView(Context context) {
        if (customView != null) {
            return customView;
        }
        return View.inflate(context, R.layout.dk_preference_checkbox, null);
    }

    @Override
    protected void decorateView(View view) {
        if (customView != null) {
            return;
        }
        CheckBox checkBox = view.findViewById(R.id.dk_checkbox);
        TextView tvTitle = view.findViewById(R.id.dk_title);
        TextView tvSubTitle = view.findViewById(R.id.dk_sub_title);

        checkBox.setChecked(checked);
        if (title != null) {
            tvTitle.setText(title);
        }
        if (subTitle != null) {
            tvSubTitle.setVisibility(View.VISIBLE);
            tvSubTitle.setText(subTitle);
        }

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            storage.storeBoolean(key, isChecked);
            listener.onPreferenceChanged(key);
        });
    }

    public TheCheckBoxPreference checked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public TheCheckBoxPreference title(int titleResId) {
        this.title = context.getString(titleResId);
        return this;
    }

    public TheCheckBoxPreference title(String title) {
        this.title = title;
        return this;
    }

    public TheCheckBoxPreference subTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public TheCheckBoxPreference subTitle(int subTitleResId) {
        this.subTitle = context.getString(subTitleResId);
        return this;
    }

    public TheCheckBoxPreference view(View view) {
        this.customView = view;
        return this;
    }

    public TheCheckBoxPreference defaultValue(boolean value) {
        this.defaultValue = value;
        return this;
    }
}
