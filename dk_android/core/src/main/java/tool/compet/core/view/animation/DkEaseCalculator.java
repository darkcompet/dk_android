/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view.animation;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Illustration: https://easings.net/
 * <p></p>
 * Cubic Bezier: https://cubic-bezier.com/
 */
public class DkEaseCalculator {
	public static float getQuadIn(float fraction) {
		return (float) pow(fraction, 2);
	}

	public static float getQuadOut(float fraction) {
		return (float) ((float) 1 - pow(1 - fraction, 2));
	}

	public static float getQuadInOut(float fraction) {
		return getPowInOut(fraction, 2);
	}

	public static float getCubicIn(float fraction) {
		return (float) pow(fraction, 3);
	}

	public static float getCubicOut(float fraction) {
		return (float) ((float) 1 - pow(1 - fraction, 3));
	}

	public static float getCubicInOut(float fraction) {
		return getPowInOut(fraction, 3);
	}

	public static float getQuartIn(float fraction) {
		return (float) pow(fraction, 4);
	}

	public static float getQuartOut(float fraction) {
		return (float) ((float) 1 - pow(1 - fraction, 4));
	}

	public static float getQuartInOut(float fraction) {
		return getPowInOut(fraction, 4);
	}

	public static float getQuintIn(float fraction) {
		return (float) pow(fraction, 5);
	}

	public static float getQuintOut(float fraction) {
		return (float) ((float) 1 - pow(1 - fraction, 5));
	}

	public static float getQuintInOut(float fraction) {
		return getPowInOut(fraction, 5);
	}

	public static float getBackIn(float fraction) {
		return (float) (fraction * fraction * ((1.7 + 1f) * fraction - 1.7));
	}

	public static float getBackOut(float fraction) {
		return (float) (--fraction * fraction * ((1.7 + 1f) * fraction + 1.7) + 1f);
	}

	public static float getBackInOut(float fraction) {
		float amount = 1.7f;
		amount *= 1.525;

		if ((fraction *= 2) < 1) {
			return (float) (0.5 * (fraction * fraction * ((amount + 1) * fraction - amount)));
		}
		return (float) (0.5 * ((fraction -= 2) * fraction * ((amount + 1) * fraction + amount) + 2));
	}

	/**
	 * @param fraction Elapsed time / Total time
	 * @return easedValue
	 */
	public static float getBounceIn(float fraction) {
		return 1f - getBounceOut(1f - fraction);
	}

	/**
	 * @param fraction Elapsed time / Total time
	 * @return easedValue
	 */
	public static float getBounceOut(float fraction) {
		if (fraction < 1f / 2.75) {
			return (float) (7.5625 * fraction * fraction);
		}
		else if (fraction < 2f / 2.75) {
			return (float) (7.5625 * (fraction -= 1.5 / 2.75) * fraction + 0.75);
		}
		else if (fraction < 2.5f / 2.75) {
			return (float) (7.5625 * (fraction -= 2.25 / 2.75) * fraction + 0.9375);
		}
		return (float) (7.5625 * (fraction -= 2.625 / 2.75) * fraction + 0.984375);
	}

	public static float getBounceInOut(float fraction) {
		if (fraction < 0.5f) {
			return getBounceIn(fraction * 2f) * 0.5f;
		}
		return getBounceOut(fraction * 2f - 1f) * 0.5f + 0.5f;
	}

	public static float getElasticIn(float fraction) {
		double amplitude = 1;
		double period = 0.3;

		if (fraction == 0f || fraction == 1f) {
			return fraction;
		}
		double dpi = PI * 2;
		double s = period / dpi * asin(1 / amplitude);

		return (float) -(amplitude * pow(2f, 10f * (fraction -= 1f)) * sin((fraction - s) * dpi / period));
	}

	public static float getElasticOut(float fraction) {
		double amplitude = 1;
		double period = 0.3;

		if (fraction == 0f || fraction == 1f) {
			return fraction;
		}
		double dpi = PI * 2;
		double s = period / dpi * asin(1 / amplitude);

		return (float) (amplitude * pow(2, -10 * fraction) * sin((fraction - s) * dpi / period) + 1);
	}

	public static float getElasticInOut(float fraction) {
		double amplitude = 1;
		double period = 0.45;
		double dpi = PI * 2;

		double s = period / dpi * asin(1 / amplitude);

		if ((fraction *= 2) < 1) {
			return (float) (-0.5f * (amplitude * pow(2, 10 * (fraction -= 1f)) * sin((fraction - s) * dpi / period)));
		}
		return (float) (amplitude * pow(2, -10 * (fraction -= 1)) * sin((fraction - s) * dpi / period) * 0.5 + 1);
	}

	public static float getSineIn(float fraction) {
		return (float) (1f - cos(fraction * PI / 2f));
	}

	public static float getSineOut(float fraction) {
		return (float) sin(fraction * PI / 2f);
	}

	public static float getSineInOut(float fraction) {
		return (float) (-0.5f * (cos(PI * fraction) - 1f));
	}

	public static float getCircIn(float fraction) {
		return (float) -(sqrt(1f - fraction * fraction) - 1);
	}

	public static float getCircOut(float fraction) {
		return (float) sqrt(1f - (--fraction) * fraction);
	}

	public static float getCircInOut(float fraction) {
		if ((fraction *= 2f) < 1f) {
			return (float) (-0.5f * (sqrt(1f - fraction * fraction) - 1f));
		}
		return (float) (0.5f * (sqrt(1f - (fraction -= 2f) * fraction) + 1f));
	}

	public static float getExpoIn(float fraction) {
		return (float) pow(2, 10 * (fraction - 1));
	}

	public static float getExpoOut(float fraction) {
		return (float) -pow(2, -10 * fraction) + 1;
	}

	public static float getExpoInOut(float fraction) {
		if ((fraction *= 2) < 1) {
			return (float) pow(2, 10 * (fraction - 1)) * 0.5f;
		}
		return (float) (-pow(2, -10 * --fraction) + 2f) * 0.5f;
	}

	/**
	 * @param fraction Elapsed time / Total time
	 * @param pow      pow The exponent to use (ex. 3 would return a cubic ease).
	 * @return easedValue
	 */
	private static float getPowInOut(float fraction, double pow) {
		if ((fraction *= 2) < 1) {
			return (float) (0.5 * pow(fraction, pow));
		}
		return (float) (1 - 0.5 * abs(pow(2 - fraction, pow)));
	}
}
