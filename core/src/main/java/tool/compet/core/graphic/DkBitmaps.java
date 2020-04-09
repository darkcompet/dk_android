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

package tool.compet.core.graphic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tool.compet.core.storage.DkFiles;
import tool.compet.core.util.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * This class, provides common basic operations on Bitmap.
 */
public final class DkBitmaps {
	public static long getSize(Bitmap input) {
		if (input == null) {
			return 0L;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			return input.getAllocationByteCount();
		}
		return input.getByteCount();
	}

	/** https://developer.android.com/topic/performance/graphics/load-bitmap */
	public static int[] getDimension(File bitmapFile) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opts);
		return new int[] {opts.outWidth, opts.outHeight};
	}

	public static boolean save(Bitmap input, String filePath) throws IOException {
		return save(input, new File(filePath));
	}

	public static boolean save(Bitmap bitmap, File file) throws IOException {
		if (!file.exists()) {
			DkFiles.createFileDeeply(file);
		}
		OutputStream os = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
		os.close();
		return true;
	}

	public static Bitmap load(File file) {
		if (file == null) {
			return null;
		}
		return load(file.getPath());
	}

	public static Bitmap load(Context context, int imgRes) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return load(context, imgRes, opts);
	}

	public static Bitmap load(Context context, int imgRes, BitmapFactory.Options opts) {
		Bitmap res = BitmapFactory.decodeResource(context.getResources(), imgRes, opts);
		if (DEBUG) {
			DkLogs.log(DkBitmaps.class, "loaded bitmap size: %d", getSize(res));
		}
		return res;
	}

	public static Bitmap load(String filePath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return load(filePath, opts);
	}

	public static Bitmap load(String filePath, BitmapFactory.Options opts) {
		Bitmap res = BitmapFactory.decodeFile(filePath, opts);
		if (DEBUG) {
			DkLogs.log(DkBitmaps.class, "loaded bitmap size: %d", getSize(res));
		}
		return res;
	}

	public static Bitmap load(Context context, Uri uri) throws IOException {
		return load(context.getContentResolver().openInputStream(uri));
	}

	public static Bitmap load(InputStream is) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return load(is, opts);
	}

	public static Bitmap load(InputStream is, BitmapFactory.Options opts) {
		Bitmap res = BitmapFactory.decodeStream(is, null, opts);
		if (DEBUG) {
			DkLogs.log(DkBitmaps.class, "Loaded bitmap size: %d", getSize(res));
		}
		return res;
	}

	public static Bitmap decodeRegion(InputStream is, int left, int top, int right, int bottom) throws IOException {
		return decodeRegion(is, left, top, right, bottom, null);
	}

	public static Bitmap decodeRegion(InputStream is, int left, int top, int right, int bottom,
		BitmapFactory.Options opts) throws IOException {

		BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
		return decoder.decodeRegion(new Rect(left, top, right, bottom), opts);
	}

	public static byte[] toByteArray(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	public static Bitmap rotate(Bitmap bitmap, int degrees) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);

		return Bitmap.createBitmap(bitmap,
			0, 0,
			bitmap.getWidth(), bitmap.getHeight(),
			matrix, true);
	}
}
