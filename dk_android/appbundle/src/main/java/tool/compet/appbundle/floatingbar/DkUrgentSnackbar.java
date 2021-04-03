/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
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
public class DkUrgentSnackbar extends DkSnackbar {
	protected DkUrgentSnackbar(Context context, ViewGroup parent, View bar) {
		super(context, parent, bar);
	}

	// It will hide super newIns() method from outside-invoke
	public static DkUrgentSnackbar newIns(ViewGroup parent) {
		parent = MyHelper.findSuperFrameLayout(parent);

		if (parent == null) {
			throw new RuntimeException("No suitable parent found");
		}
		// prepare required params for the constructor
		Context context = parent.getContext();
		View bar = LayoutInflater.from(context).inflate(R.layout.dk_snackbar, parent, false);

		return new DkUrgentSnackbar(context, parent, bar);
	}

	// It will hide super newIns() method from outside-invoke
	public static DkUrgentSnackbar newIns(Activity activity) {
		return newIns(activity.findViewById(android.R.id.content));
	}

	@Override
	public void show() {
		getManager().dismissAll();
		super.show();
	}
}
