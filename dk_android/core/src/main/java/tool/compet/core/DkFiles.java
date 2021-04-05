/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This class, provides common basic operations on file, directory.
 */
public class DkFiles {
	// region Java core

	public static boolean createFile(String filePath) {
		return createFile(new File(filePath));
	}

	/**
	 * Create file deeply (include its parent directory if not exist) if not exist.
	 *
	 * @return true if file was existed, or new file was created. Otherwise false.
	 */
	public static boolean createFile(File file) {
		if (file.exists()) return true;

		try {
			File parent = file.getParentFile();

			if (parent != null && ! parent.exists() && parent.mkdirs()) {
				if (BuildConfig.DEBUG) {
					DkLogs.info(DkFiles.class, "Created new directory: " + parent.getPath());
				}
			}
			return file.createNewFile();
		}
		catch (Exception e) {
			return false;
		}
	}

	public static boolean createDir(String dirPath) {
		return createDir(new File(dirPath));
	}

	/**
	 * Create directory deeply (include its parent directory if not exist) if not exist.
	 *
	 * @return true if new directory was created or given directory was existed, otherwise false.
	 */
	public static boolean createDir(File dir) {
		if (dir.exists()) return true;

		File parent = dir.getParentFile();

		if (parent != null && ! parent.exists() && parent.mkdirs()) {
			if (BuildConfig.DEBUG) {
				DkLogs.info(DkFiles.class, "Created new directory: " + parent.getPath());
			}
		}
		return dir.mkdir();
	}

	public static boolean delete(String filePath) {
		return delete(new File(filePath));
	}

	/**
	 * Delete file or directory. Note that, Java does not delete dirty folder,
	 * so first, we need delete dirs on the parent path of this file.
	 * @return true if file not exist or file was deleted. Otherwise false.
	 */
	public static boolean delete(File file) {
		if (file == null || ! file.exists()) {
			return true;
		}
		if (! file.isDirectory()) {
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

	public static void save(String utf8Chars, String filePath, boolean append) throws IOException {
		save(utf8Chars == null ? "".getBytes() : utf8Chars.getBytes(), filePath, append);
	}

	/**
	 * Save data to the file.
	 */
	public static void save(byte[] data, String filePath, boolean append) throws IOException {
		createFile(filePath);

		OutputStream os = new FileOutputStream(filePath, append);
		os.write(data);
		os.close();
	}

	public static String loadAsString(String filePath) throws IOException {
		createFile(filePath);

		String line;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(filePath));

		while ((line = br.readLine()) != null) {
			sb.append(line).append(DkConst.LS);
		}

		br.close();

		return sb.toString();
	}

	public static byte[] loadAsBytes(String filePath) {
		try {
			createFile(filePath);
			return loadAsBytes(new FileInputStream(filePath));
		}
		catch (FileNotFoundException e) {
			return null;
		}
	}

	public static byte[] loadAsBytes(InputStream is) {
		try {
			int capacity = 2 << 13;
			DkByteArrayList result = new DkByteArrayList(capacity);
			byte[] buffer = new byte[capacity];

			while (is.read(buffer) != -1) {
				result.addAll(buffer);
			}

			return result.toArray();
		}
		catch (Exception e) {
			return null;
		}
	}

	public static BufferedReader newUtf8Reader(File file) throws Exception {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
	}

	public static BufferedWriter newUtf8Writer(File file) throws Exception {
		return newUtf8Writer(file, false);
	}

	public static BufferedWriter newUtf8Writer(File file, boolean append) throws Exception {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), Charset.forName("UTF-8")));
	}

	public static List<File> collectFilesRecursively(File dir) {
		List<File> result = new ArrayList<>();
		File[] chilren = dir.listFiles();

		if (chilren != null) {
			for (File child : chilren) {
				if (child.isFile()) {
					result.add(child);
				}
				else {
					result.addAll(collectFilesRecursively(child));
				}
			}
		}

		return result;
	}

	/**
	 * @param file a file, eg,. welcome.bk.txt
	 * @return name of this file without extension, eg,. welcome.bk
	 */
	public static String filename(File file) {
		String name = file.getName();
		int endIndex = name.lastIndexOf(".");

		return endIndex < 0 ? name : name.substring(0, endIndex);
	}

	public static List<String> readFileAsUtf8Lines(File file) throws Exception {
		List<String> lines = new ArrayList<>();
		BufferedReader reader = newUtf8Reader(file);
		String readLine;

		while ((readLine = reader.readLine()) != null) {
			lines.add(readLine);
		}

		reader.close();

		return lines;
	}

	public static String makePath(String... names) {
		if (names == null) {
			return DkConst.EMPTY_STRING;
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (String name : names) {
			if (first) {
				first = false;
				sb.append(name);
			}
			else {
				sb.append(File.separator).append(name);
			}
		}

		return sb.toString();
	}

	// endregion Java core

	// region Android core

	/**
	 * Returns internal root directory of current app.
	 */
	public static File getInternalDir(Context context) {
		return context.getFilesDir();
	}

	/**
	 * Returns external root directory of current device.
	 */
	public static File getExternalDir() {
		return Environment.getExternalStorageDirectory();
	}

	/**
	 * Check external storage is available to write.
	 */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * Check external storage is available for read.
	 */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY));
	}

	// endregion Android core
}
