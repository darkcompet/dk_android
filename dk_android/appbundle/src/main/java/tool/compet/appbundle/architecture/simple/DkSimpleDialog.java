/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.simple;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.BuildConfig;
import tool.compet.appbundle.architecture.DkActivityInf;
import tool.compet.appbundle.architecture.DkDialog;
import tool.compet.appbundle.architecture.DkFragmentInf;
import tool.compet.core.view.animation.DkAnimationConfiguration;
import tool.compet.core.view.animation.DkInterpolatorProvider;
import tool.compet.core.DkLogs;
import tool.compet.core.DkRunner2;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * This provides some below features:
 * - Basic lifecycle (active, inactive...)
 * - ViewModel (overcome configuration-change)
 * - Scoped topic (pass data between/under fragments, activities, app)
 * - Animation for enter, exit
 */
public abstract class DkSimpleDialog extends DkDialog {
	public static final String TAG = DkSimpleDialog.class.getName();

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		dialog.setOnShowListener(this::onDialogShown);
		dialog.setOnDismissListener(this::onDialogDismised);

		return dialog;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		int layoutResourceId = layoutResourceId();
		if (layoutResourceId > 0) {
			if (BuildConfig.DEBUG) {
				DkLogs.info(this, "onCreateView");
			}
			return inflater.inflate(layoutResourceId, container);
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * This is time that window is displayed to user. We can get real size of window at this time.
	 */
	@Override
	public void onStart() {
		super.onStart();

		// At this time, window is displayed, so we can set size of the dialog
		Dialog dialog = getDialog();

		if (dialog != null) {
			Window window = dialog.getWindow();

			if (window != null) {
				window.setLayout(MATCH_PARENT, MATCH_PARENT);
				window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

				if (requestInputMethod()) {
					window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		}
	}

	/**
	 * After call dismiss(), this method will be called soon.
	 * It is useful to listen dismiss event of the dialog.
	 */
	@Override
	public void onDismiss(@NonNull DialogInterface dialog) {
		// Notify parent fragment goto inactive state
		Fragment parent = getParentFragment();

		// when null, parent is activity
		if (parent == null) {
			FragmentActivity activity = getActivity();
			if (activity instanceof DkActivityInf) {
				((DkActivityInf) activity).onActive(false);
			}
		}
		// otherwise parent is fragment
		else if (parent instanceof DkFragmentInf) {
			((DkFragmentInf) parent).onActive(false);
		}

		super.onDismiss(dialog);
	}

	/**
	 * Show dialog by transaction adding it via fragment manager.
	 *
	 * @param fm Fragment manager (normally this is of current fragment, activity...)
	 */
	public void show(FragmentManager fm) {
		this.show(fm, TAG);
	}

	@Override
	public void show(@NonNull FragmentManager fm, String tag) {
		// Execute all pending transactions first
		try {
			notifyParentInactive();
			fm.executePendingTransactions();
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		// Show actual
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
		close();
	}

	/**
	 * Close (dismiss) the dialog.
	 */
	@Override
	public boolean close() {
		boolean ok = false;
		// Execute all pending transactions first
		try {
			getParentFragmentManager().executePendingTransactions();
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		// Perform dismiss actual
		finally {
			try {
				super.dismiss();
				ok = true;
			}
			catch (Exception e) {
				DkLogs.error(this, e);
			}
		}
		return ok;
	}

	//
	// ViewModel region
	//

	// Get or Create new ViewModel instance which be owned by this Fragment.
	public <M extends ViewModel> M obtainOwnViewModel(String key, Class<M> modelType) {
		return new ViewModelProvider(this).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by Activity which this contains this Fragment.
	public <M extends ViewModel> M obtainHostViewModel(String key, Class<M> modelType) {
		return new ViewModelProvider(host).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by current app.
	public <M extends ViewModel> M obtainAppViewModel(String key, Class<M> modelType) {
		Application app = host.getApplication();

		if (app instanceof ViewModelStoreOwner) {
			return new ViewModelProvider((ViewModelStoreOwner) app).get(key, modelType);
		}

		throw new RuntimeException("App must be subclass of ViewModelStoreOwner");
	}

	//
	// Scoped topic region
	//

	// Obtain and Listen a topic in hostOwner
	public TheFragmentTopicRegistry joinTopic(String topicId) {
		return new TheFragmentTopicRegistry(topicId, host, this);
	}

	// Leave from a topic, and remove topic from hostOwner if no client listening
	public void leaveTopic(String topicId) {
		new TheFragmentTopicRegistry(topicId, host, this).unregisterClient();
	}

	//
	// Private region
	//

	private void notifyParentInactive() {
		// Notify inactive for parent fragment
		Fragment parent = getParentFragment();

		// when null, parent is activity
		if (parent == null) {
			FragmentActivity host = getActivity();
			if (host instanceof DkActivityInf) {
				((DkActivityInf) host).onInactive(false);
			}
		}
		// otherwise parent is fragment
		else if (parent instanceof DkFragmentInf) {
			((DkFragmentInf) parent).onInactive(false);
		}
	}

	//
	// Protected (overridable) region
	//

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
			animator.setDuration(DkAnimationConfiguration.ANIM_LARGE_EXPAND);
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
				animator.setDuration(DkAnimationConfiguration.ANIM_LARGE_COLLAPSE);
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
}
