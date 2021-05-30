/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.storage;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import tool.compet.core.DkFiles;
import tool.compet.core.DkStrings;
import tool.compet.core.graphics.DkBitmaps;

public class DkInternalStorage {
	private static final int SCOPE_FILES = 1;
	private static final int SCOPE_CACHE = 2;
	private static final int SCOPE_CUSTOM = 3;

	private final int scope;
	private String dirName;

	private DkInternalStorage(int scope) {
		this.scope = scope;
	}

	private DkInternalStorage(int scope, String dirName) {
		this.scope = scope;
		this.dirName = DkStrings.trimMore(dirName, File.separatorChar);
	}

	/**
	 * Handle files under `files` directory in internal storage.
	 */
	public static DkInternalStorage filesDirScope() {
		return new DkInternalStorage(SCOPE_FILES);
	}

	/**
	 * Handle files under `cache` directory in internal storage.
	 */
	public static DkInternalStorage cacheDirScope() {
		return new DkInternalStorage(SCOPE_CACHE);
	}

	/**
	 * Handle files under your defined `dirName` in internal storage.
	 *
	 * @param dirName For eg,. `images
	 */
	public static DkInternalStorage customDirScope(@NonNull String dirName) {
		return new DkInternalStorage(SCOPE_CUSTOM, dirName);
	}

	/**
	 * Store data under given `dirName`.
	 * @param relativeFilePath Relative file path from given `dirName`, for eg,. `app/debug/avatar.png`
	 */
	public void store(Context context, String data, String relativeFilePath) throws Exception {
		DkFiles.store(data, calcFilePath(context, relativeFilePath), false);
	}

	public void store(Context context, byte[] data, String relativeFilePath) throws IOException {
		DkFiles.store(data, calcFilePath(context, relativeFilePath), false);
	}

	public void store(Context context, Bitmap bitmap, String relativeFilePath) throws IOException {
		DkBitmaps.store(bitmap, calcFilePath(context, relativeFilePath));
	}

	public boolean delete(Context context, String relativeFilePath) {
		return DkFiles.delete(calcFilePath(context, relativeFilePath));
	}

	public String loadAsString(Context context, String relativeFilePath) throws IOException {
		return DkFiles.loadAsString(calcFilePath(context, relativeFilePath));
	}

	public Bitmap loadAsBitmap(Context context, String relativeFilePath) {
		return DkBitmaps.load(calcFilePath(context, relativeFilePath));
	}

	private String calcFilePath(Context context, String relativeFilePath) {
		relativeFilePath = DkStrings.trimMore(relativeFilePath, File.separatorChar);

		if (scope == SCOPE_FILES) {
			return DkStrings.join(File.separator, context.getFilesDir().getPath(), relativeFilePath);
		}
		if (scope == SCOPE_CACHE) {
			return DkStrings.join(File.separatorChar, context.getCacheDir().getPath(), relativeFilePath);
		}

		// SCOPE_CUSTOM
		return DkStrings.join(File.separatorChar, context.getDir(dirName, Context.MODE_PRIVATE).getPath(), relativeFilePath);
	}
}
