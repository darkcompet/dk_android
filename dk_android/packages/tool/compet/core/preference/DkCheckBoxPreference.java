/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import tool.compet.core.R;
import tool.compet.core.view.DkViews;

public class DkCheckBoxPreference extends MyBasePreference<DkCheckBoxPreference> {
	// Title
	private int titleViewId = R.id.dk_title; // title view id
	protected String title; // text for title of checkbox
	private int titleTextId; // text res id for title of checkbox

	// Checkbox
	protected int checkboxViewId = R.id.dk_checkbox; // viewId for checkbox
	protected boolean checked;

	// Summary
	protected int summaryViewId = R.id.dk_summary; // viewId for summary view
	protected String summary; // text for summary
	protected int summaryTextId; // text id for summary view

	protected View customView;

	public DkCheckBoxPreference(String key) {
		super(key);
	}

	@Override
	public void init(Context context, DkPreferenceStorage storage, DkPreferenceListener listener) {
		super.init(context, storage, listener);
		this.checked = storage.getBoolean(key);
	}

	@Override
	public View createView(Context context, ViewGroup parent) {
		View itemView = customView;
		if (itemView == null) {
			itemView = LayoutInflater.from(context).inflate(R.layout.dk_preference_item_checkbox, parent, false);
		}

		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		itemView.setLayoutParams(params);

		return itemView;
	}

	@Override
	public void decorateView(View view) {
		TextView tvTitle = view.findViewById(titleViewId);
		CheckBox cbCheck = view.findViewById(checkboxViewId);
		TextView tvSummary = view.findViewById(summaryViewId);

		view.setEnabled(enabled);

		// Setup title
		if (titleTextId > 0) {
			title = context.getString(titleTextId);
		}
		if (title != null) {
			DkViews.setTextSize(tvTitle, 1.125f * tvSummary.getTextSize());
			tvTitle.setText(title);
			tvTitle.setVisibility(View.VISIBLE);
		}
		else {
			tvTitle.setVisibility(View.GONE);
		}

		// Setup checkbox
		cbCheck.setEnabled(enabled);
		cbCheck.setChecked(checked);

		if (enabled) {
			cbCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
				checked = isChecked;

				storage.putBoolean(key, isChecked);
				listener.onPreferenceChanged(key);

				notifyDataChanged();
			});
			view.setOnClickListener(v -> cbCheck.performClick());
		}

		// Setup summary
		if (summaryTextId > 0) {
			summary = context.getString(summaryTextId);
		}
		if (summary != null) {
			tvSummary.setVisibility(View.VISIBLE);
			tvSummary.setText(summary);
		}
		else {
			tvSummary.setVisibility(View.GONE);
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
	public DkCheckBoxPreference customView(View view, int titleViewId, int checkboxViewId, int summaryViewId) {
		this.customView = view;
		this.titleViewId = titleViewId;
		this.checkboxViewId = checkboxViewId;
		this.summaryViewId = summaryViewId;
		return this;
	}
}
