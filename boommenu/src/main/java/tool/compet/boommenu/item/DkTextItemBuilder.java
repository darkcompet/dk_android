/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.boommenu.item;

import android.content.Context;
import android.graphics.Color;

import tool.compet.boommenu.DkBaseItemView;
import tool.compet.boommenu.DkItemBuilder;
import tool.compet.boommenu.R;

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
		DkTextItemView v;

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

		v = super.prepareView(context, layoutRes);

		if (iconRes > 0) {
			v.ivIcon.setImageResource(iconRes);
		}
		if (textRes > 0) {
			v.tvText.setText(textRes);
		}

		return v;
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
