/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.storage;

import android.os.Environment;

import java.io.File;

public class DkExternalStorage {
    private static DkExternalStorage INS;

    private DkExternalStorage() {
    }

    public static DkExternalStorage getIns() {
        if (INS == null) {
            synchronized (DkExternalStorage.class) {
                if (INS == null) {
                    INS = new DkExternalStorage();
                }
            }
        }
        return INS;
    }

    public File getDir() {
        return Environment.getExternalStorageDirectory();
    }
}
