/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.floatingbar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tool.compet.appbundle.R;
import tool.compet.core.DkRunner;

public class DkSnackbar extends DkFloatingbar<DkSnackbar> {
	public static final int DURATION_SHORT = 2000;
	public static final int DURATION_NORMAL = 3000;
	public static final int DURATION_LONG = 4500;

	// We handle all snackbars by a manager
	private static MyFloatingbarManager manager;

	private final TextView tvMessage;
	private final Button btnAction;

	protected DkSnackbar(Context context, ViewGroup parent, View bar) {
		super(context, parent, bar);

		duration = DURATION_NORMAL;

		tvMessage = bar.findViewById(R.id.dk_tv_message);
		btnAction = bar.findViewById(R.id.dk_btn_action);

		bar.setBackgroundColor(TYPE_NORMAL);
	}

	public static DkSnackbar newIns(View view) {
		ViewGroup parent = MyFloatingbarHelper.findSuperFrameLayout(view);
		if (parent == null) {
			throw new RuntimeException("No suitable parent found");
		}
		// Prepare required params for constructor
		Context context = parent.getContext();
		View bar = LayoutInflater.from(context).inflate(R.layout.dk_snackbar, parent, false);

		return new DkSnackbar(context, parent, bar);
	}

	public static DkSnackbar newIns(Activity activity) {
		return newIns(activity.findViewById(android.R.id.content));
	}

	@Override
	protected MyFloatingbarManager manager() {
		return manager != null ? manager : (manager = new MyFloatingbarManager());
	}

	// region Get/Set

	public DkSnackbar message(int msgRes) {
		tvMessage.setText(msgRes);
		return this;
	}

	public DkSnackbar message(CharSequence msg) {
		tvMessage.setText(msg);
		return this;
	}

	public DkSnackbar duration(long millis) {
		duration = millis;
		return this;
	}

	public DkSnackbar setOnShownCallback(DkRunner onShownCallback) {
		this.onShownCallback = onShownCallback;
		return this;
	}

	public DkSnackbar setOnDismissCallback(DkRunner dismissCallback) {
		this.onDismissCallback = dismissCallback;
		return this;
	}

	public DkSnackbar setAction(int textRes, DkRunner onClickListener) {
		return setAction(textRes, true, onClickListener);
	}

	public DkSnackbar setAction(int textRes, boolean autoDismiss, DkRunner onClickListener) {
		btnAction.setText(textRes);
		btnAction.setVisibility(View.VISIBLE);
		if (onClickListener != null) {
			btnAction.setOnClickListener(v -> {
				onClickListener.run();
				if (autoDismiss) {
					dismiss();
				}
			});
		}
		return this;
	}

	// endregion Get/Set
}
