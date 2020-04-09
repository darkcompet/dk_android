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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import tool.compet.appbundle.R;
import tool.compet.appbundle.arch.DkDialog;

public abstract class DkPreferenceBottomSheetDialog extends DkDialog {
	protected abstract Fragment newPreferenceFragment();

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new BottomSheetDialog(context, getTheme());
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup vg, @Nullable Bundle savedInsState) {
		return inflater.inflate(R.layout.dk_frag_container, vg, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// add preference fragment as child
		if (savedInstanceState == null) {
			getChildFragmentManager()
				.beginTransaction()
				.add(R.id.dk_container, newPreferenceFragment())
				.commit();
		}
	}
}
