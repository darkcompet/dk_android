/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tool.compet.appbundle.R;
import tool.compet.core.storage.DkStorageInf;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DkSelectboxPreference extends DkPreference<DkSelectboxPreference> {
    public static final int POPUP_STYLE_DIALOG = 1;
    public static final int POPUP_STYLE_POPUP = 2;

    protected int popupStyle = POPUP_STYLE_DIALOG;

    // Title
    protected int titleViewId = R.id.dk_title; // viewId for title
    protected String title; // text for title
    protected int titleResId; // text res id for title

    // Summary
    protected boolean showSummary; // show or hide
    protected int summaryResId = R.id.dk_summary; // viewId for summary
    protected String summary; // text for summary

    // Entries
    protected final List<Object> entryNameList = new ArrayList<>();
    protected final List<String> entryValueList = new ArrayList<>();

    // Selected item
    protected int selectorResId = R.id.dk_display_name; // viewId for selector
    protected String selectedName; // current seleted setting name
    protected String selectedValue; // current selected setting value

    protected View customView;

    public DkSelectboxPreference(String key) {
        super(key);
    }

    @Override
    protected void init(Context context, DkStorageInf storage, MyPreferenceListener listener) {
        super.init(context, storage, listener);
        this.selectedValue = storage.getString(key);
    }

    @Override
    protected View createView(Context context, ViewGroup parent) {
        View itemView = customView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.dk_preference_selectbox, parent, false);
        }

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        itemView.setLayoutParams(params);

        return itemView;
    }

    @Override
    protected void decorateView(View view) {
        TextView tvTitle = view.findViewById(titleViewId);
        TextView tvSummary = view.findViewById(summaryResId);
        TextView tvName = view.findViewById(selectorResId);

        // Setup title
        tvTitle.setText(title);

        // Setup summary
        tvSummary.setVisibility(showSummary ? View.VISIBLE : View.GONE);
        if (showSummary) {
            tvSummary.setText(summary);
        }

        // Setup content
        int selectedIndex = Math.max(0, entryValueList.indexOf(selectedValue));
        selectedName = calcEntryName(entryNameList.get(selectedIndex));
        tvName.setText(selectedName);

        // Setup click listern (show popup to select)
        view.setOnClickListener(v -> showPopup(selectedIndex));
    }

    private String calcEntryName(Object textIdOrString) {
        if (textIdOrString instanceof Integer) {
            return context.getString((Integer) textIdOrString);
        }
        return (String) textIdOrString;
    }

    private String[] calcEntryNames(List<Object> entryNameList) {
        String[] entryNames = new String[entryNameList.size()];
        for (int index = entryNames.length - 1; index >= 0; --index) {
            entryNames[index] = calcEntryName(entryNameList.get(index));
        }
        return entryNames;
    }

    private void showPopup(int selectedIndex) {
        String[] entryNames = calcEntryNames(entryNameList);

        if (popupStyle == POPUP_STYLE_DIALOG) {
            new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(entryNames, selectedIndex, (dlg, which) -> {
                    selectedName = entryNames[which];
                    selectedValue = entryValueList.get(which);

                    storage.setString(key, selectedValue);
                    listener.onPreferenceChanged(key);

                    dlg.dismiss();

                    notifyDataChanged();
                })
//                .setNegativeButton(R.string.cancel, (dlg, which) -> {
//                    dlg.dismiss();
//                })
//                .setPositiveButton(R.string.ok, (dlg, which) -> {
//                    dlg.dismiss();
//                })
                .show();
        }
        else if (popupStyle == POPUP_STYLE_POPUP) {
            //todo impl
        }
    }

    public DkSelectboxPreference popupStyle(int popupStyle) {
        this.popupStyle = popupStyle;
        return this;
    }

    public DkSelectboxPreference title(int titleResId) {
        this.titleResId = titleResId;
        return this;
    }

    public DkSelectboxPreference title(String title) {
        this.title = title;
        return this;
    }

    public DkSelectboxPreference showSummary(boolean show) {
        this.showSummary = show;
        return this;
    }

    public DkSelectboxPreference summary(String summary) {
        this.summary = summary;
        return this;
    }

    public DkSelectboxPreference summary(int summaryResId) {
        this.summary = context.getString(summaryResId);
        return this;
    }

    public DkSelectboxPreference addEntry(int nameTextId, String value) {
        entryNameList.add(nameTextId);
        entryValueList.add(value);
        return this;
    }

    public DkSelectboxPreference addEntry(String name, String value) {
        entryNameList.add(name);
        entryValueList.add(value);
        return this;
    }

    /**
     * Use it to customize layout of selectbox preference, caller must provide viewId of title, summmary and selector view.
     */
    public DkSelectboxPreference customView(View view, int titleResId, int summaryResId, int selectorResId) {
        this.customView = view;
        this.titleViewId = titleResId;
        this.summaryResId = summaryResId;
        this.selectorResId = selectorResId;
        return this;
    }
}
