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

package tool.compet.appbundle.arch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import tool.compet.appbundle.arch.navigator.DkFragmentNavigator;
import tool.compet.appbundle.eventbus.DkEventBus;
import tool.compet.core.util.DkLogs;
import tool.compet.core.util.Dks;

import static tool.compet.appbundle.BuildConfig.DEBUG;

/**
 * Subclass must declare annotation #DkBindXml on top.
 */
public abstract class DkPreferenceFragment extends PreferenceFragmentCompat implements DkFragment,
	SharedPreferences.OnSharedPreferenceChangeListener, DkFragmentNavigator.Callback {
	/**
	 * Specify id of preference resource for this fragment.
	 */
	protected abstract int preferenceResourceId();

	/**
	 * Be invoked when some value in preference changed.
	 */
	protected abstract void onPreferenceChanged(String key);

	// Read only fields.
	protected FragmentActivity host;
	protected Context context;
	protected ViewGroup layout;

	private DkFragmentNavigator navigator;

	// Android default preference
	private SharedPreferences androidDefaultPreference;

	@Override
	public DkFragmentNavigator getChildNavigator() {
		if (navigator == null) {
			int containerId = fragmentContainerId();

			if (containerId <= 0) {
				DkLogs.complain(this, "Invalid fragment container Id: " + containerId);
			}

			navigator = new DkFragmentNavigator(containerId, getChildFragmentManager(), this);
		}
		return navigator;
	}

	@Override
	public DkFragmentNavigator getParentNavigator() {
		Fragment parent = getParentFragment();
		DkFragmentNavigator owner = null;

		if (parent == null) {
			if (host instanceof DkSimpleActivity) {
				owner = ((DkSimpleActivity) host).getChildNavigator();
			}
		}
		else if (parent instanceof DkFragment) {
			owner = ((DkFragment) parent).getChildNavigator();
		}

		if (owner == null) {
			DkLogs.complain(this, "Must have a navigator own the fragment %s",
				getClass().getName());
		}

		return owner;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		if (DEBUG) {
			DkLogs.log(this, "onAttach (context)");
		}

		this.context = context;

		super.onAttach(context);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onAttach(@NonNull Activity activity) {
		if (DEBUG) {
			DkLogs.log(this, "onAttach (activity)");
		}

		host = (FragmentActivity) activity;

		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		if (DEBUG) {
			DkLogs.log(this, "onCreate");
		}

		super.onCreate(savedInstanceState);
		super.setRetainInstance(isRetainInstance());

		androidDefaultPreference = getPreferenceManager().getSharedPreferences();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (DEBUG) {
			DkLogs.log(this, "onCreateView");
		}

		// This is magic place, we can get preference view from super
		ViewGroup prefView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);

		int baseLayout = layoutResourceId();

		if (baseLayout > 0) {
			layout = (ViewGroup) inflater.inflate(baseLayout, container, false);
			layout.addView(prefView);
		}
		if (layout == null) {
			layout = prefView;
		}

		return layout;
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(preferenceResourceId());
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (DEBUG) {
			DkLogs.log(this, "onViewCreated");
		}

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onStart() {
		if (DEBUG) {
			DkLogs.log(this, "onStart");
		}

		androidDefaultPreference.registerOnSharedPreferenceChangeListener(this);
		DkEventBus.getIns().register(this);

		super.onStart();
	}

	@Override
	public void onResume() {
		onActive(true);
		super.onResume();
	}

	@Override
	public void onActive(boolean isResume) {
		if (DEBUG) {
			DkLogs.log(this, isResume ? "onResume" : "onFront");
		}
	}

	@Override
	public void onPause() {
		onInactive(true);
		super.onPause();
	}

	@Override
	public void onInactive(boolean isPause) {
		if (DEBUG) {
			DkLogs.log(this, isPause ? "onPause" : "onBehind");
		}
	}

	@Override
	public void onStop() {
		if (DEBUG) {
			DkLogs.log(this, "onStop");
		}

		androidDefaultPreference.unregisterOnSharedPreferenceChangeListener(this);
		DkEventBus.getIns().unregister(this);

		hideSoftKeyboard();

		super.onStop();
	}

	@Override
	public void onDestroyView() {
		if (DEBUG) {
			DkLogs.log(this, "onDestroyView");
		}

		super.onDestroyView();
	}

	@CallSuper
	@Override
	public void onDestroy() {
		if (DEBUG) {
			DkLogs.log(this, "onDestroy");
		}

		super.onDestroy();
	}

	@Override
	public void onDetach() {
		if (DEBUG) {
			DkLogs.log(this, "onDetach");
		}

		host = null;
		context = null;

		super.onDetach();
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		if (DEBUG) {
			DkLogs.log(this, "onViewStateRestored");
		}

		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (DEBUG) {
			DkLogs.log(this, "onSaveInstanceState");
		}

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference pref = getPreferenceManager().findPreference(key);

		if (pref instanceof ListPreference) {
			pref.setSummary(((ListPreference) pref).getEntry());
		}

		onPreferenceChanged(key);
	}

	@Override
	public Fragment getFragment() {
		return this;
	}

	/**
	 * @return true if this fragment handles this event itself, otherwise parent navigator should
	 *    handle it. Note that, if you wanna handle this event, override and customize this method.
	 */
	@Override
	public boolean onBackPressed() {
		return (navigator != null && navigator.childCount() == 0) && navigator.onBackPressed();
	}

	@Override
	public void dismiss() {
		getParentNavigator()
			.beginTransaction()
			.remove(this)
			.commit();
	}

	public void hideSoftKeyboard() {
		if (context != null) {
			Dks.hideSoftKeyboard(context, layout);
		}
	}
}
