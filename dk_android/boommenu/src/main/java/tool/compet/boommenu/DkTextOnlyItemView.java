/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import tool.compet.boommenu.DkBaseItemView;
import tool.compet.boommenu.R;

public class DkTextOnlyItemView extends DkBaseItemView {
	TextView tvText;

	public DkTextOnlyItemView(Context context) {
		super(context);
	}

	public DkTextOnlyItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DkTextOnlyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onFinishInflate() {
		tvText = findViewById(R.id.tvText);

		tvText.setSelected(true);

		super.onFinishInflate();
	}
}
