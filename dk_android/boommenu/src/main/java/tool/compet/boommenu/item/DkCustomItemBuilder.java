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
import tool.compet.core.util.DkLogs;

/**
 * Item builder for custom view. Your view must be subclass of DkBaseItemView and don't forget
 * call setters to setup view properties like: color, radius, useRippleEffect...
 */
public class DkCustomItemBuilder extends DkItemBuilder<DkCustomItemBuilder> {
	private int layoutRes;
	private DkBaseItemView view;

	private DkCustomItemBuilder() {
	}

	public static DkCustomItemBuilder newIns() {
		return new DkCustomItemBuilder();
	}

	@Override
	protected DkBaseItemView getView(Context context) {
		if (view == null) {
			view = super.prepareView(context, layoutRes);

			if (view == null) {
				DkLogs.complain(this, "Must specify view or layoutRes of DkBaseItemView");
			}
		}

		return view;
	}

	public DkCustomItemBuilder setView(DkBaseItemView view) {
		this.view = view;
		return this;
	}

	public DkCustomItemBuilder setView(int layoutRes) {
		this.layoutRes = layoutRes;
		return this;
	}
}
