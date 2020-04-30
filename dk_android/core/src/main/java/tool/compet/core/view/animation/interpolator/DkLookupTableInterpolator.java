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

package tool.compet.core.view.animation.interpolator;

import android.view.animation.Interpolator;

public class DkLookupTableInterpolator implements Interpolator {
	private final float[] values;
	private final float stepSize;
	private final int lastIndex;

	public DkLookupTableInterpolator(float[] interpolationValues) {
		this.values = interpolationValues;
		this.lastIndex = interpolationValues.length - 1;
		this.stepSize = 1f / lastIndex;
	}

	@Override
	public float getInterpolation(float fraction) {
		if (fraction <= 0f) {
			return 0f;
		}
		if (fraction >= 1.0f) {
			return 1.0f;
		}

		// Calculate index - We use min with length - 2 to avoid IndexOutOfBoundsException when
		// we lerp (linearly interpolate) in the return statement
		int position = Math.min((int) (fraction * lastIndex), lastIndex - 1);

		// Calculate values to account for small offsets as the lookup table has discrete values
		float quantized = position * stepSize;
		float diff = fraction - quantized;
		float weight = diff / stepSize;

		// Linearly interpolate between the table values
		return values[position] + weight * (values[position + 1] - values[position]);
	}
}
