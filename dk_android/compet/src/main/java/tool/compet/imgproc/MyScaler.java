/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.imgproc;

import android.graphics.Bitmap;

class MyScaler {
	static Bitmap scaleWithRate(Bitmap input, int widthWeight, int heightWeight) {
		int width = input.getWidth();
		int height = input.getHeight();
		long a = (long) width * heightWeight;
		long b = (long) height * widthWeight;
		int desW = width;
		int desH = height;

		if (a > b) {
			desW = height * widthWeight / heightWeight;
		}
		else if (a < b) {
			desH = width * heightWeight / widthWeight;
		}
		if (width == desW && height == desH) {
			return input;
		}

		Bitmap output = Bitmap.createBitmap(input,
			Math.abs(desW - width) / 2,
			Math.abs(desH - height) / 2, desW, desH);

		return Bitmap.createScaledBitmap(output, desW, desH, false);
	}

	static Bitmap scaleTo(Bitmap input, int dstWidth, int dstHeight) {
		int width = input.getWidth();
		int height = input.getHeight();
		if (width == dstWidth && height == dstHeight) {
			return input;
		}
		return Bitmap.createScaledBitmap(input, dstWidth, dstHeight, false);
	}
}
