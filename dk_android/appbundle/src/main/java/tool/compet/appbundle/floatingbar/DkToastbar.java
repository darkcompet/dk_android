/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.floatingbar;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tool.compet.appbundle.R;
import tool.compet.core.DkRunner;
import tool.compet.core.DkConfig;
import tool.compet.core.view.DkViews;

/**
 * Differ with Android Toast, this just show a floating text on the layout of current activity.
 * Note that, Android Toast has different way to show text, it shows text over display of device (not app).
 */
public class DkToastbar extends DkFloatingbar<DkToastbar> {
	public static final int DURATION_SHORT = 1500;
	public static final int DURATION_NORMAL = 2000;
	public static final int DURATION_LONG = 3500;

	// We handle all snackbars by a manager
	private static MyFloatingbarManager manager;

	private final TextView tvMessage;

	protected DkToastbar(Context context, ViewGroup parent, View bar) {
		super(context, parent, bar);

		duration = DURATION_NORMAL;

		tvMessage = bar.findViewById(R.id.dk_tv_message);

		// Make bar rounded
		DkViews.changeBackgroundColor(bar, "#80000000", 16, DkConfig.density());
	}

	public static DkToastbar newIns(View view) {
		ViewGroup parent = MyFloatingbarHelper.findSuperFrameLayout(view);
		if (parent == null) {
			throw new RuntimeException("No suitable parent was found");
		}
		// Prepare required params for the constructor
		Context context = parent.getContext();
		View bar = LayoutInflater.from(context).inflate(R.layout.dk_toastbar, parent, false);

		return new DkToastbar(context, parent, bar);
	}

	public static DkToastbar newIns(Activity activity) {
		return newIns(activity.findViewById(android.R.id.content));
	}

	@Override
	protected MyFloatingbarManager manager() {
		return manager != null ? manager : (manager = new MyFloatingbarManager());
	}

	@Override
	protected ValueAnimator prepareInAnimation() {
		int height = bar.getHeight();
		bar.setTranslationY(height);

		ValueAnimator va = new ValueAnimator();
		va.setFloatValues(0.0f, 1.0f);
		va.setDuration(200);
		va.setInterpolator(fastOutSlowIn);

		return va;
	}

	@Override
	protected ValueAnimator prepareOutAnimation() {
		ValueAnimator va = new ValueAnimator();
		va.setFloatValues(1.0f, 0.0f);
		va.setDuration(200);
		va.setInterpolator(fastOutSlowIn);

		return va;
	}

	@Override
	protected void onAnimationUpdate(ValueAnimator animation) {
		float alpha = (float) animation.getAnimatedValue();
		bar.setAlpha(alpha);
	}

	// region Get/Set

	public DkToastbar message(int msgRes) {
		tvMessage.setText(msgRes);
		return this;
	}

	public DkToastbar message(CharSequence msg) {
		tvMessage.setText(msg);
		return this;
	}

	public DkToastbar duration(long duration) {
		this.duration = duration;
		return this;
	}

	public DkToastbar setOnShownCallback(DkRunner onShownCallback) {
		this.onShownCallback = onShownCallback;
		return this;
	}

	public DkToastbar setOnDismissCallback(DkRunner onDismissCallback) {
		this.onDismissCallback = onDismissCallback;
		return this;
	}

	// endregion Get/Set
}
