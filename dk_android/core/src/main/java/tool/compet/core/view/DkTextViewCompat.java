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
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import tool.compet.core.graphics.drawable.DkDrawable;

/**
 * This is backward-compatibility, brings foreground (api 21+) onto older api.
 * This will override methods from api 21+, custom them to use it both version.
 * By default, it creates a foreground that uses Ripple animation to response user-touch.
 * You can setForeground() to use your own drawable with own properties (animation, gradient...).
 */
public class DkTextViewCompat extends AppCompatTextView {
	private Drawable foreground;
	private boolean isPrePress;
	private PressAction prePressAction;

	public DkTextViewCompat(Context context) {
		this(context, null);
	}

	public DkTextViewCompat(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("CustomViewStyleable")
	public DkTextViewCompat(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		setWillNotDraw(false);

		//		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DKForegroundView);
		//		String animType = a.getString(R.styleable.DKForegroundView_dk_animation);
		//		int normalColor = a.getColor(R.styleable.DKForegroundView_dk_normal_color, Color.TRANSPARENT);
		//		int pressedColor = a.getColor(R.styleable.DKForegroundView_dk_pressed_color, Color.WHITE);
		//		a.recycle();

		int[][] states = {{-android.R.attr.state_pressed}, {android.R.attr.state_pressed}};
		int[] colors = {Color.TRANSPARENT, Color.WHITE};
		ColorStateList colorStates = new ColorStateList(states, colors);

		//todo init foreground first
		if (foreground != null) {
			foreground.setCallback(this);
			setForeground(foreground);
		}
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
		super.onSizeChanged(width, height, oldwidth, oldheight);

		Drawable foreground = this.foreground;
		if (foreground != null) {
			foreground.setBounds(0, 0, width, height);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Drawable foreground = this.foreground;
		if (foreground != null) {
			foreground.draw(canvas);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		Drawable foreground = this.foreground;
		if (foreground != null) {
			foreground.draw(canvas);
		}
	}

	@Override
	protected boolean verifyDrawable(@NonNull Drawable who) {
		return super.verifyDrawable(who) || who == foreground;
	}

	@Override
	public void jumpDrawablesToCurrentState() {
		super.jumpDrawablesToCurrentState();
		//todo
	}

	@Override
	public Drawable getForeground() {
		return foreground;
	}

	// @Override from Lollipop, do NOT change method name `setForeground()`
	public void setForeground(Drawable drawable) {
		Drawable foreground = this.foreground;

		if (foreground == drawable) {
			return;
		}
		if (foreground != null) {
			foreground.setCallback(null);
			unscheduleDrawable(foreground);
		}

		this.foreground = foreground = drawable;

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

		requestLayout();
		invalidate();
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();

		Drawable foreground = this.foreground;
		if (foreground != null && foreground.isStateful()) {
			foreground.setState(getDrawableState());
		}
	}

	// @Override from Lollipop, do NOT change method name `drawableHotspotChanged()`
	public void drawableHotspotChanged(float x, float y) {
		// For older version, foreground should be instance of `DkDrawable`
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			Drawable background = getBackground();
			Drawable foreground = this.foreground;

			if (background instanceof DkDrawable) {
				((DkDrawable) background).setHotspot(x, y);
			}
			if (foreground instanceof DkDrawable) {
				((DkDrawable) foreground).setHotspot(x, y);
			}

			dispatchDrawableHotspotChanged(x, y);
		}
		// For newer version from Lollipop, just call super
		else {
			super.drawableHotspotChanged(x, y);
		}
	}

	// @Override from Lollipop, do NOT change method name `dispatchDrawableHotspotChanged()`
	public void dispatchDrawableHotspotChanged(float x, float y) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			// nothing to do
		}
		else {
			super.dispatchDrawableHotspotChanged(x, y);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			handleTouchEventForHotspotChanged(event);
		}
		return super.onTouchEvent(event);
	}

	private class PressAction implements Runnable {
		private float x, y;

		@Override
		public void run() {
			isPrePress = false;
			drawableHotspotChanged(x, y);
		}
	}

	// Check touch event to update hotspot position
	private void handleTouchEventForHotspotChanged(MotionEvent event) {
		final int actionMasked = event.getActionMasked();

		// disabled view will consume event but hotspot doest not be changed
		if (! isEnabled()) {
			if (isPrePress
				|| actionMasked == MotionEvent.ACTION_OUTSIDE
				|| actionMasked == MotionEvent.ACTION_CANCEL
				|| actionMasked == MotionEvent.ACTION_UP) {
				// Clear pre-press action if view become disabled at sometime
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
}
