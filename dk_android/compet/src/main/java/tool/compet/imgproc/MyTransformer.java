/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.imgproc;

import android.graphics.Bitmap;
import android.graphics.Color;

class MyTransformer {
	static Bitmap makeGray(Bitmap input, Bitmap output) {
		final int W = input.getWidth();
		final int H = input.getHeight();

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
}
