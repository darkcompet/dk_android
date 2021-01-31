/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import tool.compet.appbundle.binder.DkBinder;
import tool.compet.core.BuildConfig;
import tool.compet.core.log.DkLogs;

/**
 * All activities should be subclass of this to work with support of Dk library as possible.
 * This provides below some basic features:
 * - Binding layout with DkBinder
 * - Basic lifecycle methods
 * - Implements some DkActivity methods
 *
 * <p></p>
 * Be aware of lifecycle in Activity: if activity is not going to be destroyed and
 * returns to foreground after onStop(), then onRestart() -> onStart() will be called respectively.
 */
public abstract class DkBaseActivity extends AppCompatActivity implements DkActivity {
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onConfigurationChanged");
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onSaveInstanceState");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onRestoreInstanceState");
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

    @Override
    public void dismiss() {
        finish();
    }
}
