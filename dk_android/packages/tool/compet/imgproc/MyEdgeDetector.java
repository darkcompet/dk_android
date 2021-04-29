/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.imgproc;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.Nullable;

class MyEdgeDetector {
	static Bitmap detect(Bitmap input, @Nullable Bitmap output) {
		if (output == null || !output.isMutable()) {
			output = input.copy(input.getConfig(), true);
		}

		final int W = input.getWidth();
		final int H = input.getHeight();

		if (W != output.getWidth() || H != output.getHeight()) {
			return output;
		}

		int[][] filter1 = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
		int[][] filter2 = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

		for (int y = 1, H1 = H - 1; y < H1; ++y) {
			for (int x = 1, W1 = W - 1; x < W1; ++x) {
				// getPrefixSum 3-by-3 array of colors in neighborhood
				int[][] gray = new int[3][3];

				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						int argb = input.getPixel(x - 1 + i, y - 1 + j);
						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = (argb) & 0xff;

						gray[i][j] = (int) Math.round(0.299 * r + 0.587 * g + 0.114 * b);
					}
				}

				// apply filter
				int gx = 0, gy = 0;

				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						gx += gray[i][j] * filter1[i][j];
						gy += gray[i][j] * filter2[i][j];
					}
				}

				int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
				magnitude = (magnitude < 0) ? 0 : Math.min(magnitude, 255);
				magnitude = 255 - magnitude;
				int color = Color.rgb(magnitude, magnitude, magnitude);

				output.setPixel(x, y, color);
			}
		}

		return output;
	}
}
