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

package tool.compet.core.view.animation;

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
