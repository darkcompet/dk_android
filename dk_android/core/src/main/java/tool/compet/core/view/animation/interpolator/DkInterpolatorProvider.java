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
 */
public class DkInterpolatorProvider {
	private DkInterpolatorProvider() {
	}

	public static Interpolator newLinear() {
		return fraction -> fraction;
	}

	public static Interpolator newQuadIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createQuadInValues()) :
			DkEaseCalculator::getQuadIn;
	}

	public static Interpolator newQuadOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createQuadOutValues()) :
			DkEaseCalculator::getQuadOut;
	}

	public static Interpolator newQuadInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createQuadInOutValues()) :
			DkEaseCalculator::getQuadInOut;
	}

	public static Interpolator newCubicIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createCubicInValues()) :
			DkEaseCalculator::getCubicIn;
	}

	public static Interpolator newCubicOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createCubicOutValues()) :
			DkEaseCalculator::getCubicOut;
	}

	public static Interpolator newCubicInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createCubicInOutValues()) :
			DkEaseCalculator::getCubicInOut;
	}

	public static Interpolator newQuartIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createQuartInValues()) :
			DkEaseCalculator::getQuartIn;
	}

	public static Interpolator newQuartOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createQuartOutValues()) :
			DkEaseCalculator::getQuartOut;
	}

	public static Interpolator newQuartInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createQuartInOutValues()) :
			DkEaseCalculator::getQuartInOut;
	}

	public static Interpolator newQuintIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createQuintInValues()) :
			DkEaseCalculator::getQuintIn;
	}

	public static Interpolator newQuintOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createQuintOutValues()) :
			DkEaseCalculator::getQuintOut;
	}

	public static Interpolator newQuintInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createQuintInOutValues()) :
			DkEaseCalculator::getQuintInOut;
	}

	public static Interpolator newSineIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createSineInValues()) :
			DkEaseCalculator::getSineIn;
	}

	public static Interpolator newSineOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createSineOutValues()) :
			DkEaseCalculator::getSineOut;
	}

	public static Interpolator newSineInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createSineInOutValues()) :
			DkEaseCalculator::getSineInOut;
	}

	public static Interpolator newBackIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createBackInValues()) :
			DkEaseCalculator::getBackIn;
	}

	public static Interpolator newBackOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createBackOutValues()) :
			DkEaseCalculator::getBackOut;
	}

	public static Interpolator newBackInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createBackInOutValues()) :
			DkEaseCalculator::getBackInOut;
	}

	public static Interpolator newCircIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createCircInValues()) :
			DkEaseCalculator::getCircIn;
	}

	public static Interpolator newCircOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createCircOutValues()) :
			DkEaseCalculator::getCircOut;
	}

	public static Interpolator newCircInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createCircInOutValues()) :
			DkEaseCalculator::getCircInOut;
	}

	public static Interpolator newBounceIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createBounceInValues()) :
			DkEaseCalculator::getBounceIn;
	}

	public static Interpolator newBounceOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createBounceOutValues()) :
			DkEaseCalculator::getBounceOut;
	}

	public static Interpolator newBounceInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createBounceInOutValues()) :
			DkEaseCalculator::getBounceInOut;
	}

	public static Interpolator newElasticIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createElasticInValues()) :
			DkEaseCalculator::getElasticIn;
	}

	public static Interpolator newElasticOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createElasticOutValues()) :
			DkEaseCalculator::getElasticOut;
	}

	public static Interpolator newElasticInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createElasticInOutValues()) :
			DkEaseCalculator::getElasticInOut;
	}

	public static Interpolator newExpoIn(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createExpoInValues()) :
			DkEaseCalculator::getExpoIn;
	}

	public static Interpolator newExpoOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createExpoOutValues()) :
			DkEaseCalculator::getExpoOut;
	}

	public static Interpolator newExpoInOut(boolean useLookupTable) {
		return useLookupTable ? new DkLookupTableInterpolator(EaseLookupTable.createExpoInOutValues()) :
			DkEaseCalculator::getExpoInOut;
	}
}
