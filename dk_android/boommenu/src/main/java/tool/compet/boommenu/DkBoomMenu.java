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

package tool.compet.boommenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import tool.compet.boommenu.type.DkEmissionOrder;
import tool.compet.boommenu.type.DkGravity;
import tool.compet.boommenu.type.DkShape;
import tool.compet.core.type.DkCallback;
import tool.compet.core.view.DkViews;
import tool.compet.core.view.animation.DkAnimationConfiguration;
import tool.compet.core.view.animation.interpolator.DkInterpolatorProvider;

/**
 * This class will show a menu with a lot of items (cluster), start from an anchor and end in somewhere in screen,
 * with variety shape or
 * You can setup cluster via 2 fields: gravity and shape. Also you can set cluster margin
 * inside board via #setClusterMargin(). For detail, see below usage example:
 * <pre><code>
 *    DkBoomMenu menu = new DkBoomMenu(context, anchor);
 *    int[] icons;
 *    int[] texts;
 *    Runnable[] commands;
 *
 *    for (int i = 0; i < N; i++) {
 *       menu.addItemBuilder(new DkSingleTextItemBuilder()
 * 	      .setImage(icons[i])
 * 	      .setText(texts[i])
 * 	      .setCircleShape(true)
 * 	      .setDimensionRatio(true, 1f, 1f)
 * 	      .setStyle(DkSingleTextItemBuilder.STYLE_TEXT_INSIDE_ICON)
 * 	      .setMargin(4)
 * 	   );
 *    }
 *
 *    menu
 *       .setOnItemClickListener(id -> commands[id].run())
 * 	   .setGravity(DkGravity.ANCHOR_LEFT)
 * 	   .setClusterMargin(anchor.getWidth() >> 1, 0)
 * 	   .setShape(DkShape.HORIZONTAL_LINE);
 *
 * 	menu.boom();
 * </code></pre>
 */
public class DkBoomMenu {
	private static final int ANIM_STATE_NOT_YET = -1;
	private static final int ANIM_STATE_WILL_ANIMATE_SOON = 1;
	private static final int ANIM_STATE_ANIMATING = 2;
	private static final int ANIM_STATE_ANIMATED = 3;
	
	private final Context context;
	private WeakReference<View> anchor;

	private int animState = ANIM_STATE_NOT_YET;

	private boolean backgroundWholeScreen = true;
	private boolean dismissImmediate;
	private boolean dismissOnClickOutsideItem = true;
	private boolean cacheOptimization = true;
	private boolean bringAnchorToFront;
	private long animStartDelay;
	private long boomDuration = DkAnimationConfiguration.ANIM_LARGE_EXPAND;
	private long unboomDuration = DkAnimationConfiguration.ANIM_LARGE_COLLAPSE;
	private long emissionDelayBetweenItems = 50;
	private ValueAnimator animator;
	private DkCallback<Integer> onItemClickListener;

