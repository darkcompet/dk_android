/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.animation;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.view.View;

public class DkAnimators {
	public static ObjectAnimator createAnimator(View view, String property, long startDelay, long duration,
		TimeInterpolator interpolator, AnimatorListenerAdapter listener, Object... values) {

		ObjectAnimator animator = new ObjectAnimator();
		animator.setStartDelay(startDelay);
		animator.setDuration(duration);
		animator.setTarget(view);
		animator.setPropertyName(property);

		animator.setObjectValues(values);
		if (interpolator != null) {
			animator.setInterpolator(interpolator);
		}

		if (listener != null) {
			animator.addListener(listener);
		}

		return animator;
	}

	public static ObjectAnimator createAnimator(View view, String property, long startDelay, long duration,
		TypeEvaluator evaluator, AnimatorListenerAdapter listener, Object... values) {

		ObjectAnimator animator = new ObjectAnimator();
		animator.setStartDelay(startDelay);
		animator.setDuration(duration);
		animator.setTarget(view);
		animator.setPropertyName(property);

		animator.setObjectValues(values);
		if (evaluator != null) {
			animator.setEvaluator(evaluator); // it needs values setup first
		}

		if (listener != null) {
			animator.addListener(listener);
		}

		return animator;
	}
}
