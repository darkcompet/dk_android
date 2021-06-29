/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.compassview;

import android.animation.TypeEvaluator;
import android.content.Context;

import java.util.Collections;
import java.util.List;

class MyCompassController {
	private TypeEvaluator mArgbEvaluator;

	DkCompassInfo readInfo(Context context, double degrees, List<DkCompassRing> rings, String[] poleLongNames) {
		DkCompassInfo root = new DkCompassInfo();

		DkCompassInfo northInfo = new DkCompassInfo(poleLongNames[0]);
		DkCompassInfo eastInfo = new DkCompassInfo(poleLongNames[1]);
		DkCompassInfo southInfo = new DkCompassInfo(poleLongNames[2]);
		DkCompassInfo westInfo = new DkCompassInfo(poleLongNames[3]);

		root.addChild(northInfo).addChild(eastInfo).addChild(southInfo).addChild(westInfo);

		final double north = degrees;
		final double east = DkCompassHelper.calcDisplayAngle(north + 90);
		final double south = DkCompassHelper.calcDisplayAngle(north + 180);
		final double west = DkCompassHelper.calcDisplayAngle(north + 270);

		String northDegrees = DkCompassHelper.calcOneDecimalDisplayAngle(north);
		String eastDegrees = DkCompassHelper.calcOneDecimalDisplayAngle(east);
		String southDegrees = DkCompassHelper.calcOneDecimalDisplayAngle(south);
		String westDegrees = DkCompassHelper.calcOneDecimalDisplayAngle(west);
		String degreesKey = context.getString(R.string.degrees);

		northInfo.addChild(new DkCompassInfo(degreesKey, northDegrees));
		eastInfo.addChild(new DkCompassInfo(degreesKey, eastDegrees));
		southInfo.addChild(new DkCompassInfo(degreesKey, southDegrees));
		westInfo.addChild(new DkCompassInfo(degreesKey, westDegrees));

		// Calculate ring names
		rings = rings != null ? rings : Collections.emptyList();

		for (DkCompassRing ring : rings) {
			double rotateDegrees = ring.getRotatedDegrees();
			List<String> words = ring.getWords();
			final int wordCnt = words.size();
			double delta = 360.0 / wordCnt;
			double offset = DkCompassHelper.calcDisplayAngle(-delta / 2 + rotateDegrees);
			String ringName = ring.ringName;

			for (int i = 0; i < wordCnt; ++i) {
				String word = words.get(i);
				double fromDegrees = DkCompassHelper.calcDisplayAngle(offset + i * delta);
				double toDegrees = DkCompassHelper.calcDisplayAngle(fromDegrees + delta);

				collectInfo(northInfo, ringName, north, fromDegrees, toDegrees, word);
				collectInfo(eastInfo, ringName, east, fromDegrees, toDegrees, word);
				collectInfo(southInfo, ringName, south, fromDegrees, toDegrees, word);
				collectInfo(westInfo, ringName, west, fromDegrees, toDegrees, word);
			}
		}

		return root;
	}

	private void collectInfo(DkCompassInfo info, String ringName, double angle, double from, double to, String word) {
		if ((from <= angle && angle <= to) || (from > to && (from <= angle || angle <= to))) {
			info.addChild(new DkCompassInfo(ringName, word));
		}
	}

	TypeEvaluator getArgbEvaluator() {
		if (mArgbEvaluator == null) {
			mArgbEvaluator = new MyArgbEvaluator();
		}
		return mArgbEvaluator;
	}

	class MyArgbEvaluator implements TypeEvaluator {
		@Override
		public Object evaluate(float fraction, Object startValue, Object endValue) {
			int startInt = (Integer) startValue;
			float startA = ((startInt >> 24) & 0xff) / 255.0f;
			float startR = ((startInt >> 16) & 0xff) / 255.0f;
			float startG = ((startInt >> 8) & 0xff) / 255.0f;
			float startB = (startInt & 0xff) / 255.0f;

			int endInt = (Integer) endValue;
			float endA = ((endInt >> 24) & 0xff) / 255.0f;
			float endR = ((endInt >> 16) & 0xff) / 255.0f;
			float endG = ((endInt >> 8) & 0xff) / 255.0f;
			float endB = (endInt & 0xff) / 255.0f;

			// convert from sRGB to linear
			startR = (float) Math.pow(startR, 2.2);
			startG = (float) Math.pow(startG, 2.2);
			startB = (float) Math.pow(startB, 2.2);

			endR = (float) Math.pow(endR, 2.2);
			endG = (float) Math.pow(endG, 2.2);
			endB = (float) Math.pow(endB, 2.2);

			// compute the interpolated color in linear space
			float a = startA + fraction * (endA - startA);
			float r = startR + fraction * (endR - startR);
			float g = startG + fraction * (endG - startG);
			float b = startB + fraction * (endB - startB);

			// convert back to sRGB in the [0..255] range
			a = a * 255.0f;
			r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
			g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
			b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

			return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
		}
	}
}
