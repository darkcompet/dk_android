/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import tool.compet.core.type.DkCallback;
import tool.compet.core.log.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * Compact design pattern ViewLogic component. This will update View by access #view object or
 * call #sendToView() to obtain non-null #view when does not know #view is null or not.
 * <p></p>
 * This ViewLogic object can overcome configuration change, so to communicate between Screens,
 * you should use #view.getHostTopic() to obtain scoped-topic for a group of screens you wanna share.
 */
public abstract class DkCompactViewLogic<V extends DkCompactView> {
    protected static final int STATE_CREATE = 1;
    protected static final int STATE_START = 2;
    protected static final int STATE_RESUME = 3;
    protected static final int STATE_PAUSE = 4;
    protected static final int STATE_STOP = 5;
    protected static final int STATE_DESTROY = 6;

    // Reference to the View, this field  will be attached and detached respectively at #onCreate(), #onDestroy().
    // Only use this field directly if you know the view is still available, otherwise lets use `sendToView()` instead.
    @Nullable protected V view;

    protected boolean isActivityOwner;
    protected boolean isFragmentOwner;
    protected int lifeCycleState;

    // This object overcomes configuration change, useful for viewLogics.
    // Actions which sent to View when View was absent
    // We need optimize this field since 2 consequence commands maybe update
    // same part of View.
    private ArrayList<DkCallback<V>> _pendingCommands;

    // Indicates whether View has ever notified lifecycle-events to ViewLogic
    // It is useful for checking whether events (#onCreate(), #onDestroy()...) of View
    // is first time called or not, since lifecycle or configuration change maybe occured multiple times.
    protected boolean isCalledOnCreate;
    protected boolean isCalledOnActivityCreated;
    protected boolean isCalledOnViewCreated;
    protected boolean isCalledOnStart;
    protected boolean isCalledOnActive;
    protected boolean isCalledOnInactive;
    protected boolean isCalledOnStop;
    protected boolean isCalledOnDestroy;
    protected boolean isCalledOnActivityResult;
    protected boolean isCalledOnLowMemory;
    protected boolean isCalledOnSaveInstanceState;
    protected boolean isCalledOnRequestPermissionsResult;

    // Below fields are for ViewLogic of Activity
    protected boolean isCalledOnRestart;
    protected boolean isCalledOnConfigurationChanged;
    protected boolean isCalledOnRestoreInstanceState;
    protected boolean isCalledOnPostCreate;

    void attachView(V view) {
        this.view = view;
        isActivityOwner = view instanceof Activity;
        isFragmentOwner = view instanceof Fragment;
    }

    void detachView() {
        view = null;
        isActivityOwner = false;
        isFragmentOwner = false;
    }

    /**
     * Use this method can avoid checking View is null or not at each invocation. As well the action
     * also preversed when View is destroyed since configuration changed, the View will receive
     * the action at next coming time (maybe at #onResume()).
     */
    protected void sendToView(DkCallback<V> command) {
        if (view != null && lifeCycleState == STATE_RESUME) {
            command.call(view);
        }
        else {
            _addPendingCommand(command);
        }
    }

    @CallSuper
    protected void onCreate(FragmentActivity host, @Nullable Bundle savedInstanceState) {
        isCalledOnCreate = true;
        lifeCycleState = STATE_CREATE;
    }

    @CallSuper
    public void onPostCreate(DkCompactActivity host, Bundle savedInstanceState) {
        if (isFragmentOwner) {
            DkLogs.complain(this, "Only ViewLogic of Activity can call this");
        }
        else if (isActivityOwner) {
            isCalledOnPostCreate = true;
        }
    }

    @CallSuper
    protected void onStart(FragmentActivity host) {
        isCalledOnStart = true;
        lifeCycleState = STATE_START;
    }

    @CallSuper
    protected void onViewCreated(FragmentActivity host, @Nullable Bundle savedInstanceState) {
        isCalledOnViewCreated = true;
    }

    @CallSuper
    protected void onActivityCreated(FragmentActivity host, @Nullable Bundle savedInstanceState) {
        isCalledOnActivityCreated = true;
    }

    @CallSuper
    protected void onActive(FragmentActivity host, boolean isResume) {
        if (isResume) {
            lifeCycleState = STATE_RESUME;
        }
        isCalledOnActive = true;

        if (isResume && view != null && _pendingCommands != null) {
            for (DkCallback<V> action : _pendingCommands) {
                action.call(view);
            }
            if (DEBUG) {
                DkLogs.info(this, "Executed %d pending actions", _pendingCommands.size());
            }
            _pendingCommands = null;
        }
    }

    @CallSuper
    protected void onInactive(FragmentActivity host, boolean isPause) {
        if (isPause) {
            lifeCycleState = STATE_PAUSE;
        }
        isCalledOnInactive = true;
    }

    @CallSuper
    protected void onStop(FragmentActivity host) {
        lifeCycleState = STATE_STOP;
        isCalledOnStop = true;
    }

    @CallSuper
    protected void onRestart(FragmentActivity host) {
        if (isFragmentOwner) {
            DkLogs.complain(this, "Only ViewLogic of Activity can call this");
        }
        else if (isActivityOwner) {
            isCalledOnRestart = true;
        }
    }

    @CallSuper
    protected void onDestroy(FragmentActivity host) {
        lifeCycleState = STATE_DESTROY;
        isCalledOnDestroy = true;

        _pendingCommands = null;
        detachView();
    }

    @CallSuper
    protected void onLowMemory(FragmentActivity host) {
        isCalledOnLowMemory = true;
    }

    @CallSuper
    protected void onConfigurationChanged(FragmentActivity host, Configuration newConfig) {
        if (isFragmentOwner) {
            DkLogs.complain(this, "Only ViewLogic of Activity can call this");
        }
        else if (isActivityOwner) {
            isCalledOnConfigurationChanged = true;
        }
    }

    @CallSuper
    protected void onSaveInstanceState(FragmentActivity host, @NonNull Bundle outState) {
        isCalledOnSaveInstanceState = true;
    }

    @CallSuper
    protected void onRestoreInstanceState(FragmentActivity host, Bundle savedInstanceState) {
        if (isFragmentOwner) {
            DkLogs.complain(this, "Only ViewLogic of Activity can call this");
        }
        else if (isActivityOwner) {
            isCalledOnRestoreInstanceState = true;
        }
    }

    @CallSuper
    protected void onActivityResult(FragmentActivity host, int requestCode, int resultCode, Intent data) {
        isCalledOnActivityResult = true;
    }

    @CallSuper
    protected void onRequestPermissionsResult(FragmentActivity host, int rc, @NonNull String[] perms, @NonNull int[] res) {
        isCalledOnRequestPermissionsResult = true;
    }

    private void _addPendingCommand(DkCallback<V> command) {
        if (_pendingCommands == null) {
            _pendingCommands = new ArrayList<>();
        }
        _pendingCommands.add(command);
    }
}
