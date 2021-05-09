/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.collection.ArraySet;

import tool.compet.appbundle.R;
import tool.compet.core.DkArrays;
import tool.compet.core.view.DkViews;

public class DkGroupPreference extends MyBasePreference<DkGroupPreference> {
	protected CharSequence title;
	protected int titleResId;

	protected View customView;

	private ArraySet<String> children;

	public DkGroupPreference() {
		super(null);
	}

	public DkGroupPreference(String key) {
		super(key);
	}

	@Override
	public View createView(Context context, ViewGroup parent) {
		View view = customView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.dk_preference_item_group, parent, false);
		}
		return view;
	}

	@Override
	public void decorateView(View view) {
		TypedValue tv = new TypedValue();
		int[] attrs = new int[] {
			R.attr.dk_color_layout_bkg, R.attr.colorPrimary,
			R.attr.colorPrimaryDark, R.attr.colorAccent
		};
		TypedArray arr = context.obtainStyledAttributes(tv.data, attrs);
		int colorAccent = arr.getColor(3, 0);
		arr.recycle();

		float radius = view.getResources().getDimension(R.dimen.base_bkg_corner_radius_medium);

		DkViews.injectGradientDrawable(view, radius, colorAccent, new boolean[] {false, true, true, false});

		TextView tvTitle = view.findViewById(R.id.dk_title);
		TextView tvSummary = view.findViewById(R.id.dk_summary);

		if (titleResId > 0) {
			title = context.getString(titleResId);
		}
		if (title != null) {
			DkViews.setTextSize(tvTitle, tvSummary.getTextSize() * 1.25f);
			tvTitle.setText(title);
			tvTitle.setVisibility(View.VISIBLE);
		}
		else {
			tvTitle.setVisibility(View.GONE);
		}
	}

	public DkGroupPreference title(int titleResId) {
		this.titleResId = titleResId;
		return this;
	}

	public DkGroupPreference title(CharSequence title) {
		this.title = title;
		return this;
	}

	public DkGroupPreference addChild(String... preferenceKeys) {
		if (children == null) {
			children = new ArraySet<>();
		}
		children.addAll(DkArrays.asList(preferenceKeys));
		return this;
	}
}
