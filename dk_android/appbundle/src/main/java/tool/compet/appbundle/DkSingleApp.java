/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.DkApp;

// Single application (lite version compare with multidex app).
public class DkSingleApp extends Application implements DkApp, ViewModelStoreOwner {
	protected static Context appContext;
	protected ViewModelStore viewModelStore;

	@Override
	public void onCreate() {
		super.onCreate();
		appContext = getApplicationContext();
	}

	/**
	 * Should NOT use this app context to inflate a view since it maybe not support attributes for View.
	 */
	public static Context getContext() {
		return appContext;
	}

	@NonNull
	@Override
	public ViewModelStore getViewModelStore() {
		if (viewModelStore == null) {
			viewModelStore = new ViewModelStore();
		}
		return viewModelStore;
	}
}
