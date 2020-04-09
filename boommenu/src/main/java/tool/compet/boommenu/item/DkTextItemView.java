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
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import tool.compet.boommenu.DkBaseItemView;
import tool.compet.boommenu.R;

public class DkTextItemView extends DkBaseItemView {
	ImageView ivIcon;
	TextView tvText;

	public DkTextItemView(Context context) {
		super(context);
	}

	public DkTextItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DkTextItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onFinishInflate() {
		ivIcon = findViewById(R.id.ivIcon);
		tvText = findViewById(R.id.tvText);

		tvText.setSelected(true);

		super.onFinishInflate();
	}
}
