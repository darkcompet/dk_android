/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import tool.compet.appbundle.R;
import tool.compet.appbundle.compact.DkCompactDialog;
import tool.compet.core.BuildConfig;
import tool.compet.core.DkConfig;
import tool.compet.core.DkLogs;
import tool.compet.core.view.DkViews;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * By default,
 * - Title, subtitle, message, buttons are gone
 * - Auto dismiss dialog when click to buttons or outside dialog
 */
@SuppressWarnings("unchecked")
public class DkConfirmDialog<D extends DkConfirmDialog> extends DkCompactDialog<D> implements View.OnClickListener, TheConfirmDialogInterface {
	// Callback
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

	public static final int LAYOUT_TYPE_HORIZONTAL_ACTIONS = 1;
	public static final int LAYOUT_TYPE_VERTICAL_ACTIONS = 2;
	protected int layoutType = LAYOUT_TYPE_VERTICAL_ACTIONS;

	// Fullground (dialog itself)
	protected ViewGroup vFullground;

	// Background (card view)
	protected ConstraintLayout vBackground;
	private Integer backgroundColor;
	private Drawable backgroundDrawable;

	// Header
	protected View vHeader;
	protected ImageView ivIcon;
	protected TextView vTitle;
	protected TextView vSubTitle;
	protected int iconResId; // store in instance state
	protected int titleTextResId; // store in instance state
	protected int subTitleTextResId; // store in instance state
	protected Integer headerBackgroundColor; // store in instance state

	// Body
	protected ViewGroup vBody;
	protected int bodyLayoutResId; // store in instance state
	protected float widthWeight = 4f; // store in instance state
	protected float heightWeight = 3f; // store in instance state
	// Content: message
	protected TextView vMessage;
	protected int messageTextResId; // store in instance state
	protected String message; // store in instance state
	protected Integer messageBackgroundColor; // store in instance state

	// Footer
	protected TextView vCancel;
	protected TextView vReset;
	protected TextView vOk;
	protected int cancelTextResId; // store in instance state
	protected int resetTextResId; // store in instance state
	protected int okTextResId; // store in instance state
	private ConfirmCallback cancelCb; // store in ViewModel
	private ConfirmCallback resetCb; // store in ViewModel
	private ConfirmCallback okCb; // store in ViewModel

