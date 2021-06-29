/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

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
import tool.compet.core.view.DkViews;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DkSelectboxPreference extends MyBasePreference<DkSelectboxPreference> {
	public static final int POPUP_STYLE_DIALOG = 1;
	public static final int POPUP_STYLE_POPUP = 2;

	protected int popupStyle = POPUP_STYLE_DIALOG;

	// Title
	protected int titleViewId = R.id.dk_title; // viewId for title
	protected String title; // text for title
	protected int titleResId;

	// Summary
	protected int summaryViewId = R.id.dk_summary; // viewId for summary
	protected String summary; // text for summary
	protected int summaryResId;

	// Entries
	protected final List<Object> entryNameList = new ArrayList<>();
	protected final List<String> entryValueList = new ArrayList<>();

	// Selected item
	protected int selectorViewId = R.id.dk_display_name; // viewId for selector
	protected String selectedName; // current seleted setting name
	protected String selectedValue; // current selected setting value

	protected View customView;

	public DkSelectboxPreference(String key) {
		super(key);
	}

	@Override
	public void init(Context context, DkPreferenceStorage storage, DkPreferenceListener listener) {
		super.init(context, storage, listener);
		this.selectedValue = storage.getString(key);
	}

	@Override
	public View createView(Context context, ViewGroup parent) {
		View itemView = customView;
		if (itemView == null) {
			itemView = LayoutInflater.from(context).inflate(R.layout.dk_preference_item_selectbox, parent, false);
		}

		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		itemView.setLayoutParams(params);

		return itemView;
	}

	@Override
	public void decorateView(View view) {
		view.setEnabled(enabled);

		TextView tvTitle = view.findViewById(titleViewId);
		TextView tvSummary = view.findViewById(summaryViewId);
		TextView tvName = view.findViewById(selectorViewId);

		// Setup title
		if (titleResId > 0) {
			title = context.getString(titleResId);
		}
		if (title != null) {
			DkViews.setTextSize(tvTitle, 1.125f * tvSummary.getTextSize());
			tvTitle.setText(title);
			tvTitle.setVisibility(View.VISIBLE);
		}
		else {
			tvTitle.setVisibility(View.GONE);
		}

		// Setup summary
		if (summaryResId > 0) {
			summary = context.getString(summaryResId);
		}
		if (summary != null) {
			tvSummary.setText(summary);
			tvSummary.setVisibility(View.VISIBLE);
		}
		else {
			tvSummary.setVisibility(View.GONE);
		}

		// Setup content
		int selectedIndex = Math.max(0, entryValueList.indexOf(selectedValue));
		selectedName = calcEntryName(entryNameList.get(selectedIndex));
		tvName.setText(selectedName);

		// Setup click and long-click listener (show popup to select)
		if (enabled) {
			view.setOnClickListener(v -> showPopup(selectedIndex));
			view.setOnLongClickListener(v -> {
				showPopup(selectedIndex);
				return true;
			});
		}
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

					storage.putString(key, selectedValue);
					listener.onPreferenceChanged(key);

					dlg.dismiss();

					notifyDataChanged();
				})
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

	public DkSelectboxPreference summary(String summary) {
		this.summary = summary;
		return this;
	}

	public DkSelectboxPreference summary(int summaryResId) {
		this.summaryResId = summaryResId;
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
	public DkSelectboxPreference customView(View view, int titleResId, int summaryViewId, int selectorViewId) {
		this.customView = view;
		this.titleViewId = titleResId;
		this.summaryViewId = summaryViewId;
		this.selectorViewId = selectorViewId;
		return this;
	}
}
