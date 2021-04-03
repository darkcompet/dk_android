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

public class DkSnackbar extends DkFloatingbar {
	public static final int DURATION_SHORT = 2000;
	public static final int DURATION_NORMAL = 3000;
	public static final int DURATION_LONG = 4500;

	private static MyFloatingbarManager manager;
	private TextView tvMessage;
	private Button btnAction;

	protected DkSnackbar(Context context, ViewGroup parent, View bar) {
		super(context, parent, bar);

		duration = DURATION_NORMAL;

		bar.setBackgroundColor(TYPE_NORMAL);
		tvMessage = bar.findViewById(R.id.dk_tv_message);
		btnAction = bar.findViewById(R.id.btnAction);
	}

	public static DkSnackbar newIns(ViewGroup parent) {
		parent = MyHelper.findSuperFrameLayout(parent);

		if (parent == null) {
			throw new RuntimeException("No suitable parent found");
		}
		// prepare required params for constructor
		Context context = parent.getContext();
		View bar = LayoutInflater.from(context).inflate(R.layout.dk_snackbar, parent, false);

		return new DkSnackbar(context, parent, bar);
	}

	public static DkSnackbar newIns(Activity activity) {
		return newIns(activity.findViewById(android.R.id.content));
	}

	@Override
	protected MyFloatingbarManager getManager() {
		return manager != null ? manager : (manager = new MyFloatingbarManager());
	}

	public DkSnackbar setMessage(int msgRes) {
		tvMessage.setText(msgRes);
		return this;
	}

	public DkSnackbar setMessage(CharSequence msg) {
		tvMessage.setText(msg);
		return this;
	}

	public DkSnackbar setDuration(long millis) {
		duration = millis;
		return this;
	}

	public DkSnackbar setOnShownCallback(Runnable onShownCallback) {
		this.onShownCallback = onShownCallback;
		return this;
	}

	public DkSnackbar setOnDismissCallback(Runnable dismissCallback) {
		onDismissCallback = dismissCallback;
		return this;
	}

	public DkSnackbar setAction(int strRes, Runnable onClickListener) {
		return setAction(strRes, true, onClickListener);
	}

	public DkSnackbar setAction(int strRes, boolean isAutoDismiss, Runnable onClickListener) {
		btnAction.setText(strRes);
		btnAction.setVisibility(View.VISIBLE);
		if (onClickListener != null) {
			btnAction.setOnClickListener(v -> {
				onClickListener.run();
				if (isAutoDismiss) {
					dismiss();
				}
			});
		}
		return this;
	}

	public DkSnackbar asError() {
		bar.setBackgroundColor(TYPE_ERROR);
		return this;
	}

	public DkSnackbar asWarning() {
		bar.setBackgroundColor(TYPE_WARNING);
		return this;
	}

	public DkSnackbar asAsk() {
		bar.setBackgroundColor(TYPE_ASK);
		return this;
	}

	public DkSnackbar asSuccess() {
		bar.setBackgroundColor(TYPE_SUCCESS);
		return this;
	}

	public DkSnackbar asInfo() {
		bar.setBackgroundColor(TYPE_INFO);
		return this;
	}

	public DkSnackbar asType(int color) {
		bar.setBackgroundColor(color);
		return this;
	}
}
