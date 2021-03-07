/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.simple;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.architecture.DkActivity;
import tool.compet.appbundle.architecture.navigator.DkFragmentNavigator;
import tool.compet.appbundle.floatingbar.DkSnackbar;
import tool.compet.appbundle.floatingbar.DkToastbar;
import tool.compet.core.BuildConfig;
import tool.compet.core.log.DkLogs;

/**
 * This extends `DkBaseActivity` and provides below simple features:
 * - Navigator (we can forward, backward, dismiss... page easily)
 * - ViewModel (overcome configuration-change)
 * - Message display (snack, toast...)
 * - Scoped topic (pass data between/under fragments, activities, app)
 *
 * <p></p>
 * Be aware of lifecycle in Activity: if activity is not going to be destroyed and
 * returns to foreground after onStop(), then onRestart() -> onStart() will be called respectively.
 */
public abstract class DkSimpleActivity extends DkActivity implements DkFragmentNavigator.Callback {
    private DkFragmentNavigator navigator;

    /**
     * Must provide id of fragent container via {@link DkSimpleFragment#fragmentContainerId()}.
     */
    @Override
    public DkFragmentNavigator getChildNavigator() {
        if (navigator == null) {
            int containerId = fragmentContainerId();

            if (containerId <= 0) {
                DkLogs.complain(this, "Must provide fragmentContainerId (%s)", containerId);
            }

            navigator = new DkFragmentNavigator(containerId, getSupportFragmentManager(), this);
        }

        return navigator;
    }

    @Override
    public void onActive(boolean isResume) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, isResume ? "onResume" : "onActive");
        }
    }

    @Override
    public void onInactive(boolean isPause) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, isPause ? "onPause" : "onInactive");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (navigator != null) {
            navigator.saveState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (navigator != null) {
            navigator.restoreState(savedInstanceState);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    //
    // ViewModel region
    //

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

    //
    // Scoped topic region
    //

    // Obtain and Listen a topic in hostOwner
    public TheActivityTopicRegistry joinTopic(String topicId) {
        return new TheActivityTopicRegistry(topicId, this, this);
    }

    // Leave from a topic, and remove topic from hostOwner if no client listening
    public void leaveTopic(String topicId) {
        new TheActivityTopicRegistry(topicId, this, this).unregisterClient();
    }

    //
    // Utility region
    //

    public void snack(int msgRes, int type) {
        DkSnackbar.newIns(this).asType(type).setMessage(msgRes).show();
    }

    public void snack(String message, int type) {
        DkSnackbar.newIns(this).asType(type).setMessage(message).show();
    }

    public void toast(int msgRes) {
        DkToastbar.newIns(this).message(msgRes).show();
    }

    public void toast(String message) {
        DkToastbar.newIns(this).message(message).show();
    }
}
