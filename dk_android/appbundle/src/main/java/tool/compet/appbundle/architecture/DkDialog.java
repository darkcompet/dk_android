/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import tool.compet.core.BuildConfig;
import tool.compet.core.type.DkBiCallback;
import tool.compet.core.type.DkCallback;
import tool.compet.core.log.DkLogs;
import tool.compet.core.animation.DkAnimationConfiguration;
import tool.compet.core.animation.DkInterpolatorProvider;

@SuppressWarnings("unchecked")
public abstract class DkDialog<T extends DkDialog> extends AppCompatDialogFragment {
    public static final String TAG = DkDialog.class.getName();

    public static final int ANIM_ZOOM_IN = 1;
    public static final int ANIM_SWIPE_DOWN = 2;

    protected FragmentActivity host;
    protected Context context;

    protected boolean isDismissOnTouchOutside = true;
    protected boolean isCancellable = true; // like when back button pressed...

    private static Interpolator animZoomInInterpolator;
    private static Interpolator animSwipeDownInterpolator;

    // Animations
    private ValueAnimator animator;
    private boolean hasShowAnim = true;
    private int showAnimType = ANIM_ZOOM_IN;
    private boolean hasDismissAnim;
    private int dismissAnimType = -1;
    private Interpolator animInterpolator;
    private DkBiCallback<ValueAnimator, View> animUpdater;
    private DkCallback<Dialog> onShowListener;
    private DkCallback<Dialog> onDismissListener;

