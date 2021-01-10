/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tool.compet.appbundle.R;
import tool.compet.appbundle.architecture.DkDialog;
import tool.compet.appbundle.constant.ColorConst;
import tool.compet.core.BuildConfig;
import tool.compet.core.config.DkConfig;
import tool.compet.core.type.DkCallback;
import tool.compet.core.log.DkLogs;
import tool.compet.core.view.DkTextViews;
import tool.compet.core.view.DkViews;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * In default, title, message and buttons widgets has gone-visibility.
 * And all buttons are auto-dismiss for click action.
 */
@SuppressWarnings("unchecked")
public class DkConfirmDialog extends DkDialog<DkConfirmDialog> implements View.OnClickListener {
    protected View layout;

    // Header
    protected int titleRes = View.NO_ID;
    protected int subTitleRes = View.NO_ID;
    protected int headerBackgroundColor = -1;

    // Content
    protected float widthWeight = 4f;
    protected float heightWeight = 3f;

    // Message
    protected int msgRes = View.NO_ID;
    protected String msg;
    protected int msgBackgroundColor = -1;

    // Footer
    protected int cancelRes = View.NO_ID;
    protected int resetRes = View.NO_ID;
    protected int okRes = View.NO_ID;
    protected DkCallback<DkConfirmDialog> cancelCallback;
    protected DkCallback<DkConfirmDialog> resetCallback;
    protected DkCallback<DkConfirmDialog> okCallback;

    protected boolean isDismissOnClickButton = true;
    protected Runnable onDismissCallback;

    protected boolean isFullScreen;

