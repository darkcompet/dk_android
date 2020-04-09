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

public class DkImageOnlyItemBuilder extends DkItemBuilder<DkImageOnlyItemBuilder> {
	private int iconRes;

	private DkImageOnlyItemBuilder() {
	}

	public static DkImageOnlyItemBuilder newIns() {
		return new DkImageOnlyItemBuilder();
	}

	@Override
	protected DkBaseItemView getView(Context context) {
		DkImageOnlyItemView v = super.prepareView(context, R.layout.item_image_only);

		if (iconRes > 0) {
			v.ivIcon.setImageResource(iconRes);
		}

		return v;
	}

	public DkImageOnlyItemBuilder setImage(int iconRes) {
		this.iconRes = iconRes;
		return this;
	}
}
