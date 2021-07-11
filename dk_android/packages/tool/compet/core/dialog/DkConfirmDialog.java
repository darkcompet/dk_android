/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.animation.PathInterpolatorCompat;

import tool.compet.core.DkConfig;
import tool.compet.core.R;
import tool.compet.core.animation.DkAnimationConfiguration;
import tool.compet.core.animation.DkLookupTableInterpolator;
import tool.compet.core.compact.DkCompactDialogFragment;
import tool.compet.core.graphics.DkDrawables;
import tool.compet.core.view.DkViews;
import tool.compet.core4j.DkRunner2;

/**
 * By default,
 * - Title, subtitle, message, buttons are gone
 * - Auto dismiss dialog when click to buttons or outside dialog
 * - Default open, close animation
 */
@SuppressWarnings("unchecked")
public class DkConfirmDialog<D extends DkConfirmDialog>
	extends DkCompactDialogFragment<D>
	implements View.OnClickListener, TheConfirmDialogInterface {

	/**
	 * Callback
	 */
	public interface ConfirmCallback {
		void onClick(TheConfirmDialogInterface dialog, View button);
	}

	public static final String CONFIRM_TOPIC = ConfirmTopic.class.getName();
	public static class ConfirmTopic {
		public ConfirmCallback cancelCb;
		public ConfirmCallback resetCb;
		public ConfirmCallback okCb;
		public Drawable backgroundDrawable;
	}

	private static final int NORMAL = Color.parseColor("#333333");
	private static final int ASK = Color.parseColor("#009b8b");
	private static final int ERROR = Color.parseColor("#ff0000");
	private static final int WARNING = Color.parseColor("#ff9500");
	private static final int INFO = Color.parseColor("#493ebb");
	private static final int SUCCESS = Color.parseColor("#00bb4d");

	public static final int LAYOUT_TYPE_HORIZONTAL_ACTIONS = 0;
	public static final int LAYOUT_TYPE_VERTICAL_ACTIONS = 1;
	protected int layoutType = LAYOUT_TYPE_VERTICAL_ACTIONS;

	/**
	 * Dialog content.
	 */

	protected ViewGroup vContent;
	private Integer backgroundColor;
	private Drawable backgroundDrawable;

	/**
	 * Header
	 */

	protected View vHeader;
	protected TextView vTitle;
	protected int iconResId; // store in instance state
	protected int titleTextResId; // store in instance state
	protected CharSequence title; // store in instance state
	protected int subTitleTextResId; // store in instance state
	protected int headerBackgroundColor = Color.TRANSPARENT; // store in instance state

	/**
	 * Body
	 */

	protected ViewGroup vBody;
	protected int bodyLayoutResId; // store in instance state
	protected float widthPercent = 0.85f; // store in instance state
	protected float heightPercent; // store in instance state
	protected boolean dimensionRatioBasedOnWidth = true; // store in instance state
	protected float widthRatio; // store in instance state
	protected float heightRatio; // store in instance state
	// Content: message
	protected TextView vMessage;
	protected int messageTextResId; // store in instance state
	protected String message; // store in instance state
	protected Integer messageBackgroundColor; // store in instance state

	/**
	 * Footer
	 */

	protected TextView vCancel;
	protected TextView vReset;
	protected TextView vOk;
	protected int cancelTextResId; // store in instance state
	protected int resetTextResId; // store in instance state
	protected int okTextResId; // store in instance state
	private ConfirmCallback cancelCb; // store in ViewModel
	private ConfirmCallback resetCb; // store in ViewModel
	private ConfirmCallback okCb; // store in ViewModel

	/**
	 * Setting
	 */

	protected boolean isDismissOnClickButton = true;
	protected boolean isDismissOnTouchOutside = true;
	protected boolean isFullScreen;

	// Indicate this dialog is dismissable for some actions as: back pressed...
	protected boolean cancelable = true;

	/**
	 * Animation
	 */

	// Zoom-in (bigger) and then Zomm-out (smaller)
	public static final int ANIM_ZOOM_IN_OUT = 1;
	// Like as spring mocks a ball which is pulling down
	public static final int ANIM_SWIPE_DOWN = 2;

	private ValueAnimator animator;
	private boolean enableEnterAnimation = true; // whether has animation when show dialog
	private boolean enableExitAnimation; // whether has animation when dismiss dialog
	private int enterAnimationType = ANIM_ZOOM_IN_OUT;
	private int exitAnimationType = -1;

	private Interpolator animInterpolator;
	private Interpolator defaultEnterAnimInterpolator;
	private Interpolator exitAnimInterpolator;

	private DkRunner2<ValueAnimator, View> animUpdater;

	@Override
	public int layoutResourceId() {
		if (layoutType == LAYOUT_TYPE_VERTICAL_ACTIONS) {
			return R.layout.dk_confirm_dialog_vertical_actions;
		}
		return R.layout.dk_confirm_dialog_horizonal_actions;
	}

	@Override
	public int fragmentContainerId() {
		return 0;
	}

	@Override
	public boolean onBackPressed() {
		return ! cancelable; // TRUE: i will handle, FALSE: please popback
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		// Call it before `super.onViewCreated()` since super will tell Logic that View is ready.
		onInitChildren(view);

		super.onViewCreated(view, savedInstanceState);

		onSetupLayout(view);

		if (enableEnterAnimation) {
			showEnterAnimation();
		}
	}

	@Override // from View.OnClickListener interface
	public void onClick(View view) {
		// Perform callback
		final int viewId = view.getId();

		if (viewId == R.id.dk_cancel) {
			onCancelButtonClick(view);
		}
		else if (viewId == R.id.dk_reset) {
			onResetButtonClick(view);
		}
		else if (viewId == R.id.dk_ok) {
			onOkButtonClick(view);
		}

		// Dismiss (close) the dialog
		if (isDismissOnClickButton) {
			close();
		}
	}

	// region Protected

	/**
	 * Subclass can override to customize init children.
	 */
	protected void onInitChildren(View view) {
		// layout = background + foreground
		// foreground = innner-padding + content (= header + content + footer)
		// header = title + subtitle
		// content = custom-view || message
		// footer = buttons
		vContent = view.findViewById(R.id.dk_background);
		vBody = view.findViewById(R.id.dk_body);

		vHeader = view.findViewById(R.id.dk_header);
		vTitle = view.findViewById(R.id.dk_title);
		vMessage = view.findViewById(R.id.dk_message);
		vCancel = view.findViewById(R.id.dk_cancel);
		vReset = view.findViewById(R.id.dk_reset);
		vOk = view.findViewById(R.id.dk_ok);
	}

	/**
	 * Subclass can override to customize layout setting.
	 */
	@SuppressLint("ClickableViewAccessibility")
	protected void onSetupLayout(View view) {
		view.setOnTouchListener((v, event) -> {
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					return true;
				case MotionEvent.ACTION_UP: {
					if (! DkViews.isInsideView(event, vContent)) {
						onClickOutside();
					}
					break;
				}
			}
			return false;
		});

		// Dialog content (rounded corner view)
		decorContent();

		// Header
		decorHeader();
		decorIcon();
		decorTitle();

		// Body
		decorBodyView();

		// Footer
		vCancel.setOnClickListener(this);
		decorCancelButton();

		vReset.setOnClickListener(this);
		decorResetButton();

		vOk.setOnClickListener(this);
		decorOkButton();

		// Background (dialog) dimension
		ViewGroup.LayoutParams bkgLayoutParams = vContent.getLayoutParams();
		final int[] dimensions = DkConfig.displaySize();
		if (bkgLayoutParams == null) {
			bkgLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		if (isFullScreen) {
			bkgLayoutParams.width = bkgLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
		}
		if (widthPercent != 0) {
			bkgLayoutParams.width = (int) (dimensions[0] * widthPercent);
		}
		if (heightPercent != 0) {
			bkgLayoutParams.height = (int) (dimensions[1] * heightPercent);
		}
		if (widthRatio != 0 && heightRatio != 0) {
			if (dimensionRatioBasedOnWidth) {
				bkgLayoutParams.height = (int) (bkgLayoutParams.width * heightRatio / widthRatio);
			}
			else {
				bkgLayoutParams.width = (int) (bkgLayoutParams.height * widthRatio / heightRatio);
			}
		}
		vContent.setLayoutParams(bkgLayoutParams);
	}

	/**
	 * By default, this try to perform cancel-callback.
	 * Subclass can override to customize click event.
	 */
	protected void onCancelButtonClick(View button) {
		if (this.cancelCb != null) {
			this.cancelCb.onClick(this, button);
		}
	}

	/**
	 * By default, this try to perform reset-callback.
	 * Subclass can override to customize click event.
	 */
	protected void onResetButtonClick(View button) {
		if (this.resetCb != null) {
			this.resetCb.onClick(this, button);
		}
	}

	/**
	 * By default, this try to perform ok-callback.
	 * Subclass can override to customize click event.
	 */
	protected void onOkButtonClick(View button) {
		if (this.okCb != null) {
			this.okCb.onClick(this, button);
		}
	}

	/**
	 * By default, this check `isDismissOnTouchOutside` flag to dismiss dialog.
	 * Subclass can override to customize click event.
	 */
	protected void onClickOutside() {
		if (isDismissOnTouchOutside) {
			close();
		}
	}

	@CallSuper
	@Override
	protected void storeInstanceState(@NonNull Bundle outState) {
		super.storeInstanceState(outState);

		if (backgroundColor != null) {
			outState.putInt("DkConfirmDialog.backgroundColor", backgroundColor);
		}

		outState.putInt("DkConfirmDialog.iconResId", iconResId);
		outState.putInt("DkConfirmDialog.titleTextResId", titleTextResId);
		outState.putCharSequence("DkConfirmDialog.title", title);
		outState.putInt("DkConfirmDialog.subTitleTextResId", subTitleTextResId);
		outState.putInt("DkConfirmDialog.headerBackgroundColor", headerBackgroundColor);

		if (bodyLayoutResId > 0) {
			outState.putInt("DkConfirmDialog.bodyLayoutResId", bodyLayoutResId);
		}
		if (widthPercent > 0) {
			outState.putFloat("DkConfirmDialog.widthWeight", widthPercent);
		}
		if (heightPercent > 0) {
			outState.putFloat("DkConfirmDialog.heightWeight", heightPercent);
		}

		if (messageTextResId > 0) {
			outState.putInt("DkConfirmDialog.messageTextResId", messageTextResId);
		}
		if (message != null) {
			outState.putString("DkConfirmDialog.message", message);
		}
		if (messageBackgroundColor != null) {
			outState.putInt("DkConfirmDialog.messageBackgroundColor", messageBackgroundColor);
		}

		outState.putInt("DkConfirmDialog.cancelTextResId", cancelTextResId);
		outState.putInt("DkConfirmDialog.resetTextResId", resetTextResId);
		outState.putInt("DkConfirmDialog.okTextResId", okTextResId);

		outState.putBoolean("DkConfirmDialog.isDismissOnClickButton", isDismissOnClickButton);
		outState.putBoolean("DkConfirmDialog.isDismissOnTouchOutside", isDismissOnTouchOutside);
		outState.putBoolean("DkConfirmDialog.isFullScreen", isFullScreen);
		outState.putBoolean("DkConfirmDialog.cancelable", cancelable);

		ConfirmTopic confirmTopic = joinHostTopic(CONFIRM_TOPIC).obtain(ConfirmTopic.class);
		confirmTopic.cancelCb = this.cancelCb;
		confirmTopic.resetCb = this.resetCb;
		confirmTopic.okCb = this.okCb;
		if (backgroundDrawable != null) {
			confirmTopic.backgroundDrawable = backgroundDrawable;
		}
	}

	@CallSuper
	@Override
	protected void restoreInstanceState(@Nullable Bundle savedInstanceState) {
		super.restoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			backgroundColor = savedInstanceState.getInt("DkConfirmDialog.backgroundColor");

			iconResId = savedInstanceState.getInt("DkConfirmDialog.iconResId");
			titleTextResId = savedInstanceState.getInt("DkConfirmDialog.titleTextResId");
			title = savedInstanceState.getCharSequence("DkConfirmDialog.title");
			subTitleTextResId = savedInstanceState.getInt("DkConfirmDialog.subTitleTextResId");
			headerBackgroundColor = savedInstanceState.getInt("DkConfirmDialog.headerBackgroundColor");

			bodyLayoutResId = savedInstanceState.getInt("DkConfirmDialog.bodyLayoutResId");
			widthPercent = savedInstanceState.getFloat("DkConfirmDialog.widthWeight");
			heightPercent = savedInstanceState.getFloat("DkConfirmDialog.heightWeight");

			messageTextResId = savedInstanceState.getInt("DkConfirmDialog.messageTextResId");
			message = savedInstanceState.getString("DkConfirmDialog.message");
			messageBackgroundColor = savedInstanceState.getInt("DkConfirmDialog.messageBackgroundColor");

			cancelTextResId = savedInstanceState.getInt("DkConfirmDialog.cancelTextResId");
			resetTextResId = savedInstanceState.getInt("DkConfirmDialog.resetTextResId");
			okTextResId = savedInstanceState.getInt("DkConfirmDialog.okTextResId");

			isDismissOnClickButton = savedInstanceState.getBoolean("DkConfirmDialog.isDismissOnClickButton");
			isDismissOnTouchOutside = savedInstanceState.getBoolean("DkConfirmDialog.isDismissOnTouchOutside");
			isFullScreen = savedInstanceState.getBoolean("DkConfirmDialog.isFullScreen");
			cancelable = savedInstanceState.getBoolean("DkConfirmDialog.cancelable", false);

			ConfirmTopic confirmTopic = joinHostTopic(CONFIRM_TOPIC).obtain(ConfirmTopic.class);
			this.cancelCb = confirmTopic.cancelCb;
			this.resetCb = confirmTopic.resetCb;
			this.okCb = confirmTopic.okCb;
			this.backgroundDrawable = confirmTopic.backgroundDrawable;
		}
	}

	// endregion Protected

	// region Get/Set

	public D setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		if (vContent != null) {
			decorContent();
		}
		return (D) this;
	}

	public D setIcon(int iconResId) {
		this.iconResId = iconResId;
		if (vTitle != null) {
			decorIcon();
		}
		return (D) this;
	}

	public D setTitle(int titleResId) {
		this.titleTextResId = titleResId;
		if (vTitle != null) {
			decorTitle();
		}
		return (D) this;
	}

	public D setTitle(CharSequence title) {
		this.title = title;
		if (vTitle != null) {
			decorTitle();
		}
		return (D) this;
	}

	public D setMessage(int messageResId) {
		this.messageTextResId = messageResId;
		if (vMessage != null) {
			decorBodyView();
		}
		return (D) this;
	}

	public D setMessage(String message) {
		this.message = message;
		if (vMessage != null) {
			decorBodyView();
		}
		return (D) this;
	}

	public D setBodyView(int layoutResId) {
		this.bodyLayoutResId = layoutResId;
		if (vBody != null) {
			decorBodyView();
		}
		return (D) this;
	}

	public D setCancelButton(int textRes, ConfirmCallback cancelCb) {
		return (D) setCancelButtonCallback(cancelCb).setCancelButton(textRes);
	}

	public D setCancelButton(int textResId) {
		this.cancelTextResId = textResId;
		if (vCancel != null) {
			decorCancelButton();
		}
		return (D) this;
	}

	public D setCancelButtonCallback(ConfirmCallback cancelCb) {
		this.cancelCb = cancelCb;
		return (D) this;
	}

	public D setResetButton(int textRes, ConfirmCallback resetCb) {
		return (D) setResetButtonCallback(resetCb).setResetButton(textRes);
	}

	public D setResetButton(int textResId) {
		this.resetTextResId = textResId;
		if (vReset != null) {
			decorResetButton();
		}
		return (D) this;
	}

	public D setResetButtonCallback(ConfirmCallback resetCb) {
		this.resetCb = resetCb;
		return (D) this;
	}

	public D setOkButton(int textRes, ConfirmCallback okCb) {
		return (D) setOkButtonCallback(okCb).setOkButton(textRes);
	}

	public D setOkButton(int textResId) {
		this.okTextResId = textResId;
		if (vOk != null) {
			decorOkButton();
		}
		return (D) this;
	}

	public D setOkButtonCallback(ConfirmCallback okCb) {
		this.okCb = okCb;
		return (D) this;
	}

	public D setDismissOnTouchOutside(boolean isDismissOnTouchOutside) {
		this.isDismissOnTouchOutside = isDismissOnTouchOutside;
		return (D) this;
	}

	public D setDismissOnClickButton(boolean dismissOnClickButton) {
		this.isDismissOnClickButton = dismissOnClickButton;
		return (D) this;
	}

	public D setFullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;
		return (D) this;
	}

	/**
	 * Set percent of width, height based on device size.
	 * @param widthPercent Percent based on device width.
	 */
	public D setWidthPercent(float widthPercent) {
		this.widthPercent = widthPercent;
		return (D) this;
	}

	/**
	 * Set percent of width, height based on device size.
	 * @param heightPercent Percent based on device height.
	 */
	public D setHeightPercent(float heightPercent) {
		this.heightPercent = heightPercent;
		return (D) this;
	}


	/**
	 * Set percent of width, height based on device size.
	 * @param widthPercent Percent based on device width.
	 * @param heightPercent Percent based on device height.
	 */
	public D setDimensionPercent(float widthPercent, float heightPercent) {
		this.widthPercent = widthPercent;
		this.heightPercent = heightPercent;
		return (D) this;
	}

	/**
	 * Set ratio between width and height.
	 * @param widthRatio Ratio of width.
	 * @param heightRatio Ratio of height.
	 * @param basedOnWidth Base when calculate rate, true (based on width), false (based on height).
	 */
	public D setDimensionRatio(float widthRatio, float heightRatio, boolean basedOnWidth) {
		this.widthRatio = widthRatio;
		this.heightRatio = heightRatio;
		this.dimensionRatioBasedOnWidth = basedOnWidth;
		return (D) this;
	}

	/**
	 * This dialog provides some layout type, for eg,. vertical layout, horizontal layout...
	 */
	public D setLayoutType(int layoutType) {
		this.layoutType = layoutType;
		return (D) this;
	}

	public D asSuccess() {
		return asColor(SUCCESS);
	}

	public D asError() {
		return asColor(ERROR);
	}

	public D asWarning() {
		return asColor(WARNING);
	}

	public D asAsk() {
		return asColor(ASK);
	}

	public D asInfo() {
		return asColor(INFO);
	}

	public D asColor(int color) {
		return setHeaderBackgroundColor(color);
	}

	public D setHeaderBackgroundColor(int color) {
		this.headerBackgroundColor = color;
		if (vHeader != null) {
			vHeader.setBackgroundColor(color);
		}
		return (D) this;
	}

	public D setMessageBackgroundColor(int messageBackgroundColor) {
		this.messageBackgroundColor = messageBackgroundColor;
		if (vMessage != null) {
			vMessage.setBackgroundColor(messageBackgroundColor);
		}
		return (D) this;
	}

	public void setEnableEnterAnimation(boolean enableEnterAnimation) {
		this.enableEnterAnimation = enableEnterAnimation;
	}

	public void setEnableExitAnimation(boolean enableExitAnimation) {
		this.enableExitAnimation = enableExitAnimation;
	}

	// endregion Get/Set

	// region Private

	private void decorContent() {
		if (vContent != null) {
			if (backgroundColor != null) {
				vContent.setBackgroundColor(backgroundColor);
			}
			if (backgroundDrawable != null) {
				ViewCompat.setBackground(vContent, backgroundDrawable);
			}
		}
	}

	private void decorHeader() {
		if (vHeader != null) {
			vHeader.setBackgroundColor(headerBackgroundColor);
		}
	}

	private void decorIcon() {
		if (vTitle != null) {
			if (iconResId > 0) {
				if (layoutType == LAYOUT_TYPE_VERTICAL_ACTIONS) {
					Drawable left = DkDrawables.loadDrawable(context, iconResId);
					vTitle.setCompoundDrawables(left, null, null, null);
				}
				else if (layoutType == LAYOUT_TYPE_HORIZONTAL_ACTIONS) {
					Drawable left = DkDrawables.loadDrawable(context, iconResId);
					vTitle.setCompoundDrawables(left, null, null, null);
				}

				if (vTitle.getVisibility() != View.VISIBLE) {
					vTitle.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void decorTitle() {
		if (vTitle != null) {
			if (titleTextResId > 0) {
				title = context.getString(titleTextResId);
			}
			if (title != null) {
				DkViews.setTextSize(vTitle, 1.25f * vReset.getTextSize());
				vTitle.setText(title);
				vTitle.setVisibility(View.VISIBLE);
			}
			else {
				vTitle.setVisibility(View.GONE);
			}
		}
	}

	private void decorBodyView() {
		if (vMessage != null && vBody != null) {
			if (messageTextResId > 0 || message != null) {
				if (messageBackgroundColor != null) {
					vMessage.setBackgroundColor(messageBackgroundColor);
				}
				if (messageTextResId > 0) {
					message = context.getString(messageTextResId);
				}
				DkViews.setTextSize(vMessage, 1.125f * vReset.getTextSize());
				vMessage.setMovementMethod(new ScrollingMovementMethod());
				vMessage.setText(message);
				vMessage.setVisibility(View.VISIBLE);
			}
			else if (bodyLayoutResId > 0) {
				vBody.removeAllViews();
				vBody.addView(View.inflate(context, bodyLayoutResId, null));
			}
		}
	}

	private void decorCancelButton() {
		if (vCancel != null) {
			if (cancelTextResId > 0) {
				vCancel.setVisibility(View.VISIBLE);
				vCancel.setText(cancelTextResId);
			}
			else {
				vCancel.setVisibility(View.GONE);
			}
		}
	}

	private void decorResetButton() {
		if (vReset != null) {
			if (resetTextResId > 0) {
				vReset.setVisibility(View.VISIBLE);
				vReset.setText(resetTextResId);
			}
			else {
				vReset.setVisibility(View.GONE);
			}
		}
	}

	private void decorOkButton() {
		if (vOk != null) {
			if (okTextResId > 0) {
				vOk.setVisibility(View.VISIBLE);
				vOk.setText(okTextResId);
			}
			else {
				vOk.setVisibility(View.GONE);
			}
		}
	}

	private void showEnterAnimation() {
		if (vContent != null) {
			// Jump to end state to complete last animation
			if (animator == null) {
				animator = ValueAnimator.ofFloat(0.85f, 1f);
			}
			else {
				animator.end();
				animator.removeAllUpdateListeners();
				animator.removeAllListeners();
			}

			animUpdater = acquireAnimationUpdater();
			animInterpolator = acquireEnterAnimationInterpolator();

			animator.setDuration(150);
			animator.setInterpolator(animInterpolator);
			animator.addUpdateListener(anim -> {
				animUpdater.run(anim, vContent);
			});
			animator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					//					super.onAnimationEnd(animation);
					//					onShowAnimationEnd(dialog);
				}
			});
			animator.start();
		}
	}

	private void showExitAnimation() {
		if (animator != null) {
			animUpdater = acquireAnimationUpdater();
			animInterpolator = acquireEnterAnimationInterpolator();

			animator.removeAllListeners();
			animator.removeAllUpdateListeners();
			animator.setDuration(DkAnimationConfiguration.ANIM_LARGE_COLLAPSE_DURATION);
			animator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					//						super.onAnimationEnd(animation);
					//						onDismissAnimationEnd(dialog);
				}
			});
			animator.reverse();
		}
	}

	private Interpolator acquireEnterAnimationInterpolator() {
		if (enterAnimationType == ANIM_ZOOM_IN_OUT) {
			if (defaultEnterAnimInterpolator == null) {
				defaultEnterAnimInterpolator = PathInterpolatorCompat.create(
					0.72f, 1.32f,
					0.90f, 1.33f);
			}
		}
		else if (enterAnimationType == ANIM_SWIPE_DOWN) {
			if (defaultEnterAnimInterpolator == null) {
				defaultEnterAnimInterpolator = new DkLookupTableInterpolator(null); // DkInterpolatorProvider.easeElasticOut()
			}
		}
		else {
			throw new RuntimeException("Invalid animType");
		}
		return (animInterpolator = defaultEnterAnimInterpolator);
	}

	private static float[] enterAnimationlookupTable = {
		1f
	};

	private static float[] exitAnimationlookupTable = {
		1f
	};

	private DkRunner2<ValueAnimator, View> acquireAnimationUpdater() {
		if (animUpdater == null) {
			switch (enterAnimationType) {
				case ANIM_ZOOM_IN_OUT: {
					animUpdater = (va, view) -> {
						float t = va.getAnimatedFraction();
						float scaleFactor = (float) va.getAnimatedValue();

						view.setScaleX(scaleFactor);
						view.setScaleY(scaleFactor);
					};
					break;
				}
				case ANIM_SWIPE_DOWN: {
					animUpdater = (va, view) -> {
						view.setY((va.getAnimatedFraction() - 1) * view.getHeight() / 2);
					};
					break;
				}
				default: {
					throw new RuntimeException("Invalid animType");
				}
			}
		}
		return animUpdater;
	}

	// endregion Private

	// onCreate() -> onCreateDialog() -> onCreateView()

	// onViewCreated() -> onViewStateRestored() -> onStart()

	//	@Override
	//	public void onStart() {
	//		if (BuildConfig.DEBUG) {
	//			DkLogs.info(this, "onStart");
	//		}
	//		super.onStart();
	//
	//		// At this time, window is displayed, so we can set size of the dialog
	//		Dialog dialog = getDialog();
	//
	//		if (dialog != null) {
	//			Window window = dialog.getWindow();
	//
	//			if (window != null) {
	//				window.setLayout(MATCH_PARENT, MATCH_PARENT);
	//				window.setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
	//
	//				if (requestInputMethod()) {
	//					window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	//				}
	//			}
	//		}
	//	}


	// onSaveInstanceState() -> onDestroy()
}
