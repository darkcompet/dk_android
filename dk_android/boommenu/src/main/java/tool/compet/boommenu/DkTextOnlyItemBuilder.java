/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;

public class DkTextOnlyItemBuilder extends DkItemBuilder<DkTextOnlyItemBuilder> {
	private int textRes;

	public DkTextOnlyItemBuilder() {
	}

	@Override
	protected DkBaseItemView getView(Context context) {
		DkTextOnlyItemView v = super.prepareView(context, R.layout.item_text_only);

		if (textRes > 0) {
			v.tvText.setText(textRes);
		}

		return v;
	}

	public void setText(int strRes) {
		this.textRes = strRes;
	}
}
