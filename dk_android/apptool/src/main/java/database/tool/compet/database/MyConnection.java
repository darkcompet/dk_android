/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.Cursor;

import java.util.List;

interface MyConnection {
    Cursor rawQuery(String query);
    <M> List<M> rawQuery(String query, Class<M> modelClass);
    void executeQuery(String query);
}
