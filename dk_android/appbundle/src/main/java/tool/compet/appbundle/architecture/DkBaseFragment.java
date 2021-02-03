/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import tool.compet.appbundle.BuildConfig;
import tool.compet.appbundle.binder.DkBinder;
import tool.compet.core.log.DkLogs;

/**
 * All fragments should be subclass of this to work with support of Dk library as possible.
 * This provides below some basic features:
 * - Obtain host (current activity), context instance
 * - Binding layout with DkBinder
 * - Basic lifecycle methods
 * - Implements some DkFragment methods
 */
public abstract class DkBaseFragment extends Fragment implements DkFragment {
    protected FragmentActivity host;
    protected Context context;
    protected ViewGroup layout;

    @Override
    public void onAttach(@NonNull Context context) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onAttach (context)");
        }
        if (this.host == null) {
            this.host = getActivity();
        }
        this.context = context;

        super.onAttach(context);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(@NonNull Activity activity) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onAttach (activity)");
        }
        if (this.context == null) {
            this.context = getContext();
        }
        this.host = (FragmentActivity) activity;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onCreateView");
        }

        int layoutId = layoutResourceId();
        if (layoutId <= 0) {
            DkLogs.complain(this, "Invalid layoutId: %d", layoutId);
        }

        layout = (ViewGroup) inflater.inflate(layoutResourceId(), container, false);
        DkBinder.bindViews(this, layout);

        DkLogs.debug(this, "create home view, layout: " + layout);

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onViewCreated");
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onActivityCreated");
        }
        super.onActivityCreated(savedInstanceState);
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
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onViewStateRestored");
        }
        super.onViewStateRestored(savedInstanceState);
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
