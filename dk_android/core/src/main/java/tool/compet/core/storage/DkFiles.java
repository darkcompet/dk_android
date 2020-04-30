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

package tool.compet.core.storage;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import tool.compet.core.constant.DkConst;
import tool.compet.core.util.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * This class, provides common basic operations on file, directory.
 */
public class DkFiles {
	public static File getInternalDir(Context context) {
		return context.getFilesDir();
	}

	public static File getExternalDir() {
		return Environment.getExternalStorageDirectory();
	}

	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED);
	}

	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY));
	}

	public static boolean createFileDeeply(String filePath) throws IOException {
		return createFileDeeply(new File(filePath));
	}

	/**
	 * @return true if and only if file was created, otherwise false.
	 */
	public static boolean createFileDeeply(File file) throws IOException {
		if (!file.exists()) {
			File parent = file.getParentFile();

			if (!parent.exists() && parent.mkdirs()) {
				if (DEBUG) {
					DkLogs.log(DkFiles.class, "Created new directory: " + parent.getPath());
				}
			}
			return file.createNewFile();
		}
		return false;
	}

	public static boolean createDirDeeply(String dirPath) {
		return createDirDeeply(new File(dirPath));
	}

	/**
	 * @return true if and only if directory was created, otherwise false.
	 */
	public static boolean createDirDeeply(File dir) {
		if (!dir.exists()) {
			File parent = dir.getParentFile();

			if (!parent.exists() && parent.mkdirs()) {
				if (DEBUG) {
					DkLogs.log(DkFiles.class, "Created new directory: " + parent.getPath());
				}
			}
			return dir.mkdir();
		}
		return false;
	}

	public static boolean delete(String filePath) {
		return delete(new File(filePath));
	}

	/**
	 * Delete file or directory. Note that, Java does not delete dirty folder,
	 * so we will delete recursively folders on the path of this file.
	 */
	public static boolean delete(File file) {
		if (file == null || !file.exists()) {
			return true;
		}
		if (!file.isDirectory()) {
			return file.delete();
		}

		File[] children = file.listFiles();

		if (children != null) {
			for (File child : children) {
				delete(child);
			}
		}
		return file.delete();
	}

	public static void save(byte[] data, String filePath, boolean append) throws IOException {
		createFileDeeply(filePath);

		OutputStream os = new FileOutputStream(filePath, append);
		os.write(data);
		os.close();
	}

	public static void save(String data, String filePath, boolean append) throws IOException {
		save(data == null ? "".getBytes() : data.getBytes(), filePath, append);
	}

	public static String loadAsString(String filePath) throws IOException {
		createFileDeeply(filePath);

		String line;
		String ls = DkConst.LS;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(filePath));

		while ((line = br.readLine()) != null) {
			sb.append(line).append(ls);
		}

		br.close();

		return sb.toString();
	}
}
