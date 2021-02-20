/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

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

import tool.compet.appbundle.architecture.navigator.DkFragmentNavigator;
import tool.compet.appbundle.architecture.simple.DkSimpleActivity;
import tool.compet.appbundle.architecture.simple.DkSimpleFragment;
import tool.compet.core.log.DkLogs;
import tool.compet.core.util.DkUtils;

import static tool.compet.appbundle.BuildConfig.DEBUG;

/**
 * Subclass must declare annotation #DkBindXml on top.
 */
public abstract class DkPreferenceFragment extends PreferenceFragmentCompat
    implements DkFragmentInf, SharedPreferences.OnSharedPreferenceChangeListener, DkFragmentNavigator.Callback {
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

    public DkFragmentNavigator getParentNavigator() {
        Fragment parent = getParentFragment();
        DkFragmentNavigator owner = null;

        if (parent == null) {
            if (host instanceof DkSimpleActivity) {
                owner = ((DkSimpleActivity) host).getChildNavigator();
            }
        }
        else if (parent instanceof DkSimpleFragment) {
            owner = ((DkSimpleFragment) parent).getChildNavigator();
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
            DkLogs.info(this, "onAttach (context)");
        }

        this.context = context;

        super.onAttach(context);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(@NonNull Activity activity) {
        if (DEBUG) {
            DkLogs.info(this, "onAttach (activity)");
        }

        host = (FragmentActivity) activity;

        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (DEBUG) {
            DkLogs.info(this, "onCreate");
        }

        super.onCreate(savedInstanceState);
        super.setRetainInstance(isRetainInstance());

        androidDefaultPreference = getPreferenceManager().getSharedPreferences();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (DEBUG) {
            DkLogs.info(this, "onCreateView");
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
            DkLogs.info(this, "onViewCreated");
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        if (DEBUG) {
            DkLogs.info(this, "onStart");
        }

        androidDefaultPreference.registerOnSharedPreferenceChangeListener(this);

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
        if (DEBUG) {
            DkLogs.info(this, isPause ? "onPause" : "onBehind");
        }
    }

    @Override
    public void onStop() {
        if (DEBUG) {
            DkLogs.info(this, "onStop");
        }

        androidDefaultPreference.unregisterOnSharedPreferenceChangeListener(this);

        hideSoftKeyboard();

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) {
            DkLogs.info(this, "onDestroyView");
        }

        super.onDestroyView();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        if (DEBUG) {
            DkLogs.info(this, "onDestroy");
        }

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (DEBUG) {
            DkLogs.info(this, "onDetach");
        }

        host = null;
        context = null;

        super.onDetach();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (DEBUG) {
            DkLogs.info(this, "onViewStateRestored");
        }

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (DEBUG) {
            DkLogs.info(this, "onSaveInstanceState");
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
     * Called when user pressed to physical back button, this is normally passed from current activity.
     * When this view got an event, this send signal to children first, if no child was found, or
     * child has handled the event successfully, then this will call `dismiss()` on it to finish itself.
     *
     * @return true if this view has dismissed successfully, otherwise false.
     */
    @Override
    public boolean onBackPressed() {
        if (navigator == null || navigator.handleOnBackPressed()) {
            return this.dismiss();
        }
        return false;
    }

    /**
     * Finish this view by tell parent remove this from navigator.
     */
    @Override
    public boolean dismiss() {
        return getParentNavigator().beginTransaction().remove(this).commit();
    }

    public void hideSoftKeyboard() {
        if (context != null) {
            DkUtils.hideSoftKeyboard(context, layout);
        }
    }
}
