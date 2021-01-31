/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.collection.ArrayMap;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import tool.compet.core.reflection.DkReflectionFinder;

import static tool.compet.packages.database.MyConst.ROWID;

/**
 * Data access object, you can execute commands to the table which be associated with this.
 */
public abstract class DkAndroidSqliteDao<M> extends TheDao<M> { // M: table model
    // Readable database
    protected abstract SQLiteDatabase getReadableDatabase();
    // Writable database
    protected abstract SQLiteDatabase getWritableDatabase();
    // Each dao map with one table
    protected abstract String tableName();
    // Each dao map with one model of table
    protected abstract Class<M> modelClass();

    private final MyAndroidSqliteConnection connection = new MyAndroidSqliteConnection() {
        @Override
        protected SQLiteDatabase getReadableDatabase() {
            return DkAndroidSqliteDao.this.getReadableDatabase();
        }

        @Override
        protected SQLiteDatabase getWritableDatabase() {
            return DkAndroidSqliteDao.this.getWritableDatabase();
        }
    };
    private final MySqliteGrammar grammar = new MySqliteGrammar();

    @Override
    public TheQueryBuilder<M> newQuery() {
        return new TheAndroidSqliteQueryBuilder<>(connection, grammar, tableName(), modelClass());
    }

    @Override
    public TheQueryBuilder<M> newQuery(String table) {
        return new TheAndroidSqliteQueryBuilder<>(connection, grammar, table, modelClass());
    }

    @Override
    public <T> TheQueryBuilder<T> newQuery(Class<T> modelClass) {
        return new TheAndroidSqliteQueryBuilder<>(connection, grammar, tableName(), modelClass);
    }

    @Override
    public <T> TheQueryBuilder<T> newQuery(String table, Class<T> modelClass) {
        return new TheAndroidSqliteQueryBuilder<>(connection, grammar, table, modelClass);
    }

    @Override
    public M find(long rowid) {
        return newQuery().where(ROWID, rowid).first();
    }

    @Override
    public void delete(long rowid) {
        newQuery().where(ROWID, rowid).delete();
    }

    @Override
    public void clear() {
        // Just delete all data
        newQuery().delete();
    }

    @Override
    public void truncate() {
        // Delete all data
        clear();

        // Reset autoincrement pk
        newQuery("sqlite_sequence").where("name", tableName()).delete();
    }

    @Override
    public long insert(Object model) {
        Map<String, Object> params = new ArrayMap<>();
        Class modelClass = model.getClass();
        List<Field> fields = DkReflectionFinder.getIns().findFields(modelClass, DkColumnInfo.class);

        for (Field field : fields) {
            try {
                DkColumnInfo colInfo = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class));
                // For pk: ignore
                if (colInfo.primaryKey()) {
                    continue;
                }
                String colName = colInfo.name();
                Object value = MyGrammarHelper.todbvalue(field.get(model));
                params.put(colName, value);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return newQuery().insert(params);
    }

    @Override
    public void update(Object model) {
        Map<String, Object> updates = new ArrayMap<>();
        Class modelClass = model.getClass();
        List<Field> fields = DkReflectionFinder.getIns().findFields(modelClass, DkColumnInfo.class);
        Map<String, Field> pk_field = new ArrayMap<>();

        for (Field field : fields) {
            try {
                DkColumnInfo colInfo = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class));
                // For pk: remember to build condition
                if (colInfo.primaryKey()) {
                    pk_field.put(colInfo.name(), field);
                    continue;
                }
                String colName = colInfo.name();
                Object value = field.get(model);
                updates.put(colName, value);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        TheQueryBuilder<M> queryBuilder = newQuery();

        if (pk_field.size() == 0) {
            throw new RuntimeException("Must contain at least 1 pk for update");
        }
        else {
            for (Map.Entry<String, Field> entry : pk_field.entrySet()) {
                try {
                    queryBuilder.where(entry.getKey(), entry.getValue().get(model));
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        queryBuilder.update(updates);
    }

    @Override
    // Refer when impl: https://www.sqlitetutorial.net/sqlite-replace-statement/
    public void upsert(Object model) {
//        INSERT INTO tmp (email, username) VALUES ('mail1', 'name1')
//        ON CONFLICT (email, username)
//        DO UPDATE SET email='kkkk3'

        String insertFields = "";
        String insertValues = "";
        String pkList = "";
        String updateSet = "";

        String upsertQuery = "insert into" +
            " " + MyGrammarHelper.wrapName(tableName()) +
            " (" + insertFields + ")" +
            " values " + insertValues +
            " on conflict (" + pkList + ")" +
            " do update set " + updateSet;

        newQuery().execute(upsertQuery.trim());
    }

    @Override
    public boolean isEmpty() {
        // Auto-increment PK has 4 equivalent names: `rowid`, `_rowid_`, `oid` and `id` (our curstom pk)
        return connection.rawQuery("select `_rowid_` from " + MyGrammarHelper.wrapName(tableName()) + " limit 1").getCount() == 0;
    }

    @Override
    public long count() {
        long rowCount = 0;
        Cursor cursor = connection.rawQuery("select count(`_rowid_`) from " + MyGrammarHelper.wrapName(tableName()));
        if (cursor.moveToFirst()) {
            rowCount = cursor.getLong(0);
        }
        cursor.close();
        return rowCount;
    }
}
