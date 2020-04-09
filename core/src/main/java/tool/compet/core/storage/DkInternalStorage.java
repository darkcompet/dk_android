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
import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;

import tool.compet.core.graphic.DkBitmaps;
import tool.compet.core.util.DkStrings;
import tool.compet.core.util.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

public class DkInternalStorage {
	private static DkInternalStorage INS;

	private final Context appContext;

	private DkInternalStorage(Context appContext) {
		this.appContext = appContext;
	}

	public static void install(Context appContext) {
		if (INS == null) {
			INS = new DkInternalStorage(appContext);
		}
	}

	public static DkInternalStorage getIns() {
		if (INS == null) {
			DkLogs.complain(DkInternalStorage.class, "Must call install() first");
		}
		return INS;
	}

	private String makeFilePath(String folderName, String fileName) throws IOException {
		folderName = DkStrings.trimExtras(folderName, File.separatorChar);
		fileName = DkStrings.trimExtras(fileName, File.separatorChar);

		if (DEBUG) {
			DkLogs.log(this, "Internal getFilesDir().getPath(): %s", appContext.getFilesDir().getPath());
			DkLogs.log(this, "Internal getDir().getPath()/folderName/fileName: " +
				DkStrings.join(File.separator, appContext.getDir(folderName, Context.MODE_PRIVATE).getPath(), fileName));
		}

		return DkStrings.join(File.separatorChar, appContext.getDir(folderName, Context.MODE_PRIVATE).getPath(), fileName);
	}

	public void save(byte[] data, String folderName, String fileName) throws IOException {
		DkFiles.save(data, makeFilePath(folderName, fileName), false);
	}

	public void save(Bitmap bitmap, String folderName, String fileName) throws IOException {
		DkBitmaps.save(bitmap, makeFilePath(folderName, fileName));
	}

	public boolean delete(String folderName, String fileName) throws IOException {
		return DkFiles.delete(makeFilePath(folderName, fileName));
	}

	public String loadAsString(String folderName, String fileName) throws IOException {
		return DkFiles.loadAsString(makeFilePath(folderName, fileName));
	}

	public Bitmap loadAsBitmap(String folderName, String fileName) throws IOException {
		return DkBitmaps.load(makeFilePath(folderName, fileName));
	}
}
