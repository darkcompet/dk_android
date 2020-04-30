/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.appbundle.floatingbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import tool.compet.core.view.DkViews;

import static android.graphics.Color.parseColor;

/**
 * This class uses 2 inputs: a view (bar) and ancestor ViewGroup of the view.
 * Perform your own animation on the bar with support of animation setting.
 * <p></p>
 * Here is usage example:
 * <pre>
 *
 * </pre>
 */
public abstract class DkFloatingbar implements View.OnTouchListener {
	protected abstract FloatingbarManager getManager();

	protected static final long INFINITE_DURATION = -1;

	private static final int MSG_SHOW = 1;
	private static final int MSG_DISMISS = 2;

	// Color for type of bar
	public static final int TYPE_NORMAL = parseColor("#333333");
	public static final int TYPE_ASK = parseColor("#009b8b");
	public static final int TYPE_ERROR = parseColor("#ff0000");
	public static final int TYPE_WARNING = parseColor("#ff9500");
	public static final int TYPE_INFO = parseColor("#493ebb");
	public static final int TYPE_SUCCESS = parseColor("#00bb4d");

	// Use parent to animate this bar
	protected final ViewGroup parent;
	// Actual bar view
	protected View bar;
	// Duration for each animation
	protected long duration = INFINITE_DURATION;
	// Specify whether this bar be dismiss on touch
	protected boolean isDismissOnTouch = true;
	// Callback after shown
	protected Runnable onShownCallback;
	// Callback on Dismiss
	protected Runnable onDismissCallback;
	// Animation
	protected static Interpolator fastOutSlowIn = new FastOutSlowInInterpolator();

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

	private FloatingbarManager.Callback mActionCallback = new FloatingbarManager.Callback() {
		@Override
		public void show() {
			handler.sendMessageDelayed(Message.obtain(handler, MSG_SHOW, DkFloatingbar.this), 0);
		}

		@Override
		public void dismiss() {
			handler.sendMessageDelayed(Message.obtain(handler, MSG_DISMISS, DkFloatingbar.this), 0);
		}
	};

	public DkFloatingbar(Context context, ViewGroup parent, View bar) {
		this.parent = parent;
		this.bar = bar;
		accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);

		bar.setOnTouchListener(this);
	}

	public void show() {
		// just tell manager schedule to show this bar
		getManager().show(duration, mActionCallback);
	}

	public void dismiss() {
		// just tell manager schedule to dismiss this bar
		getManager().dismiss(mActionCallback);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean eat = isDismissOnTouch;

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (eat && DkViews.isInsideView(event, v)) {
					this.dismiss();
				}
				break;
			}
		}
		return eat;
	}


	/**
	 * Scan up to find a layout like FrameLayout which can make layer of views.
	 * This try to find Android root layout as possible.
	 *
	 * @return Nomarly, it results Root FrameLayout of current Activity.
	 */
	protected static ViewGroup findSuitableParent(View view) {
		ViewGroup layout = null;
		ViewGroup fallback = null;
		ViewParent viewParent = view.getParent();

		if (viewParent instanceof ViewGroup) {
			layout = (ViewGroup) viewParent;
		}

		do {
			if (layout instanceof CoordinatorLayout) {
				return layout;
			}
			if (layout instanceof FrameLayout) {
				if (layout.getId() == android.R.id.content) {
					return layout;
				}
				fallback = layout;
			}
			if (layout != null) {
				ViewParent parent = layout.getParent();
				layout = (parent instanceof ViewGroup) ? (ViewGroup) parent : null;
			}
		}
		while (layout != null);

		return fallback;
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

	/**
	 * Override this method to setup initial state of bar and customize ValueAnimator for in-animation.
	 */
	protected ValueAnimator prepareInAnimation() {
		int height = bar.getHeight();
		bar.setTranslationY(height);

		ValueAnimator va = new ValueAnimator();
		va.setIntValues(height, 0);
		va.setDuration(200);
		va.setInterpolator(fastOutSlowIn);

		return va;
	}

	/**
	 * Override this method to setup initial state of bar and customize ValueAnimator for out-animation.
	 */
	protected ValueAnimator prepareOutAnimation() {
		ValueAnimator va = new ValueAnimator();
		va.setIntValues(0, bar.getHeight());
		va.setDuration(200);
		va.setInterpolator(fastOutSlowIn);

		return va;
	}

	/**
	 * Override this method to customize bar while updateing animation.
	 */
	protected void onAnimationUpdate(ValueAnimator animation) {
		int y = (int) animation.getAnimatedValue();
		bar.setTranslationY(y);
	}

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
		// tell manager when this view is shown
		getManager().onViewShown(mActionCallback);

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
		getManager().onViewDismissed(mActionCallback);

		if (onDismissCallback != null) {
			onDismissCallback.run();
		}

		// remove view from parent
		ViewParent parent = bar.getParent();
		if (parent instanceof ViewGroup) {
			((ViewGroup) parent).removeView(bar);
		}
	}
}
