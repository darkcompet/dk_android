/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tool.compet.appbundle.R;
import tool.compet.appbundle.architecture.simple.DkSimpleDialog;
import tool.compet.appbundle.constant.ColorConst;
import tool.compet.core.BuildConfig;
import tool.compet.core.config.DkConfig;
import tool.compet.core.log.DkLogs;
import tool.compet.core.view.DkTextViews;
import tool.compet.core.view.DkViews;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * By default,
 * - Title, subtitle, message, buttons are gone
 * - Auto dismiss dialog when click to buttons or outside dialog
 */
public class DkConfirmDialog extends DkSimpleDialog
    implements View.OnClickListener, TheConfirmDialogInterface {
    //
    // Callback
    //
    public interface ConfirmCallback {
        void onClick(TheConfirmDialogInterface dialog, View button);
    }
    public static final String CONFIRM_TOPIC = ConfirmTopic.class.getName();
    public static class ConfirmTopic {
        public ConfirmCallback cancelCb;
        public ConfirmCallback resetCb;
        public ConfirmCallback okCb;
    }

    protected ViewGroup vBackground;
    protected ViewGroup vForeground;

    // Header
    protected View vHeader;
    protected TextView vTitle;
    protected TextView vSubTitle;
    protected int titleTextResId; // store in instance state
    protected int subTitleTextResId; // store in instance state
    protected Integer headerBackgroundColor; // store in instance state

    // Content: body view
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
        return R.layout.dk_dialog_confirm;
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
        vBackground = view.findViewById(R.id.dk_background);
        vForeground = view.findViewById(R.id.dk_foreground);
        vBody = view.findViewById(R.id.dk_body);

        vHeader = view.findViewById(R.id.dk_header);
        vTitle = view.findViewById(R.id.dk_title);
        vCancel = view.findViewById(R.id.dk_cancel);
        vReset = view.findViewById(R.id.dk_reset);
        vOk = view.findViewById(R.id.dk_ok);
        vSubTitle = view.findViewById(R.id.dk_subtitle);
        vMessage = view.findViewById(R.id.dk_message);

        vBackground.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE: {
                    if (! DkViews.isInsideView(event, vForeground)) {
                        onClickOutside();
                    }
                    break;
                }
            }
            return false;
        });

        //
        // Header
        //

        if (headerBackgroundColor != null) {
            vHeader.setBackgroundColor(headerBackgroundColor);
        }
        decorTitle();
        decorSubTitle();

        //
        // Body
        //

        decorBodyView();

        //
        // Footer
        //

        vCancel.setOnClickListener(this);
        decorCancelButton();

        vReset.setOnClickListener(this);
        decorResetButton();

        vOk.setOnClickListener(this);
        decorOkButton();

        //
        // Background (dialog) dimension
        //

        ViewGroup.LayoutParams bkgLayoutParams = vForeground.getLayoutParams();
        if (bkgLayoutParams == null) {
            bkgLayoutParams = new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, Gravity.CENTER);
        }
        if (isFullScreen) {
            bkgLayoutParams.width = bkgLayoutParams.height = MATCH_PARENT;
        }
        else {
            int d = Math.min(DkConfig.device.displaySize[0], DkConfig.device.displaySize[1]);
            bkgLayoutParams.width = (d >> 2) + (d >> 1); // 0.75
//            bkgLayoutParams.height = (int) (bkgLayoutParams.width * heightWeight / widthWeight);
        }
        vForeground.setLayoutParams(bkgLayoutParams);
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

    public DkConfirmDialog setTitle(int titleResId) {
        this.titleTextResId = titleResId;
        if (vTitle != null) {
            decorTitle();
        }
        return this;
    }

    public DkConfirmDialog setSubTitle(int subTitleRes) {
        this.subTitleTextResId = subTitleRes;
        if (vSubTitle != null) {
            decorSubTitle();
        }
        return this;
    }

    public DkConfirmDialog setMessage(int messageResId) {
        this.messageTextResId = messageResId;
        if (vMessage != null) {
            decorBodyView();
        }
        return this;
    }

    public DkConfirmDialog setMessage(String message) {
        this.message = message;
        if (vMessage != null) {
            decorBodyView();
        }
        return this;
    }

    public DkConfirmDialog setBodyView(int layoutResId) {
        this.bodyLayoutResId = layoutResId;
        if (vBody != null) {
            decorBodyView();
        }
        return this;
    }

    public DkConfirmDialog setCancelButton(int textRes, ConfirmCallback cancelCb) {
        return setCancelButtonCallback(cancelCb).setCancelButton(textRes);
    }

    public DkConfirmDialog setCancelButton(int textResId) {
        this.cancelTextResId = textResId;
        if (vCancel != null) {
            decorCancelButton();
        }
        return this;
    }

    public DkConfirmDialog setCancelButtonCallback(ConfirmCallback cancelCb) {
        this.cancelCb = cancelCb;
        return this;
    }

    public DkConfirmDialog setResetButton(int textRes, ConfirmCallback resetCb) {
        return setResetButtonCallback(resetCb).setResetButton(textRes);
    }

    public DkConfirmDialog setResetButton(int textResId) {
        this.resetTextResId = textResId;
        if (vReset != null) {
            decorResetButton();
        }
        return this;
    }

    public DkConfirmDialog setResetButtonCallback(ConfirmCallback resetCb) {
        this.resetCb = resetCb;
        return this;
    }

    public DkConfirmDialog setOkButton(int textRes, ConfirmCallback okCb) {
        return setOkButtonCallback(okCb).setOkButton(textRes);
    }

    public DkConfirmDialog setOkButton(int textResId) {
        this.okTextResId = textResId;
        if (vOk != null) {
            decorOkButton();
        }
        return this;
    }

    public DkConfirmDialog setOkButtonCallback(ConfirmCallback okCb) {
        this.okCb = okCb;
        return this;
    }

    public DkConfirmDialog setDismissOnTouchOutside(boolean isDismissOnTouchOutside) {
        this.isDismissOnTouchOutside = isDismissOnTouchOutside;
        return this;
    }

    public DkConfirmDialog setDismissOnClickButton(boolean dismissOnClickButton) {
        this.isDismissOnClickButton = dismissOnClickButton;
        return this;
    }

    public DkConfirmDialog setFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
        return this;
    }

    public DkConfirmDialog setDimensionWithRate(float widthWeight, float heightWeight) {
        this.widthWeight = widthWeight;
        this.heightWeight = heightWeight;
        return this;
    }

    public DkConfirmDialog asSuccess() {
        return asType(ColorConst.SUCCESS);
    }

    public DkConfirmDialog asError() {
        return asType(ColorConst.ERROR);
    }

    public DkConfirmDialog asWarning() {
        return asType(ColorConst.WARNING);
    }

    public DkConfirmDialog asAsk() {
        return asType(ColorConst.ASK);
    }

    public DkConfirmDialog asInfo() {
        return asType(ColorConst.INFO);
    }

    public DkConfirmDialog asType(int color) {
        return setHeaderBackgroundColor(color);
    }

    public DkConfirmDialog setHeaderBackgroundColor(int color) {
        this.headerBackgroundColor = color;
        if (vHeader != null) {
            vHeader.setBackgroundColor(color);
        }
        return this;
    }

    public DkConfirmDialog setMessageBackgroundColor(int messageBackgroundColor) {
        this.messageBackgroundColor = messageBackgroundColor;
        if (vMessage != null) {
            vMessage.setBackgroundColor(messageBackgroundColor);
        }
        return this;
    }

    //
    // Protected region
    //

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

        ConfirmTopic confirmTopic = joinTopic(CONFIRM_TOPIC).obtain(ConfirmTopic.class);
        confirmTopic.cancelCb = this.cancelCb;
        confirmTopic.resetCb = this.resetCb;
        confirmTopic.okCb = this.okCb;
    }

    // Subclass can override this to restore something
    protected void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
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

            ConfirmTopic confirmTopic = joinTopic(CONFIRM_TOPIC).obtain(ConfirmTopic.class);
            this.cancelCb = confirmTopic.cancelCb;
            this.resetCb = confirmTopic.resetCb;
            this.okCb = confirmTopic.okCb;
        }
    }

    //
    // Private region
    //

    private void decorTitle() {
        if (vTitle ==  null) {
            return;
        }
        if (titleTextResId > 0) {
            vTitle.setText(titleTextResId);
            vTitle.setVisibility(View.VISIBLE);
            DkTextViews.scaleTextSize(vTitle, 1.3f);
        }
        else {
            vTitle.setVisibility(View.GONE);
        }
    }

    private void decorSubTitle() {
        if (vSubTitle ==  null) {
            return;
        }
        if (subTitleTextResId > 0) {
            vSubTitle.setText(subTitleTextResId);
            vSubTitle.setVisibility(View.VISIBLE);
            DkTextViews.scaleTextSize(vSubTitle, 0.85f);
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
            if (messageTextResId > 0) {
                message = context.getString(messageTextResId);
            }

            vMessage.setVisibility(View.VISIBLE);
            vMessage.setText(message);
            DkTextViews.scaleTextSize(vMessage, 1.2f);

            if (messageBackgroundColor != null) {
                vMessage.setBackgroundColor(messageBackgroundColor);
            }
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
}
