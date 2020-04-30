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

package tool.compet.appbundle.floatingbar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tool.compet.appbundle.R;

/**
 * When show it, it urgently dismiss all current bars and show next.
 * It is useful when you wanna update message continuously.
 */
public class DkUrgentToastbar extends DkToastbar {
	protected DkUrgentToastbar(Context context, ViewGroup parent, View bar) {
		super(context, parent, bar);
	}

	// It will hide super newIns() method from outside-invoke
	public static DkUrgentToastbar newIns(ViewGroup parent) {
		parent = findSuitableParent(parent);

		if (parent == null) {
			throw new RuntimeException("No suitable parent found");
		}
		// prepare required params for the constructor
		Context context = parent.getContext();
		View bar = LayoutInflater.from(context).inflate(R.layout.dk_toastbar, parent, false);

		return new DkUrgentToastbar(context, parent, bar);
	}

	// It will hide super newIns() method from outside-invoke
	public static DkUrgentToastbar newIns(Activity activity) {
		return newIns(activity.findViewById(android.R.id.content));
	}

	@Override
	public void show() {
		getManager().dismissAll();
		super.show();
	}
}
