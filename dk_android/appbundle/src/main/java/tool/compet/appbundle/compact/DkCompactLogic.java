/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.compact;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import tool.compet.appbundle.BuildConfig;
import tool.compet.core.DkLogs;
import tool.compet.core.DkRunner1;

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
public abstract class DkCompactLogic<V extends DkCompactView, D> extends ViewModel {
	// Indicate state of this logic and view
	protected static final int STATE_INVALID = 0;
	protected static final int STATE_INIT = 1;
	protected static final int STATE_CREATE = 2;
	protected static final int STATE_START = 3;
	protected static final int STATE_VIEW_READY = 4;
	protected static final int STATE_ACTIVE = 5;
	protected static final int STATE_RESUME = 6;
	protected static final int STATE_PAUSE = 7;
	protected static final int STATE_INACTIVE = 8;
	protected static final int STATE_STOP = 9;
	protected static final int STATE_DESTROY = 10;
	protected int state = STATE_INVALID;

	/**
	 * Reference to the View, this field  will be attached and detached respectively at #onCreate(), #onDestroy().
	 * Only use this field directly if you know the view is still available, otherwise lets use `sendToView()` instead.
	 * #Nullable
	 */
	protected V view;
	protected D data;

	/**
	 * This object overcomes configuration change, useful for viewLogics.
	 * Actions which sent to View when View was absent
	 * We need optimize this field since 2 consequence commands maybe update
	 * same part of View.
	 */
	private List<DkRunner1<V>> pendingCommands;

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
			addPendingCommand(command);
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
	 * It is coupled with `onDestroy()`.
	 */
	@CallSuper
	protected void onViewCreate(FragmentActivity host, @Nullable Bundle savedInstanceState) {
		state = STATE_CREATE;
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onStop()`.
	 */
	@CallSuper
	protected void onViewStart(FragmentActivity host) {
		state = STATE_START;
	}

	/**
	 * Called multiple times from View.
	 */
	@CallSuper
	protected void onViewReady(FragmentActivity host, @Nullable Bundle savedInstanceState) {
		state = STATE_VIEW_READY;
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onInactive()`.
	 */
	@CallSuper
	protected void onViewActive(FragmentActivity host, boolean isResume) {
		state = isResume ? STATE_RESUME : STATE_ACTIVE;

		if (isResume && view != null && pendingCommands != null) {
			for (DkRunner1<V> action : pendingCommands) {
				action.run(view);
			}
			if (BuildConfig.DEBUG) {
				DkLogs.info(this, "Executed %d pending actions", pendingCommands.size());
			}
			pendingCommands = null;
		}
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onActive()`.
	 */
	@CallSuper
	protected void onViewInactive(FragmentActivity host, boolean isPause) {
		state = isPause ? STATE_PAUSE : STATE_INACTIVE;
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onStart()`.
	 */
	@CallSuper
	protected void onViewStop(FragmentActivity host) {
		state = STATE_STOP;
	}

	/**
	 * Called multiple times from View.
	 * It is coupled with `onCreate()`.
	 */
	@CallSuper
	protected void onViewDestroy(FragmentActivity host) {
		state = STATE_DESTROY;
		view = null;
	}

	protected void onViewSaveInstanceState(Bundle outState) {
	}

	protected void onViewRestoreInstanceState(Bundle savedInstanceState) {
	}

	/**
	 * Called only one time when this is destroyed. At this time, the view is also destroyed.
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

	private void addPendingCommand(DkRunner1<V> command) {
		if (pendingCommands == null) {
			pendingCommands = new ArrayList<>();
		}
		pendingCommands.add(command);
	}
}
