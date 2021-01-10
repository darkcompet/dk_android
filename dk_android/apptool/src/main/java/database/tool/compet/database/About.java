/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.sqlite.SQLiteDatabase;

public class About {
    void readme() {
        // 1. Not need implement any other package, just embed to the app
    }

    void foo() {
        new Dao().foo();
        new Dao().find(1);
        new Dao().first(1);
        new Dao().insert(null);
        new Dao().update(null, null);
        new Dao().delete(1);
    }

    static class Model extends DkEntity {
    }

    static class Dao extends DkDao<Model> {
        @Override
        protected String getTableName() {
            return "user";
        }

        @Override
        protected Class<Model> getModelClass() {
            return Model.class;
        }

        @Override
        public SQLiteDatabase getReadableDatabase() {
            return null;
        }

        @Override
        public SQLiteDatabase getWritableDatabase() {
            return null;
        }

        void foo() {
            newQuery().where("id", 1).delete();
        }
    }
}
