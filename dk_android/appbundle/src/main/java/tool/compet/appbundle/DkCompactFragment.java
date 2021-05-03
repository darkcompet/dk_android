/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.floatingbar.DkSnackbar;
import tool.compet.appbundle.floatingbar.DkToastbar;
import tool.compet.appbundle.floatingbar.DkUrgentSnackbar;
import tool.compet.appbundle.floatingbar.DkUrgentToastbar;
import tool.compet.core.DkLogs;

/**
 * Compact fragment which provides a lot of optional features:
 * - Debug logs in lifecycle methods
 * - [Optional] ViewModel store owner (overcome configuration-change)
 * - [Optional] Bind layout, views with DkBinder, enable/disable via `enableBindingView()`
 * - [Optional] Navigator (we can forward, backward, dismiss... page easily)
 * - [Optional] Scoped topic (pass data between/under fragments, activities, app)
 * - [Optional] ViewLogic design pattern (coupling View and ViewLogic), enable/disable via `enableViewLogicDesignPattern()`
 * - [Optional] Floating bar to show message (snackbar, toastbar, ...)
 */
public abstract class DkCompactFragment<VL extends DkCompactViewLogic> extends Fragment
	implements DkFragment, DkFragmentNavigator.Callback, DkCompactView {

	// Allow init ViewLogic which couples with this View
	protected boolean enableViewLogicDesignPattern() {
		return true;
	}

	// Allow DkBinder binds the layout and views
	protected boolean enableBindingView() {
		return true;
	}

	// Current application
	protected DkApp app;
	// Current fragment activity
	protected FragmentActivity host;
	// Current context
	protected Context context;
	// Layout of this view (normally is ViewGroup, but sometime, user maybe layout with single view)
	protected View layout;
	// Child navigator
	protected DkFragmentNavigator childNavigator;
	// ViewLogic (to instantiate it, subclass just provide generic type of ViewLogic when extends this view)
	@MyInjectViewLogic protected VL viewLogic;

	// region Navigator

	/**
	 * Must provide id of fragent container via {@link DkCompactFragment#fragmentContainerId()}.
	 */
	public DkFragmentNavigator getChildNavigator() {
		if (childNavigator == null) {
			int containerId = fragmentContainerId();
			if (containerId <= 0) {
				DkLogs.complain(this, "Must provide fragmentContainerId (%d) inside layout", containerId);
			}
			childNavigator = new DkFragmentNavigator(containerId, getChildFragmentManager(), this);
		}
		return childNavigator;
	}

	public DkFragmentNavigator getParentNavigator() {
		Fragment parent = getParentFragment();
		DkFragmentNavigator owner = null;

		if (parent == null) {
			if (host instanceof DkCompactActivity) {
				owner = ((DkCompactActivity) host).getChildNavigator();
			}
		}
		else if (parent instanceof DkCompactFragment) {
			owner = ((DkCompactFragment) parent).getChildNavigator();
		}

		if (owner == null) {
			DkLogs.complain(this, "Must have a parent navigator own this fragment `%s`", getClass().getName());
		}

		return owner;
	}

	// endregion Navigator

	@Override
	public void onAttach(@NonNull Context context) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onAttach (context)");
		}
		if (this.host == null) {
			this.host = getActivity();
		}
		if (this.context == null) {
			this.context = context;
		}
		if (this.app == null) {
			this.app = (DkApp) this.host.getApplication();
		}

		super.onAttach(context);
	}

	@Override
	@SuppressWarnings("deprecation") // still work on lower OS
	public void onAttach(@NonNull Activity activity) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onAttach (activity)");
		}
		if (this.context == null) {
			this.context = getContext();
		}
		if (this.host == null) {
			this.host = (FragmentActivity) activity;
		}
		if (this.app == null) {
			this.app = (DkApp) activity.getApplication();
		}

		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreate");
		}
		super.setRetainInstance(isRetainInstance()); // retain instance while configuration changes
		super.onCreate(savedInstanceState);

		// Must run after #super.onCreate()
		if (enableViewLogicDesignPattern()) {
			viewLogic = new MyCompactInjector(this).injectViewLogic();

			if (viewLogic != null) {
				viewLogic.onCreate(host, savedInstanceState);
			}
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreateView");
		}

		int layoutId = layoutResourceId();
		if (layoutId > 0) {
			layout = inflater.inflate(layoutId, container, false);

			// Bind views
			if (enableBindingView()) {
				DkBinder.bindViews(this, layout);
			}
		}

		return layout;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onViewCreated");
		}
		super.onViewCreated(view, savedInstanceState);
		if (viewLogic != null) {
			viewLogic.onViewCreated(host, savedInstanceState);
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onActivityCreated");
		}
		super.onActivityCreated(savedInstanceState);
		if (viewLogic != null) {
			viewLogic.onActivityCreated(host, savedInstanceState);
		}
	}

	@Override
	public void onStart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStart");
		}
		super.onStart();
		if (viewLogic != null) {
			viewLogic.onStart(host);
		}
	}

	@Override
	public void onResume() {
		onActive(true);
		super.onResume();
	}

	@Override
	public void onActive(boolean isResume) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isResume ? "onResume" : "onFront");
		}
		if (viewLogic != null) {
			viewLogic.onActive(host, isResume);
		}
	}

	@Override
	public void onPause() {
		onInactive(true);
		super.onPause();
	}

	@Override
	public void onInactive(boolean isPause) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isPause ? "onPause" : "onBehind");
		}
		if (viewLogic != null) {
			viewLogic.onInactive(host, isPause);
		}
	}

	@Override
	public void onStop() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStop");
		}
		if (viewLogic != null) {
			viewLogic.onStop(host);
		}
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDestroyView");
		}
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDestroy");
		}
		if (viewLogic != null) {
			viewLogic.onDestroy(host);
			viewLogic = null;
		}
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDetach");
		}

		this.app = null;
		this.host = null;
		this.context = null;
		this.layout = null;

		super.onDetach();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onActivityResult");
		}
		if (viewLogic != null) {
			viewLogic.onActivityResult(host, requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onActivityResult");
		}
		if (viewLogic != null) {
			viewLogic.onRequestPermissionsResult(host, requestCode, permissions, grantResults);
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public void onLowMemory() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onLowMemory");
		}
		if (viewLogic != null) {
			viewLogic.onLowMemory(host);
		}
		super.onLowMemory();
	}

	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onSaveInstanceState");
		}
		if (childNavigator != null) {
			childNavigator.saveState(outState);
		}
		if (viewLogic != null) {
			viewLogic.onSaveInstanceState(host, outState);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onViewStateRestored");
		}
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

	// region ViewModel

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

	// endregion ViewModel

	// region Scoped topic

	// Obtain topic controller and then clear its materials
	public TheFragmentTopicController cleanTopic(String topicId) {
		return new TheFragmentTopicController(topicId, host, this).clear();
	}

	// Obtain topic controller
	public TheFragmentTopicController refTopic(String topicId) {
		return new TheFragmentTopicController(topicId, host, this);
	}

	// endregion Scoped topic

	// region Floating bar

	public DkSnackbar snackbar() {
		return DkSnackbar.newIns(layout);
	}

	public DkSnackbar urgentSnackbar() {
		return DkUrgentSnackbar.newIns(layout);
	}

	public DkToastbar toastbar() {
		return DkToastbar.newIns(layout);
	}

	public DkToastbar urgentToastbar() {
		return DkUrgentToastbar.newIns(layout);
	}

	// endregion Floating bar
}
