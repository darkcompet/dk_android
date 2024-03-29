/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.compactview;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import tool.compet.BuildConfig;
import tool.compet.core.DkLogcats;
import tool.compet.core4j.DkRunner1;

/**
 * Compact design pattern `Logic` component. This will update `View` by access `view` object or
 * call `sendToView()` to obtain non-null view when does not know view is null or not.
 * <p></p>
 * In theory, Logic should not aware type of the view, and should see view as a callback or listener.
 * But for convenience, we declare view type for quickly access from editor.
 * <p></p>
 * This Logic object can overcome configuration change, so to communicate between pages,
 * you should use `view.obtainHostTopic()` to obtain scoped-topic for pages you wanna share.
 * <p></p>
 * Note that, view lifecycle methods maybe called multiple times since lifecycle or configuration maybe
 * often triggered.
 */
public abstract class DkCompactLogic<V extends DkCompactView, M> extends ViewModel {
	// Indicate state of this logic and view
	protected static final int STATE_INVALID = -1;
	protected static final int STATE_INIT = 0;
	protected static final int STATE_CREATE = 1;
	protected static final int STATE_VIEW_READY = 2;
	protected static final int STATE_START = 3;
	protected static final int STATE_RESUME = 4;
	protected static final int STATE_PAUSE = 5;
	protected static final int STATE_STOP = 6;
	protected static final int STATE_DESTROY = 7;
	protected int state = STATE_INVALID;

	// Reference to the View, this field  will be attached and detached respectively at #onCreate(), #onDestroy().
	// Only use this field directly if you know the view is still available, otherwise lets use `sendToView()` instead.
	// #Nullable
	protected V view;
	protected M model;

	// This object overcomes configuration change, useful for viewLogics.
	// Actions which sent to View when View was absent
	// We need optimize this field since 2 consequence commands maybe update
	// same part of View.
	private List<DkRunner1<V>> pendingActions;

	/**
	 * Use this method can avoid checking View is null or not at each invocation. As well the action
	 * also preversed when View is destroyed since configuration changed, the View will receive
	 * the action at next coming time (maybe at #onResume()).
	 */
	protected void sendToView(DkRunner1<V> command) {
		// View is not null, but layout maybe not yet ready, so we should see status of lifecycle state
		if (view != null && state >= STATE_VIEW_READY && state < STATE_DESTROY) {
			command.run(view);
		}
		else {
			addPendingAction(command);
		}
	}

	/**
	 * Called only one time when create Logic and Data.
	 * It is coupled with `onCleared()`.
	 */
	@CallSuper
	protected void onInit(FragmentActivity host, @Nullable Bundle savedInstanceState) {
		state = STATE_INIT;
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onViewDestroy()`.
	 */
	@CallSuper
	protected void onViewCreate(FragmentActivity host, @Nullable Bundle savedInstanceState) {
		state = STATE_CREATE;
	}

	/**
	 * Called multiple times from View. The app can use `view` directly from this time,
	 * since `layout` inside View was initialized completely.
	 */
	@CallSuper
	protected void onViewReady(FragmentActivity host, @Nullable Bundle savedInstanceState) {
		state = STATE_VIEW_READY;

		// Execute pending actions
		if (pendingActions != null && pendingActions.size() > 0) {
			for (DkRunner1<V> action : pendingActions) {
				action.run(view);
			}
			if (BuildConfig.DEBUG) {
				DkLogcats.info(this, "Executed %d pending actions", pendingActions.size());
			}
			pendingActions.clear();
		}
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onViewStop()`.
	 */
	@CallSuper
	protected void onViewStart(FragmentActivity host) {
		state = STATE_START;
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onViewInactive()`.
	 */
	@CallSuper
	protected void onViewResume(FragmentActivity host) {
		state = STATE_RESUME;
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onViewActive()`.
	 */
	@CallSuper
	protected void onViewPause(FragmentActivity host) {
		state = STATE_PAUSE;
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onViewStart()`.
	 */
	@CallSuper
	protected void onViewStop(FragmentActivity host) {
		state = STATE_STOP;
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onViewCreate()`.
	 * Subclass should stop using `view` at this time since we `layout` inside View
	 * is not ready.
	 */
	@CallSuper
	protected void onViewDestroy(FragmentActivity host) {
		state = STATE_DESTROY;
		view = null;
	}

	@CallSuper
	protected void onViewSaveInstanceState(FragmentActivity host, Bundle outState) {
	}

	@CallSuper
	protected void onViewRestoreInstanceState(FragmentActivity host, Bundle savedInstanceState) {
	}

	/**
	 * Called only one time when this logic is destroyed.
	 * It is coupled with `onInit()`.
	 */
	@Override
	protected void onCleared() {
		super.onCleared();
	}

	/**
	 * Called when the app is in low memory.
	 */
	@CallSuper
	protected void onLowMemory(FragmentActivity host) {
	}

	private void addPendingAction(DkRunner1<V> action) {
		if (pendingActions == null) {
			pendingActions = new ArrayList<>();
		}
		pendingActions.add(action);
	}
}
