/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.imgproc;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

/**
 * Image Processing for Android bitmap.
 */
public class DkImageProcs {
	public static Bitmap makeGray(Bitmap input) {
		// Bitmap output = input.copy(input.getConfig(), true);
		return makeGray(input, null);
	}

	// Make image more human readable by apply removing noise, gray scale...
	public static Bitmap makeGray(Bitmap input, @Nullable Bitmap output) {
		return MyTransformer.makeGray(input, output);
	}

	public static Bitmap rotate(Bitmap input, int degrees) {
		return rotate(input, degrees, input.getWidth() >> 1, input.getHeight() >> 1);
	}

	// Rotate image with given angle in degrees
	public static Bitmap rotate(Bitmap input, float degrees, int pivotX, int pivotY) {
		return MyRotator.rotate(input, degrees, pivotX, pivotY);
	}

	// Scale to given dimension
	public static Bitmap scale(Bitmap input, int dstWidth, int dstHeight) {
		return MyScaler.scaleTo(input, dstWidth, dstHeight);
	}

	// Scale given bitmap to fit with given ratio (with : height)
	public static Bitmap scaleWithRate(Bitmap input, int widthWeight, int heightWeight) {
		return MyScaler.scaleWithRate(input, widthWeight, heightWeight);
	}

	// Simple detect edge of given image
	public static Bitmap detectEdge(Bitmap input, @Nullable Bitmap output) {
		return MyEdgeDetector.detect(input, output);
	}
}
