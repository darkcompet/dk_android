/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
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
import tool.compet.appbundle.architecture.DkDialog;

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
