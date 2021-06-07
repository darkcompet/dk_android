/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.compact;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.BuildConfig;
import tool.compet.appbundle.DkActivity;
import tool.compet.appbundle.DkApp;
import tool.compet.appbundle.DkDialogFragment;
import tool.compet.appbundle.DkFragment;
import tool.compet.appbundle.navigator.DkFragmentNavigator;
import tool.compet.appbundle.navigator.DkNavigatorOwner;
import tool.compet.appbundle.topic.DkTopicOwner;
import tool.compet.core.DkLogs;
import tool.compet.core.DkRunner2;
import tool.compet.core.DkUtils;
import tool.compet.core.view.DkAnimationConfiguration;
import tool.compet.core.view.DkInterpolatorProvider;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * This is standard dialog and provides some below features:
 * - [Optional] ViewLogic design pattern which can overcome configuration changes.
 */
@SuppressWarnings("unchecked")
public abstract class DkCompactDialog<D> extends AppCompatDialogFragment
	implements DkDialogFragment, DkNavigatorOwner {

	public static final String TAG = DkCompactDialog.class.getName();

	protected DkApp app;
	protected FragmentActivity host;
	protected Context context;
	protected View layout;

	@Override // from `DkNavigatorOwner`
	public DkFragmentNavigator getChildNavigator() {
		throw new RuntimeException("By default, dialog does not provide child navigator");
	}

	@Override // from `DkNavigatorOwner`
	public DkFragmentNavigator getParentNavigator() {
		Fragment parent = getParentFragment();
		DkFragmentNavigator owner = null;

		if (parent == null) {
			if (host instanceof DkNavigatorOwner) {
				owner = ((DkNavigatorOwner) host).getChildNavigator();
			}
		}
		else if (parent instanceof DkNavigatorOwner) {
			owner = ((DkNavigatorOwner) parent).getChildNavigator();
		}

		if (owner == null) {
			DkUtils.complainAt(this, "Must have a parent navigator own this fragment `%s`", getClass().getName());
		}

		return owner;
	}

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
		if (this.app == null && this.host != null) {
			this.app = (DkApp) this.host.getApplication();
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
		if (this.app == null) {
			this.app = (DkApp) activity.getApplication();
		}

		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreate");
		}
//		super.setRetainInstance(isRetainInstance());
		super.onCreate(savedInstanceState);
	}

	// onCreate() -> onCreateDialog() -> onCreateView()
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreateDialog");
		}
		Dialog dialog = super.onCreateDialog(savedInstanceState);

//		dialog.setOnShowListener(this::onDialogShown);
//		dialog.setOnDismissListener(this::onDialogDismised);

		return dialog;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onCreateView");
		}
		int layoutId = layoutResourceId();
		if (layoutId > 0) {
			// Pass `false` to indicate don't attach this layout to parent
			return inflater.inflate(layoutId, container, false);
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
	
	/**
	 * This is time that window is displayed to user. We can get real size of window at this time.
	 */
	@Override
	public void onStart() {
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "onStart");
		}
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

		this.app = null;
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

	/**
	 * Open dialog via `DkFragmentNavigator` way.
	 */
	public boolean open(DkFragmentNavigator navigator) {
		return navigator.beginTransaction().add(this).commit();
	}

	/**
	 * Close dialog by tell parent remove this.
	 */
	@Override // from `DkFragment`
	public boolean close() {
		return getParentNavigator().beginTransaction().remove(this).commit();
	}

	/**
	 * Called when dialog was cancelled.
	 */
	@Override // from `DialogFragment`
	public void onCancel(@NonNull DialogInterface dialog) {
		super.onCancel(dialog);
	}

	/**
	 * Called when dialog was dismissed.
	 */
	@Override // from `DialogFragment`
	public void onDismiss(@NonNull DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	/**
	 * Show dialog via `DialogFragment` way.
	 *
	 * @param fm Fragment manager (normally this is of current fragment, activity...)
	 */
	public boolean show(FragmentManager fm) {
		throw new RuntimeException("For now do NOT use this");
	}

	@Override
	public void show(@NonNull FragmentManager fm, String tag) {
		throw new RuntimeException("For now do NOT use this");
	}

	private boolean showActual(@NonNull FragmentManager fm, String tag) {
		// Execute all pending transactions first
		try {
			notifyParentInactive();
			fm.executePendingTransactions();
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}

		// Show actual
		try {
			// perform transaction inside parent FM
			super.show(fm, tag);
			return true;
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}

		return false;
	}

	/**
	 * We override `dismiss()` to call own `DkFragment.close()`.
	 */
	@Override // from `DialogFragment`
	public void dismiss() {
		DkUtils.complainAt(this, "For now do NOT use this");
	}

	public Fragment instantiateFragment(Class<? extends Fragment> fragClass) {
		return getParentFragmentManager().getFragmentFactory().instantiate(context.getClassLoader(), fragClass.getName());
	}

	// region ViewModel

	// Get or Create new ViewModel instance which be owned by this Fragment.
	public <M extends ViewModel> M obtainOwnViewModel(String key, Class<M> modelType) {
		return new ViewModelProvider(this).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by Activity which hosts this Fragment.
	public <M extends ViewModel> M obtainHostViewModel(String key, Class<M> modelType) {
		return new ViewModelProvider(host).get(key, modelType);
	}

	// Get or Create new ViewModel instance which be owned by current app.
	public <M extends ViewModel> M obtainAppViewModel(String key, Class<M> modelType) {
		Application app = host.getApplication();

		if (app instanceof ViewModelStoreOwner) {
			return new ViewModelProvider((ViewModelStoreOwner) app).get(key, modelType);
		}

		throw new RuntimeException("App must be subclass of `ViewModelStoreOwner`");
	}

	// endregion ViewModel

	// region Scoped topic

	/**
	 * Obtain the topic owner at app scope.
	 * When all owners of the topic were destroyed, the topic and its material will be cleared.
	 */
	public DkTopicOwner joinAppTopic(String topicId) {
		return new DkTopicOwner(topicId, app).registerClient(this);
	}

	/**
	 * Obtain the topic owner at host scope.
	 * When all owners of the topic were destroyed, the topic and its material will be cleared.
	 */
	public DkTopicOwner joinHostTopic(String topicId) {
		return new DkTopicOwner(topicId, host).registerClient(this);
	}

	/**
	 * Obtain the topic owner at own scope.
	 * When all owners of the topic were destroyed, the topic and its material will be cleared.
	 */
	public DkTopicOwner joinOwnTopic(String topicId) {
		return new DkTopicOwner(topicId, this).registerClient(this);
	}

	/**
	 * Just obtain the topic owner at app scope.
	 */
	public DkTopicOwner viewAppTopic(String topicId) {
		return new DkTopicOwner(topicId, app);
	}

	/**
	 * Just obtain the topic owner at host scope.
	 */
	public DkTopicOwner viewHostTopic(String topicId) {
		return new DkTopicOwner(topicId, host);
	}

	/**
	 * Just obtain the topic owner at own scope.
	 */
	public DkTopicOwner viewOwnTopic(String topicId) {
		return new DkTopicOwner(topicId, this);
	}

	// endregion Scoped topic

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
		super.setCancelable(cancelable);
		return (D) this;
	}

	// endregion Get/Set

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
