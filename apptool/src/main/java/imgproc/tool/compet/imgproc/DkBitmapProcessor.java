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

package tool.compet.imgproc;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import tool.compet.core.graphic.DkBitmaps;
import tool.compet.core.util.DkLogs;

public class DkBitmapProcessor {
	public static Bitmap makeGray(Bitmap input) {
//		Bitmap output = input.copy(input.getConfig(), true);
		return makeGray(input, null);
	}

	public static Bitmap makeGray(Bitmap input, Bitmap output) {
		final int W = input.getWidth(), H = input.getHeight();

		if (output == null) {
			output = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
		}
		if (!output.isMutable()) {
			output = input.copy(input.getConfig(), true);
		}
		if (W != output.getWidth() || H != output.getHeight()) {
			return output;
		}
		for (int y = 0; y < H; ++y) {
			for (int x = 0; x < W; ++x) {
				int argb = input.getPixel(x, y);
				int a = (argb >> 24) & 0xff;
				int r = (argb >> 16) & 0xff;
				int g = (argb >> 8) & 0xff;
				int b = (argb) & 0xff;
				int gray = (int) Math.round(0.299 * r + 0.587 * g + 0.114 * b);
				int color = Color.argb(a, gray, gray, gray);
				output.setPixel(x, y, color);
			}
		}
		return output;
	}

	public static Bitmap rotate(Bitmap input, int degrees) {
		return rotate(input, degrees, input.getWidth() >> 1, input.getHeight() >> 1);
	}

	public static Bitmap rotate(Bitmap input, float degrees, int pivotX, int pivotY) {
		try {
			Matrix matrix = new Matrix();
			matrix.postRotate(degrees, pivotX, pivotY);
			return Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), matrix, true);
		}
		catch (Exception e) {
			DkLogs.logex(DkBitmaps.class, e);
			return input;
		}
	}

	public static Bitmap scale(Bitmap input, int dstWidth, int dstHeight) {
		if (input == null) return null;
		int width = input.getWidth(), height = input.getHeight();
		if (width == dstWidth && height == dstHeight) return input;
		return Bitmap.createScaledBitmap(input, dstWidth, dstHeight, false);
	}

	public static Bitmap scaleRate(Bitmap input, int widthWeight, int heightWeight) {
		int width = input.getWidth(), height = input.getHeight();
		long a = (long) width * heightWeight, b = (long) height * widthWeight;
		int desW = width, desH = height;

		if (a > b) {
			desW = height * widthWeight / heightWeight;
		} else if (a < b) {
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
}
