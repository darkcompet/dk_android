/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.view.gesturedetector;

class MyHelper {
	/**
	 * @return angle between 2 lines in degress from [-180, 180]
	 */
	static float calcDegreesBetweenLines(
		float l1StartX, float l1StartY, float l1EndX, float l1EndY,
		float l2StartX, float l2StartY, float l2EndX, float l2EndY) {

		double angle1 = Math.atan2(l1EndY - l1StartY, l1EndX - l1StartX);
		double angle2 = Math.atan2(l2EndY - l2StartY, l2EndX - l2StartX);
		double delta = Math.toDegrees(angle1 - angle2);

		if (delta >= 360 || delta <= -360) {
			delta %= 360;
		}
		if (delta > 180) {
			delta -= 360;
		}
		if (delta < -180) {
			delta += 360;
		}

		return (float) delta;
	}
}
