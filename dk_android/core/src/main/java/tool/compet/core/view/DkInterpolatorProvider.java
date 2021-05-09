/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.view.animation.Interpolator;

/**
 * All supported interpolators will be provided by this class.
 * <p></p>
 * This contains 2 version of interpolators: poor-performance and good-performance.
 * <p></p>
 * For poor-performance version, it calculates interpolation every time without
 * cache-implementation or optimization.
 * <p></p>
 * Contrast with it, good-performance version lookups table which pre-calculated interpolation values
 * to get approximated value of given fraction.
 *
 * Refer:
 */
public class DkInterpolatorProvider {
	public static Interpolator newLinear() {
		return fraction -> fraction;
	}

	public static Interpolator newQuadIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createQuadInValues()) :
			DkEaseCalculator::getQuadIn;
	}

	public static Interpolator newQuadOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createQuadOutValues()) :
			DkEaseCalculator::getQuadOut;
	}

	public static Interpolator newQuadInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createQuadInOutValues()) :
			DkEaseCalculator::getQuadInOut;
	}

	public static Interpolator newCubicIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createCubicInValues()) :
			DkEaseCalculator::getCubicIn;
	}

	public static Interpolator newCubicOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createCubicOutValues()) :
			DkEaseCalculator::getCubicOut;
	}

	public static Interpolator newCubicInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createCubicInOutValues()) :
			DkEaseCalculator::getCubicInOut;
	}

	public static Interpolator newQuartIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createQuartInValues()) :
			DkEaseCalculator::getQuartIn;
	}

	public static Interpolator newQuartOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createQuartOutValues()) :
			DkEaseCalculator::getQuartOut;
	}

	public static Interpolator newQuartInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createQuartInOutValues()) :
			DkEaseCalculator::getQuartInOut;
	}

	public static Interpolator newQuintIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createQuintInValues()) :
			DkEaseCalculator::getQuintIn;
	}

	public static Interpolator newQuintOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createQuintOutValues()) :
			DkEaseCalculator::getQuintOut;
	}

	public static Interpolator newQuintInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createQuintInOutValues()) :
			DkEaseCalculator::getQuintInOut;
	}

	public static Interpolator newSineIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createSineInValues()) :
			DkEaseCalculator::getSineIn;
	}

	public static Interpolator newSineOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createSineOutValues()) :
			DkEaseCalculator::getSineOut;
	}

	public static Interpolator newSineInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createSineInOutValues()) :
			DkEaseCalculator::getSineInOut;
	}

	public static Interpolator newBackIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createBackInValues()) :
			DkEaseCalculator::getBackIn;
	}

	public static Interpolator newBackOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createBackOutValues()) :
			DkEaseCalculator::getBackOut;
	}

	public static Interpolator newBackInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createBackInOutValues()) :
			DkEaseCalculator::getBackInOut;
	}

	public static Interpolator newCircIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createCircInValues()) :
			DkEaseCalculator::getCircIn;
	}

	public static Interpolator newCircOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createCircOutValues()) :
			DkEaseCalculator::getCircOut;
	}

	public static Interpolator newCircInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createCircInOutValues()) :
			DkEaseCalculator::getCircInOut;
	}

	public static Interpolator newBounceIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createBounceInValues()) :
			DkEaseCalculator::getBounceIn;
	}

	public static Interpolator newBounceOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createBounceOutValues()) :
			DkEaseCalculator::getBounceOut;
	}

	public static Interpolator newBounceInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createBounceInOutValues()) :
			DkEaseCalculator::getBounceInOut;
	}

	public static Interpolator newElasticIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createElasticInValues()) :
			DkEaseCalculator::getElasticIn;
	}

	public static Interpolator newElasticOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createElasticOutValues()) :
			DkEaseCalculator::getElasticOut;
	}

	public static Interpolator newElasticInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createElasticInOutValues()) :
			DkEaseCalculator::getElasticInOut;
	}

	public static Interpolator newExpoIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createExpoInValues()) :
			DkEaseCalculator::getExpoIn;
	}

	public static Interpolator newExpoOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createExpoOutValues()) :
			DkEaseCalculator::getExpoOut;
	}

	public static Interpolator newExpoInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(MyEaseLookupTable.createExpoInOutValues()) :
			DkEaseCalculator::getExpoInOut;
	}
}
