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

package tool.compet.core.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import tool.compet.core.type.DkCallback;

/**
 * Utility class for View.
 */
public class DkViews {
	/**
	 * Get dimension of a view when it is laid out.
	 *
	 * @param view target view.
	 * @param callback dimension callback with {width, height}.
	 */
	public static void getViewDimension(View view, DkCallback<int[]> callback) {
		view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
				else {
					view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}

				if (callback != null) {
					callback.call(new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()});
				}
			}
		});
	}

	public static Animation translateAnimation(long durationMillis, int fromX, int toX, int fromY, int toY) {
		TranslateAnimation anim = new TranslateAnimation(fromX, toX, fromY, toY);
		anim.setDuration(durationMillis);
		anim.setFillAfter(true);
		return anim;
	}

	public static void tintMenuIcon(Menu menu, int color) {
		for (int i = menu.size() - 1; i >= 0; --i) {
			Drawable drawable = menu.getItem(i).getIcon();

			if (drawable != null) {
				drawable.mutate();
				drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			}
		}
	}

	public static void changeToolbarTextFont(Context context, Toolbar toolbar) {
		for (int i = 0, N = toolbar.getChildCount(); i < N; i++) {
			View view = toolbar.getChildAt(i);

			if (view instanceof TextView) {
				TextView tv = (TextView) view;

				if (tv.getText().equals(toolbar.getTitle())) {
					DkTextViews.applyFont(context, tv);
					break;
				}
			}
		}
	}

	public static void expandView(View view, long duration) {
		view.measure(-1, -2);
		final int targetHeight = view.getMeasuredHeight();

		view.getLayoutParams().height = 1;
		view.setVisibility(View.VISIBLE);
		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				view.getLayoutParams().height = (interpolatedTime == 1) ? -2 : (int) (targetHeight * interpolatedTime);
				view.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		anim.setDuration(duration);
		view.startAnimation(anim);
	}

	public static void collapseView(View view, long duration) {
		final int initialHeight = view.getMeasuredHeight();

		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					view.setVisibility(View.GONE);
				}
				else {
					view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					view.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		anim.setDuration(duration);
		view.startAnimation(anim);
	}

	public static void loadWebviewFromHtml(WebView webView, String htmlContent) {
		webView.loadDataWithBaseURL("",
			htmlContent,
			"text/html",
			"utf-8",
			"");
	}

	public static void decorateProgressBar(ProgressBar pb, int color) {
		pb.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
	}

	public static View getListViewItem(int pos, ListView listView) {
		int firstItem = listView.getFirstVisiblePosition();
		int lastItem = firstItem + listView.getChildCount() - 1;

		if (pos < firstItem || pos > lastItem) {
			return listView.getAdapter().getView(pos, null, listView);
		}

		int childIndex = pos - firstItem;

		return listView.getChildAt(childIndex);
	}

	public static Bitmap getBitmapFromView(View view) {
		view.setDrawingCacheEnabled(true);
		view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
		view.buildDrawingCache();

		if (view.getDrawingCache() == null) {
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		view.destroyDrawingCache();

		return bitmap;
	}

	public static void changeBackgroundColor(View view, String color, float radius, float density) {
		changeBackgroundColor(view, Color.parseColor(color), radius, density);
	}

	public static void changeBackgroundColor(View view, int argb, float radius, float density) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setColor(argb);
		drawable.setShape(GradientDrawable.RECTANGLE);
		drawable.setCornerRadius(radius * density);

		view.setBackgroundDrawable(drawable);
	}

	public static void setStatusBarColor(Activity act, int colorId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = act.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(colorId);
		}
	}

	/**
	 * 4 borders will be rounded
	 * @param color {normalColor, pressedColor}
	 */
	public static void injectStateListDrawable(View view, float cornerRadius, int[] color) {
		injectStateListDrawable(view, cornerRadius, color, new boolean[]{true, true, true, true});
	}

	/**
	 * @param color {normalColor, pressedColor}
	 * @param border {topLeft, topRight, bottomRight, bottomLeft}
	 */
	public static void injectStateListDrawable(View view, float cornerRadius, int[] color, boolean[] border) {
		int normalColor = color[0];
		int pressedColor = color[1];
		float topLeftRad = border[0] ? cornerRadius : 0;
		float topRightRad = border[1] ? cornerRadius : 0;
		float bottomRightRad = border[2] ? cornerRadius : 0;
		float bottomLeftRad = border[3] ? cornerRadius : 0;

		StateListDrawable stateList = new StateListDrawable();

		GradientDrawable pressedDrawable = new GradientDrawable();
		pressedDrawable.setColor(pressedColor);
		pressedDrawable.setCornerRadii(new float[]{
			topLeftRad, topLeftRad,
			topRightRad, topRightRad,
			bottomRightRad, bottomRightRad,
			bottomLeftRad, bottomLeftRad});

		GradientDrawable normalDrawable = new GradientDrawable();
		normalDrawable.setColor(normalColor);
		normalDrawable.setCornerRadii(new float[]{
			topLeftRad, topLeftRad,
			topRightRad, topRightRad,
			bottomRightRad, bottomRightRad,
			bottomLeftRad, bottomLeftRad});

		stateList.addState(new int[] {android.R.attr.state_pressed}, pressedDrawable);
		stateList.addState(new int[] {}, normalDrawable);

		ViewCompat.setBackground(view, stateList);
	}

	public static void injectGradientDrawable(View view, float cornerRadius, int normalColor) {
		boolean[] border = new boolean[]{true, true, true, true};
		injectGradientDrawable(view, cornerRadius, normalColor, border);
	}

	/**
	 * @param border {left-top, top-right, right-bottom, bottom-left}
	 */
	public static void injectGradientDrawable(View view, float cornerRadius, int normalColor, boolean[] border) {
		float topLeftRad = border[0] ? cornerRadius : 0;
		float topRightRad = border[1] ? cornerRadius : 0;
		float bottomRightRad = border[2] ? cornerRadius : 0;
		float bottomLeftRad = border[3] ? cornerRadius : 0;

		GradientDrawable normalDrawable = new GradientDrawable();
		normalDrawable.setColor(normalColor);
		normalDrawable.setCornerRadii(new float[]{
			topLeftRad, topLeftRad,
			topRightRad, topRightRad,
			bottomRightRad, bottomRightRad,
			bottomLeftRad, bottomLeftRad});

		ViewCompat.setBackground(view, normalDrawable);
	}

	public static boolean isInScrollingContainer(View view) {
		ViewParent parent = view.getParent();

		while (parent instanceof ViewGroup) {
			if (((ViewGroup) parent).shouldDelayChildPressedState()) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	public static boolean isInsideView(MotionEvent event, View view) {
		float x = event.getX();
		float y = event.getY();
		int w = view.getWidth();
		int h = view.getHeight();

		return x >= 0 && y >= 0 && x <= w && y <= h;
	}

	public static boolean isInsideView(float localX, float localY, int w, int h) {
		return localX >= 0 && localY >= 0 && localX <= w && localY <= h;
	}

	/**
	 * Check whether the touch is visually inside the view.
	 *
	 * @param w view's width
	 * @param h view's height
	 * @param localX touched point in x-axis inside view
	 * @param localY touched point in y-axis inside view
	 * @param slop obtain by ViewConfiguration.get(context).getScaledTouchSlop()
	 */
	public static boolean isInsideView(float localX, float localY, int w, int h, int slop) {
		return localX >= -slop && localY >= -slop && localX < (w + slop) && localY < (h + slop);
	}

	public static Rect getOffsetFromDescendantToAncestor(View descendant, ViewGroup ancestor) {
		Rect offset = new Rect();
		ancestor.offsetDescendantRectToMyCoords(descendant, offset);
		return offset;
	}

	public static Rect getOffsetFromAncestorToDescendant(View descendant, ViewGroup ancestor) {
		Rect offset = new Rect();
		ancestor.offsetRectIntoDescendantCoords(descendant, offset);
		return offset;
	}

	/**
	 * Scan up to find a layout like FrameLayout which can make layer of views.
	 * This try to find Android root layout as possible.
	 *
	 * @return Nomarly, it results Root FrameLayout of current Activity.
	 */
	public static ViewGroup findSuperFrameLayout(View view) {
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
}
