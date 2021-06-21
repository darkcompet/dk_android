/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.dialog;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tool.compet.appbundle.R;
import tool.compet.appbundle.compact.DkCompactDialogFragment;

/**
 * You can use this to show or close waiting dialog, or extends this to customize behaviors.
 */
@SuppressWarnings("unchecked")
public class DkPleaseWaitDialog<D> extends DkCompactDialogFragment<D> {
	protected ProgressBar pbLoading;
	protected TextView tvMessage;

	protected String message;
	protected int messageResId = View.NO_ID;
	protected int filterColor = Color.WHITE;

	// Indicate this dialog is dismissable for some actions as: back pressed...
	protected boolean cancelable;

	@Override
	public int layoutResourceId() {
		return R.layout.dk_dialog_please_wait;
	}

	@Override
	public int fragmentContainerId() {
		return View.NO_ID;
	}

	@Override
	public boolean onBackPressed() {
		return ! cancelable; // TRUE: i will handle, FALSE: please popback
	}

	@Override
	protected void storeInstanceState(@NonNull Bundle outState) {
		super.storeInstanceState(outState);

		outState.putBoolean("DkCompactDialogFragment.cancelable", cancelable);
		outState.putInt("DkPleaseWaitDialog.messageResId", messageResId);
		outState.putString("DkPleaseWaitDialog.message", message);
		outState.putInt("DkPleaseWaitDialog.filterColor", filterColor);
	}

	@Override
	protected void restoreInstanceState(@Nullable Bundle savedInstanceState) {
		super.restoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			cancelable = savedInstanceState.getBoolean("DkCompactDialogFragment.cancelable", false);
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

	public D setCancellable(boolean cancelable) {
		this.cancelable = cancelable;
		return (D) this;
	}

	public D setMessage(int messageResId) {
		this.messageResId = messageResId;
		if (tvMessage != null) {
			tvMessage.setText(messageResId);
		}
		return (D) this;
	}

	public D setMessage(String message) {
		this.message = message;
		if (tvMessage != null) {
			tvMessage.setText(message);
		}
		return (D) this;
	}

	/**
	 * @param color Set to null to turn off color filter
	 */
	public D setColorFilter(int color) {
		this.filterColor = color;
		if (pbLoading != null) {
			pbLoading.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		}
		return (D) this;
	}
}
