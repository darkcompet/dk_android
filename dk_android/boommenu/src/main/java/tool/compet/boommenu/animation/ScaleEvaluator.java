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

package tool.compet.boommenu.animation;

import android.view.animation.Interpolator;

import tool.compet.core.view.animation.interpolator.DkInterpolatorProvider;

public class ScaleEvaluator {
	private final float startValue;
	private final float endValue;
	private final Interpolator scaleInterpolator;

	public ScaleEvaluator(float startValue, float endValue) {
		this.startValue = startValue;
		this.endValue = endValue;
		this.scaleInterpolator = DkInterpolatorProvider.newCubicOut(true);
	}

	public float getAnimatedValue(float fraction) {
		return startValue + scaleInterpolator.getInterpolation(fraction) * (endValue - startValue);
	}
}
