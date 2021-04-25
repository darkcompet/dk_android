/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import tool.compet.appbundle.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Just show view (this is used for decoration purposed).
 */
public class DkViewPreference extends MyBasePreference<DkViewPreference> {
	private int layoutResId;
	private View view;

	public DkViewPreference(String key) {
		super(key);
	}

	@Override
	protected View createView(Context context, ViewGroup parent) {
		View itemView = view;
		if (itemView == null) {
			if (layoutResId <= 0) {
				layoutResId = R.layout.dk_preference_default_custom_view;
			}
			itemView = LayoutInflater.from(context).inflate(layoutResId, parent, false);
		}

		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		itemView.setLayoutParams(params);

		return itemView;
	}

	@Override
	protected void decorateView(View view) {
	}

	public DkViewPreference view(int layoutResId) {
		this.layoutResId = layoutResId;
		return this;
	}

	public DkViewPreference view(View view) {
		this.view = view;
		return this;
	}
}
