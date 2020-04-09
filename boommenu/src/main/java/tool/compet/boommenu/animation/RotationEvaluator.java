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

import androidx.core.view.animation.PathInterpolatorCompat;

public class RotationEvaluator {
	private final float startAngle;
	private final float endAngle;
	private final Interpolator rotationInterpolator;

	public RotationEvaluator(float startAngle, float endAngle) {
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.rotationInterpolator = PathInterpolatorCompat.create(
			0.22f,
			1.51f,
			0.84f,
			1.38f);
	}

	public float getAnimatedValue(float fraction) {
		return startAngle + rotationInterpolator.getInterpolation(fraction) * (endAngle - startAngle);
	}
}
