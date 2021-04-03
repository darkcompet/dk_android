/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;
import android.graphics.Color;

/**
 * This item has one image and one text. You can set location of text relative to image by call setStyle().
 */
public class DkTextItemBuilder extends DkItemBuilder<DkTextItemBuilder> {
	public static final int STYLE_TEXT_INSIDE_ICON = 1; // text at bottom and inside of icon
	public static final int STYLE_TEXT_LEFT_OUT_ICON = 2; // text at left and outside of icon
	public static final int STYLE_TEXT_RIGHT_OUT_ICON = 3; // text at right and outside of icon
	public static final int STYLE_TEXT_TOP_OUT_ICON = 4; // text at top and outside of icon
	public static final int STYLE_TEXT_BOTTOM_OUT_ICON = 5; // text at bottom and outside of icon

	private int style = STYLE_TEXT_INSIDE_ICON;

	private int iconRes;
	private int iconBorderColor = Color.TRANSPARENT;
	private int iconBorderThickness = Integer.MIN_VALUE;
	private int textRes;

	@Override
	protected DkBaseItemView getView(Context context) {
		int layoutRes;
		DkTextItemView itemView;

		if (style == STYLE_TEXT_INSIDE_ICON) {
			layoutRes = R.layout.item_text_inside;
		}
		else if (style == STYLE_TEXT_LEFT_OUT_ICON) {
			layoutRes = R.layout.item_text_left;
		}
		else if (style == STYLE_TEXT_RIGHT_OUT_ICON) {
			layoutRes = R.layout.item_text_right;
		}
		else if (style == STYLE_TEXT_TOP_OUT_ICON) {
			layoutRes = R.layout.item_text_top;
		}
		else if (style == STYLE_TEXT_BOTTOM_OUT_ICON) {
			layoutRes = R.layout.item_text_bottom;
		}
		else {
			throw new RuntimeException("Invalid style");
		}

		itemView = super.prepareView(context, layoutRes);

		if (iconRes > 0) {
			itemView.ivIcon.setImageResource(iconRes);
		}
		if (textRes > 0) {
			itemView.tvText.setText(textRes);
		}

		return itemView;
	}

	public DkTextItemBuilder setStyle(int style) {
		this.style = style;
		return this;
	}

	public DkTextItemBuilder setIcon(int iconRes) {
		this.iconRes = iconRes;
		return this;
	}

	public DkTextItemBuilder setText(int textRes) {
		this.textRes = textRes;
		return this;
	}
}
