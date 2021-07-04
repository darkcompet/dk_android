/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.compact;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
public abstract class DkCompactDialogFragment<D>
	extends DkCompactFragment
	implements DkDialogFragment {

	/**
	 * By default, we disable binder for dialog.
	 */
	@Override
	protected boolean enableBindingView() {
		return false;
	}

	/**
	 * By default, we disable VML design pattern for dialog.
	 */
	@Override
	protected boolean enableViewLogicDesignPattern() {
		return false;
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
	}

	@CallSuper
	protected void restoreInstanceState(@Nullable Bundle savedInstanceState) {
	}

	// region Protected (overridable)

	// endregion Protected (overridable)

	// region Private

	// endregion Private

	// region Get/Set

	// endregion Get/Set
}
