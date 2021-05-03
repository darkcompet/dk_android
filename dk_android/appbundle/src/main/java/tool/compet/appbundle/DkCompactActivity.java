/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle;

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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.floatingbar.DkSnackbar;
import tool.compet.appbundle.floatingbar.DkToastbar;
import tool.compet.core.BuildConfig;
import tool.compet.core.DkLogs;

/**
 * This is base activity and provides below basic features:
 * - Basic lifecycle methods
 * - Navigator (we can forward, backward, dismiss... page easily)
 * - ViewModel (overcome configuration-change)
 * - Message display (snack, toast...)
 * - Scoped topic (pass data between/under fragments, activities, app)
 *
 * <p></p>
 * Be aware of lifecycle in Activity: if activity is not going to be destroyed and
 * returns to foreground after onStop(), then onRestart() -> onStart() will be called respectively.
 */
public abstract class DkCompactActivity<VL extends DkCompactViewLogic> extends AppCompatActivity
	implements DkActivity, DkFragmentNavigator.Callback, DkCompactView {

	// Allow init ViewLogic which couples with this View
	protected boolean enableViewLogicDesignPattern() {
		return true;
	}

	// Allow DkBinder binds the layout and views
	protected boolean enableBindingView() {
		return true;
	}

	// Current app
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

	/**
	 * Must provide id of fragent container via {@link DkCompactFragment#fragmentContainerId()}.
	 */
	@Override
	public DkFragmentNavigator getChildNavigator() {
		if (childNavigator == null) {
			int containerId = fragmentContainerId();

			if (containerId <= 0) {
				DkLogs.complain(this, "Must provide fragmentContainerId (%s)", containerId);
			}

			childNavigator = new DkFragmentNavigator(containerId, getSupportFragmentManager(), this);
		}

		return childNavigator;
	}

	/**
	 * Subclass should use getIntent() in onResume() instead since we called #setIntent() here
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onNewIntent: " + intent);
		}

		setIntent(intent);

		super.onNewIntent(intent);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "attachBaseContext()");
		}
		super.attachBaseContext(newBase);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreate");
		}

		super.onCreate(savedInstanceState);

		app = (DkApp) getApplication();
		host = this;
		context = this;

		// Must run after #super.onCreate()
		if (enableViewLogicDesignPattern()) {
			viewLogic = new MyCompactInjector(this).injectViewLogic();

			if (viewLogic != null) {
				viewLogic.onCreate(this, savedInstanceState);
			}
		}

		// Set content view
		int layoutId = layoutResourceId();
		if (layoutId > 0) {
			layout = View.inflate(this, layoutId, null);
			setContentView(layout);

			// Bind views
			if (enableBindingView()) {
				DkBinder.bindViews(this, layout);
			}
		}
	}

	@CallSuper
	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onPostCreate");
		}
		super.onPostCreate(savedInstanceState);
		if (viewLogic != null) {
			viewLogic.onPostCreate(this, savedInstanceState);
		}
	}

	@Override
	protected void onStart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStart");
		}
		super.onStart();
		if (viewLogic != null) {
			viewLogic.onStart(this);
		}
	}

	@Override
	protected void onResume() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onResume");
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onPause");
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStop");
		}
		super.onStop();
		if (viewLogic != null) {
			viewLogic.onRestart(this);
		}
	}

	// after onStop() is onCreate() or onDestroy()
	@Override
	protected void onRestart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRestart");
		}
		super.onRestart();
		if (viewLogic != null) {
			viewLogic.onRestart(this);
		}
	}

	@Override
	protected void onDestroy() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDestroy");
		}
		if (viewLogic != null) {
			viewLogic.onDestroy(this);
			viewLogic = null;
		}

		this.app = null;
		this.host = null;
		this.context = null;
		this.layout = null;

		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onLowMemory");
		}
		super.onLowMemory();
		if (viewLogic != null) {
			viewLogic.onLowMemory(this);
		}
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onConfigurationChanged");
		}
		super.onConfigurationChanged(newConfig);
		if (viewLogic != null) {
			viewLogic.onConfigurationChanged(this, newConfig);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRestoreInstanceState");
		}
		if (viewLogic != null) {
			viewLogic.onActivityResult(this, requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int rc, @NonNull String[] perms, @NonNull int[] res) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRequestPermissionsResult");
		}
		if (viewLogic != null) {
			viewLogic.onRequestPermissionsResult(this, rc, perms, res);
		}
		super.onRequestPermissionsResult(rc, perms, res);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public boolean close() {
		finish();
		return true;
	}

	@Override
	public void onActive(boolean isResume) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isResume ? "onResume" : "onActive");
		}
		if (viewLogic != null) {
			viewLogic.onActive(this, isResume);
		}
	}

	@Override
	public void onInactive(boolean isPause) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isPause ? "onPause" : "onInactive");
		}
		if (viewLogic != null) {
			viewLogic.onInactive(this, isPause);
		}
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onSaveInstanceState");
		}
		if (childNavigator != null) {
			childNavigator.saveState(outState);
		}
		if (viewLogic != null) {
			viewLogic.onSaveInstanceState(this, outState);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRestoreInstanceState");
		}
		if (childNavigator != null) {
			childNavigator.restoreState(savedInstanceState);
		}
		if (viewLogic != null) {
			viewLogic.onRestoreInstanceState(this, savedInstanceState);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	// region ViewModel

	// Get or Create new ViewModel instance which be owned by this activity.
	public <M extends ViewModel> M getOwnViewModel(String key, Class<M> modelType) {
		return new ViewModelProvider(this).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by current app.
	public <M extends ViewModel> M getAppViewModel(String key, Class<M> modelType) {
		Application app = getApplication();

		if (app instanceof ViewModelStoreOwner) {
			return new ViewModelProvider((ViewModelStoreOwner) app).get(key, modelType);
		}

		throw new RuntimeException("App must be subclass of `ViewModelStoreOwner`");
	}

	// endregion ViewModel

	// region Scoped topic

	// Target a topic in hostOwner

	// Obtain topic controller and then clear its materials
	public TheActivityTopicController cleanTopic(String topicId) {
		return new TheActivityTopicController(topicId, host, this).clear();
	}

	// Obtain topic provider
	public TheActivityTopicController refTopic(String topicId) {
		return new TheActivityTopicController(topicId, this, this);
	}

	// endregion Scoped topic

	// region Utility

	public void snack(int msgRes, int type) {
		DkSnackbar.newIns(this).color(type).message(msgRes).show();
	}

	public void snack(String message, int type) {
		DkSnackbar.newIns(this).color(type).message(message).show();
	}

	public void toast(int msgRes) {
		DkToastbar.newIns(this).message(msgRes).show();
	}

	public void toast(String message) {
		DkToastbar.newIns(this).message(message).show();
	}

	// endregion Utility
}
