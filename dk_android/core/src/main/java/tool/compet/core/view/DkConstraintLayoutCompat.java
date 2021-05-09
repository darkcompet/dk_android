/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tool.compet.core.DkConfig;
import tool.compet.core.DkConst;
import tool.compet.core.graphics.drawable.DkDrawable;
import tool.compet.core.graphics.drawable.DkRippleDrawable;

/**
 * This is backward-compatibility, brings foreground of newer api into older api.
 * For older api, by default, it creates a foreground that uses Ripple animation to response user-touch.
 * You can call `setForeground()` to use your own drawable with own properties (animation, gradient, state, ...).
 */
public class DkConstraintLayoutCompat extends ConstraintLayout {
	// We only handle foreground when super does not support
	private static final boolean isForegroundAvailableAtSuper = false;//DkConst.SDK_VERSION >= Build.VERSION_CODES.M;
	private Drawable foreground; // for api 23-

	// We only handle hotspot when super does not support
	private static final boolean isHotspotAvailableAtSuper = false;//DkConst.SDK_VERSION >= Build.VERSION_CODES.LOLLIPOP;
	private boolean isPrePress;
	private PressAction prePressAction;

	public DkConstraintLayoutCompat(Context context) {
		this(context, null);
	}

	public DkConstraintLayoutCompat(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("CustomViewStyleable")
	public DkConstraintLayoutCompat(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// Don't redraw while this time
		setWillNotDraw(false);

//		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DKForegroundView);
//		String animType = a.getString(R.styleable.DKForegroundView_dk_animation);
//		int normalColor = a.getColor(R.styleable.DKForegroundView_dk_normal_color, Color.TRANSPARENT);
//		int pressedColor = a.getColor(R.styleable.DKForegroundView_dk_pressed_color, Color.WHITE);
//		a.recycle();

//		int[][] states = {{-android.R.attr.state_pressed}, {android.R.attr.state_pressed}};
//		int[] colors = {Color.TRANSPARENT, Color.WHITE};
//		ColorStateList colorStates = new ColorStateList(states, colors);

		// Even user declare foreground at attribute (xml), we still create foreground by default
		Drawable foreground = getForeground(); // is of own or super
		if (foreground == null) {
			foreground = acquireDefaultForeground();
			setForeground(foreground);
		}
	}

	// By default, this provides `RippleDrawable` foreground for newer api (21+),
	// and `DkRippleDrawable` foreground for older api (20-).
	@Nullable
	protected Drawable acquireDefaultForeground() {
		int[][] states = {
			{android.R.attr.state_pressed}, // pressed state
//			{android.R.attr.state_focused}, // focused state
//			{android.R.attr.state_activated}, // activated state
			{android.R.attr.state_empty}, // normal state
		};
		int[] colors = {
			DkConfig.colorAccent(getContext()), // pressed color
//			Color.BLUE, // focused color
//			Color.YELLOW, // activated color
			Color.TRANSPARENT, // normal color
		};
		ColorStateList colorStateList = new ColorStateList(states, colors);

		if (isHotspotAvailableAtSuper) {
			return new RippleDrawable(colorStateList, null, null);
		}
		return new DkRippleDrawable(colorStateList);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
		super.onSizeChanged(width, height, oldwidth, oldheight);

		if (! isForegroundAvailableAtSuper) {
			Drawable foreground = this.foreground;
			if (foreground != null) {
				foreground.setBounds(0, 0, width, height);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (! isForegroundAvailableAtSuper) {
			Drawable foreground = this.foreground;
			if (foreground != null) {
				foreground.draw(canvas);
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (! isForegroundAvailableAtSuper) {
			Drawable foreground = this.foreground;
			if (foreground != null) {
				foreground.draw(canvas);
			}
		}
	}

	@Override
	protected boolean verifyDrawable(@NonNull Drawable something) {
		return super.verifyDrawable(something) || something == foreground;
	}
	
	// Override from api 16
	public void setBackground(Drawable background) {
		if (DkConst.SDK_VERSION >= Build.VERSION_CODES.JELLY_BEAN) {
			super.setBackground(background);
		}
		else {
			super.setBackgroundDrawable(background);
		}
	}

	// Override from api 23
	public Drawable getForeground() {
		if (isForegroundAvailableAtSuper) {
			return super.getForeground();
		}
		return foreground;
	}

	// Override from api 23
	public void setForeground(Drawable drawable) {
		if (isForegroundAvailableAtSuper) {
			super.setForeground(drawable);
			return;
		}

		// If same drawable, just ignore
		Drawable foreground = this.foreground;
		if (foreground == drawable) {
			return;
		}

		// For new foreground, reset it first
		if (foreground != null) {
			foreground.setCallback(null);
			unscheduleDrawable(foreground);
		}

		// Perform set
		this.foreground = foreground = drawable;

		// Set callback to listen events from foreground
		if (foreground != null) {
			setWillNotDraw(false);

			foreground.setCallback(this);

			if (foreground.isStateful()) {
				foreground.setState(getDrawableState());
			}
		}
		else {
			setWillNotDraw(true);
		}

		// Invalidate
		requestLayout();
		invalidate();
	}

	// Override from api 23
	protected void drawableStateChanged() {
		if (isForegroundAvailableAtSuper) {
			super.drawableStateChanged();
		}
		else {
			Drawable foreground = this.foreground;
			if (foreground != null && foreground.isStateful()) {
				foreground.setState(getDrawableState());
			}
		}
	}

	// Override from api 21
	public void drawableHotspotChanged(float x, float y) {
		if (isHotspotAvailableAtSuper) {
			super.drawableHotspotChanged(x, y);
		}
		
		// Handle hotspot changed for foreground at older version
		// Note: below is modified from super
		if (! isForegroundAvailableAtSuper) {
			// For older version, foreground should be instance of `DkDrawable`
			Drawable foreground = this.getForeground();

			if (foreground instanceof DkDrawable) {
				((DkDrawable) foreground).setHotspot(x, y);
			}

			dispatchDrawableHotspotChanged(x, y);
		}
	}

	// Override from api 22
	public void dispatchDrawableHotspotChanged(float x, float y) {
		if (DkConst.SDK_VERSION >= Build.VERSION_CODES.LOLLIPOP_MR1) {
			super.dispatchDrawableHotspotChanged(x, y);
		}
		else {
			//todo impl `hotspot changed` at old version
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (! isHotspotAvailableAtSuper) {
			handleTouchEventForHotspotChanged(event);
		}
		return super.onTouchEvent(event);
	}

	// Check touch event to update hotspot position for old api
	private void handleTouchEventForHotspotChanged(MotionEvent event) {
		final int actionMasked = event.getActionMasked();

		// Don't consume event in disabled state
		if (! isEnabled()) {
			if (isPrePress
				|| actionMasked == MotionEvent.ACTION_OUTSIDE
				|| actionMasked == MotionEvent.ACTION_CANCEL
				|| actionMasked == MotionEvent.ACTION_UP) {
				// Clear pre-press action
				isPrePress = false;
				removeCallbacks(prePressAction);
			}
			return;
		}

		final float x = event.getX();
		final float y = event.getY();
		final boolean clickable = isClickable();

		switch (actionMasked) {
			case MotionEvent.ACTION_DOWN: {
				// Only clickable view can perform press
				if (! clickable) {
					break;
				}
				// View in scrolling container should delay press action
				if (DkViews.isInScrollingContainer(this)) {
					isPrePress = true;

					PressAction prePressAction = this.prePressAction;
					if (prePressAction == null) {
						this.prePressAction = prePressAction = new PressAction();
					}
					prePressAction.x = x;
					prePressAction.y = y;
					postDelayed(prePressAction, ViewConfiguration.getTapTimeout());
				}
				// Otherwise, perform press immediately
				else {
					drawableHotspotChanged(x, y);
				}
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (clickable) {
					 drawableHotspotChanged(x, y);
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (clickable && isPrePress) {
					isPrePress = false;
					removeCallbacks(prePressAction);

					 drawableHotspotChanged(x, y);
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				if (isPrePress) {
					isPrePress = false;
					removeCallbacks(prePressAction);
				}
				break;
			}
		}
	}

	private class PressAction implements Runnable {
		float x;
		float y;

		@Override
		public void run() {
			isPrePress = false;
			drawableHotspotChanged(x, y);
		}
	}
}
