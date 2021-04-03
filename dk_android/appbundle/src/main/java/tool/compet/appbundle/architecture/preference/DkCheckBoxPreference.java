/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import tool.compet.appbundle.R;

public class DkCheckBoxPreference extends DkPreference<DkCheckBoxPreference> {
	// Checkbox
	protected boolean checked;
	protected int checkboxViewId = R.id.dk_checkbox; // viewId for checkbox
	protected String title; // text for title of checkbox
	private int titleTextId; // text res id for title of checkbox

	// Summary (under checkbox)
	protected boolean showSummary; // show or hide
	protected int summaryViewId = R.id.dk_summary; // viewId for summary view
	protected String summary; // text for summary
	protected int summaryTextId; // text id for summary view

	protected View customView;

	public DkCheckBoxPreference(String key) {
		super(key);
	}

	@Override
	protected void init(Context context, DkPreferenceStorage storage, MyPreferenceListener listener) {
		super.init(context, storage, listener);
		this.checked = storage.getBoolean(key);
	}

	@Override
	protected View createView(Context context, ViewGroup parent) {
		View itemView = customView;
		if (itemView == null) {
			itemView = LayoutInflater.from(context).inflate(R.layout.dk_preference_checkbox, parent, false);
		}

		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		itemView.setLayoutParams(params);

		return itemView;
	}

	@Override
	protected void decorateView(View view) {
		CheckBox cbText = view.findViewById(checkboxViewId);
		TextView tvSummary = view.findViewById(summaryViewId);

		// Setup checkbox
		if (titleTextId > 0) {
			title = context.getString(titleTextId);
		}
		cbText.setText(title);
		cbText.setChecked(checked);
		cbText.setOnCheckedChangeListener((buttonView, isChecked) -> {
			checked = isChecked;

			storage.setBoolean(key, isChecked);
			listener.onPreferenceChanged(key);

			notifyDataChanged();
		});

		// Setup summary
		tvSummary.setVisibility(showSummary ? View.VISIBLE : View.GONE);
		if (showSummary) {
			if (summaryTextId > 0) {
				summary = context.getString(summaryTextId);
			}
			tvSummary.setText(summary);
		}
	}

	public DkCheckBoxPreference checked(boolean checked) {
		this.checked = checked;
		return this;
	}

	public DkCheckBoxPreference title(int titleResId) {
		this.titleTextId = titleResId;
		return this;
	}

	public DkCheckBoxPreference title(String title) {
		this.title = title;
		return this;
	}

	public DkCheckBoxPreference showSummary(boolean show) {
		this.showSummary = show;
		return this;
	}

	public DkCheckBoxPreference sumary(int sumaryTextId) {
		this.summaryTextId = sumaryTextId;
		return this;
	}

	public DkCheckBoxPreference sumary(String summary) {
		this.summary = summary;
		return this;
	}

	public DkCheckBoxPreference defaultChecked(boolean value) {
		this.checked = value;
		return this;
	}

	/**
	 * Use it to customize layout of checkbox preference, caller must provide viewId of checkbox and summmary view.
	 */
	public DkCheckBoxPreference customView(View view, int checkboxViewId, int summaryViewId) {
		this.customView = view;
		this.checkboxViewId = checkboxViewId;
		this.summaryViewId = summaryViewId;
		return this;
	}
}
