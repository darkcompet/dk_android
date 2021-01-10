/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.storage;

import android.os.Environment;

import java.io.File;

public class DkExternalStorage {
    private DkExternalStorage() {
    }

    public File getDir() {
        return Environment.getExternalStorageDirectory();
    }
}
