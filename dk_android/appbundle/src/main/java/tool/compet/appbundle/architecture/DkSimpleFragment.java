/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.floatingbar.DkSnackbar;
import tool.compet.appbundle.floatingbar.DkToastbar;
import tool.compet.core.log.DkLogs;
import tool.compet.core.util.DkUtils;

/**
 * This extends `DkBaseFragment` and provides some below simple features:
 * - Navigator (we can forward, backward, dismiss... page easily)
 * - ViewModel (overcome configuration-change)
 * - Message display (snack, toast...)
 * - Scoped topic (pass data between/under fragments, activities, app)
 */
public abstract class DkSimpleFragment extends DkBaseFragment implements DkViewModelStore, DkFragmentNavigator.Callback {
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
            this.navigator = new DkFragmentNavigator(containerId, getChildFragmentManager(), this);
        }
        return navigator;
    }

    @Override
    public DkFragmentNavigator getParentNavigator() {
        Fragment parent = getParentFragment();
        DkFragmentNavigator owner = null;

        if (parent == null) {
            if (host instanceof DkSimpleActivity) {
                owner = ((DkSimpleActivity) host).getChildNavigator();
            }
        }
        else if (parent instanceof DkFragment) {
            owner = ((DkFragment) parent).getChildNavigator();
        }

        if (owner == null) {
            DkLogs.complain(this, "Must have a parent navigator own the fragment: %s", getClass().getName());
        }

        return owner;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (navigator != null) {
            navigator.saveState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (navigator != null) {
            navigator.restoreState(savedInstanceState);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * This will try to send back-event to children first. If has no child here,
     * then #dismiss() will be called in parent navigator.
     */
    @Override
    public boolean onBackPressed() {
        return (navigator != null && navigator.childCount() != 0) && navigator.onBackPressed();
    }

    /**
     * This will actual dismiss the view even though children exists.
     */
    @Override
    public void dismiss() {
        getParentNavigator().beginTransaction().remove(this).commit();
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
     * Get or Create new ViewModel instance which be owned by Activity which this contains this Fragment.
     */
    @Override
    public <M extends ViewModel> M getHostViewModel(Class<M> modelType) {
        return new ViewModelProvider(host).get(modelType);
    }

    /**
     * Get or Create new ViewModel instance which be owned by Activity which this contains this Fragment.
     */
    @Override
    public <M extends ViewModel> M getHostViewModel(String key, Class<M> modelType) {
        return new ViewModelProvider(host).get(key, modelType);
    }

    @Override
    public <M extends ViewModel> M getAppViewModel(Class<M> modelType) {
        return getAppViewModel(modelType.getName(), modelType);
    }

    @Override
    public <M extends ViewModel> M getAppViewModel(String key, Class<M> modelType) {
        Application app = host.getApplication();

        if (app instanceof DkApp) {
            return new ViewModelProvider((DkApp) app).get(key, modelType);
        }

        throw new RuntimeException("App must be subclass of #DkApp");
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
     * Get or Create shared ViewModel instance which be owned by this Fragment.
     */
    @Override
    public <M> M ownTopic(String topicId, Class<M> modelType, boolean listen) {
        return topic(this, topicId, modelType, listen);
    }

    @Override
    public <M> M hostTopic(Class<M> modelType) {
        return hostTopic(modelType.getName(), modelType, true);
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
     * Get or Create shared ViewModel instance which be owned by current Activity.
     */
    @Override
    public <M> M hostTopic(String topicId, Class<M> modelType, boolean listen) {
        return topic(host, topicId, modelType, listen);
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
        return appTopic(modelType.getName(), modelType, true);
    }

    /**
     * Get or Create shared ViewModel instance which be owned by the current app.
     */
    @Override
    public <M> M appTopic(String topicId, Class<M> modelType, boolean listen) {
        Application app = host.getApplication();

        if (app instanceof DkApp) {
            return topic(((DkApp) app), topicId, modelType, listen);
        }

        throw new RuntimeException("The app must implement #DkApp");
    }

    /**
     * Get or Create (new if not exists) shared model instance which be owned by a owner (Application, Activity, Fragment, ...).
     * The topic will be removed when no client observes the topic or the owner's ViewModel was destroyed.
     * Note that, you must call this method when host of this is in active state.
     *
     * @param listen true if you also wanna listen the topic, that is, the view will
     *               become listener of the topic. Otherwise just getOrCreate.
     */
    @Override
    public <M> M topic(ViewModelStoreOwner owner, String topicName, Class<M> modelType, boolean listen) {
        return new MyTopicProvider(owner, this).getOrCreateModelAtTopic(topicName, modelType, listen);
    }

    public void hideSoftKeyboard() {
        if (context != null) {
            DkUtils.hideSoftKeyboard(context, layout);
        }
    }

    public void snack(int msgRes, int type) {
        DkSnackbar.newIns(layout).asType(type).setMessage(msgRes).show();
    }

    public void snack(String message, int type) {
        DkSnackbar.newIns(layout).asType(type).setMessage(message).show();
    }

    public void toast(int msgRes) {
        DkToastbar.newIns(layout).setMessage(msgRes).show();
    }

    public void toast(String message) {
        DkToastbar.newIns(layout).setMessage(message).show();
    }
}
