/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;

public class DkDualTextItemBuilder extends DkItemBuilder<DkDualTextItemBuilder> {
	public static final int STYLE_TEXT_LEFT = 1;
	public static final int STYLE_TEXT_RIGHT = 2;

	private int style = STYLE_TEXT_RIGHT;
	private int iconRes;
	private int titleRes;
	private int summaryRes;

	public DkDualTextItemBuilder() {
	}

	@Override
	protected DkBaseItemView getView(Context context) {
		int layoutRes;

		if (style == STYLE_TEXT_LEFT) {
			layoutRes = R.layout.item_dual_text_left;
		}
		else if (style == STYLE_TEXT_RIGHT) {
			layoutRes = R.layout.item_dual_text_right;
		}
		else {
			throw new RuntimeException("Invalid style");
		}

		DkDualTextItemView itemView = super.prepareView(context, layoutRes);

		if (iconRes > 0) {
			itemView.ivIcon.setImageResource(iconRes);
		}
		if (titleRes > 0) {
			itemView.tvTitle.setText(titleRes);
		}
		if (summaryRes > 0) {
			itemView.tvSummary.setText(summaryRes);
		}

		return itemView;
	}

	public DkDualTextItemBuilder setStyle(int style) {
		this.style = style;
		return this;
	}

	public DkDualTextItemBuilder setImage(int iconRes) {
		this.iconRes = iconRes;
		return this;
	}

	public DkDualTextItemBuilder setTitle(int titleRes) {
		this.titleRes = titleRes;
		return this;
	}

	public DkDualTextItemBuilder setSummary(int subTextRes) {
		this.summaryRes = subTextRes;
		return this;
	}
}
