/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.compassview;

import android.graphics.Path;

import java.util.List;

import tool.compet.core.config.DkConfig;
import tool.compet.core.DkMaths;
import tool.compet.core.DkStrings;

public class DkCompasses {
	/**
	 * Use this for expression. It returns angle in-range [0, 360) in degrees based on Oy-axis.
	 */
	public static double calcDisplayAngle(double degrees) {
		if (degrees >= 360 || degrees <= -360) {
			degrees %= 360;
		}
		if (degrees >= 360) {
			degrees -= 360;
		}
		if (degrees < 0) {
			degrees += 360;
		}
		return degrees;
	}

	public static String calcOneDecimalDisplayAngle(String degrees) {
		return calcOneDecimalDisplayAngle(DkMaths.parseDouble(degrees));
	}

	public static String calcOneDecimalDisplayAngle(double degrees) {
		degrees = calcDisplayAngle(degrees);
		String angle = DkStrings.format("%.1f", degrees);

		if (angle.startsWith("360")) {
			angle = angle.replace("360", "0");
		}

		return angle;
	}

	public static double point2degrees(double px, double py, int boardCx, int boardCy) {
		double dx = px - boardCx, dy = -py + boardCy;
		double res = (dx == 0) ? ((dy < 0) ? 180 : 0) : 90 - Math.toDegrees(Math.atan(dy / dx));
		if (dx < 0) {
			res += 180;
		}
		return DkMaths.convertAngleToRange180(res);
	}

	public static void applyWordCase(DkRing ring) {
		List<String> words = ring.getWords();

		if (!ring.isWordUpperCase() && !ring.isWordLowerCase()) {
			return;
		}
		if (words == null || words.size() == 0) {
			return;
		}

		if (ring.isWordLowerCase()) {
			for (int i = words.size() - 1; i >= 0; --i) {
				words.set(i, words.get(i).toLowerCase(DkConfig.app.locale));
			}
		}
		else if (ring.isWordUpperCase()) {
			for (int i = words.size() - 1; i >= 0; --i) {
				words.set(i, words.get(i).toUpperCase(DkConfig.app.locale));
			}
		}
	}

	public static Path newArrowAt(float headX, float headY, float width, float height) {
		float dw = width / 2, dh = height / 2;
		Path path = new Path();
		path.moveTo(headX, headY);
		path.lineTo(headX - dw, headY + dh);
		path.lineTo(headX, headY + dh * 2 / 3);
		path.lineTo(headX + dw, headY + dh);
		path.lineTo(headX, headY);
		path.close();
		return path;
	}
}