	private ClusterManager cluster = new ClusterManager();
	private boolean shouldRebuildItems = true;
	private BackgroundLayout background;
	private int backgroundWidth;
	private int backgroundHeight;
	private BackgroundLayout.Listener backgroundListener = new BackgroundLayout.Listener() {
		@Override
		public void onSizeChanged(int w, int h, int oldw, int oldh) {
			backgroundWidth = w;
			backgroundHeight = h;

			if (w != oldw || h != oldh) {
				shouldRebuildItems = true;
			}
		}

		@Override
		public boolean onTranslate(float dx, float dy) {
			cluster.translateItems(dx, dy, backgroundWidth, backgroundHeight);
			return true;
		}

		@Override
		public boolean onClick(View v) {
			if (dismissOnClickOutsideItem) {
				unboom(dismissImmediate);
				return true;
			}
			return false;
		}

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP
				&& (animState == ANIM_STATE_WILL_ANIMATE_SOON || animState == ANIM_STATE_ANIMATED)) {
				// dismiss menu with immediate flag
				unboom(dismissImmediate);
				return true;
			}
			return false;
		}
	};

	public DkBoomMenu(Context context) {
		this.context = context;
	}

	public DkBoomMenu(Context context, View anchor) {
		this.context = context;
		this.anchor = new WeakReference<>(anchor);
	}

	/**
	 * Expanding (with animation) all items into background.
	 */
	public void boom() {
		boom(false);
	}

	/**
	 * Expanding (animate if not immediate) all items into background.
	 */
	public void boom(boolean immediate) {
		if (animState == ANIM_STATE_NOT_YET) {
			setupBackground();
			setupMenuItems();

			if (immediate) {
				animState = ANIM_STATE_ANIMATED;
				for (DkItem item : cluster.items) {
					item.show();
				}
				showAndForcusBackground();
			}
			else {
				animState = ANIM_STATE_WILL_ANIMATE_SOON;
				startAnimation(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						animState = ANIM_STATE_ANIMATING;
						showAndForcusBackground();
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						cleanupState();
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						animState = ANIM_STATE_ANIMATED;
					}
				});
			}
		}
	}

	/**
	 * Collapsing (with animation) all items and dismiss menu.
	 */
	public void unboom() {
		unboom(false);
	}

	/**
	 * Animate collapsing all menu items and remove from background.
	 */
	public void unboom(boolean immediate) {
		if (animState == ANIM_STATE_WILL_ANIMATE_SOON) {
			cancelAnimation();
			cleanupState();
		}
		else if (animState == ANIM_STATE_ANIMATED) {
			if (immediate) {
				cleanupState();
			}
			else {
				animState = ANIM_STATE_WILL_ANIMATE_SOON;
				reverseAnimation(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						animState = ANIM_STATE_ANIMATING;
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						cleanupState();
					}
				});
			}
		}
	}

	private void showAndForcusBackground() {
		background.setVisibility(View.VISIBLE);
		background.setFocusableInTouchMode(true);
		background.requestFocus();
	}

	private void cancelAnimation() {
		if (animator != null) {
			animator.cancel();
			animator.removeAllListeners();
			animator.removeAllUpdateListeners();
		}
	}

	private void cleanupState() {
		animState = ANIM_STATE_NOT_YET;
		removeMenuItems();
		removeBackground();
	}

	public void addItemBuilder(DkItemBuilder itemBuilder) {
		cluster.itemBuilders.add(itemBuilder);
	}

	/**
	 * Create background if not yet existed -> just add as child to ancestor without showing.
	 */
	private void setupBackground() {
		View anchor = this.anchor.get();
		BackgroundLayout background = this.background;

		if (anchor == null) {
			throw new RuntimeException("Anchor is null. Please call setAnchor() or use 2nd constructor.");
		}
		if (background == null) {
			background = this.background = new BackgroundLayout(context);
		}

		background.setVisibility(View.GONE);
		background.listener = backgroundListener;
		ViewGroup parent = findSuitableParent(anchor);

		if (parent != null) {
			if (background.getParent() instanceof ViewGroup) {
				((ViewGroup) background.getParent()).removeView(background);
			}
			background.updateDimension(parent.getWidth(), parent.getHeight());
			parent.addView(background);
		}

		if (bringAnchorToFront) {
			anchor.bringToFront();
		}
	}

	/**
	 * Build items -> calculate start, end positions -> scale each item to 0 -> add to background.
	 */
	private void setupMenuItems() {
		View anchor = this.anchor.get();
		ViewGroup parent = findSuitableParent(anchor);

		if (anchor == null) {
			throw new RuntimeException("You must provide anchor. Maybe call setAnchor().");
		}
		if (isShouldBuildItems()) {
			cluster.buildItems(context, anchor, parent, new DkItemBuilder.Listener() {
				@Override
				public void onTranslate(DkItem item, float dx, float dy) {
					cluster.translateItems(dx, dy, backgroundWidth, backgroundHeight);
				}

				@Override
				public void onClick(DkItem item, float x, float y) {
					if (item.dismissMenuOnClickItem) {
						unboom(item.dismissMenuImmediate);
					}
					if (onItemClickListener != null) {
						onItemClickListener.call(item.index);
					}
				}
			});
		}

		cluster.calcStartEndPositions(anchor, parent);

		for (DkItem item : cluster.items) {
			item.hide();
			background.addView(item.view);
		}
	}

	private boolean isShouldBuildItems() {
		if (shouldRebuildItems) {
			shouldRebuildItems = false;
			return true;
		}
		return cluster.items.size() == 0;
	}

	private void startAnimation(AnimatorListenerAdapter listener) {
		final ArrayList<DkItem> items = cluster.items;
		final int itemCount = items.size();
		long totalDuration = boomDuration + emissionDelayBetweenItems * (itemCount - 1);

		for (DkItem item : items) {
			totalDuration += item.animStartDelay;
		}

		background.setupAnimation(animStartDelay, totalDuration, totalDuration);
		cluster.setupAnimation(animStartDelay, emissionDelayBetweenItems, boomDuration, totalDuration);

		animator = ValueAnimator.ofInt(0, 1);
		animator.setInterpolator(DkInterpolatorProvider.newLinear());
		animator.setStartDelay(animStartDelay);
		animator.setDuration(totalDuration);
		animator.addListener(listener);
		animator.addUpdateListener(va -> {
			float f = va.getAnimatedFraction();

			background.onAnimationUpdate(f);

			for (DkItem item : items) {
				item.onAnimationUpdate(f);
			}
		});
		animator.start();
	}

	private void reverseAnimation(AnimatorListenerAdapter listener) {
		final ArrayList<DkItem> items = cluster.items;
		final int itemCount = items.size();
		long totalDuration = unboomDuration + emissionDelayBetweenItems * (itemCount - 1);

		for (DkItem item : items) {
			totalDuration += item.animStartDelay;
		}

		background.setupAnimation(animStartDelay, totalDuration, totalDuration);
		cluster.setupAnimation(animStartDelay, emissionDelayBetweenItems, unboomDuration, totalDuration);

		animator.removeAllListeners();
		animator.removeAllUpdateListeners();
		animator.setDuration(totalDuration);
		animator.addListener(listener);
		animator.addUpdateListener(va -> {
			float f = va.getAnimatedFraction();

			background.onAnimationUpdate(f);

			for (DkItem item : items) {
				item.onAnimationUpdate(f);
			}
		});
		animator.reverse();
	}

	private void removeMenuItems() {
		if (background != null) {
			background.removeAllViews();
		}

		if (!cacheOptimization) {
			cluster.items.clear();
		}
	}

	private void removeBackground() {
		BackgroundLayout background = this.background;

		if (background != null) {
			ViewParent parent = background.getParent();

			if (parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(background);
			}
		}

		if (!cacheOptimization) {
			this.background = null;
		}
	}

	private ViewGroup findSuitableParent(View view) {
		if (view == null) {
			return null;
		}
		if (backgroundWholeScreen) {
			return DkViews.findSuperFrameLayout(view);
		}

		ViewParent parent = view.getParent();

		return parent instanceof ViewGroup ? (ViewGroup) parent : null;
	}

	public DkBoomMenu setBackgroundDimColor(int dimColor) {
		background.dimColor = dimColor;
		return this;
	}

	public DkBoomMenu setBackgroundWholeScreen(boolean wholeScreen) {
		this.backgroundWholeScreen = wholeScreen;
		return this;
	}

	public DkBoomMenu setDismissOnClickOutsideItem(boolean dismiss, boolean dismissImmediate) {
		this.dismissOnClickOutsideItem = dismiss;
		this.dismissImmediate = dismissImmediate;
		return this;
	}

	public DkBoomMenu setAnchor(View anchor) {
		this.anchor = new WeakReference<>(anchor);
		return this;
	}

	public DkBoomMenu setBoomDuration(long boomDuration) {
		this.boomDuration = boomDuration;
		return this;
	}

	public DkBoomMenu setUnboomDuration(long unboomDuration) {
		this.unboomDuration = unboomDuration;
		return this;
	}

	public DkBoomMenu setBoomStartDelay(long animStartDelay) {
		this.animStartDelay = animStartDelay;
		return this;
	}

	public DkBoomMenu setClusterMargin(int horizontalMargin, int verticalMargin) {
		cluster.horizontalOffset = horizontalMargin;
		cluster.verticalOffset = verticalMargin;
		return this;
	}

	public DkBoomMenu setGravity(DkGravity gravity) {
		cluster.gravity = gravity;
		return this;
	}

	public DkBoomMenu setShape(DkShape shape) {
		cluster.shape = shape;
		return this;
	}

	public DkBoomMenu setEmissionDelayBetweenItems(long emissionDelay) {
		this.emissionDelayBetweenItems = emissionDelay;
		return this;
	}

	public DkBoomMenu setItemsEmissionOrder(DkEmissionOrder order) {
		cluster.emissionOrder = order;
		return this;
	}

	public DkBoomMenu setBringAnchorToFront(boolean bringAnchorToFront) {
		this.bringAnchorToFront = bringAnchorToFront;
		return this;
	}

	public DkBoomMenu setItemsPositionCalculator(DiPositionCalculator calculator) {
		cluster.calculator = calculator;
		return this;
	}

	public DkBoomMenu setRandomItemsStartPosition(boolean randomStartPosition) {
		cluster.randomStartPosition = randomStartPosition;
		return this;
	}

	public DkBoomMenu setAutoScaleClusterIfOversize(boolean autoScale) {
		cluster.autoScaleIfOversize = autoScale;
		return this;
	}

	public DkBoomMenu setAllowClusterOutsideBoard(boolean allow) {
		cluster.allowOutsideBoard = allow;
		return this;
	}

	public DkBoomMenu setOnItemClickListener(DkCallback<Integer> onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
		return this;
	}

	public DkBoomMenu setAutoUnisize(boolean autoUnisize) {
		cluster.autoUnisize = autoUnisize;
		return this;
	}
}
