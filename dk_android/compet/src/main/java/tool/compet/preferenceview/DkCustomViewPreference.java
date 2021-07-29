/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.preferenceview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import tool.compet.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Just show view (this is used for decoration purposed).
 */
public class DkCustomViewPreference extends TheBasePreference<DkCustomViewPreference> {
	private int layoutResId;
	private View view;

	public DkCustomViewPreference() {
		super(null);
	}

	public DkCustomViewPreference(String key) {
		super(key);
	}

	@Override
	public View createView(Context context, ViewGroup parent) {
		View itemView = view;
		if (itemView == null) {
			if (layoutResId <= 0) {
				layoutResId = R.layout.dk_preference_item_default_custom_view;
			}
			itemView = LayoutInflater.from(context).inflate(layoutResId, parent, false);
		}

		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		itemView.setLayoutParams(params);

		return itemView;
	}

	// Subclass can override this to customize storage
	@Override
	public void decorateView(View view) {
	}

	public DkCustomViewPreference view(int layoutResId) {
		this.layoutResId = layoutResId;
		return this;
	}

	public DkCustomViewPreference view(View view) {
		this.view = view;
		return this;
	}
}
