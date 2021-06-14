/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.floatingbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import tool.compet.core.DkRunner;
import tool.compet.core.view.DkViews;

/**
 * This class uses 2 inputs: a view (bar) and ancestor ViewGroup of the view.
 * Perform your own animation on the bar with support of animation setting.
 */
@SuppressWarnings("unchecked")
public abstract class DkFloatingbar<B> implements View.OnTouchListener {
	protected abstract MyFloatingbarManager manager();

	protected static final long INFINITE_DURATION = -1;

	private static final int MSG_SHOW = 1;
	private static final int MSG_DISMISS = 2;

	// Background color of bar (for each type)
	public static final int TYPE_NORMAL = Color.parseColor("#333333");
	public static final int TYPE_CONFIRM = Color.parseColor("#009b8b");
	public static final int TYPE_ERROR = Color.parseColor("#ff0000");
	public static final int TYPE_WARNING = Color.parseColor("#ff9500");
	public static final int TYPE_INFO = Color.parseColor("#493ebb");
	public static final int TYPE_SUCCESS = Color.parseColor("#00bb4d");

	// Context from outside passed
	protected final Context context;
	// Use parent to animate this bar
	protected final ViewGroup parent;
	// Layout of the bar
	protected final View bar;
	// Duration for each animation
	protected long duration = INFINITE_DURATION;
	// Specify whether this bar be dismiss on touch
	protected boolean isDismissOnTouch = true;
	// Callback after shown
	protected DkRunner onShownCallback;
	// Callback on Dismiss
	protected DkRunner onDismissCallback;
	// Animation
	protected static Interpolator fastOutSlowIn = new FastOutSlowInInterpolator();

	protected ValueAnimator inAnimator;
	protected ValueAnimator outAnimator;

	private final AccessibilityManager accessibilityManager;
	// Animation poster
	private static final Handler handler;

	static {
		handler = new Handler(Looper.getMainLooper(), msg -> {
			switch (msg.what) {
				case MSG_SHOW: {
					((DkFloatingbar) msg.obj).showView();
					break;
				}
				case MSG_DISMISS: {
					((DkFloatingbar) msg.obj).dismissView();
					break;
				}
			}
			return false;
		});
	}

	private final MyFloatingbarManager.Callback actionCallback = new MyFloatingbarManager.Callback() {
		@Override
		public void show() {
			handler.sendMessageDelayed(Message.obtain(handler, MSG_SHOW, DkFloatingbar.this), 0);
		}

		@Override
		public void dismiss() {
			handler.sendMessageDelayed(Message.obtain(handler, MSG_DISMISS, DkFloatingbar.this), 0);
		}

		@Override
		public void dismissNow() {
			handler.removeCallbacksAndMessages(null);

			if (inAnimator != null) {
				inAnimator.end();
			}
			if (outAnimator != null) {
				outAnimator.end();
			}

			onViewDismissed();
		}
	};

	public DkFloatingbar(Context context, ViewGroup parent, View bar) {
		this.context = context;
		this.parent = parent;
		this.bar = bar;
		this.accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);

		bar.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean dismissOnTouch = isDismissOnTouch;

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (dismissOnTouch && DkViews.isInsideView(event, v)) {
					this.dismiss();
				}
				break;
			}
		}
		return dismissOnTouch;
	}

	public void show() {
		// Just tell manager schedule to show this bar
		manager().show(duration, actionCallback);
	}

	/**
	 * Close current bars of this bar-type (snackbar or toastbar), then show new bar immediate.
	 */
	public void showImmediate() {
		manager().dismissAllImmediate();
		show();
	}

	public void dismiss() {
		// Just tell manager schedule to dismiss this bar
		manager().dismiss(actionCallback);
	}

	public B asError() {
		return color(TYPE_ERROR);
	}

	public B asWarning() {
		return color(TYPE_WARNING);
	}

	public B asConfirm() {
		return color(TYPE_CONFIRM);
	}

	public B asSuccess() {
		return color(TYPE_SUCCESS);
	}

	public B asInfo() {
		return color(TYPE_INFO);
	}

	public B color(int color) {
		bar.setBackgroundColor(color);
		return (B) this;
	}

	/**
	 * Override this method to setup initial state of bar and customize ValueAnimator for in-animation.
	 */
	protected ValueAnimator prepareInAnimation() {
		int height = bar.getHeight();
		bar.setTranslationY(height);

		if (inAnimator == null) {
			inAnimator = new ValueAnimator();
		}
		inAnimator.setIntValues(height, 0);
		inAnimator.setDuration(200);
		inAnimator.setInterpolator(fastOutSlowIn);

		return inAnimator;
	}

	/**
	 * Override this method to setup initial state of bar and customize ValueAnimator for out-animation.
	 */
	protected ValueAnimator prepareOutAnimation() {
		if (outAnimator == null) {
			outAnimator  = new ValueAnimator();
		}
		outAnimator.setIntValues(0, bar.getHeight());
		outAnimator.setDuration(200);
		outAnimator.setInterpolator(fastOutSlowIn);

		return outAnimator;
	}

	/**
	 * Override this method to customize bar while updateing animation.
	 */
	protected void onAnimationUpdate(ValueAnimator animation) {
		int y = (int) animation.getAnimatedValue();
		bar.setTranslationY(y);
	}

	// region Private

	private void animateViewIn() {
		ValueAnimator va = prepareInAnimation();
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				onViewShown();
			}
		});
		va.addUpdateListener(this::onAnimationUpdate);
		va.start();
	}

	private void onViewShown() {
		// Tell manager when this view is shown
		manager().onViewShown(actionCallback);

		if (onShownCallback != null) {
			onShownCallback.run();
		}
	}

	private void animateViewOut() {
		ValueAnimator va = prepareOutAnimation();
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				onViewDismissed();
			}
		});
		va.addUpdateListener(this::onAnimationUpdate);
		va.start();
	}

	private void onViewDismissed() {
		// tell manager when this view is dismissed
		manager().onViewDismissed(actionCallback);

		if (onDismissCallback != null) {
			onDismissCallback.run();
		}

		// remove view from parent
		ViewParent parent = bar.getParent();
		if (parent instanceof ViewGroup) {
			((ViewGroup) parent).removeView(bar);
		}
	}

	private void showView() {
		if (bar.getParent() == null) {
			parent.addView(bar);
		}

		if (bar.getWidth() > 0 && bar.getHeight() > 0) {
			if (shouldAnimate()) {
				animateViewIn();
			}
			else {
				onViewShown();
			}
		}
		else {
			bar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
				@Override
				public void onLayoutChange(View v, int left, int top, int right, int bottom,
					int oldLeft, int oldTop, int oldRight, int oldBottom) {

					bar.removeOnLayoutChangeListener(this);

					if (shouldAnimate()) {
						animateViewIn();
					}
					else {
						onViewShown();
					}
				}
			});
		}
	}

	private boolean shouldAnimate() {
		return !accessibilityManager.isEnabled();
	}

	private void dismissView() {
		if (shouldAnimate() && bar.getVisibility() == View.VISIBLE) {
			animateViewOut();
		}
		else {
			onViewDismissed();
		}
	}

	// endregion Private
}
