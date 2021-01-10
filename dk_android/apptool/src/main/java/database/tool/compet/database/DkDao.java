/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import tool.compet.core.log.DkLogs;
import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.util.DkStrings;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * Data access object, you can execute commands to the table which be associated with this.
 * <p></p>
 * Subclass must provide tableName and modelType for us.
 * <p></p>
 * Override onCreate(), onUpgrade() to hear upgrading events.
 * <p></p>
 * This will use {@link DkQueryBuilder} to make a SQL sentence, but maybe you have to
 * write a complex query manually.
 */
public abstract class DkDao<M extends DkEntity> implements MyConnection {
    protected abstract String getTableName();
    protected abstract Class<M> getModelClass();
    public abstract SQLiteDatabase getReadableDatabase();
    public abstract SQLiteDatabase getWritableDatabase();

    protected final String tableName;
    protected final Class<M> modelClass;
    private final DkReflectionFinder reflectionFinder = DkReflectionFinder.getInstalledIns();

    protected DkDao() {
        this.tableName = getTableName();
        this.modelClass = getModelClass();
    }

    /**
     * Called when create new table.
     */
    public void onCreate(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder();
        builder.append("create table if not exists ").append('`').append(tableName).append('`').append(" (");

        List<String> definitions = new ArrayList<>();
        DkReflectionFinder reflectionFinder = DkReflectionFinder.getInstalledIns();
        List<Field> fields = reflectionFinder.findFields(modelClass, DkColumnInfo.class, true, false);

        for (Field field : fields) {
            DkColumnInfo columnInfo = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class));
            String definition = MyDaoHelper.calcColumnDefinition(columnInfo, field);

            definitions.add(definition);
        }

        builder.append(DkStrings.join(", ", definitions));
        builder.append(")");

        getWritableDatabase().execSQL(builder.toString());
    }

    /**
     * Called when upgrade the table.
     */
    public void onUpgrade(SQLiteDatabase db) {
    }

    protected DkQueryBuilder<M> newQuery() {
        return new DkQueryBuilder<>(this, tableName, modelClass);
    }

    protected <T> DkQueryBuilder<T> newQuery(Class<T> modelClass) {
        return new DkQueryBuilder<>(this, tableName, modelClass);
    }

    protected <T> DkQueryBuilder<T> newQuery(String tableName, Class<T> modelClass) {
        return new DkQueryBuilder<>(this, tableName, modelClass);
    }

    @Override
    public Cursor rawQuery(String query) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor;

        try {
            if (DEBUG) {
                DkLogs.info(this, "rawQuery: " + query);
            }
            database.beginTransaction();
            cursor = database.rawQuery(query, null);
            database.setTransactionSuccessful();
        }
        finally {
            database.endTransaction();
        }

        return cursor;
    }

    @Override
    public <M> List<M> rawQuery(String query, Class<M> modelClass) {
        List<M> result = new ArrayList<>();
        Cursor cursor = this.rawQuery(query);

        if (cursor == null) {
            return result;
        }
        try {
            if (cursor.moveToFirst()) {
                do {
                    result.add(MyDaoHelper.row2obj(cursor, modelClass));
                }
                while (cursor.moveToNext());
            }
        }
        finally {
            cursor.close();
        }

        return result;
    }

    @Override
    public void executeQuery(String query) {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            if (DEBUG) {
                DkLogs.info(this, "execQuery: " + query);
            }
            database.beginTransaction();
            database.execSQL(query);
            database.setTransactionSuccessful();
        }
        finally {
            database.endTransaction();
        }
    }

    public boolean hasTable() {
        String query = DkStrings.format("select * from sqlite_master where `name` = %s and `type` = 'table'", tableName);
        Cursor cursor = rawQuery(query);
        return cursor != null && cursor.getCount() > 0 && cursor.moveToFirst() && cursor.getInt(0) > 0;
    }

    public void drop() {
        getWritableDatabase().execSQL(DkStrings.format("drop table if exists `%s`", tableName));
    }

    public boolean isEmpty() {
        return count() == 0;
    }

    public void clear() {
        newQuery().delete();
    }

    public long count() {
        return newQuery().count();
    }

    public M first(long id) {
        return newQuery().where("id", id).first();
    }

    public M first(String idColName, long id) {
        return newQuery().where(idColName, id).first();
    }

    public List<M> find(long id) {
        return newQuery().where("id", id).get();
    }

    public List<M> find(String idColName, long id) {
        return newQuery().where(idColName, id).get();
    }

    public void update(DkEntity model) {
        update(model, null);
    }

    public void update(DkEntity model, @Nullable Set<String> excludeNames) {
        List<Field> fields = reflectionFinder.findFields(this.modelClass, DkColumnInfo.class, true, false);
        Map<String, Object> params = new ArrayMap<>();

        for (Field field : fields) {
            DkColumnInfo columnInfo = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class));
            String name = columnInfo.name();
            if (excludeNames != null && excludeNames.contains(name)) {
                continue;
            }
            // Update except primary key
            if (! columnInfo.primaryKey()) {
                try {
                    Object value = field.get(model);
                    params.put(name, value);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        newQuery().where("id", model.id).update(params);
    }

    public void insert(M model) {
        Map<String, Object> params = new ArrayMap<>();
        List<Field> fields = reflectionFinder.findFields(this.modelClass, DkColumnInfo.class, true, false);

        for (Field field : fields) {
            String name = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class)).name();
            Object value;
            try {
                value = field.get(model);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            params.put(name, value);
        }

        newQuery().insert(params);
    }

    public void upsert(M model) {
        Cursor cursor = rawQuery("select id from " + tableName + " where id = " + model.id);
        if (count(cursor) > 0) {
            update(model);
        }
        else {
            insert(model);
        }
    }

    private long count(Cursor cursor) {
        long rowCnt = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                rowCnt = cursor.getLong(0);
            }
            cursor.close();
        }

        return rowCnt;
    }

    public void delete(long id) {
        newQuery().where("id", id).delete();
    }

    public void delete(String idColName, long id) {
        newQuery().where(idColName, id).delete();
    }
}
