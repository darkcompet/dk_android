/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.view.animation.Interpolator;

/**
 * This provides basic well-known interpolators. It contains 2 version of interpolators:
 * poor-performance and good-performance.
 * - For poor-performance version, it calculates interpolation every time without
 * cache-implementation or optimization.
 * - Contrast with it, good-performance version lookups table which pre-calculated interpolation values
 * to get approximated value of given fraction.
 */
public class DkInterpolatorProvider {
	public static Interpolator newLinear() {
		return fraction -> fraction;
	}

	public static Interpolator newQuadIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createQuadInValues()) : DkEaseCalculator::getQuadIn;
	}

	public static Interpolator newQuadOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createQuadOutValues()) : DkEaseCalculator::getQuadOut;
	}

	public static Interpolator newQuadInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createQuadInOutValues()) : DkEaseCalculator::getQuadInOut;
	}

	public static Interpolator newCubicIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createCubicInValues()) : DkEaseCalculator::getCubicIn;
	}

	public static Interpolator newCubicOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createCubicOutValues()) : DkEaseCalculator::getCubicOut;
	}

	public static Interpolator newCubicInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createCubicInOutValues()) : DkEaseCalculator::getCubicInOut;
	}

	public static Interpolator newQuartIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createQuartInValues()) : DkEaseCalculator::getQuartIn;
	}

	public static Interpolator newQuartOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createQuartOutValues()) : DkEaseCalculator::getQuartOut;
	}

	public static Interpolator newQuartInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createQuartInOutValues()) : DkEaseCalculator::getQuartInOut;
	}

	public static Interpolator newQuintIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createQuintInValues()) : DkEaseCalculator::getQuintIn;
	}

	public static Interpolator newQuintOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createQuintOutValues()) : DkEaseCalculator::getQuintOut;
	}

	public static Interpolator newQuintInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createQuintInOutValues()) : DkEaseCalculator::getQuintInOut;
	}

	public static Interpolator newSineIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createSineInValues()) : DkEaseCalculator::getSineIn;
	}

	public static Interpolator newSineOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createSineOutValues()) : DkEaseCalculator::getSineOut;
	}

	public static Interpolator newSineInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createSineInOutValues()) : DkEaseCalculator::getSineInOut;
	}

	public static Interpolator newBackIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createBackInValues()) : DkEaseCalculator::getBackIn;
	}

	public static Interpolator newBackOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createBackOutValues()) : DkEaseCalculator::getBackOut;
	}

	public static Interpolator newBackInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createBackInOutValues()) : DkEaseCalculator::getBackInOut;
	}

	public static Interpolator newCircIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createCircInValues()) : DkEaseCalculator::getCircIn;
	}

	public static Interpolator newCircOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createCircOutValues()) : DkEaseCalculator::getCircOut;
	}

	public static Interpolator newCircInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createCircInOutValues()) : DkEaseCalculator::getCircInOut;
	}

	public static Interpolator newBounceIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createBounceInValues()) : DkEaseCalculator::getBounceIn;
	}

	public static Interpolator newBounceOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createBounceOutValues()) : DkEaseCalculator::getBounceOut;
	}

	public static Interpolator newBounceInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createBounceInOutValues()) : DkEaseCalculator::getBounceInOut;
	}

	public static Interpolator newElasticIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createElasticInValues()) : DkEaseCalculator::getElasticIn;
	}

	public static Interpolator newElasticOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createElasticOutValues()) : DkEaseCalculator::getElasticOut;
	}

	public static Interpolator newElasticInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createElasticInOutValues()) : DkEaseCalculator::getElasticInOut;
	}

	public static Interpolator newExpoIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createExpoInValues()) : DkEaseCalculator::getExpoIn;
	}

	public static Interpolator newExpoOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createExpoOutValues()) : DkEaseCalculator::getExpoOut;
	}

	public static Interpolator newExpoInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(DkLookupTableGenerator.createExpoInOutValues()) : DkEaseCalculator::getExpoInOut;
	}
}