    public static DkConfirmDialog newIns() {
        return new DkConfirmDialog();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle sis) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onCreateView");
        }
        super.onCreateView(inflater, container, sis);

        // layout = innner-padding + bounds (= title + content + buttons)
        final View layout = inflater.inflate(R.layout.dk_dialog_confirm, container, false);
        final ViewGroup vgBounds = layout.findViewById(R.id.vgBounds);
        final ViewGroup vgContent = layout.findViewById(R.id.vgContent);

        layout.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE: {
                    if (isDismissOnTouchOutside && !DkViews.isInsideView(event, vgBounds)) {
                        dismiss();
                    }
                    break;
                }
            }
            return false;
        });

        if (headerBackgroundColor >= 0) {
            vgBounds.findViewById(R.id.vgHeader).setBackgroundColor(headerBackgroundColor);
        }

        if (titleRes != View.NO_ID) {
            TextView tvTitle = vgBounds.findViewById(R.id.tvTitle);
            tvTitle.setVisibility(View.VISIBLE);
            DkTextViews.scaleTextSize(tvTitle, 1.3f);
            tvTitle.setText(titleRes);
        }

        if (subTitleRes != View.NO_ID) {
            TextView tvSubTitle = vgBounds.findViewById(R.id.tvSubTitle);
            tvSubTitle.setVisibility(View.VISIBLE);
            DkTextViews.scaleTextSize(tvSubTitle, 0.85f);
            tvSubTitle.setText(subTitleRes);
        }

        if (msg == null && msgRes != View.NO_ID) {
            msg = getString(msgRes);
        }

        if (msg != null) {
            TextView tvMsg = vgBounds.findViewById(R.id.tvMsg);
            tvMsg.setVisibility(View.VISIBLE);
            DkTextViews.scaleTextSize(tvMsg, 1.2f);
            tvMsg.setText(msg);

            if (msgBackgroundColor >= 0) {
                tvMsg.setBackgroundColor(msgBackgroundColor);
            }
        }
        else if (this.layout != null) {
            vgContent.removeAllViews();
            vgContent.addView(this.layout);
        }

        if (cancelRes != View.NO_ID) {
            TextView btnCancel = vgBounds.findViewById(R.id.btnCancel);
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(this);
            btnCancel.setText(cancelRes);
        }

        if (resetRes != View.NO_ID) {
            TextView btnReset = vgBounds.findViewById(R.id.btnReset);
            btnReset.setVisibility(View.VISIBLE);
            btnReset.setOnClickListener(this);
            btnReset.setText(resetRes);
        }

        if (okRes != View.NO_ID) {
            TextView btnOk = vgBounds.findViewById(R.id.btnOk);
            btnOk.setVisibility(View.VISIBLE);
            btnOk.setOnClickListener(this);
            btnOk.setText(okRes);
        }

        ViewGroup.LayoutParams boundsLayoutParams = vgBounds.getLayoutParams();

        if (isFullScreen) {
            boundsLayoutParams.width = boundsLayoutParams.height = MATCH_PARENT;
        }
        else {
            int d = Math.min(DkConfig.device.displaySize[0], DkConfig.device.displaySize[1]);
            boundsLayoutParams.width = (d >> 3) + (d >> 2) + (d >> 1);
            boundsLayoutParams.height = (int) (boundsLayoutParams.width * heightWeight / widthWeight);
        }

        vgBounds.setLayoutParams(boundsLayoutParams);

        return (this.layout = layout);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnCancel) {
            if (cancelCallback != null) {
                cancelCallback.call(this);
            }
        }
        else if (id == R.id.btnReset) {
            if (resetCallback != null) {
                resetCallback.call(this);
            }
        }
        else if (id == R.id.btnOk) {
            if (okCallback != null) {
                okCallback.call(this);
            }
        }

        if (isDismissOnClickButton) {
            super.dismiss();
        }
    }

    @Override
    public void onStart() {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onStart");
        }
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            Window window = dialog.getWindow();

            if (window != null) {
                window.setLayout(MATCH_PARENT, MATCH_PARENT);
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onDismiss");
        }
        if (onDismissCallback != null) {
            onDismissCallback.run();
        }

        super.onDismiss(dialog);
    }

    public DkConfirmDialog setTitle(int strId) {
        titleRes = strId;
        return this;
    }

    public DkConfirmDialog setMessage(int strId) {
        msgRes = strId;
        return this;
    }

    public DkConfirmDialog setMessage(String msg) {
        this.msg = msg;
        return this;
    }

    public DkConfirmDialog setView(View view) {
        layout = view;
        return this;
    }

    public DkConfirmDialog setCancelButton(int strId, DkCallback<DkConfirmDialog> callback) {
        cancelRes = strId;
        cancelCallback = callback;
        return this;
    }

    public DkConfirmDialog setResetButton(int strId, DkCallback<DkConfirmDialog> callback) {
        resetRes = strId;
        resetCallback = callback;
        return this;
    }

    public DkConfirmDialog setOkButton(int strId, DkCallback<DkConfirmDialog> callback) {
        okRes = strId;
        okCallback = callback;
        return this;
    }

    public DkConfirmDialog setIsDismissOnTouchOutside(boolean isDismissOnTouchOutside) {
        this.isDismissOnTouchOutside = isDismissOnTouchOutside;
        return this;
    }

    public DkConfirmDialog setDismissOnClickButton(boolean dismissOnClickButton) {
        isDismissOnClickButton = dismissOnClickButton;
        return this;
    }

    public DkConfirmDialog setFullScreen() {
        isFullScreen = true;
        return this;
    }

    public DkConfirmDialog setDimensionWithRate(float widthWeight, float heightWeight) {
        this.widthWeight = widthWeight;
        this.heightWeight = heightWeight;
        return this;
    }

    public DkConfirmDialog asSuccess() {
        this.headerBackgroundColor = ColorConst.SUCCESS;
        return this;
    }

    public DkConfirmDialog asError() {
        this.headerBackgroundColor = ColorConst.ERROR;
        return this;
    }

    public DkConfirmDialog asWarning() {
        this.headerBackgroundColor = ColorConst.WARNING;
        return this;
    }

    public DkConfirmDialog asAsk() {
        this.headerBackgroundColor = ColorConst.ASK;
        return this;
    }

    public DkConfirmDialog asInfo() {
        this.headerBackgroundColor = ColorConst.INFO;
        return this;
    }

    public DkConfirmDialog asType(int color) {
        this.headerBackgroundColor = color;
        return this;
    }

    public View findView(int viewId) {
        return layout == null ? null : layout.findViewById(viewId);
    }
}
