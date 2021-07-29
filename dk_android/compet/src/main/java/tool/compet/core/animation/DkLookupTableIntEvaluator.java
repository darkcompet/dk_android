/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.animation;

import android.animation.IntEvaluator;

public class DkLookupTableIntEvaluator extends IntEvaluator {
	private final int[] values;

	public DkLookupTableIntEvaluator(int[] values) {
		this.values = values;
	}

	@Override
	public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
		if (fraction <= 0.0f) return values[0];
		if (fraction >= 1.0f) return values[values.length - 1];

		// Calculate index in lookup-table from current progress (fraction)
		final int lastIndex = values.length - 1;
		final float position = fraction * lastIndex;
		int index = (int) position;
		// We cut-down at `lastIndex - 1` since later suppliment
		if (index >= lastIndex) {
			index = lastIndex - 1;
		}

		final float weight = position - index;
		// value := (1 - w) * f(i) + w * f(i + 1);

		return (int) (values[index] + weight * (values[index + 1] - values[index]));
	}
}
