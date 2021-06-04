/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.compact;

import android.app.Activity;
import android.app.AlertDialog;
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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.DkActivity;
import tool.compet.appbundle.DkApp;
import tool.compet.appbundle.R;
import tool.compet.appbundle.binder.DkBinder;
import tool.compet.appbundle.floatingbar.DkSnackbar;
import tool.compet.appbundle.floatingbar.DkToastbar;
import tool.compet.appbundle.navigator.DkFragmentNavigator;
import tool.compet.appbundle.navigator.DkNavigatorOwner;
import tool.compet.appbundle.topic.DkTopicOwner;
import tool.compet.core.BuildConfig;
import tool.compet.core.DkLogs;

/**
 * This is standard activity and provides below basic features:
 * - Debug log in lifecycle methods.
 * - [Optional] Navigator (we can forward, backward, dismiss... page easily)
 * - [Optional] ViewModel (overcome configuration-change)
 * - [Optional] Message display (snack, toast...)
 * - [Optional] Scoped topic (pass data between/under fragments, activities, app)
 *
 * <p></p>
 * Be aware of lifecycle in Activity: if activity is not going to be destroyed and
 * returns to foreground after onStop(), then onRestart() -> onStart() will be called respectively.
 */
public abstract class DkCompactActivity<L extends DkCompactLogic, D> extends AppCompatActivity
	implements DkActivity, DkFragmentNavigator.Callback, DkCompactView, DkNavigatorOwner {

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

			childNavigator = new DkFragmentNavigator(containerId, getSupportFragmentManager(), this);
		}

		return childNavigator;
	}

	@Override // from `DkNavigatorOwner`
	public DkFragmentNavigator getParentNavigator() {
		throw new RuntimeException("By default, activity does not provide parent navigator");
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
		context = this;

		// Must run after #super.onCreate()
		if (enableViewLogicDesignPattern()) {
			MyCompactRegistry.wire(this);

			if (logic != null) {
				logic.onCreate(this, savedInstanceState);
			}
		}

		// Set content view
		int layoutId = layoutResourceId();
		if (layoutId > 0) {
			// Pass `null` to indicate don't attach this layout to parent
			layout = View.inflate(this, layoutId, null);
			setContentView(layout);

			// Bind views
			if (enableBindingView()) {
				DkBinder.bindViews(this, layout);
			}
		}

		// Debug log as visual
		if (BuildConfig.DEBUG) {
			// Observe log to show at active state of the view
			MutableLiveData<String> log = new MutableLiveData<>();
			log.observe(this, message -> {
				new AlertDialog.Builder(this)
					.setTitle(R.string.error)
					.setMessage(message)
					.setCancelable(true)
					.setPositiveButton(R.string.close, ((dialog, which) -> {
						dialog.dismiss();
					}))
					.show();
			});

			// Show log via livedata
			DkLogs.logCallback = (type, message) -> {
				if (DkLogs.TYPE_WARNING.equals(type) || DkLogs.TYPE_ERROR.equals(type) || DkLogs.TYPE_EMERGENCY.equals(type)) {
					log.postValue(message);
				}
			};
		}
	}

	@CallSuper
	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onPostCreate");
		}
		super.onPostCreate(savedInstanceState);
		if (logic != null) {
			logic.onPostCreate(this, savedInstanceState);
		}
	}

	@Override
	protected void onStart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStart");
		}
		super.onStart();
		if (logic != null) {
			logic.onStart(this);
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
		if (logic != null) {
			logic.onRestart(this);
		}
	}

	// after onStop() is onCreate() or onDestroy()
	@Override
	protected void onRestart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRestart");
		}
		super.onRestart();
		if (logic != null) {
			logic.onRestart(this);
		}
	}

	@Override
	protected void onDestroy() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDestroy");
		}
		if (logic != null) {
			logic.onDestroy(this);
			logic = null;
		}

		this.app = null;
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
		if (logic != null) {
			logic.onLowMemory(this);
		}
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onConfigurationChanged");
		}
		super.onConfigurationChanged(newConfig);
		if (logic != null) {
			logic.onConfigurationChanged(this, newConfig);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRestoreInstanceState");
		}
		if (logic != null) {
			logic.onActivityResult(this, requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int rc, @NonNull String[] perms, @NonNull int[] res) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRequestPermissionsResult");
		}
		if (logic != null) {
			logic.onRequestPermissionsResult(this, rc, perms, res);
		}
		super.onRequestPermissionsResult(rc, perms, res);
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

	@Override
	public void onActive(boolean isResume) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isResume ? "onResume" : "onActive");
		}
		if (logic != null) {
			logic.onActive(this, isResume);
		}
	}

	@Override
	public void onInactive(boolean isPause) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isPause ? "onPause" : "onInactive");
		}
		if (logic != null) {
			logic.onInactive(this, isPause);
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
		if (logic != null) {
			logic.onSaveInstanceState(this, outState);
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
		if (logic != null) {
			logic.onRestoreInstanceState(this, savedInstanceState);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	// region ViewModel

	// Get or Create new ViewModel instance which be owned by this activity.
	public <M extends ViewModel> M obtainOwnViewModel(String key, Class<M> modelType) {
		return new ViewModelProvider(this).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by current app.
	public <M extends ViewModel> M obtainAppViewModel(String key, Class<M> modelType) {
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

	// region Floating bar

	public DkSnackbar snackbar() {
		return DkSnackbar.newIns(layout);
	}

	public DkToastbar toastbar() {
		return DkToastbar.newIns(layout);
	}

	// endregion Floating bar
}
