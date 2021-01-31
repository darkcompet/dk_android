/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import java.util.List;

// Database connection (read, write...)
abstract class MyDatabaseConnection {
    // Provide get query
    protected abstract <M> List<M> rawQuery(String query, Class<M> modelClass);

    // Provide insert, update... query
    protected abstract void execQuery(String query);
}
