/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
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

import tool.compet.core.view.animation.DkAnimationConfiguration;
import tool.compet.core.view.animation.DkInterpolatorProvider;
import tool.compet.core.view.DkViews;

/**
 * This class shows a menu with a lot of items (cluster), start from an anchor and end in
 * somewhere in screen with various ending-shapes.
 * You can setup cluster via 2 fields: gravity and shape. Also you can set cluster margin
 * inside board via #setClusterMargin(). For detail, see below example:
 * <pre><code>
 *    DkBoomMenu.newIns(context, anchor)
 *        .addItemBuilder(new DkTextItemBuilder()
 *            .setOnClickListener(v -> onFeedbackClick())
 *            .setIcon(R.drawable.ic_feedback)
 *            .setText(R.string.feedback)
 *            .setStyle(DkTextItemBuilder.STYLE_TEXT_RIGHT_OUT_ICON)
 *        )
 *        .addItemBuilder(new DkTextItemBuilder()
 *            .setOnClickListener(v -> onRateAppClick())
 *            .setIcon(R.drawable.ic_star)
 *            .setText(R.string.rate_app)
 *            .setStyle(DkTextItemBuilder.STYLE_TEXT_RIGHT_OUT_ICON)
 *        )
 *        .setShape(DkShape.VERTICAL_LINE)
 *        .setGravity(DkGravity.ANCHOR_BOTTOM_LEFT)
 *        .setClusterMargin(anchor.getWidth(), 0)
 *        .boom();
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
	private boolean dismissOnBackPressed = true;
	private boolean cacheOptimization = true;
	private boolean bringAnchorToFront;
	private long animStartDelay;
	private long boomDuration = DkAnimationConfiguration.ANIM_LARGE_EXPAND;
	private long unboomDuration = DkAnimationConfiguration.ANIM_LARGE_COLLAPSE;
	private long emissionDelayBetweenItems = 50;
	private ValueAnimator animator;

	private final MyItemClusterManager itemClusterManager = new MyItemClusterManager();
	private boolean shouldRebuildItems = true;
	private MyBackgroundLayout background;
	private int backgroundWidth;
	private int backgroundHeight;
	private final MyBackgroundLayout.Listener backgroundListener = new MyBackgroundLayout.Listener() {
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
			itemClusterManager.translateItems(dx, dy, backgroundWidth, backgroundHeight);
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
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
				if (dismissOnBackPressed && (animState == ANIM_STATE_WILL_ANIMATE_SOON || animState == ANIM_STATE_ANIMATED)) {
					unboom(dismissImmediate);
				}
				return true;
			}
			return false;
		}
	};

	private DkBoomMenu(Context context, View anchor) {
		this.context = context;
		this.anchor = new WeakReference<>(anchor);
	}

	public static DkBoomMenu newIns(Context context, View anchor) {
		return new DkBoomMenu(context, anchor);
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
				for (DkItem item : itemClusterManager.items) {
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
						cancelAnimAndCleanupLayout();
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
			cancelAnimAndCleanupLayout();
			return;
		}
		if (animState == ANIM_STATE_ANIMATED) {
			if (immediate) {
				cancelAnimAndCleanupLayout();
				return;
			}
			animState = ANIM_STATE_WILL_ANIMATE_SOON;
			reverseAnimation(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					animState = ANIM_STATE_ANIMATING;
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					cancelAnimAndCleanupLayout();
				}
			});
		}
	}

	private void showAndForcusBackground() {
		background.setVisibility(View.VISIBLE);
		background.setFocusableInTouchMode(true);
		background.requestFocus();
	}

	private void cancelAnimAndCleanupLayout() {
		// Cancel animation
		if (animator != null) {
			animator.cancel();
			animator.removeAllListeners();
			animator.removeAllUpdateListeners();
		}
		animState = ANIM_STATE_NOT_YET;

		// Cleanup view
		removeMenuItems();
		removeBackground();
	}

	/**
	 * Create background if not yet existed -> just add as child to ancestor without showing.
	 */
	private void setupBackground() {
		View anchor = this.anchor.get();
		MyBackgroundLayout background = this.background;

		if (anchor == null) {
			throw new RuntimeException("Anchor is null. Please call setAnchor() or use 2nd constructor.");
		}
		if (background == null) {
			background = this.background = new MyBackgroundLayout(context);
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
			itemClusterManager.buildItems(context, anchor, parent, new DkItemBuilder.Callback() {
				@Override
				public void onTranslate(DkItem item, float dx, float dy) {
					itemClusterManager.translateItems(dx, dy, backgroundWidth, backgroundHeight);
				}

				@Override
				public void onClick(DkItem item, float x, float y) {
					if (item.dismissMenuOnClickItem) {
						unboom(item.dismissMenuImmediate);
					}
					if (item.onClickLisener != null) {
						item.onClickLisener.onClick(item.view, item.index);
					}
				}
			});
		}

		itemClusterManager.calcStartEndPositions(anchor, parent);

		for (DkItem item : itemClusterManager.items) {
			item.hide();
			background.addView(item.view);
		}
	}

	private boolean isShouldBuildItems() {
		if (shouldRebuildItems) {
			shouldRebuildItems = false;
			return true;
		}
		return itemClusterManager.items.size() == 0;
	}

	private void startAnimation(AnimatorListenerAdapter listener) {
		final ArrayList<DkItem> items = itemClusterManager.items;
		final int itemCount = items.size();
		long totalDuration = boomDuration + emissionDelayBetweenItems * (itemCount - 1);

		for (DkItem item : items) {
			totalDuration += item.animStartDelay;
		}

		background.setupAnimation(animStartDelay, totalDuration, totalDuration);
		itemClusterManager.setupAnimation(animStartDelay, emissionDelayBetweenItems, boomDuration, totalDuration);

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
		final ArrayList<DkItem> items = itemClusterManager.items;
		final int itemCount = items.size();
		long totalDuration = unboomDuration + emissionDelayBetweenItems * (itemCount - 1);

		for (DkItem item : items) {
			totalDuration += item.animStartDelay;
		}

		background.setupAnimation(animStartDelay, totalDuration, totalDuration);
		itemClusterManager.setupAnimation(animStartDelay, emissionDelayBetweenItems, unboomDuration, totalDuration);

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
			itemClusterManager.items.clear();
		}
	}

	private void removeBackground() {
		MyBackgroundLayout background = this.background;

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
			return MyHelper.findSuperFrameLayout(view);
		}

		ViewParent parent = view.getParent();

		return parent instanceof ViewGroup ? (ViewGroup) parent : null;
	}

	//
	// Setup region
	//

	public DkBoomMenu addItemBuilder(DkItemBuilder itemBuilder) {
		itemClusterManager.itemBuilders.add(itemBuilder);
		return this;
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

	public DkBoomMenu setDismissOnBackPressed(boolean dismiss, boolean dismissImmediate) {
		this.dismissOnBackPressed = dismiss;
		this.dismissImmediate = dismissImmediate;
		return this;
	}

	public DkBoomMenu setDismissOnBackPressed(boolean dismiss) {
		this.dismissOnBackPressed = dismiss;
		return this;
	}

	public DkBoomMenu setDismissImmediate(boolean dismissImmediate) {
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
		itemClusterManager.horizontalOffset = horizontalMargin;
		itemClusterManager.verticalOffset = verticalMargin;
		return this;
	}

	public DkBoomMenu setGravity(DkGravity gravity) {
		itemClusterManager.gravity = gravity;
		return this;
	}

	public DkBoomMenu setShape(DkShape shape) {
		itemClusterManager.shape = shape;
		return this;
	}

	public DkBoomMenu setEmissionDelayBetweenItems(long emissionDelay) {
		this.emissionDelayBetweenItems = emissionDelay;
		return this;
	}

	public DkBoomMenu setItemsEmissionOrder(DkEmissionOrder order) {
		itemClusterManager.emissionOrder = order;
		return this;
	}

	public DkBoomMenu setBringAnchorToFront(boolean bringAnchorToFront) {
		this.bringAnchorToFront = bringAnchorToFront;
		return this;
	}

	public DkBoomMenu setItemsPositionCalculator(DkPositionCalculator calculator) {
		itemClusterManager.calculator = calculator;
		return this;
	}

	public DkBoomMenu setRandomItemsStartPosition(boolean randomStartPosition) {
		itemClusterManager.randomStartPosition = randomStartPosition;
		return this;
	}

	public DkBoomMenu setAutoScaleClusterIfOversize(boolean autoScale) {
		itemClusterManager.autoScaleIfOversize = autoScale;
		return this;
	}

	public DkBoomMenu setAllowClusterOutsideBoard(boolean allow) {
		itemClusterManager.allowOutsideBoard = allow;
		return this;
	}

	public DkBoomMenu setAutoUnisize(boolean autoUnisize) {
		itemClusterManager.autoUnisize = autoUnisize;
		return this;
	}

	public DkBoomMenu setCacheOptimization(boolean cacheOptimization) {
		this.cacheOptimization = cacheOptimization;
		return this;
	}

	//
	// Setting for all items
	//

	public DkBoomMenu setOnItemClickListener(DkOnItemClickListener onItemClickListener) {
		itemClusterManager.onItemClickListener = onItemClickListener;
		return this;
	}

	public DkBoomMenu setItemDismissOnBackPressed(boolean dismiss) {
		itemClusterManager.itemDismissOnBackPressed = dismiss;
		return this;
	}

	public DkBoomMenu setItemDismissImmediate(boolean immediate) {
		itemClusterManager.itemDismissImmediate = immediate;
		return this;
	}

	public DkBoomMenu enableItemRotation(boolean enable) {
		itemClusterManager.itemEnableRotation = enable;
		return this;
	}

	public DkBoomMenu setItemMargin(int dp) {
		itemClusterManager.itemMargin = dp;
		return this;
	}
}
