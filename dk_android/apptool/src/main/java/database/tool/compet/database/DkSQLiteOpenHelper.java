/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 本クラスを継承するサブクラスはSingletonクラスであるべきです。
 */
public abstract class DkSQLiteOpenHelper extends SQLiteOpenHelper {
    protected final String name;
    protected final int version;

    protected DkSQLiteOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.name = name;
        this.version = version;
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(name);
    }
}
