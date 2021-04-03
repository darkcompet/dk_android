/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import tool.compet.core.BuildConfig;
import tool.compet.core.DkLogs;

/**
 * Dialog which extends DialogFragment to live with lifecycle of the app.
 */
public abstract class DkDialog<T extends DkDialog> extends AppCompatDialogFragment implements DkFragmentInf {
	public static final String TAG = DkDialog.class.getName();

	protected FragmentActivity host; // host at `onAttach()`
	protected Context context; // context at `onAttach()`
	protected View layout; // view at `onViewCreate()`

	@Override
	public void onAttach(@NonNull Context context) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onAttach (context)");
		}
		if (this.context == null) {
			this.context = context;
		}
		if (this.host == null) {
			this.host = getActivity();
		}

		super.onAttach(context);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onAttach(@NonNull Activity activity) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onAttach (activity)");
		}
		if (this.context == null) {
			this.context = activity;
		}
		if (this.host == null) {
			host = (FragmentActivity) activity;
		}

		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreate");
		}
		super.setRetainInstance(isRetainInstance());
		super.onCreate(savedInstanceState);
	}

	// onCreate() -> onCreateDialog() -> onCreateView()
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreateDialog");
		}
		return super.onCreateDialog(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreateView");
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	// onCreateView() -> onActivityCreated() -> onViewStateRestored()
	// By default, dialog will set view which be created at onCreateView() at this time
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onActivityCreated");
		}
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onViewStateRestored");
		}
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onViewCreated");
		}
		this.layout = view;
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onStart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStart");
		}
		super.onStart();
	}

	@Override
	public void onResume() {
		onActive(true);
		super.onResume();
	}

	@Override
	public void onActive(boolean isResume) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isResume ? "onResume" : "onFront");
		}
	}

	@Override
	public void onPause() {
		onInactive(true);
		super.onPause();
	}

	@Override
	public void onInactive(boolean isPause) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, isPause ? "onPause" : "onBehind");
		}
	}

	@Override
	public void onStop() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStop");
		}
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDestroyView");
		}
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDestroy");
		}
		super.onDestroy();
	}

	@CallSuper
	@Override
	public void onDetach() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onDetach");
		}

		this.host = null;
		this.context = null;
		this.layout = null;

		super.onDetach();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onActivityResult");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onActivityResult");
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public void onLowMemory() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onLowMemory");
		}
		super.onLowMemory();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onSaveInstanceState");
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public Fragment getFragment() {
		return this;
	}
}
