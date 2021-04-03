/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.compact;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tool.compet.appbundle.architecture.simple.DkSimpleActivity;

/**
 * This is extended version of `DkSimpleActivity`, compact basic and needed features.
 */
public abstract class DkCompactActivity<VL extends DkCompactViewLogic> extends DkSimpleActivity implements DkCompactView {
	// To instantiate ViewLogic, subclass should provide generic type of ViewLogic when extends the class
	@MyInjectViewLogic
	protected VL viewLogic;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Must run after #super.onCreate()
		viewLogic = new MyCompactInjector(this).inject();
		if (viewLogic != null) {
			viewLogic.onCreate(this, savedInstanceState);
		}
	}

	@CallSuper
	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (viewLogic != null) {
			viewLogic.onPostCreate(this, savedInstanceState);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (viewLogic != null) {
			viewLogic.onStart(this);
		}
	}

	@Override
	public void onActive(boolean isResume) {
		super.onActive(isResume);
		if (viewLogic != null) {
			viewLogic.onActive(this, isResume);
		}
	}

	@Override
	public void onInactive(boolean isPause) {
		super.onInactive(isPause);
		if (viewLogic != null) {
			viewLogic.onInactive(this, isPause);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (viewLogic != null) {
			viewLogic.onRestart(this);
		}
	}

	// Note: after onStop() is onCreate() or onDestroy()
	@Override
	protected void onRestart() {
		super.onRestart();
		if (viewLogic != null) {
			viewLogic.onRestart(this);
		}
	}

	@Override
	protected void onDestroy() {
		if (viewLogic != null) {
			viewLogic.onDestroy(this);
			viewLogic = null;
		}
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (viewLogic != null) {
			viewLogic.onLowMemory(this);
		}
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (viewLogic != null) {
			viewLogic.onConfigurationChanged(this, newConfig);
		}
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		if (viewLogic != null) {
			viewLogic.onSaveInstanceState(this, outState);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		if (viewLogic != null) {
			viewLogic.onRestoreInstanceState(this, savedInstanceState);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (viewLogic != null) {
			viewLogic.onActivityResult(this, requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int rc, @NonNull String[] perms, @NonNull int[] res) {
		if (viewLogic != null) {
			viewLogic.onRequestPermissionsResult(this, rc, perms, res);
		}
		super.onRequestPermissionsResult(rc, perms, res);
	}
}
