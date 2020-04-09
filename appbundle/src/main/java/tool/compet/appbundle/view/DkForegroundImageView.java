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

package tool.compet.appbundle.view;

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
import androidx.appcompat.widget.AppCompatImageView;

import tool.compet.core.graphic.drawable.DkIDrawable;
import tool.compet.core.view.DkViews;

/**
 * This is backward-compatibility, brings foreground (api 21+) onto older api.
 * This will override methods from api 21+, custom them to use it both version.
 * By default, it creates a foreground that uses Ripple animation to response user-touch.
 * You can setForeground() to use your own drawable with own properties (animation, gradient...).
 */
public class DkForegroundImageView extends AppCompatImageView {
	public static final boolean IS_PRE_LOLLIPOP = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;

	private Drawable mForeground;
	private boolean mIsPrePress;
	private PressAction mPrePressAction;

	public DkForegroundImageView(Context context) {
		this(context, null);
	}

	public DkForegroundImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("CustomViewStyleable")
	public DkForegroundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		if (mForeground != null) {
			mForeground.setCallback(this);
			setForeground(mForeground);
		}
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
		super.onSizeChanged(width, height, oldwidth, oldheight);
		Drawable fg = mForeground;
		if (fg != null) {
			fg.setBounds(0, 0, width, height);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Drawable fg = mForeground;

		if (fg != null) {
			fg.draw(canvas);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		Drawable fg = mForeground;

		if (fg != null) {
			fg.draw(canvas);
		}
	}

	@Override
	protected boolean verifyDrawable(@NonNull Drawable who) {
		return super.verifyDrawable(who) || who == mForeground;
	}

	@Override
	public void jumpDrawablesToCurrentState() {
		super.jumpDrawablesToCurrentState();
		//todo
	}

	@Override
	public Drawable getForeground() {
		return mForeground;
	}

	// @Override from api 21+
	public void setForeground(Drawable drawable) {
		Drawable fg = mForeground;

		if (fg == drawable) {
			return;
		}

		if (fg != null) {
			fg.setCallback(null);
			unscheduleDrawable(fg);
		}

		mForeground = fg = drawable;

		if (fg != null) {
			setWillNotDraw(false);

			fg.setCallback(this);

			if (fg.isStateful()) {
				fg.setState(getDrawableState());
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
		Drawable fg = mForeground;

		if (fg != null && fg.isStateful()) {
			fg.setState(getDrawableState());
		}
	}

	// @Override from api 21+
	public void drawableHotspotChanged(float x, float y) {
		Drawable fg = mForeground;

		if (IS_PRE_LOLLIPOP) {
			// setHotspot for own drawable since pre-lollipop has not hotspot concept
			if (fg instanceof DkIDrawable) {
				((DkIDrawable) fg).setHotspot(x, y);
			}
		}
		else {
			// api 21+, notify background that hotspot changed
			super.drawableHotspotChanged(x, y);
			// set foreground new hotspot
			fg.setHotspot(x, y);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (IS_PRE_LOLLIPOP) {
			trackDrawableHotspotChanged(event);
		}
		return super.onTouchEvent(event);
	}

	private class PressAction implements Runnable {
		private float x, y;

		@Override
		public void run() {
			mIsPrePress = false;
			drawableHotspotChanged(x, y);
		}
	}

	public void trackDrawableHotspotChanged(MotionEvent event) {
		final int actionMasked = event.getActionMasked();

		// disabled view will consume event but hotspot doest not be changed
		if (!isEnabled()) {
			if (mIsPrePress
				|| actionMasked == MotionEvent.ACTION_OUTSIDE
				|| actionMasked == MotionEvent.ACTION_CANCEL
				|| actionMasked == MotionEvent.ACTION_UP) {
				// clear pre-press action if view become disabled at sometime
				mIsPrePress = false;
				removeCallbacks(mPrePressAction);
			}
			return;
		}

		final float x = event.getX();
		final float y = event.getY();
		final boolean clickable = isClickable();

		switch (actionMasked) {
			case MotionEvent.ACTION_DOWN: {
				// only clickable view can perform press
				if (!clickable) {
					break;
				}
				// view in scrolling container should delay press action
				if (DkViews.isInScrollingContainer(this)) {
					mIsPrePress = true;

					PressAction prePressAction = mPrePressAction;
					if (prePressAction == null) {
						mPrePressAction = prePressAction = new PressAction();
					}
					prePressAction.x = x;
					prePressAction.y = y;
					postDelayed(prePressAction, ViewConfiguration.getTapTimeout());
				}
				// other perform press immediately
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
				if (clickable && mIsPrePress) {
					mIsPrePress = false;
					removeCallbacks(mPrePressAction);
					drawableHotspotChanged(x, y);
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				if (mIsPrePress) {
					mIsPrePress = false;
					removeCallbacks(mPrePressAction);
				}
				break;
			}
		}
	}
}
