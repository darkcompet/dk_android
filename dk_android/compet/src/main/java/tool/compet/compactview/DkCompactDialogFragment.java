/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.compactview;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import tool.compet.core.DkDialogFragment;

/**
 * This is standard dialog and provides some below features:
 * - [Optional] Navigator (back, next fragment)
 * - [Optional] ViewModel (overcome configuration-changes)
 * - [Optional] Scoped topic (for communication between host and other fragments)
 *
 * In theory, this does not provide ViewLogic design pattern since we consider
 * a dialog as a view of its parent (activity or fragment).
 */
public abstract class DkCompactDialogFragment<L extends DkCompactLogic, M, B extends ViewDataBinding>
	extends DkCompactFragment<L, M, B>
	implements DkDialogFragment {

	// Indicate this dialog is dismissable for some actions as: back pressed...
	// Default value is true, so this dialog can dismissed by user's cancel-action
	protected boolean cancelable = true;

	/**
	 * For dialog, it is not usual to make Logic and Model of Vml design pattern,
	 * so by default, we disable Vml design pattern for this.
	 */
	@Override
	protected boolean enableVmlDesignPattern() {
		return false;
	}

	/**
	 * Databinding is useful at all, so by default, we enable databinding for dialog,
	 */
	@Override
	protected boolean enableDataBinding() {
		return true;
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
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		restoreInstanceState(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		storeInstanceState(outState);
	}

	@Override // onViewCreated() -> onViewStateRestored() -> onStart()
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);

		restoreInstanceState(savedInstanceState);
	}

	@CallSuper
	protected void storeInstanceState(@NonNull Bundle outState) {
		outState.putBoolean("DkCompactDialogFragment.cancelable", cancelable);
	}

	@CallSuper
	protected void restoreInstanceState(@Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			cancelable = savedInstanceState.getBoolean("DkCompactDialogFragment.cancelable", false);
		}
	}

	// region Protected (overridable)

	// endregion Protected (overridable)

	// region Private

	// endregion Private

	// region Get/Set

	// endregion Get/Set
}
