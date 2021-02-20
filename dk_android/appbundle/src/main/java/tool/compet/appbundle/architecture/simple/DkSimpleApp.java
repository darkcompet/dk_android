/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.simple;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;

import tool.compet.appbundle.architecture.DkAppInf;

public class DkSimpleApp extends Application implements DkAppInf {
    protected static Context appContext;
    protected ViewModelStore viewModelStore;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    /**
     * Should not use app context to inflate a view since it maybe not support attributes for View.
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
