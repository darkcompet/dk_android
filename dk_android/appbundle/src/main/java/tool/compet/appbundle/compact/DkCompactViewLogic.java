/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.compact;

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
import java.util.List;

import tool.compet.core.DkLogs;
import tool.compet.core.DkRunner1;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * Compact design pattern ViewLogic component. This will update View by access #view object or
 * call #sendToView() to obtain non-null #view when does not know #view is null or not.
 * <p></p>
 * This ViewLogic object can overcome configuration change, so to communicate between Screens,
 * you should use #view.getHostTopic() to obtain scoped-topic for a group of screens you wanna share.
 * Note that, state of view maybe changed multiple times since lifecycle or configuration maybe
 * often occured.
 */
public abstract class DkCompactViewLogic<V extends DkCompactView> {
	// Lifecycle state of the view
	protected int lifeCycleState = STATE_INVALID;
	// Lifecycle state value of `lifeCycleState`
	protected static final int STATE_INVALID = 0;
	protected static final int STATE_CREATE = 1;
	protected static final int STATE_START = 2;
	protected static final int STATE_VIEW_CREATED = 3;
	protected static final int STATE_ACTIVITY_CREATED = 4;
	protected static final int STATE_ACTIVE = 5;
	protected static final int STATE_RESUME = 6;
	protected static final int STATE_PAUSE = 7;
	protected static final int STATE_INACTIVE = 8;
	protected static final int STATE_STOP = 9;
	protected static final int STATE_DESTROY = 10;

	// Reference to the View, this field  will be attached and detached respectively at #onCreate(), #onDestroy().
	// Only use this field directly if you know the view is still available, otherwise lets use `sendToView()` instead.
	// @Nullable
	protected V view;

	protected boolean isActivityOwner;
	protected boolean isFragmentOwner;

	// This object overcomes configuration change, useful for viewLogics.
	// Actions which sent to View when View was absent
	// We need optimize this field since 2 consequence commands maybe update
	// same part of View.
	private List<DkRunner1<V>> pendingCommands;

	// Below fields are for ViewLogic of Activity
	protected boolean isCalledOnRestart;
	protected boolean isCalledOnConfigurationChanged;
	protected boolean isCalledOnRestoreInstanceState;
	protected boolean isCalledOnPostCreate;

	void attachView(V view) {
		this.view = view;
		this.isActivityOwner = view instanceof Activity;
		this.isFragmentOwner = view instanceof Fragment;
	}

	void detachView() {
		this.view = null;
		this.isActivityOwner = false;
		this.isFragmentOwner = false;
	}

	/**
	 * Use this method can avoid checking View is null or not at each invocation. As well the action
	 * also preversed when View is destroyed since configuration changed, the View will receive
	 * the action at next coming time (maybe at #onResume()).
	 */
	protected void sendToView(DkRunner1<V> command) {
		if (view != null) {
			command.run(view);
		}
		else {
			addPendingCommand(command);
		}
	}

	@CallSuper
	protected void onCreate(FragmentActivity host, @Nullable Bundle savedInstanceState) {
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
		lifeCycleState = STATE_START;
	}

	@CallSuper
	protected void onViewCreated(FragmentActivity host, @Nullable Bundle savedInstanceState) {
		lifeCycleState = STATE_VIEW_CREATED;
	}

	@CallSuper
	protected void onActivityCreated(FragmentActivity host, @Nullable Bundle savedInstanceState) {
		lifeCycleState = STATE_ACTIVITY_CREATED;
	}

	@CallSuper
	protected void onActive(FragmentActivity host, boolean isResume) {
		lifeCycleState = isResume ? STATE_RESUME : STATE_ACTIVE;

		if (isResume && view != null && pendingCommands != null) {
			for (DkRunner1<V> action : pendingCommands) {
				action.run(view);
			}
			if (DEBUG) {
				DkLogs.info(this, "Executed %d pending actions", pendingCommands.size());
			}
			pendingCommands = null;
		}
	}

	@CallSuper
	protected void onInactive(FragmentActivity host, boolean isPause) {
		lifeCycleState = isPause ? STATE_PAUSE : STATE_INACTIVE;
	}

	@CallSuper
	protected void onStop(FragmentActivity host) {
		lifeCycleState = STATE_STOP;
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
		pendingCommands = null;
		detachView();
	}

	@CallSuper
	protected void onLowMemory(FragmentActivity host) {
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
	}

	@CallSuper
	protected void onRequestPermissionsResult(FragmentActivity host, int rc, @NonNull String[] perms, @NonNull int[] res) {
	}

	private void addPendingCommand(DkRunner1<V> command) {
		if (pendingCommands == null) {
			pendingCommands = new ArrayList<>();
		}
		pendingCommands.add(command);
	}
}
