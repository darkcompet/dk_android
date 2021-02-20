/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.storage;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;

import tool.compet.core.graphic.DkBitmaps;
import tool.compet.core.log.DkLogs;
import tool.compet.core.util.DkStrings;

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
        folderName = DkStrings.trimMore(folderName, File.separatorChar);
        fileName = DkStrings.trimMore(fileName, File.separatorChar);

        if (DEBUG) {
            DkLogs.info(this, "Internal getFilesDir().getPath(): %s", appContext.getFilesDir().getPath());
            DkLogs.info(this, "Internal getDir().getPath()/folderName/fileName: " +
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
