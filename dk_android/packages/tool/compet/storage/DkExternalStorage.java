/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.storage;

import android.os.Environment;

import java.io.File;

public class DkExternalStorage {
	/**
	 * Check external storage is available to write.
	 */
	public static boolean isWritable() {
		String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * Check external storage is available for read.
	 */
	public static boolean isReadable() {
		String state = Environment.getExternalStorageState();
		return (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY));
	}

	/**
	 * Get root directory of external storage.
	 */
	public static File getDir() {
		return Environment.getExternalStorageDirectory();
	}
}
