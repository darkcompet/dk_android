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

import tool.compet.boommenu.DkBaseItemView;
import tool.compet.boommenu.DkItemBuilder;
import tool.compet.boommenu.R;

public class DkDualTextItemBuilder extends DkItemBuilder<DkDualTextItemBuilder> {
	public static final int STYLE_TEXT_LEFT = 1;
	public static final int STYLE_TEXT_RIGHT = 2;

	private int style = STYLE_TEXT_RIGHT;
	private int iconRes;
	private int textRes;
	private int subTextRes;

	private DkDualTextItemBuilder() {
	}

	public static DkDualTextItemBuilder newIns() {
		return new DkDualTextItemBuilder();
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

		DkDualTextItemView v = super.prepareView(context, layoutRes);

		if (iconRes > 0) {
			v.ivIcon.setImageResource(iconRes);
		}
		if (textRes > 0) {
			v.tvText.setText(textRes);
		}
		if (subTextRes > 0) {
			v.tvSubText.setText(subTextRes);
		}

		return v;
	}

	public DkDualTextItemBuilder setStyle(int style) {
		this.style = style;
		return this;
	}

	public DkDualTextItemBuilder setImage(int iconRes) {
		this.iconRes = iconRes;
		return this;
	}

	public DkDualTextItemBuilder setText(int textRes) {
		this.textRes = textRes;
		return this;
	}

	public DkDualTextItemBuilder setSubText(int subTextRes) {
		this.subTextRes = subTextRes;
		return this;
	}
}
