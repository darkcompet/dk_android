/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import tool.compet.appbundle.R;
import tool.compet.core.storage.DkPreferenceStorage;

public class TheSelectboxPreference extends DkPreference<TheSelectboxPreference> {
    private String title; // for pref which has title
    private String subTitle; // for pref which has title
    private final List<String> entryNameList = new ArrayList<>();
    private final List<String> entryValueList = new ArrayList<>();
    private View customView;

    public TheSelectboxPreference(Context context, DkPreferenceStorage storage, String key, MyPreferenceListener onChangeListener) {
        super(context, storage, key, onChangeListener);
    }

    @Override
    protected View createView(Context context) {
        if (customView != null) {
            return customView;
        }
        return View.inflate(context, R.layout.dk_preference_selectbox, null);
    }

    @Override
    protected void decorateView(View view) {
        if (customView != null) {
            return;
        }
        TextView tvTitle = view.findViewById(R.id.dk_title);
        TextView tvSubTitle = view.findViewById(R.id.dk_sub_title);

        if (title != null) {
            tvTitle.setText(title);
        }
        if (subTitle != null) {
            tvSubTitle.setVisibility(View.VISIBLE);
            tvSubTitle.setText(subTitle);
        }

        view.setOnClickListener(v -> {
            String[] entryNames = entryNameList.toArray(new String[0]);

            new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(entryNames, 0, (dlg, which) -> {
                    String value = entryValueList.get(which);
                    storage.storeString(key, value);
                    listener.onPreferenceChanged(key);
                    dlg.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dlg, which) -> {
                    dlg.dismiss();
                })
                .setPositiveButton(R.string.ok, (dlg, which) -> {
                    dlg.dismiss();
                })
                .show();
        });
    }

    public TheSelectboxPreference title(int titleResId) {
        this.title = context.getString(titleResId);
        return this;
    }

    public TheSelectboxPreference title(String title) {
        this.title = title;
        return this;
    }

    public TheSelectboxPreference subTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public TheSelectboxPreference subTitle(int subTitleResId) {
        this.subTitle = context.getString(subTitleResId);
        return this;
    }

    public TheSelectboxPreference entry(int name, String value) {
        return entry(context.getString(name), value);
    }

    public TheSelectboxPreference entry(String name, String value) {
        entryNameList.add(name);
        entryValueList.add(value);
        return this;
    }

//    public TheSelectboxPreference entryNames(int[] names) {
//        this.entryNames = new String[names.length];
//        for (int index = names.length - 1; index >= 0; --index) {
//            this.entryNames[index] = context.getString(names[index]);
//        }
//        return this;
//    }

//    public TheSelectboxPreference entryNames(String[] names) {
//        this.entryNames = names;
//        return this;
//    }

//    public TheSelectboxPreference entryValues(String[] values) {
//        this.entryValues = values;
//        return this;
//    }

    public TheSelectboxPreference view(View view) {
        this.customView = view;
        return this;
    }
}
