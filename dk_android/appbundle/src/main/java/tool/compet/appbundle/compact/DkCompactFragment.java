/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.compact;

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

import tool.compet.appbundle.BuildConfig;
import tool.compet.appbundle.DkApp;
import tool.compet.appbundle.DkFragment;
import tool.compet.appbundle.binder.DkBinder;
import tool.compet.appbundle.floatingbar.DkSnackbar;
import tool.compet.appbundle.floatingbar.DkToastbar;
import tool.compet.appbundle.navigator.DkFragmentNavigator;
import tool.compet.appbundle.navigator.DkNavigatorOwner;
import tool.compet.appbundle.topic.DkTopicOwner;
import tool.compet.core.DkLogs;

/**
 * This is standard fragment which provides a lot of optional features:
 * - Debug logs in lifecycle methods
 * - [Optional] ViewModel store owner (overcome configuration-change)
 * - [Optional] Bind layout, views with DkBinder, enable/disable via `enableBindingView()`
 * - [Optional] Navigator (we can forward, backward, dismiss... page easily)
 * - [Optional] Scoped topic (pass data between/under fragments, activities, app)
 * - [Optional] ViewLogic design pattern (coupling View and ViewLogic), enable/disable via `enableViewLogicDesignPattern()`
 * - [Optional] Floating bar to show message (snackbar, toastbar, ...)
 */
public abstract class DkCompactFragment<L extends DkCompactLogic, D> extends Fragment
	implements DkFragment, DkFragmentNavigator.Callback, DkCompactView, DkNavigatorOwner {

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
	// Logic for View (to instantiate it, subclass just provide generic type of logic when extends this view)
	@MyInjectLogic protected L logic;
	// Data for View (to instantiate it, subclass just provide generic type of data when extends this view)
	@MyInjectData protected D data;

	// region Navigator

	/**
	 * Must provide id of fragent container via {@link DkCompactFragment#fragmentContainerId()}.
	 */
	@Override // from `DkNavigatorOwner`
	public DkFragmentNavigator getChildNavigator() {
		if (childNavigator == null) {
			int containerId = fragmentContainerId();

			if (containerId <= 0) {
				DkLogs.complain(this, "Must provide `fragmentContainerId()`");
			}

			childNavigator = new DkFragmentNavigator(containerId, getChildFragmentManager(), this);
		}
		return childNavigator;
	}

	@Override // from `DkNavigatorOwner`
	public DkFragmentNavigator getParentNavigator() {
		Fragment parent = getParentFragment();
		DkFragmentNavigator parentNavigator = null;

		if (parent == null) {
			if (host instanceof DkNavigatorOwner) {
				parentNavigator = ((DkNavigatorOwner) host).getChildNavigator();
			}
		}
		else if (parent instanceof DkNavigatorOwner) {
			parentNavigator = ((DkNavigatorOwner) parent).getChildNavigator();
		}

		if (parentNavigator == null) {
			DkLogs.complain(this, "Must have a parent navigator own this fragment `%s`", getClass().getName());
		}

		return parentNavigator;
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
		if (this.app == null && this.host != null) {
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
			MyCompactRegistry.wire(this);

			if (logic != null) {
				logic.onCreate(host, savedInstanceState);
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
			// Pass `false` to indicate don't attach this layout to parent
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
		if (logic != null) {
			logic.onViewCreated(host, savedInstanceState);
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onActivityCreated");
		}
		super.onActivityCreated(savedInstanceState);
		if (logic != null) {
			logic.onActivityCreated(host, savedInstanceState);
		}
	}

	@Override
	public void onStart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStart");
		}
		super.onStart();
		if (logic != null) {
			logic.onStart(host);
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
		if (logic != null) {
			logic.onActive(host, isResume);
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
		if (logic != null) {
			logic.onInactive(host, isPause);
		}
	}

	@Override
	public void onStop() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStop");
		}
		if (logic != null) {
			logic.onStop(host);
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
		if (logic != null) {
			logic.onDestroy(host);
			logic = null;
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
		if (logic != null) {
			logic.onActivityResult(host, requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onActivityResult");
		}
		if (logic != null) {
			logic.onRequestPermissionsResult(host, requestCode, permissions, grantResults);
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public void onLowMemory() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onLowMemory");
		}
		if (logic != null) {
			logic.onLowMemory(host);
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
		if (logic != null) {
			logic.onSaveInstanceState(host, outState);
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
	 * Open dialog via parent navigator.
	 */
	public boolean open(DkFragmentNavigator navigator) {
		return navigator.beginTransaction().add(getClass()).commit();
	}

	/**
	 * Open dialog via parent navigator.
	 */
	public boolean open(DkFragmentNavigator navigator, int enterAnimRes, int exitAnimRes) {
		return navigator.beginTransaction().setAnims(enterAnimRes, exitAnimRes).add(getClass()).commit();
	}

	/**
	 * Close this view by tell parent navigator remove this.
	 */
	@Override // from `DkFragment`
	public boolean close() {
		return getParentNavigator().beginTransaction().remove(getClass().getName()).commit();
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

	/**
	 * Obtain the topic owner at app scope.
	 * When all owners of the topic were destroyed, the topic and its material will be cleared.
	 */
	public DkTopicOwner joinAppTopic(String topicId) {
		return new DkTopicOwner(topicId, app).registerClient(this);
	}

	/**
	 * Obtain the topic owner at host scope.
	 * When all owners of the topic were destroyed, the topic and its material will be cleared.
	 */
	public DkTopicOwner joinHostTopic(String topicId) {
		return new DkTopicOwner(topicId, host).registerClient(this);
	}

	/**
	 * Obtain the topic owner at own scope.
	 * When all owners of the topic were destroyed, the topic and its material will be cleared.
	 */
	public DkTopicOwner joinOwnTopic(String topicId) {
		return new DkTopicOwner(topicId, this).registerClient(this);
	}

	/**
	 * Just obtain the topic owner at app scope.
	 */
	public DkTopicOwner viewAppTopic(String topicId) {
		return new DkTopicOwner(topicId, app);
	}

	/**
	 * Just obtain the topic owner at host scope.
	 */
	public DkTopicOwner viewHostTopic(String topicId) {
		return new DkTopicOwner(topicId, host);
	}

	/**
	 * Just obtain the topic owner at own scope.
	 */
	public DkTopicOwner viewOwnTopic(String topicId) {
		return new DkTopicOwner(topicId, this);
	}

	// endregion Scoped topic

	// region Floating bar

	public DkSnackbar snackbar() {
		return DkSnackbar.newIns(layout);
	}

	public DkToastbar toastbar() {
		return DkToastbar.newIns(layout);
	}

	// endregion Floating bar
}