    @Override
    public void onAttach(@NonNull Context context) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onAttach");
        }

        notifyParentInactive();

        this.context = context;
        super.onAttach(context);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(@NonNull Activity activity) {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onAttach");
        }
        if (context == null) {
            context = activity;
        }
        host = (FragmentActivity) activity;

        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Dialog dialog = getDialog();

        if (dialog != null) {
            dialog.setCancelable(isCancellable);

            dialog.setOnShowListener(dlg -> {
                if (hasShowAnim) {
                    if (onShowListener != null) {
                        onShowListener.call(dialog);
                    }
                }
                else {
                    animator = ValueAnimator.ofFloat(0f, 1f);
                    requireAnimationUpdater();
                    requireAnimationInterpolator();

                    animator.setDuration(DkAnimationConfiguration.ANIM_LARGE_EXPAND);
                    animator.setInterpolator(animInterpolator);
                    animator.addUpdateListener(anim -> {
                        animUpdater.call(anim, view);
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (onShowListener != null) {
                                onShowListener.call(dialog);
                            }
                        }
                    });
                    animator.start();
                }
            });

            dialog.setOnDismissListener(dlg -> {
                if (hasDismissAnim) {
                    if (animator == null) {
                        return;
                    }
                    requireAnimationUpdater();
                    requireAnimationInterpolator();
                    animator.removeAllListeners();
                    animator.setDuration(DkAnimationConfiguration.ANIM_LARGE_COLLAPSE);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (onDismissListener != null) {
                                onDismissListener.call(dialog);
                            }
                            super.onAnimationEnd(animation);
                        }
                    });
                    animator.reverse();
                }
                else {
                    if (onDismissListener != null) {
                        onDismissListener.call(dialog);
                    }
                }
            });
        }
    }

    @CallSuper
    @Override
    public void onDetach() {
        if (BuildConfig.DEBUG) {
            DkLogs.info(this, "onDetach");
        }

        host = null;
        context = null;

        super.onDetach();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        // Notify inactive for parent fragment
        Fragment parent = getParentFragment();

        // when null, parent is activity
        if (parent == null) {
            FragmentActivity activity = getActivity();

            if (activity instanceof DkSimpleActivity) {
                ((DkSimpleActivity) activity).onActive(false);
            }
        }
        // otherwise parent is fragment
        else if (parent instanceof DkFragment) {
            ((DkFragment) parent).onActive(false);
        }

        super.onDismiss(dialog);
    }

    public void show(FragmentManager fm) {
        show(fm, TAG);
    }

    @Override
    public void show(@NonNull FragmentManager fm, String tag) {
        // Execute all pending transactions first
        try {
            fm.executePendingTransactions();
        }
        catch (Exception e) {
            DkLogs.error(this, e);
        }
        // Perform show actual
        finally {
            try {
                // perform transaction inside parent FM
                super.show(fm, tag);
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
        }
    }

    @Override
    public void dismiss() {
        // Execute all pending transactions first
        try {
            FragmentManager fm = getFragmentManager();

            if (fm != null) {
                fm.executePendingTransactions();
            }
        }
        catch (Exception e) {
            DkLogs.error(this, e);
        }
        // Perform dismiss actual
        finally {
            try {
                super.dismiss();
            }
            catch (Exception e) {
                DkLogs.error(this, e);
            }
        }
    }

    private void notifyParentInactive() {
        // Notify inactive for parent fragment
        Fragment parent = getParentFragment();

        // when null, parent is activity
        if (parent == null) {
            FragmentActivity host = getActivity();

            if (host instanceof DkSimpleActivity) {
                ((DkSimpleActivity) host).onInactive(false);
            }
        }
        // otherwise parent is fragment
        else if (parent instanceof DkFragment) {
            ((DkFragment) parent).onInactive(false);
        }
    }

    private void requireAnimationInterpolator() {
        if (animInterpolator == null) {
            switch (showAnimType) {
                case ANIM_ZOOM_IN: {
                    if (animZoomInInterpolator == null) {
                        animZoomInInterpolator = PathInterpolatorCompat.create(
                            0.78f, 1.27f,
                            0.87f, 1.06f);
                    }
                    animInterpolator = animZoomInInterpolator;
                    break;
                }
                case ANIM_SWIPE_DOWN: {
                    if (animSwipeDownInterpolator == null) {
                        animSwipeDownInterpolator = DkInterpolatorProvider.newElasticOut(true);
                    }
                    animInterpolator = animSwipeDownInterpolator;
                    break;
                }
                default: {
                    throw new RuntimeException("Invalid animType");
                }
            }
        }
    }

    private void requireAnimationUpdater() {
        if (animUpdater == null) {
            switch (showAnimType) {
                case ANIM_ZOOM_IN: {
                    animUpdater = (va, view) -> {
                        float sf = va.getAnimatedFraction();
                        view.setScaleX(sf);
                        view.setScaleY(sf);
                    };
                    break;
                }
                case ANIM_SWIPE_DOWN: {
                    animUpdater = (va, view) -> {
                        view.setY((va.getAnimatedFraction() - 1) * view.getHeight() / 2);
                    };
                    break;
                }
                default: {
                    throw new RuntimeException("Invalid animType");
                }
            }
        }
    }

    public T setAnimationType(int animType) {
        this.showAnimType = animType;
        return (T) this;
    }

    public T setAnimation(Interpolator animInterpolator, DkBiCallback<ValueAnimator, View> animUpdateCb) {
        this.animInterpolator = animInterpolator;
        this.animUpdater = animUpdateCb;
        return (T) this;
    }

    public T setOnShowListener(DkCallback<Dialog> showListener) {
        this.onShowListener = showListener;
        return (T) this;
    }

    public T setOnDismissListener(DkCallback<Dialog> dismissListener) {
        this.onDismissListener = dismissListener;
        return (T) this;
    }

    public T setDismissOnTouchOutside(boolean dismissOnTouchOutside) {
        isDismissOnTouchOutside = dismissOnTouchOutside;
        return (T) this;
    }

    public T setCancellable(boolean cancellable) {
        isCancellable = cancellable;
        return (T) this;
    }

    public T setHasShowAnim(boolean hasShowAnim) {
        this.hasShowAnim = hasShowAnim;
        return (T) this;
    }
}
