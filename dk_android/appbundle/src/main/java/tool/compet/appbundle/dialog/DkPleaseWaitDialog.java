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
import tool.compet.appbundle.arch.DkDialog;
import tool.compet.core.BuildConfig;
import tool.compet.core.util.DkLogs;

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
