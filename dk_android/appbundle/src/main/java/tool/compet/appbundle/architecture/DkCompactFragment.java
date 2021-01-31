/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This extends `DkSimpleFragment`, and provides:
 * - ViewLogic (like Controller for View)
 */
public abstract class DkCompactFragment<VL extends DkCompactViewLogic> extends DkSimpleFragment implements DkCompactView {
    // To instantiate ViewLogic, subclass should provide generic type of ViewLogic when extends the class
    @MyInjectViewLogic
    protected VL viewLogic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Must run after #super.onCreate()
        viewLogic = new MyCompactInjector(this).inject();
        if (viewLogic != null) {
            viewLogic.onCreate(host, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewLogic != null) {
            viewLogic.onViewCreated(host, savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (viewLogic != null) {
            viewLogic.onActivityCreated(host, savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (viewLogic != null) {
            viewLogic.onStart(host);
        }
    }

    @Override
    public void onActive(boolean isResume) {
        super.onActive(isResume);
        if (viewLogic != null) {
            viewLogic.onActive(host, isResume);
        }
    }

    @Override
    public void onInactive(boolean isPause) {
        super.onInactive(isPause);
        if (viewLogic != null) {
            viewLogic.onInactive(host, isPause);
        }
    }

    @Override
    public void onStop() {
        if (viewLogic != null) {
            viewLogic.onStop(host);
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (viewLogic != null) {
            viewLogic.onDestroy(host);
            viewLogic = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (viewLogic != null) {
            viewLogic.onActivityResult(host, requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (viewLogic != null) {
            viewLogic.onRequestPermissionsResult(host, requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLowMemory() {
        if (viewLogic != null) {
            viewLogic.onLowMemory(host);
        }
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (viewLogic != null) {
            viewLogic.onSaveInstanceState(host, outState);
        }
        super.onSaveInstanceState(outState);
    }
}
