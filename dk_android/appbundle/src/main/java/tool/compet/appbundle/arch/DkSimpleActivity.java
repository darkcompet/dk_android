/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.appbundle.arch;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.arch.navigator.DkFragmentNavigator;
import tool.compet.appbundle.arch.scopedTopic.TopicProvider;
import tool.compet.appbundle.binder.DkBinder;
import tool.compet.appbundle.floatingbar.DkSnackbar;
import tool.compet.appbundle.floatingbar.DkToastbar;
import tool.compet.core.BuildConfig;
import tool.compet.core.util.DkLogs;

/**
 * All activities should subclass this to work with support of Dk library as possible.
 * <p></p>
 * Be aware of lifecycle in Activity: if activity is not going to be destroyed and
 * returns to foreground after onStop(), then onRestart() -> onStart() will be called respectively.
 */
public abstract class DkSimpleActivity extends AppCompatActivity implements DkActivity,
	DkViewModelStore, DkFragmentNavigator.Callback {

	private DkFragmentNavigator navigator;

	/**
	 * Must provide id of fragent container via {@link DkSimpleFragment#fragmentContainerId()}.
	 */
	@Override
	public DkFragmentNavigator getChildNavigator() {
		if (navigator == null) {
			int containerId = fragmentContainerId();

			if (containerId <= 0) {
				DkLogs.complain(this, "Invalid fragmentContainerId: " + containerId);
			}

			navigator = new DkFragmentNavigator(containerId, getSupportFragmentManager(), this);
		}

		return navigator;
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
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreate");
		}

		super.onCreate(savedInstanceState);

		// Set content view
		int layoutId = layoutResourceId();

		if (layoutId <= 0) {
			DkLogs.complain(this, "Invalid layoutId: %d", layoutId);
		}

		View layout = View.inflate(this, layoutId, null);
		setContentView(layout);

		DkBinder.bindViews(this, layout);
	}

	@CallSuper
	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onPostCreate");
		}
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStart");
		}
		super.onStart();
	}

	@Override
	protected void onResume() {
		onActive(true);
		super.onResume();
	}

	@Override
	public void onActive(boolean isResume) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isResume ? "onResume" : "onFront");
		}
	}

	@Override
	protected void onPause() {
		onInactive(true);
		super.onPause();
	}

	@Override
	public void onInactive(boolean isPause) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isPause ? "onPause" : "onBehind");
		}
	}

	@Override
	protected void onStop() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStop");
		}
		super.onStop();
	}

	// after onStop() is onCreate() or onDestroy()
	@Override
	protected void onRestart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRestart");
		}
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDestroy");
		}
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onLowMemory");
		}
		super.onLowMemory();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onConfigurationChanged");
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onSaveInstanceState");
		}
		if (navigator != null) {
			navigator.saveState(outState);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRestoreInstanceState");
		}
		if (navigator != null) {
			navigator.restoreState(savedInstanceState);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRestoreInstanceState");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int rc, @NonNull String[] perms, @NonNull int[] res) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onRequestPermissionsResult");
		}
		super.onRequestPermissionsResult(rc, perms, res);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	/**
	 * Get or Create new ViewModel instance which be owned by this Fragment.
	 */
	@Override
	public <M extends ViewModel> M getOwnViewModel(Class<M> modelType) {
		return new ViewModelProvider(this).get(modelType);
	}

	/**
	 * Get or Create new ViewModel instance which be owned by this Fragment.
	 */
	@Override
	public <M extends ViewModel> M getOwnViewModel(String key, Class<M> modelType) {
		return new ViewModelProvider(this).get(key, modelType);
	}

	/**
	 * Same with getAppViewModel().
	 */
	@Override
	public <M extends ViewModel> M getHostViewModel(Class<M> modelType) {
		return getAppViewModel(modelType.getName(), modelType);
	}

	/**
	 * Same with getAppViewModel().
	 */
	@Override
	public <M extends ViewModel> M getHostViewModel(String key, Class<M> modelType) {
		return getAppViewModel(key, modelType);
	}

	@Override
	public <M extends ViewModel> M getAppViewModel(Class<M> modelType) {
		return getAppViewModel(modelType.getName(), modelType);
	}

	@Override
	public <M extends ViewModel> M getAppViewModel(String key, Class<M> modelType) {
		Application app = getApplication();

		if (app instanceof DkSimpleApp) {
			return new ViewModelProvider((DkSimpleApp) app).get(key, modelType);
		}

		throw new RuntimeException("Not yet support");
	}

	/**
	 * Join and Get shared ViewModel instance which be owned by this Activity.
	 *
	 * @param register true if you want this View register the topic, otherwise just preview.
	 */
	@Override
	public <M> M getOwnTopic(Class<M> modelType, boolean register) {
		return getTopic(this, modelType.getName(), modelType, register);
	}

	/**
	 * Join and Get shared model instance which be owned by this Activity.
	 *
	 * @param register true if you want this View register the topic, otherwise just preview.
	 */
	@Override
	public <M> M getOwnTopic(String topicId, Class<M> modelType, boolean register) {
		return getTopic(this, topicId, modelType, register);
	}

	/**
	 * Same with #getAppTopic().
	 */
	@Override
	public <M> M getHostTopic(Class<M> modelType, boolean register) {
		return getAppTopic(modelType.getName(), modelType, register);
	}

	/**
	 * Same with #getAppTopic().
	 */
	@Override
	public <M> M getHostTopic(String topicId, Class<M> modelType, boolean register) {
		return getAppTopic(topicId, modelType, register);
	}

	/**
	 * Get shared model instance which be owned by current Application.
	 *
	 * @param register true if you want this View register the topic, otherwise just preview.
	 */
	@Override
	public <M> M getAppTopic(Class<M> modelType, boolean register) {
		return getAppTopic(modelType.getName(), modelType, register);
	}

	/**
	 * Get shared model instance which be owned by current Application.
	 *
	 * @param register true if you want this View register the topic, otherwise just preview.
	 */
	@Override
	public <M> M getAppTopic(String topicId, Class<M> modelType, boolean register) {
		Application app = getApplication();

		if (app instanceof DkSimpleApp) {
			return getTopic(((DkSimpleApp) app), topicId, modelType, register);
		}

		throw new RuntimeException("Not yet support");
	}

	/**
	 * Join and Get shared model instance which be owned by a owner (Application, Activity or Fragment...).
	 * The instance will be removed when no client observes the topic.
	 * Note that, you must call this method when host is in active state.
	 *
	 * @param register true if you want this View register the topic, otherwise just preview.
	 */
	@Override
	public <M> M getTopic(ViewModelStoreOwner owner, String topicId, Class<M> modelType, boolean register) {
		return new TopicProvider(owner, this).getTopic(topicId, modelType, register);
	}

	@Override
	public void dismiss() {
		finish();
	}

	public void snack(int msgRes, int type) {
		DkSnackbar.newIns(this)
			.asType(type)
			.setMessage(msgRes)
			.show();
	}

	public void snack(String message, int type) {
		DkSnackbar.newIns(this)
			.asType(type)
			.setMessage(message)
			.show();
	}

	public void toast(int msgRes) {
		DkToastbar.newIns(this)
			.setMessage(msgRes)
			.show();
	}

	public void toast(String message) {
		DkToastbar.newIns(this)
			.setMessage(message)
			.show();
	}
}
