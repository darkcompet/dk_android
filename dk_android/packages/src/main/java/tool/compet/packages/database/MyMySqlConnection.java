/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import java.util.List;

abstract class MyMySqlConnection extends MyDatabaseConnection {
    abstract void getDatabase();

    @Override
    protected <M> List<M> rawQuery(String query, Class<M> modelClass) {
        return null;
    }

    @Override
    protected void execQuery(String query) {
    }
}
