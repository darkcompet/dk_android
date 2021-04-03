/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view.animation;

import android.animation.IntEvaluator;

public class DkLookupTableIntEvaluator extends IntEvaluator {
	private final int lastIndex;
	private final int[] values;
	private final float stepSize;

	public DkLookupTableIntEvaluator(int[] values) {
		this.values = values;
		this.lastIndex = values.length - 1;
		this.stepSize = 1f / lastIndex;
	}

	@Override
	public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
		if (fraction <= 0f) {
			return values[0];
		}
		if (fraction >= 1.0f) {
			return values[lastIndex - 1];
		}

		// Calculate index - We use min with length - 2 to avoid IndexOutOfBoundsException when
		// we lerp (linearly interpolate) in the return statement
		int position = Math.min((int) (fraction * lastIndex), lastIndex - 1);

		// Calculate values to account for small offsets as the lookup table has discrete values
		float quantized = position * stepSize;
		float diff = fraction - quantized;
		float weight = diff / stepSize;

		// Linearly interpolate between the table values
		return (int) (values[position] + weight * (values[position + 1] - values[position]));
	}
}
