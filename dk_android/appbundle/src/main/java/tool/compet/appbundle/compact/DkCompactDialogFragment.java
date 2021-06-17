/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.compact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tool.compet.appbundle.DkDialogFragment;
import tool.compet.appbundle.navigator.DkFragmentNavigator;
import tool.compet.appbundle.navigator.DkNavigatorOwner;

/**
 * This is standard dialog and provides some below features:
 * - [Optional] Navigator (back, next fragment)
 * - [Optional] ViewModel (overcome configuration-changes)
 * - [Optional] Scoped topic (for communication between host and other fragments)
 *
 * In theory, this does not provide ViewLogic design pattern since we consider
 * a dialog as a view of its parent (activity or fragment).
 */
@SuppressWarnings("unchecked")
public abstract class DkCompactDialogFragment<D>
	extends DkCompactFragment
	implements DkDialogFragment, DkFragmentNavigator.Callback, DkNavigatorOwner {

	protected boolean cancelable;

	/**
	 * By default, we disable binder for dialog.
	 */
	@Override
	protected boolean enableBindingView() {
		return false;
	}

	/**
	 * By default, we disable ViewLogic design pattern for dialog.
	 */
	@Override
	protected boolean enableViewLogicDesignPattern() {
		return false;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}

	// region Protected (overridable)

	// endregion Protected (overridable)

	// region Private

	// endregion Private

	// region Get/Set

	public D setCancellable(boolean cancelable) {
		this.cancelable = cancelable;
		return (D) this;
	}

	// endregion Get/Set
}
