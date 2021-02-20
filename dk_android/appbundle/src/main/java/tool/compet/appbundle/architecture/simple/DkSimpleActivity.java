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
import tool.compet.appbundle.architecture.DkViewModelStoreInf;
import tool.compet.appbundle.architecture.navigator.DkFragmentNavigator;
import tool.compet.appbundle.architecture.topic.DkTopicProvider;
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
public abstract class DkSimpleActivity extends DkActivity implements DkViewModelStoreInf, DkFragmentNavigator.Callback {
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

    @Override
    public <M> M ownTopic(Class<M> modelClass) {
        return ownTopic(modelClass, true);
    }

    @Override
    public <M> M ownTopic(Class<M> modelType, boolean listen) {
        return ownTopic(modelType.getName(), modelType, listen);
    }

    @Override
    public <M> M ownTopic(String topicId, Class<M> modelType) {
        return ownTopic(topicId, modelType, true);
    }

    /**
     * Get or Create shared model instance which be owned by this Activity.
     */
    @Override
    public <M> M ownTopic(String topicId, Class<M> modelType, boolean listen) {
        return topic(this, topicId, modelType, listen);
    }

    @Override
    public <M> M hostTopic(Class<M> modelClass) {
        return hostTopic(modelClass, true);
    }
    
    @Override
    public <M> M hostTopic(Class<M> modelType, boolean listen) {
        return hostTopic(modelType.getName(), modelType, listen);
    }

    @Override
    public <M> M hostTopic(String topicId, Class<M> modelType) {
        return hostTopic(topicId, modelType, true);
    }

    /**
     * Same with `appTopic()`.
     */
    @Override
    public <M> M hostTopic(String topicId, Class<M> modelType, boolean listen) {
        return appTopic(topicId, modelType, listen);
    }

    @Override
    public <M> M appTopic(Class<M> modelClass) {
        return appTopic(modelClass, true);
    }

    @Override
    public <M> M appTopic(Class<M> modelType, boolean listen) {
        return appTopic(modelType.getName(), modelType, listen);
    }

    @Override
    public <M> M appTopic(String topicId, Class<M> modelType) {
        return appTopic(topicId, modelType, true);
    }

    /**
     * Get or Create shared model instance which be owned by current app.
     */
    @Override
    public <M> M appTopic(String topicId, Class<M> modelType, boolean listen) {
        Application app = getApplication();

        if (app instanceof ViewModelStoreOwner) {
            return topic(((ViewModelStoreOwner) app), topicId, modelType, listen);
        }

        throw new RuntimeException("App must be subclass of ViewModelStoreOwner");
    }

    /**
     * Get or Create (new if not exists) shared model instance which be owned by a owner (Application, Activity, Fragment, ...).
     * The topic will be removed when no client observes the topic or the owner's ViewModel was destroyed.
     * Note that, you must call this method when host of this is in active state.
     *
     * @param listen true if you also wanna listen the topic, that is, this view will
     *               become listener of the topic. Otherwise just getOrCreate.
     */
    @Override
    public <M> M topic(ViewModelStoreOwner owner, String topicId, Class<M> modelType, boolean listen) {
        return new DkTopicProvider(owner, this).getOrCreateModelAtTopic(topicId, modelType, listen);
    }

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
