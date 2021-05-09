/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.storage;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;

import tool.compet.core.graphics.DkBitmaps;
import tool.compet.core.DkFiles;
import tool.compet.core.DkLogs;
import tool.compet.core.DkStrings;

import static tool.compet.core.BuildConfig.DEBUG;

public class DkInternalStorage {
	private final Context context;

	public DkInternalStorage(Context context) {
		this.context = context;
	}

	private String makeFilePath(String folderName, String fileName) {
		folderName = DkStrings.trimMore(folderName, File.separatorChar);
		fileName = DkStrings.trimMore(fileName, File.separatorChar);

		if (DEBUG) {
			DkLogs.info(this, "Internal getFilesDir().getPath(): %s", context.getFilesDir().getPath());
			DkLogs.info(this, "Internal getDir().getPath()/folderName/fileName: " +
				DkStrings.join(File.separator, context.getDir(folderName, Context.MODE_PRIVATE).getPath(), fileName));
		}

		return DkStrings.join(File.separatorChar, context.getDir(folderName, Context.MODE_PRIVATE).getPath(), fileName);
	}

	public void save(byte[] data, String folderName, String fileName) throws IOException {
		DkFiles.save(data, makeFilePath(folderName, fileName), false);
	}

	public void save(Bitmap bitmap, String folderName, String fileName) throws IOException {
		DkBitmaps.save(bitmap, makeFilePath(folderName, fileName));
	}

	public boolean delete(String folderName, String fileName) {
		return DkFiles.delete(makeFilePath(folderName, fileName));
	}

	public String loadAsString(String folderName, String fileName) throws IOException {
		return DkFiles.loadAsString(makeFilePath(folderName, fileName));
	}

	public Bitmap loadAsBitmap(String folderName, String fileName) {
		return DkBitmaps.load(makeFilePath(folderName, fileName));
	}
}
