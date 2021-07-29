/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.compactview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.BuildConfig;
import tool.compet.core.DkActivity;
import tool.compet.core.DkApp;
import tool.compet.core.DkLogcats;
import tool.compet.floatingbar.DkSnackbar;
import tool.compet.floatingbar.DkToastbar;
import tool.compet.navigation.DkFragmentNavigator;
import tool.compet.navigation.DkNavigatorOwner;
import tool.compet.topic.DkTopicOwner;
import tool.compet.core4j.DkUtils;

/**
 * This is standard activity and provides below basic features:
 * - Debug log in lifecycle methods.
 * - [Optional] Navigator (we can forward, backward, dismiss... page easily)
 * - [Optional] ViewModel (overcome configuration-change)
 * - [Optional] Message display (snack, toast...)
 * - [Optional] Scoped topic (pass data between/under fragments, activities, app)
 * - [Optional] ViewLogic design pattern (coupling View and Logic), enable/disable via `enableViewLogicDesignPattern()`.
 * When use this pattern, should remember that, Logic and View should not wait opposite returned value,
 * they should call opposite in one way and receive result at other callback-method from oppposite.
 * - [Optional] Utility (floating bar, open activity or fragment, ...)
 *
 * <p></p>
 * Be aware of lifecycle in Activity: if activity is not going to be destroyed and
 * returns to foreground after onStop(), then onRestart() -> onStart() will be called respectively.
 */
public abstract class DkCompactActivity<L extends DkCompactLogic, M, B extends ViewDataBinding>
	extends AppCompatActivity
	implements DkActivity, DkCompactView, DkNavigatorOwner {

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

	// Current app
	protected DkApp app;
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

	/**
	 * Subclass should use `getIntent()` in `onResume()` instead since we called `setIntent()` here.
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onNewIntent: " + intent);
		}
		setIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "attachBaseContext()");
		}
		super.attachBaseContext(newBase);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onCreate");
		}

		super.onCreate(savedInstanceState);

		app = (DkApp) getApplication();
		context = this;

		// Must run after #super.onCreate()
		if (enableVmlDesignPattern()) {
			MyVmlDesignPatternMaker.make(this, this, savedInstanceState);

			if (logic != null) {
				logic.onViewCreate(this, savedInstanceState);
			}
		}

		if (enableDataBinding()) {
			int layoutId = layoutResourceId();
			if (layoutId > 0) {
				// Pass `false` to indicate don't attach this layout to parent
				binder = DataBindingUtil.setContentView(this, layoutId);
				layout = binder.getRoot();
			}
			else {
				DkLogcats.notice(this, "This fragment has no layout?");
			}
		}
	}

	@CallSuper
	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onPostCreate");
		}
		// Let Logic run first, so View can use latest data which be updated from Logic
		if (logic != null) {
			logic.onViewReady(this, savedInstanceState);
		}
		super.onPostCreate(savedInstanceState);
	}

	@Override // onPostCreate() -> onRestoreInstanceState() -> onStart()
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onRestoreInstanceState");
		}
		if (childNavigator != null) {
			childNavigator.restoreInstanceState(savedInstanceState);
		}
		if (logic != null) {
			logic.onViewRestoreInstanceState(this, savedInstanceState);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onStart() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onStart");
		}
		if (logic != null) {
			logic.onViewStart(this);
		}
		super.onStart();
	}

	@Override
	protected void onResume() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onResume");
		}
		if (logic != null) {
			logic.onViewResume(this);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onPause");
		}
		if (logic != null) {
			logic.onViewPause(this);
		}
		super.onPause();
	}

	@Override // maybe called before onStop() or onDestroy()
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onSaveInstanceState");
		}
		if (childNavigator != null) {
			childNavigator.storeInstanceState(outState);
		}
		if (logic != null) {
			logic.onViewSaveInstanceState(this, outState);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onStop");
		}
		if (logic != null) {
			logic.onViewStop(this);
		}
		super.onStop();
	}

	// after onStop() is onCreate() or onDestroy()
	@Override
	protected void onRestart() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onRestart");
		}
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onDestroy");
		}
		if (logic != null) {
			logic.onViewDestroy(this);
		}

		this.app = null;
		this.context = null;

		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onLowMemory");
		}
		if (logic != null) {
			logic.onLowMemory(this);
		}
		super.onLowMemory();
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "onConfigurationChanged");
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	/**
	 * Finish this view by tell parent finish this.
	 */
	@Override
	public boolean close() {
		finish();
		return true;
	}

	public Fragment instantiateFragment(Class<? extends Fragment> fragClass) {
		return getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), fragClass.getName());
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

			childNavigator = new DkFragmentNavigator(containerId, getSupportFragmentManager());
		}

		return childNavigator;
	}

	@Override // from `DkNavigatorOwner`
	public DkFragmentNavigator getParentNavigator() {
		throw new RuntimeException("By default, activity does not provide parent navigator");
	}

	// endregion Navigator

	// region ViewModel

	// Get or Create new ViewModel instance which be owned by this activity.
	public <VM extends ViewModel> VM obtainOwnViewModel(String key, Class<VM> modelType) {
		return new ViewModelProvider(this).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by current app.
	public <VM extends ViewModel> VM obtainAppViewModel(String key, Class<VM> modelType) {
		Application app = getApplication();

		if (app instanceof ViewModelStoreOwner) {
			return new ViewModelProvider((ViewModelStoreOwner) app).get(key, modelType);
		}

		throw new RuntimeException("App must be subclass of `ViewModelStoreOwner`");
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
	 * Just obtain the topic owner at own scope.
	 */
	public DkTopicOwner viewOwnTopic(String topicId) {
		return new DkTopicOwner(topicId, this);
	}

	// endregion Scoped topic

	// region Utility

	/**
	 * Listen lifecycle callbacks of descendant fragments managed by this activity.
	 *
	 * @param recursive TRUE to listen all descendant fragments under this host, that is,
	 *                  it includes all child fragments of child fragment-managers and so on.
	 *                  FALSE to listen only child fragments of the child-fragment-manager of this activity.
	 */
	public void registerFragmentLifecycleCallbacks(FragmentManager.FragmentLifecycleCallbacks callback, boolean recursive) {
		getSupportFragmentManager().registerFragmentLifecycleCallbacks(callback, recursive);
	}

	public DkSnackbar snackbar() {
		return DkSnackbar.newIns(layout);
	}

	public DkToastbar toastbar() {
		return DkToastbar.newIns(layout);
	}

	// endregion Utility
}
