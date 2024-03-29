/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tool.compet.BuildConfig;
import tool.compet.core4j.DkFiles;
import tool.compet.core.DkLogcats;

/**
 * Utility class for Bitmap.
 * Ref: https://developer.android.com/codelabs/advanced-android-kotlin-training-shaders#6
 */
public final class DkBitmaps {
	public static long size(@Nullable Bitmap input) {
		if (input == null) {
			return 0L;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			return input.getAllocationByteCount();
		}
		return input.getByteCount();
	}

	/**
	 * See more: https://developer.android.com/topic/performance/graphics/load-bitmap
	 */
	public static int[] getDimension(File bitmapFile) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opts);

		return new int[] {opts.outWidth, opts.outHeight};
	}

	public static boolean store(Bitmap input, String filePath) throws IOException {
		return store(input, new File(filePath));
	}

	public static boolean store(@NonNull Bitmap bitmap, File file) throws IOException {
		if (! file.exists()) {
			DkFiles.createFile(file);
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
		if (BuildConfig.DEBUG) {
			DkLogcats.info(DkBitmaps.class, "Loaded bitmap size: %d", size(res));
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
		if (BuildConfig.DEBUG) {
			DkLogcats.info(DkBitmaps.class, "Loaded bitmap size: %d", size(res));
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
		if (BuildConfig.DEBUG) {
			DkLogcats.info(DkBitmaps.class, "Loaded bitmap size: %d", size(res));
		}
		return res;
	}

	public static Bitmap decodeRegion(InputStream is, int left, int top, int right, int bottom) throws IOException {
		return decodeRegion(is, left, top, right, bottom, null);
	}

	public static Bitmap decodeRegion(InputStream is, int left, int top, int right, int bottom, BitmapFactory.Options opts) throws IOException {
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

		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
}
