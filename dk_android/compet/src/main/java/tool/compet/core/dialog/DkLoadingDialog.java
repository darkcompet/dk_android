/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.dialog;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tool.compet.R;
import tool.compet.compactview.DkCompactDialogFragment;

/**
 * You can use this to show or close waiting dialog, or extends this to customize behaviors.
 */
public class DkLoadingDialog extends DkCompactDialogFragment {
	protected ProgressBar pbLoading;
	protected TextView tvMessage;

	protected String message;
	protected int messageResId = View.NO_ID;
	protected int filterColor = Color.WHITE;

	public DkLoadingDialog() {
		// By default, we make loading dialog cannot cancelable
		this.cancelable = false;
	}

	@Override
	public int layoutResourceId() {
		return R.layout.dk_dialog_please_wait;
	}

	@CallSuper
	@Override
	protected void storeInstanceState(@NonNull Bundle outState) {
		super.storeInstanceState(outState);

		outState.putInt("DkPleaseWaitDialog.messageResId", messageResId);
		outState.putString("DkPleaseWaitDialog.message", message);
		outState.putInt("DkPleaseWaitDialog.filterColor", filterColor);
	}

	@CallSuper
	@Override
	protected void restoreInstanceState(@Nullable Bundle savedInstanceState) {
		super.restoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			messageResId = savedInstanceState.getInt("DkPleaseWaitDialog.messageResId");
			message = savedInstanceState.getString("DkPleaseWaitDialog.message");
			filterColor = savedInstanceState.getInt("DkPleaseWaitDialog.filterColor");
		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		// Call it before `super.onViewCreated()` since super will tell Logic that View is ready.
		onInitChildren(view);

		super.onViewCreated(view, savedInstanceState);

		onSetupLayout(view);
	}

	/**
	 * Subclass can override this to initialize children.
	 */
	protected void onInitChildren(View view) {
		pbLoading = view.findViewById(R.id.dk_pb_loading);
		tvMessage = view.findViewById(R.id.dk_tv_message);
	}

	/**
	 * Subclass can override this to setup customized view.
	 */
	protected void onSetupLayout(View view) {
		// Set message
		if (messageResId != View.NO_ID) {
			message = context.getString(messageResId);
		}
		setMessage(message);

		// Set color filter for progress
		setColorFilter(filterColor);
	}

	//
	// Get/Set region
	//

	public DkLoadingDialog setCancellable(boolean cancelable) {
		this.cancelable = cancelable;
		return this;
	}

	public DkLoadingDialog setMessage(int messageResId) {
		this.messageResId = messageResId;
		if (tvMessage != null) {
			tvMessage.setText(messageResId);
		}
		return this;
	}

	public DkLoadingDialog setMessage(String message) {
		this.message = message;
		if (tvMessage != null) {
			tvMessage.setText(message);
		}
		return this;
	}

	/**
	 * @param color Set to null to turn off color filter
	 */
	public DkLoadingDialog setColorFilter(int color) {
		this.filterColor = color;
		if (pbLoading != null) {
			pbLoading.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		}
		return this;
	}
}
