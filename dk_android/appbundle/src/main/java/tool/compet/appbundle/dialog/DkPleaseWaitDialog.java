/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tool.compet.appbundle.R;
import tool.compet.appbundle.architecture.DkDialog;
import tool.compet.core.BuildConfig;
import tool.compet.core.log.DkLogs;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public final class DkPleaseWaitDialog extends DkDialog {
    private int msgRes = -1;
    private CharSequence msg;
    private int filterColor = Color.WHITE;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onCreateView");
        }

        View layout = View.inflate(context, R.layout.dk_dialog_please_wait, null);

        ProgressBar pbWaiting = layout.findViewById(R.id.pbLoading);
        TextView tvMessage = layout.findViewById(R.id.tvMessage);

        if (msgRes > 0) {
            tvMessage.setText(msgRes);
        }
        if (msg != null) {
            tvMessage.setText(msg);
        }
        if (filterColor != 0) {
            pbWaiting.getIndeterminateDrawable().setColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
        }

        return layout;
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

    public DkPleaseWaitDialog setMessage(int msgRes) {
        this.msgRes = msgRes;
        return this;
    }

    public DkPleaseWaitDialog setMessage(CharSequence msg) {
        this.msg = msg;
        return this;
    }

    public DkPleaseWaitDialog setProgressColorFilter(int color) {
        this.filterColor = color;
        return this;
    }
}