	// Setting
	// private boolean mCancelable = true; // owned by `DialogFragment` (dismiss on back pressed...)
	// private boolean mShowsDialog = true; // owned by `DialogFragment` (attach fragment's view into dialog or not)
	protected boolean isDismissOnClickButton = true;
	protected boolean isDismissOnTouchOutside = true;
	protected boolean isFullScreen;

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
		return false;
	}

	@Override
	public boolean isRetainInstance() {
		return false;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		onStoreInstanceState(outState);

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		onSetupLayout(view);
	}

	// Subclass can override to customize layout setting
	@SuppressLint("ClickableViewAccessibility")
	protected void onSetupLayout(View view) {
		// layout = background + foreground
		// foreground = innner-padding + content (= header + content + footer)
		// header = title + subtitle
		// content = custom-view || message
		// footer = buttons
		vFullground = view.findViewById(R.id.dk_fullground);
		vBackground = view.findViewById(R.id.dk_background);
		vBody = view.findViewById(R.id.dk_body);

		vHeader = view.findViewById(R.id.dk_header);
		ivIcon = view.findViewById(R.id.dk_icon);
		vTitle = view.findViewById(R.id.dk_title);
		vSubTitle = view.findViewById(R.id.dk_subtitle);
		vMessage = view.findViewById(R.id.dk_message);
		vCancel = view.findViewById(R.id.dk_cancel);
		vReset = view.findViewById(R.id.dk_reset);
		vOk = view.findViewById(R.id.dk_ok);

		vFullground.setOnTouchListener((v, event) -> {
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					return true;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_OUTSIDE: {
					if (! DkViews.isInsideView(event, vBackground)) {
						onClickOutside();
					}
					break;
				}
			}
			return false;
		});

		// Background (card view)
		decorBackground();

		// Header
		decorHeader();
		decorIcon();
		decorTitle();
		decorSubTitle();

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
		ViewGroup.LayoutParams bkgLayoutParams = vBackground.getLayoutParams();
		if (bkgLayoutParams == null) {
			bkgLayoutParams = new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, Gravity.CENTER);
		}
		if (isFullScreen) {
			bkgLayoutParams.width = bkgLayoutParams.height = MATCH_PARENT;
		}
		else {
			int[] displaySize = DkConfig.displaySize();
			int ds = Math.min(displaySize[0], displaySize[1]);
			bkgLayoutParams.width = (ds >> 2) + (ds >> 1); // 0.75 * deviceSize
			if (widthWeight > 0) {
				bkgLayoutParams.height = (int) (bkgLayoutParams.width * heightWeight / widthWeight);
			}
		}
		vBackground.setLayoutParams(bkgLayoutParams);
	}

	@Override
	public void onStart() {
		super.onStart();

		//        Dialog dialog = getDialog();
		//        Window window = dialog.getWindow();
		//        ViewGroup.LayoutParams bkgLayoutParams = vForeground.getLayoutParams();
		//        DkLogs.debug(this, "bkgLayoutParams: %d, %d", bkgLayoutParams.width, bkgLayoutParams.height);
		//        DkLogs.debug(this, "vForeground: %d, %d", vForeground.getMeasuredWidth(), vForeground.getMeasuredHeight());
		//        DkLogs.debug(this, "vForeground: %d, %d", vForeground.getWidth(), vForeground.getHeight());
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();

		if (viewId == R.id.dk_cancel) {
			onCancelButtonClick(view);
		}
		else if (viewId == R.id.dk_reset) {
			onResetButtonClick(view);
		}
		else if (viewId == R.id.dk_ok) {
			onOkButtonClick(view);
		}

		if (isDismissOnClickButton) {
			dismiss();
		}
	}

	@Override
	public void onDismiss(@NonNull android.content.DialogInterface dialog) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDismiss");
		}
		onDismissDialog(dialog);

		super.onDismiss(dialog);
	}

	// region Get/Set

	public D setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		if (vBackground != null) {
			decorBackground();
		}
		return (D) this;
	}

	public D setIcon(int iconResId) {
		this.iconResId = iconResId;
		if (ivIcon != null) {
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

	public D setSubTitle(int subTitleRes) {
		this.subTitleTextResId = subTitleRes;
		if (vSubTitle != null) {
			decorSubTitle();
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

	public D setDimensionRatio(float widthWeight, float heightWeight) {
		this.widthWeight = widthWeight;
		this.heightWeight = heightWeight;
		return (D) this;
	}

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

	// endregion Get/Set

	// region Protected

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
			dismiss();
		}
	}

	/**
	 * Call when start dismiss dialog.
	 * Subclass can override this to here start dismiss event (is NOT dismissed-event)
	 */
	protected void onDismissDialog(android.content.DialogInterface dialog) {
	}

	// Subclass can override this to store something
	protected void onStoreInstanceState(@NonNull Bundle outState) {
		if (backgroundColor != null) {
			outState.putInt("DkConfirmDialog.backgroundColor", backgroundColor);
		}

		outState.putInt("DkConfirmDialog.iconResId", iconResId);
		outState.putInt("DkConfirmDialog.titleTextResId", titleTextResId);
		outState.putInt("DkConfirmDialog.subTitleTextResId", subTitleTextResId);
		if (headerBackgroundColor != null) {
			outState.putInt("DkConfirmDialog.headerBackgroundColor", headerBackgroundColor);
		}

		if (bodyLayoutResId > 0) {
			outState.putInt("DkConfirmDialog.bodyLayoutResId", bodyLayoutResId);
		}
		if (widthWeight > 0) {
			outState.putFloat("DkConfirmDialog.widthWeight", widthWeight);
		}
		if (heightWeight > 0) {
			outState.putFloat("DkConfirmDialog.heightWeight", heightWeight);
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

		ConfirmTopic confirmTopic = refTopic(CONFIRM_TOPIC).obtain(ConfirmTopic.class);
		confirmTopic.cancelCb = this.cancelCb;
		confirmTopic.resetCb = this.resetCb;
		confirmTopic.okCb = this.okCb;
		if (backgroundDrawable != null) {
			confirmTopic.backgroundDrawable = backgroundDrawable;
		}
	}

	// Subclass can override this to restore something
	protected void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			backgroundColor = savedInstanceState.getInt("DkConfirmDialog.backgroundColor");

			iconResId = savedInstanceState.getInt("DkConfirmDialog.iconResId");
			titleTextResId = savedInstanceState.getInt("DkConfirmDialog.titleTextResId");
			subTitleTextResId = savedInstanceState.getInt("DkConfirmDialog.subTitleTextResId");
			headerBackgroundColor = savedInstanceState.getInt("DkConfirmDialog.headerBackgroundColor");

			bodyLayoutResId = savedInstanceState.getInt("DkConfirmDialog.bodyLayoutResId");
			widthWeight = savedInstanceState.getFloat("DkConfirmDialog.widthWeight");
			heightWeight = savedInstanceState.getFloat("DkConfirmDialog.heightWeight");

			messageTextResId = savedInstanceState.getInt("DkConfirmDialog.messageTextResId");
			message = savedInstanceState.getString("DkConfirmDialog.message");
			messageBackgroundColor = savedInstanceState.getInt("DkConfirmDialog.messageBackgroundColor");

			cancelTextResId = savedInstanceState.getInt("DkConfirmDialog.cancelTextResId");
			resetTextResId = savedInstanceState.getInt("DkConfirmDialog.resetTextResId");
			okTextResId = savedInstanceState.getInt("DkConfirmDialog.okTextResId");

			isDismissOnClickButton = savedInstanceState.getBoolean("DkConfirmDialog.isDismissOnClickButton");
			isDismissOnTouchOutside = savedInstanceState.getBoolean("DkConfirmDialog.isDismissOnTouchOutside");
			isFullScreen = savedInstanceState.getBoolean("DkConfirmDialog.isFullScreen");

			ConfirmTopic confirmTopic = refTopic(CONFIRM_TOPIC).obtain(ConfirmTopic.class);
			this.cancelCb = confirmTopic.cancelCb;
			this.resetCb = confirmTopic.resetCb;
			this.okCb = confirmTopic.okCb;
			this.backgroundDrawable = confirmTopic.backgroundDrawable;
		}
	}

	// endregion Protected

	// region Private

	private void decorBackground() {
		if (vBackground == null) {
			return;
		}
		if (backgroundColor != null) {
			vBackground.setBackgroundColor(backgroundColor);
		}
		if (backgroundDrawable != null) {
			ViewCompat.setBackground(vBackground, backgroundDrawable);
		}
	}

	private void decorHeader() {
		if (vHeader == null) {
			return;
		}
		if (headerBackgroundColor != null) {
			vHeader.setBackgroundColor(headerBackgroundColor);
		}
	}

	private void decorIcon() {
		if (ivIcon == null) {
			return;
		}
		if (iconResId > 0) {
			ivIcon.setImageResource(iconResId);
			ivIcon.setVisibility(View.VISIBLE);
		}
		else {
			ivIcon.setVisibility(View.GONE);
		}
	}

	private void decorTitle() {
		if (vTitle == null) {
			return;
		}
		if (titleTextResId > 0) {
			DkViews.setTextSize(vTitle, 1.25f * vReset.getTextSize());
			vTitle.setText(titleTextResId);
			vTitle.setVisibility(View.VISIBLE);
		}
		else {
			vTitle.setVisibility(View.GONE);
		}
	}

	private void decorSubTitle() {
		if (vSubTitle == null) {
			return;
		}
		if (subTitleTextResId > 0) {
			DkViews.setTextSize(vSubTitle, 0.85f * vReset.getTextSize());
			vSubTitle.setText(subTitleTextResId);
			vSubTitle.setVisibility(View.VISIBLE);
		}
		else {
			vSubTitle.setVisibility(View.GONE);
		}
	}

	private void decorBodyView() {
		if (vMessage == null || vBody == null) {
			return;
		}
		if (messageTextResId > 0 || message != null) {
			if (messageBackgroundColor != null) {
				vMessage.setBackgroundColor(messageBackgroundColor);
			}
			if (messageTextResId > 0) {
				message = context.getString(messageTextResId);
			}
			DkViews.setTextSize(vMessage, 1.125f * vReset.getTextSize());
			vMessage.setMovementMethod(new ScrollingMovementMethod());
			vMessage.setVisibility(View.VISIBLE);
			vMessage.setText(message);
		}
		else {
			if (bodyLayoutResId > 0) {
				vBody.removeAllViews();
				vBody.addView(View.inflate(context, bodyLayoutResId, null));
			}
		}
	}

	private void decorCancelButton() {
		if (vCancel == null) {
			return;
		}
		if (cancelTextResId > 0) {
			vCancel.setVisibility(View.VISIBLE);
			vCancel.setText(cancelTextResId);
		}
		else {
			vCancel.setVisibility(View.GONE);
		}
	}

	private void decorResetButton() {
		if (vReset == null) {
			return;
		}
		if (resetTextResId > 0) {
			vReset.setVisibility(View.VISIBLE);
			vReset.setText(resetTextResId);
		}
		else {
			vReset.setVisibility(View.GONE);
		}
	}

	private void decorOkButton() {
		if (vOk == null) {
			return;
		}
		if (okTextResId > 0) {
			vOk.setVisibility(View.VISIBLE);
			vOk.setText(okTextResId);
		}
		else {
			vOk.setVisibility(View.GONE);
		}
	}

	// endregion Private
}
