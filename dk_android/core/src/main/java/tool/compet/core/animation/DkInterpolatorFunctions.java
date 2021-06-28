/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.animation;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * + Ease:
 *    - Illustration: https://easings.net/
 *    - Cubic Bezier: https://cubic-bezier.com/
 * + XXX
 */
public class DkInterpolatorFunctions {
	//
	// Basic Linear
	//

	public static float linear(float fraction) {
		return fraction;
	}

	//
	// Ease Sine
	//

	public static float easeSineIn(float fraction) {
		return (float) (1f - cos(fraction * PI / 2f));
	}

	public static float easeSineOut(float fraction) {
		return (float) sin(fraction * PI / 2f);
	}

	public static float easeSineInOut(float fraction) {
		return (float) (-0.5f * (cos(PI * fraction) - 1f));
	}

	//
	// Ease Quad
	//

	public static float easeQuadIn(float fraction) {
		return (float) pow(fraction, 2);
	}

	public static float easeQuadOut(float fraction) {
		return (float) ((float) 1 - pow(1 - fraction, 2));
	}

	public static float easeQuadInOut(float fraction) {
		return easePowInOut(fraction, 2);
	}

	//
	// Ease Cubic
	//

	public static float easeCubicIn(float fraction) {
		return (float) pow(fraction, 3);
	}

	public static float easeCubicOut(float fraction) {
		return (float) ((float) 1 - pow(1 - fraction, 3));
	}

	public static float easeCubicInOut(float fraction) {
		return easePowInOut(fraction, 3);
	}

	//
	// Ease Quart
	//

	public static float easeQuartIn(float fraction) {
		return (float) pow(fraction, 4);
	}

	public static float easeQuartOut(float fraction) {
		return (float) ((float) 1 - pow(1 - fraction, 4));
	}

	public static float easeQuartInOut(float fraction) {
		return easePowInOut(fraction, 4);
	}

	//
	// Ease Quint
	//

	public static float easeQuintIn(float fraction) {
		return (float) pow(fraction, 5);
	}

	public static float easeQuintOut(float fraction) {
		return (float) ((float) 1 - pow(1 - fraction, 5));
	}

	public static float easeQuintInOut(float fraction) {
		return easePowInOut(fraction, 5);
	}

	//
	// Ease Expo
	//

	public static float easeExpoIn(float fraction) {
		return (float) pow(2, 10 * (fraction - 1));
	}

	public static float easeExpoOut(float fraction) {
		return (float) -pow(2, -10 * fraction) + 1;
	}

	public static float easeExpoInOut(float fraction) {
		if ((fraction *= 2) < 1) {
			return (float) pow(2, 10 * (fraction - 1)) * 0.5f;
		}
		return (float) (-pow(2, -10 * --fraction) + 2f) * 0.5f;
	}

	//
	// Ease Circ
	//

	public static float easeCircIn(float fraction) {
		return (float) -(sqrt(1f - fraction * fraction) - 1);
	}

	public static float easeCircOut(float fraction) {
		return (float) sqrt(1f - (--fraction) * fraction);
	}

	public static float easeCircInOut(float fraction) {
		if ((fraction *= 2f) < 1f) {
			return (float) (-0.5f * (sqrt(1f - fraction * fraction) - 1f));
		}
		return (float) (0.5f * (sqrt(1f - (fraction -= 2f) * fraction) + 1f));
	}

	//
	// Ease Back
	//

	public static float easeBackIn(float fraction) {
		return (float) (fraction * fraction * ((1.7 + 1f) * fraction - 1.7));
	}

	public static float easeBackOut(float fraction) {
		return (float) (--fraction * fraction * ((1.7 + 1f) * fraction + 1.7) + 1f);
	}

	public static float easeBackInOut(float fraction) {
		float amount = 1.7f;
		amount *= 1.525;

		if ((fraction *= 2) < 1) {
			return (float) (0.5 * (fraction * fraction * ((amount + 1) * fraction - amount)));
		}
		return (float) (0.5 * ((fraction -= 2) * fraction * ((amount + 1) * fraction + amount) + 2));
	}

	//
	// Ease Elastic
	//

	public static float easeElasticIn(float fraction) {
		double amplitude = 1;
		double period = 0.3;

		if (fraction == 0f || fraction == 1f) {
			return fraction;
		}
		double dpi = PI * 2;
		double s = period / dpi * asin(1 / amplitude);

		return (float) -(amplitude * pow(2f, 10f * (fraction -= 1f)) * sin((fraction - s) * dpi / period));
	}

	public static float easeElasticOut(float fraction) {
		double amplitude = 1;
		double period = 0.3;

		if (fraction == 0f || fraction == 1f) {
			return fraction;
		}
		double dpi = PI * 2;
		double s = period / dpi * asin(1 / amplitude);

		return (float) (amplitude * pow(2, -10 * fraction) * sin((fraction - s) * dpi / period) + 1);
	}

	public static float easeElasticInOut(float fraction) {
		double amplitude = 1;
		double period = 0.45;
		double dpi = PI * 2;

		double s = period / dpi * asin(1 / amplitude);

		if ((fraction *= 2) < 1) {
			return (float) (-0.5f * (amplitude * pow(2, 10 * (fraction -= 1f)) * sin((fraction - s) * dpi / period)));
		}
		return (float) (amplitude * pow(2, -10 * (fraction -= 1)) * sin((fraction - s) * dpi / period) * 0.5 + 1);
	}

	//
	// Ease Bounce
	//

	public static float easeBounceIn(float fraction) {
		return 1f - easeBounceOut(1f - fraction);
	}

	public static float easeBounceOut(float fraction) {
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

	public static float easeBounceInOut(float fraction) {
		if (fraction < 0.5f) {
			return easeBounceIn(fraction * 2f) * 0.5f;
		}
		return easeBounceOut(fraction * 2f - 1f) * 0.5f + 0.5f;
	}

	// region Private

	private static float easePowInOut(float fraction, double pow) {
		if ((fraction *= 2) < 1) {
			return (float) (0.5 * pow(fraction, pow));
		}
		return (float) (1 - 0.5 * abs(pow(2 - fraction, pow)));
	}

	// endregion Private
}
