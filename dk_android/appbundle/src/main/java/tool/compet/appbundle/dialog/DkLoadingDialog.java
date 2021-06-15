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

import tool.compet.appbundle.compact.DkCompactDialogFragment;
import tool.compet.appbundle.R;

/**
 * You can use this to show or close waiting dialog, or extends this to customize behaviors.
 */
@SuppressWarnings("unchecked")
public class DkLoadingDialog<D> extends DkCompactDialogFragment<D> {
	protected ProgressBar pbLoading;
	protected TextView tvMessage;

	protected String message;
	protected int messageResId = View.NO_ID;
	protected Integer filterColor = Color.WHITE;

	@Override
	public int layoutResourceId() {
		return R.layout.dk_dialog_please_wait;
	}

	@Override
	public int fragmentContainerId() {
		return 0;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		onRestoreState(savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		onSaveState(outState);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Subclass can override this to customize which fields to persist to hard disk.
	 */
	protected void onSaveState(@NonNull Bundle outState) {
		outState.putInt("DkLoadingDialog.messageResId", messageResId);
		outState.putString("DkLoadingDialog.message", message);
		outState.putInt("DkLoadingDialog.filterColor", filterColor);
	}

	/**
	 * Subclass can override this to customize which fields to restore from hard disk.
	 */
	protected void onRestoreState(@Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			messageResId = savedInstanceState.getInt("DkLoadingDialog.messageResId");
			message = savedInstanceState.getString("DkLoadingDialog.message");
			filterColor = savedInstanceState.getInt("DkLoadingDialog.filterColor");
		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		onSetupView(view);
	}

	/**
	 * Subclass can override this to setup customized view.
	 */
	protected void onSetupView(View view) {
		pbLoading = view.findViewById(R.id.dk_pb_loading);
		tvMessage = view.findViewById(R.id.dk_tv_message);

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
	public D setColorFilter(@Nullable Integer color) {
		this.filterColor = color;
		if (pbLoading != null && color != null) {
			pbLoading.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		}
		return (D) this;
	}
}
