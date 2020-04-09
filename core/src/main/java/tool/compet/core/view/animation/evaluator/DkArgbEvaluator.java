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

package tool.compet.core.view.animation.evaluator;

import android.animation.TypeEvaluator;

import static java.lang.Math.pow;
import static java.lang.Math.round;

public class DkArgbEvaluator implements TypeEvaluator<Integer> {
	/**
	 * This function returns the calculated in-between value for a color
	 * given integers that represent the start and end values in the four
	 * bytes of the 32-bit int. Each channel is separately linearly interpolated
	 * and the resulting calculated values are recombined into the return value.
	 *
	 * @param fraction The fraction from the starting to the ending values
	 * @param startValue A 32-bit int value representing colors in the
	 * separate bytes of the parameter
	 * @param endValue A 32-bit int value representing colors in the
	 * separate bytes of the parameter
	 * @return A value that is calculated to be the linearly interpolated
	 * result, derived by separating the start and end values into separate
	 * color channels and interpolating each one separately, recombining the
	 * resulting values in the same way.
	 */
	@Override
	public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
		int startInt = startValue;
		float startA = ((startInt >> 24) & 0xff) / 255.0f;
		float startR = ((startInt >> 16) & 0xff) / 255.0f;
		float startG = ((startInt >>  8) & 0xff) / 255.0f;
		float startB = ( startInt        & 0xff) / 255.0f;

		int endInt = endValue;
		float endA = ((endInt >> 24) & 0xff) / 255.0f;
		float endR = ((endInt >> 16) & 0xff) / 255.0f;
		float endG = ((endInt >>  8) & 0xff) / 255.0f;
		float endB = ( endInt        & 0xff) / 255.0f;

		// convert from sRGB to linear
		startR = (float) pow(startR, 2.2);
		startG = (float) pow(startG, 2.2);
		startB = (float) pow(startB, 2.2);

		endR = (float) pow(endR, 2.2);
		endG = (float) pow(endG, 2.2);
		endB = (float) pow(endB, 2.2);

		// compute the interpolated color in linear space
		float a = startA + fraction * (endA - startA);
		float r = startR + fraction * (endR - startR);
		float g = startG + fraction * (endG - startG);
		float b = startB + fraction * (endB - startB);

		// convert back to sRGB in the [0..255] range
		double h = 1.0 / 2.2;
		a = a * 255.0f;
		r = (float) pow(r, h) * 255.0f;
		g = (float) pow(g, h) * 255.0f;
		b = (float) pow(b, h) * 255.0f;

		return round(a) << 24 | round(r) << 16 | round(g) << 8 | round(b);
	}
}
