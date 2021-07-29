/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.compactview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.BuildConfig;
import tool.compet.core.DkApp;
import tool.compet.core.DkFragment;
import tool.compet.core.DkLogcats;
import tool.compet.floatingbar.DkSnackbar;
import tool.compet.floatingbar.DkToastbar;
import tool.compet.navigation.DkFragmentNavigator;
import tool.compet.navigation.DkNavigatorOwner;
import tool.compet.topic.DkTopicOwner;
import tool.compet.core4j.DkUtils;

/**
 * This is standard fragment which provides a lot of optional features:
 * - Debug logs in lifecycle methods
 * - [Optional] ViewModel (model which be overcomed configuration-changes)
 * - [Optional] Bind layout, views with DkBinder, enable/disable via `enableBindingView()`
 * - [Optional] Navigator (we can forward, backward, dismiss... page easily)
 * - [Optional] Scoped topic (pass data between/under fragments, activities, app)
 * - [Optional] ViewLogic design pattern (coupling View&Logic via Model), enable/disable via `enableViewLogicDesignPattern()`.
 * When use this pattern, should remember that, Logic and View should not wait opposite returned value,
 * they should call opposite in one way and receive result at other callback-method from opposite.
 * - [Optional] Utility (floating bar, open activity or fragment, ...)
 */
public abstract class DkCompactFragment<L extends DkCompactLogic, M, B extends ViewDataBinding>
	extends Fragment
	implements DkFragment, DkCompactView, DkNavigatorOwner {

	/**
	 * Allow init Logic which couples with this View.
	 * So we can access the via `View.logic`.
	 */
	protected boolean enableVmlDesignPattern() {
		return true;
	}

	/**
	 * Allow init child views via databinding feature.
	 * So we can access to child views via `binder.*` instead of calling findViewById().
	 */
	protected boolean enableDataBinding() {
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
	@MyInjectLogic
	protected L logic;
	// Model for View (to instantiate it, subclass just provide generic type of model when extends this view)
	@MyInjectModel
	protected M model;
	// Binder for databinding (to initialize child views instead of findViewById())
	protected B binder;

	@Override
	public void onAttach(@NonNull Context context) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onAttach (context)");
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
			DkLogcats.info(this, "onAttach (activity)");
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
			DkLogcats.info(this, "onCreate");
		}
		super.onCreate(savedInstanceState);

		// Must run after #super.onCreate()
		if (enableVmlDesignPattern()) {
			MyVmlDesignPatternMaker.make(this, host, savedInstanceState);

			if (logic != null) {
				logic.onViewCreate(host, savedInstanceState);
			}
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onCreateView");
		}

		if (enableDataBinding()) {
			int layoutId = layoutResourceId();
			if (layoutId > 0) {
				// Pass `false` to indicate don't attach this layout to parent
				binder = DataBindingUtil.inflate(inflater, layoutId, container, false);
				layout = binder.getRoot();
			}
			else {
				DkLogcats.notice(this, "This fragment has no layout?");
			}
		}

		return layout;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onViewCreated");
		}
		// Let Logic run first, so View can use latest data which be updated from Logic
		if (logic != null) {
			logic.onViewReady(host, savedInstanceState);
		}
		super.onViewCreated(view, savedInstanceState);
	}

	@Override // onViewCreated() -> onViewStateRestored() -> onStart()
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onViewStateRestored");
		}
		if (childNavigator != null) {
			childNavigator.restoreInstanceState(savedInstanceState);
		}
		if (logic != null) {
			logic.onViewRestoreInstanceState(host, savedInstanceState);
		}
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onStart");
		}
		if (logic != null) {
			logic.onViewStart(host);
		}
		super.onStart();
	}

	@Override
	public void onResume() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onResume");
		}
		if (logic != null) {
			logic.onViewResume(host);
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onPause");
		}
		if (logic != null) {
			logic.onViewPause(host);
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onStop");
		}
		if (logic != null) {
			logic.onViewStop(host);
		}
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onDestroyView");
		}
		super.onDestroyView();
	}

	@Override // called before onDestroy()
	public void onSaveInstanceState(@NonNull Bundle outState) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onSaveInstanceState");
		}
		if (childNavigator != null) {
			childNavigator.storeInstanceState(outState);
		}
		if (logic != null) {
			logic.onViewSaveInstanceState(host, outState);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onDestroy");
		}
		if (logic != null) {
			logic.onViewDestroy(host);
		}
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onDetach");
		}

		this.host = null;
		this.context = null;

		super.onDetach();
	}

	@Override
	public void onLowMemory() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onLowMemory");
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
	@Override
	public boolean open(DkFragmentNavigator navigator) {
		return navigator.beginTransaction().add(this).commit();
	}

	/**
	 * Open dialog via parent navigator.
	 */
	public boolean open(DkFragmentNavigator navigator, int enterAnimRes, int exitAnimRes) {
		return navigator.beginTransaction().setAnims(enterAnimRes, exitAnimRes).add(this).commit();
	}

	/**
	 * Close this view by tell parent navigator remove this.
	 */
	@Override // from `DkFragment`
	public boolean close() {
		try {
			// Multiple times of calling `getParentNavigator()` maybe cause exception
			return getParentNavigator().beginTransaction().remove(this).commit();
		}
		catch (Exception e) {
			DkLogcats.error(this, e);
			return false;
		}
	}

	// region Navigator

	/**
	 * Must provide id of fragent container via {@link DkCompactFragment#fragmentContainerId()}.
	 */
	@Override // from `DkNavigatorOwner`
	public DkFragmentNavigator getChildNavigator() {
		if (childNavigator == null) {
			int containerId = fragmentContainerId();

			if (containerId <= 0) {
				DkUtils.complainAt(this, "Must provide `fragmentContainerId()`");
			}

			childNavigator = new DkFragmentNavigator(containerId, getChildFragmentManager());
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
			DkUtils.complainAt(this, "Must have a parent navigator own this fragment `%s`", getClass().getName());
		}

		return parentNavigator;
	}

	// endregion Navigator

	// region ViewModel

	// Get or Create new ViewModel instance which be owned by this Fragment.
	public <VM extends ViewModel> VM obtainOwnViewModel(String key, Class<VM> modelType) {
		return new ViewModelProvider(this).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by Activity which hosts this Fragment.
	public <VM extends ViewModel> VM obtainHostViewModel(String key, Class<VM> modelType) {
		return new ViewModelProvider(host).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by current app.
	public <VM extends ViewModel> VM obtainAppViewModel(String key, Class<VM> modelType) {
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

	// region Utility

	public Fragment instantiateFragment(Class<? extends Fragment> fragClass) {
		return getParentFragmentManager().getFragmentFactory().instantiate(context.getClassLoader(), fragClass.getName());
	}

	/**
	 * Listen lifecycle callbacks of descendant fragments managed by this activity.
	 *
	 * @param recursive TRUE to listen all descendant fragments under this host, that is,
	 *                  it includes all child fragments of child fragment-managers and so on.
	 *                  FALSE to listen only child fragments of the child-fragment-manager of this activity.
	 */
	public void registerFragmentLifecycleCallbacks(FragmentManager.FragmentLifecycleCallbacks callback, boolean recursive) {
		getChildFragmentManager().registerFragmentLifecycleCallbacks(callback, recursive);
	}

	public DkSnackbar snackbar() {
		return DkSnackbar.newIns(layout);
	}

	public DkToastbar toastbar() {
		return DkToastbar.newIns(layout);
	}

	// endregion Utility
}
