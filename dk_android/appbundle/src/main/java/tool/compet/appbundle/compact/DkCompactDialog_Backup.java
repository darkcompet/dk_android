/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.compact;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import tool.compet.appbundle.DkActivity;
import tool.compet.appbundle.DkDialogFragment;
import tool.compet.appbundle.DkFragment;
import tool.compet.appbundle.navigator.DkFragmentNavigator;
import tool.compet.appbundle.navigator.DkNavigatorOwner;
import tool.compet.core.DkRunner2;
import tool.compet.core.view.DkAnimationConfiguration;
import tool.compet.core.view.DkInterpolatorProvider;

/**
 * This is standard dialog and provides some below features:
 * - [Optional] Navigator (back, next fragment)
 * - [Optional] ViewModel (overcome configuration-changes)
 * - [Optional] Scoped topic (for communication between host and other fragments)
 *
 * In theory, this does not provide ViewLogic design pattern since we consider
 * a dialog as a view of its parent (activity or fragment).
 */
@SuppressWarnings("unchecked")
public abstract class DkCompactDialog_Backup<D>
	extends DkCompactFragment
	implements DkDialogFragment, DkFragmentNavigator.Callback, DkNavigatorOwner {

	// onCreate() -> onCreateDialog() -> onCreateView()

	// onViewCreated() -> onViewStateRestored() -> onStart()
	
	/**
	 * This is time that window is displayed to user. We can get real size of window at this time.
	 */
//	@Override
//	public void onStart() {
//		if (BuildConfig.DEBUG) {
//			DkLogs.info(this, "onStart");
//		}
//		super.onStart();
//
//		// At this time, window is displayed, so we can set size of the dialog
//		Dialog dialog = getDialog();
//
//		if (dialog != null) {
//			Window window = dialog.getWindow();
//
//			if (window != null) {
//				window.setLayout(MATCH_PARENT, MATCH_PARENT);
//				window.setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
//
//				if (requestInputMethod()) {
//					window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//				}
//			}
//		}
//	}


	// onSaveInstanceState() -> onDestroy()

	public static final int ANIM_ZOOM_IN = 1;
	public static final int ANIM_SWIPE_DOWN = 2;
	private static Interpolator animZoomInInterpolator;
	private static Interpolator animSwipeDownInterpolator;

	private ValueAnimator animator;
	private boolean enableShowAnim = true; // whether has animation when show dialog
	private int showAnimType = ANIM_ZOOM_IN;
	private boolean enableDismissAnim; // whether has animation when dismiss dialog
	private int dismissAnimType = -1;
	private Interpolator animInterpolator;
	private DkRunner2<ValueAnimator, View> animUpdater;

	private boolean cancelable;

	private Interpolator getAnimationInterpolator() {
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
		return animInterpolator;
	}

	private DkRunner2<ValueAnimator, View> getAnimationUpdater() {
		if (animUpdater == null) {
			switch (showAnimType) {
				case ANIM_ZOOM_IN: {
					animUpdater = (va, view) -> {
						if (view != null) {
							float sf = va.getAnimatedFraction();
							view.setScaleX(sf);
							view.setScaleY(sf);
						}
					};
					break;
				}
				case ANIM_SWIPE_DOWN: {
					animUpdater = (va, view) -> {
						if (view != null) {
							view.setY((va.getAnimatedFraction() - 1) * view.getHeight() / 2);
						}
					};
					break;
				}
				default: {
					throw new RuntimeException("Invalid animType");
				}
			}
		}
		return animUpdater;
	}

	// region Protected (overridable)

	/**
	 * Override this to request this should show keyboard for input when dialog displayed.
	 */
	protected boolean requestInputMethod() {
		return false;
	}

	/**
	 * Override this to hear event when dialog is shown.
	 */
	protected void onDialogShown(DialogInterface dialog) {
		if (enableShowAnim) {
			animUpdater = getAnimationUpdater();
			animInterpolator = getAnimationInterpolator();

			animator = ValueAnimator.ofFloat(0f, 1f);
			animator.setDuration(DkAnimationConfiguration.ANIM_LARGE_EXPAND_DURATION);
			animator.setInterpolator(animInterpolator);
			animator.addUpdateListener(anim -> {
				animUpdater.run(anim, layout);
			});
			animator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					onShowAnimationEnd(dialog);
				}
			});
			animator.start();
		}
		else {
			onShowAnimationEnd(dialog);
		}
	}

	/**
	 * Override this to hear event when dialog is dismissed.
	 */
	protected void onDialogDismised(DialogInterface dialog) {
		if (enableDismissAnim) {
			if (animator != null) {
				animUpdater = getAnimationUpdater();
				animInterpolator = getAnimationInterpolator();

				animator.removeAllListeners();
				animator.setDuration(DkAnimationConfiguration.ANIM_LARGE_COLLAPSE_DURATION);
				animator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						onDismissAnimationEnd(dialog);
						super.onAnimationEnd(animation);
					}
				});
				animator.reverse();
			}
		}
		else {
			onDismissAnimationEnd(dialog);
		}
	}

	/**
	 * End event of shown-animation for dialog.
	 */
	protected void onShowAnimationEnd(DialogInterface dialog) {
	}

	/**
	 * End event of dismissed-animation for dialog.
	 */
	protected void onDismissAnimationEnd(DialogInterface dialog) {
	}

	// endregion Protected (overridable)

	// region Get/Set

	public D setCancellable(boolean cancelable) {
		this.cancelable = cancelable;
		return (D) this;
	}

	// endregion Get/Set

	// region Private

	private void notifyParentInactive() {
		// Notify inactive for parent fragment
		Fragment parent = getParentFragment();

		// when null, parent is activity
		if (parent == null) {
			FragmentActivity host = getActivity();
			if (host instanceof DkActivity) {
				((DkActivity) host).onInactive(false);
			}
		}
		// otherwise parent is fragment
		else if (parent instanceof DkFragment) {
			((DkFragment) parent).onInactive(false);
		}
	}

	// endregion Private
}
