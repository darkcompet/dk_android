/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.simple;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.architecture.DkFragment;
import tool.compet.appbundle.architecture.navigator.DkFragmentNavigator;
import tool.compet.appbundle.floatingbar.DkSnackbar;
import tool.compet.appbundle.floatingbar.DkToastbar;
import tool.compet.appbundle.floatingbar.DkUrgentSnackbar;
import tool.compet.appbundle.floatingbar.DkUrgentToastbar;
import tool.compet.core.DkLogs;
import tool.compet.core.DkUtils;

/**
 * This extends `DkBaseFragment` and provides some below simple features:
 * - Navigator (we can forward, backward, dismiss... page easily)
 * - ViewModel (overcome configuration-change)
 * - Message display (snack, toast...)
 * - Scoped topic (pass data between/under fragments, activities, app)
 */
public abstract class DkSimpleFragment extends DkFragment implements DkFragmentNavigator.Callback {
	// Manages child fragments
	protected DkFragmentNavigator childNavigator;

	/**
	 * Must provide id of fragent container via {@link DkSimpleFragment#fragmentContainerId()}.
	 */
	public DkFragmentNavigator getChildNavigator() {
		if (childNavigator == null) {
			int containerId = fragmentContainerId();

			if (containerId <= 0) {
				DkLogs.complain(this, "Must provide fragmentContainerId (%d)", containerId);
			}

			childNavigator = new DkFragmentNavigator(containerId, getChildFragmentManager(), this);
		}
		return childNavigator;
	}

	public DkFragmentNavigator getParentNavigator() {
		Fragment parent = getParentFragment();
		DkFragmentNavigator owner = null;

		if (parent == null) {
			if (host instanceof DkSimpleActivity) {
				owner = ((DkSimpleActivity) host).getChildNavigator();
			}
		}
		else if (parent instanceof DkSimpleFragment) {
			owner = ((DkSimpleFragment) parent).getChildNavigator();
		}

		if (owner == null) {
			DkLogs.complain(this, "Must have a parent navigator own the fragment: %s", getClass().getName());
		}

		return owner;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		if (childNavigator != null) {
			childNavigator.saveState(outState);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		if (childNavigator != null) {
			childNavigator.restoreState(savedInstanceState);
		}
		super.onViewStateRestored(savedInstanceState);
	}

	/**
	 * Called when user pressed to physical back button, this is normally passed from current activity.
	 * When this view got an event, this send signal to children first, if no child was found,
	 * then this will call `close()` on it to dismiss itself.
	 *
	 * @return true if this view or child of it has dismissed successfully, otherwise false.
	 */
	@Override
	public boolean onBackPressed() {
		if (childNavigator == null || childNavigator.childCount() == 0) {
			return this.close();
		}
		return childNavigator.handleOnBackPressed();
	}

	/**
	 * Finish this view by tell parent remove this from navigator.
	 */
	@Override
	public boolean close() {
		return getParentNavigator().beginTransaction().remove(this).commit();
	}

	//
	// ViewModel region
	//

	// Get or Create new ViewModel instance which be owned by this Fragment.
	public <M extends ViewModel> M obtainOwnViewModel(String key, Class<M> modelType) {
		return new ViewModelProvider(this).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by Activity which this contains this Fragment.
	public <M extends ViewModel> M obtainHostViewModel(String key, Class<M> modelType) {
		return new ViewModelProvider(host).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by current app.
	public <M extends ViewModel> M obtainAppViewModel(String key, Class<M> modelType) {
		Application app = host.getApplication();

		if (app instanceof ViewModelStoreOwner) {
			return new ViewModelProvider((ViewModelStoreOwner) app).get(key, modelType);
		}

		throw new RuntimeException("App must be subclass of ViewModelStoreOwner");
	}

	//
	// Scoped topic region
	//

	// Obtain and Listen a topic in hostOwner
	public TheFragmentTopicRegistry joinTopic(String topicId) {
		return new TheFragmentTopicRegistry(topicId, host, this);
	}

	// Leave from a topic, and remove topic from hostOwner if no client listening
	public void leaveTopic(String topicId) {
		new TheFragmentTopicRegistry(topicId, host, this).unregisterClient();
	}

	//
	// Utility region
	//

	public void hideSoftKeyboard() {
		if (context != null && layout != null) {
			DkUtils.hideSoftKeyboard(context, layout);
		}
	}

	public DkSnackbar snackbar() {
		return DkSnackbar.newIns(layout);
	}

	public DkSnackbar urgentSnackbar() {
		return DkUrgentSnackbar.newIns(layout);
	}

	public void snack(int msgRes, int type) {
		snackbar().asType(type).setMessage(msgRes).show();
	}

	public void snackNow(int msgRes, int type) {
		urgentSnackbar().asType(type).setMessage(msgRes).show();
	}

	public void snack(String message, int type) {
		snackbar().asType(type).setMessage(message).show();
	}

	public void snackNow(String message, int type) {
		urgentSnackbar().asType(type).setMessage(message).show();
	}

	public DkToastbar toastbar() {
		return DkToastbar.newIns(layout);
	}

	public DkToastbar urgentToastbar() {
		return DkUrgentToastbar.newIns(layout);
	}

	public void toast(int msgRes) {
		toastbar().message(msgRes).show();
	}

	public void toastNow(int msgRes) {
		urgentToastbar().message(msgRes).show();
	}

	public void toast(String message) {
		toastbar().message(message).show();
	}

	public void toastNow(String message) {
		urgentToastbar().message(message).show();
	}
}
