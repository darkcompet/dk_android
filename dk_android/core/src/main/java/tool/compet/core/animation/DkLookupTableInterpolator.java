/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.animation;

import android.view.animation.Interpolator;

/**
 * Suppose we use `ValueAnimator` in animation. Usually, we pass an interpolator function
 * to interpolate input to obtain various animation styles.
 * But normally, interpolator is complex and maybe take a long time to calculate result value,
 * instead of calculating at each step, we can use lookup-table to approximate result value.
 */
public class DkLookupTableInterpolator implements Interpolator {
	private final float[] lookupTable; // interpolated values

	/**
	 * @param lookupTable Interpolated values, must start with 0.0000f and end with 1.0000f.
	 */
	public DkLookupTableInterpolator(float[] lookupTable) {
		this.lookupTable = lookupTable;
	}

	/**
	 * @param fraction Animation progress, range in [0f, 1f]. In theory, it is (currentElapsedTime / totalDuration).
	 * @return Interpolated animation value, range in [0f, 1f].
	 */
	@Override
	public float getInterpolation(float fraction) {
		// f := fraction (progress in range [0f, 1f])
		// v(f) := interpolated result value
		// v_s: start value, v_e: end value
		// v(f) = v_s + (v_e - v_s) * interpolate(f)
		if (fraction <= 0.0f) return 0f;
		if (fraction >= 1.0f) return 1.0f;

		// Calculate index in lookup-table from current progress (fraction)
		final int lastIndex = lookupTable.length - 1;
		final float position = fraction * lastIndex;
		int index = (int) position;
		// We cut-down at `lastIndex - 1` since later suppliment
		if (index >= lastIndex) {
			index = lastIndex - 1;
		}

		final float weight = position - index;
		// value := (1 - w) * f(i) + w * f(i + 1);

		return lookupTable[index] + weight * (lookupTable[index + 1] - lookupTable[index]);
	}
}
