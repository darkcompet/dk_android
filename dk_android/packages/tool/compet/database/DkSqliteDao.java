/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import tool.compet.core.BuildConfig;
import tool.compet.core.DkLogcats;
import tool.compet.database4j.DkColumnInfo;
import tool.compet.database4j.OwnGrammarHelper;
import tool.compet.database4j.TheDao;
import tool.compet.database4j.TheQueryBuilder;
import tool.compet.reflection4j.DkReflectionFinder;

import static tool.compet.database.MySqliteConst.ROWID;

/**
 * Data access object, you can execute commands to the table which be associated with this.
 */
public abstract class DkSqliteDao<M> extends TheDao<M> { // M: table model
	/**
	 * Requires readable database. Normally, this return read/write database instance,
	 * but for some problem (full disk...), the read only database was returned.
	 * So call this is maybe same with `getWritableDatabase()` in general.
	 *
	 * @throws SQLiteException If cannot open database.
	 */
	public abstract SQLiteDatabase getReadableDatabase() throws SQLiteException;

	/**
	 * Requires read/write database. This attempts to get write-database instance,
	 * but sometimes maybe failed since some problems (full disk...), so exception
	 * maybe raised.
	 *
	 * @throws SQLiteException If cannot open database for writing.
	 */
	public abstract SQLiteDatabase getWritableDatabase() throws SQLiteException;

	public void beginTransaction() {
		beginTransaction(getWritableDatabase());
	}

	/**
	 * Must call this to open a transaction.
	 */
	public void beginTransaction(SQLiteDatabase db) {
		getWritableDatabase().beginTransaction();
	}

	public void setTransactionSuccessful() {
		setTransactionSuccessful(getWritableDatabase());
	}

	/**
	 * Call this to commit transaction. Otherwise, transaction will be auto rollbacked.
	 */
	public void setTransactionSuccessful(SQLiteDatabase db) {
		db.setTransactionSuccessful();
	}

	public void endTransaction() {
		endTransaction(getWritableDatabase());
	}

	/**
	 * Must call this at finally block.
	 */
	public void endTransaction(SQLiteDatabase db) {
		db.endTransaction();
	}

	protected static final MySqliteGrammar grammar = new MySqliteGrammar();

	@Override
	public TheQueryBuilder<M> newQuery() {
		return new TheSqliteQueryBuilder<>(this, grammar, tableName(), modelClass());
	}

	@Override
	public TheQueryBuilder<M> newQuery(String table) {
		return new TheSqliteQueryBuilder<>(this, grammar, table, modelClass());
	}

	@Override
	public <T> TheQueryBuilder<T> newQuery(Class<T> modelClass) {
		return new TheSqliteQueryBuilder(this, grammar, tableName(), modelClass);
	}

	@Override
	public <T> TheQueryBuilder<T> newQuery(String table, Class<T> modelClass) {
		return new TheSqliteQueryBuilder(this, grammar, table, modelClass);
	}

	public Cursor rawQuery(String query) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "rawQuery: " + query);
		}
		return getReadableDatabase().rawQuery(query, null);
	}

	@NonNull
	@Override
	public List<M> rawQuery(String query, Class<M> modelClass) {
		Cursor cursor = this.rawQuery(query);
		if (cursor == null) {
			return Collections.emptyList();
		}

		final List<M> result = new ArrayList<>();
		try {
			if (cursor.moveToFirst()) {
				do {
					result.add(MySqliteHelper.row2obj(cursor, modelClass));
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
	public void execQuery(String query) {
		if (BuildConfig.DEBUG) {
			DkLogcats.info(this, "execQuery: " + query);
		}
		getWritableDatabase().execSQL(query);
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
		// In Sqlite, truncate is same with delete-all since when clear table, autoincrement key will also be reset.
		clear();
	}

	@Override
	public long insert(Object model) {
		return insert(model, null);
	}

	/**
	 * Conveniently insert with a model. We separate model via primary keys.
	 * Given model MUST contains primary keys and some fillable fields for insertion.
	 *
	 * @param model A db model which contains `DkColumnInfo` annotation in each field.
	 * @param fillable When null is passed, we check with default fillable defined in `DkColumnInfo`.
	 */
	@Override
	public long insert(Object model, @Nullable String[] fillable) {
		return newQuery().insert(model, fillable);
	}

	@Override
	public long lastInsertRowId() {
		long insertRowId = -1;
		Cursor cursor = rawQuery("select last_insert_rowid()");
		if (cursor.moveToFirst()) {
			insertRowId = cursor.getLong(0);
		}
		cursor.close();

		return insertRowId;
	}

	@Override
	public void update(Object model) {
		this.update(model, null);
	}

	/**
	 * Conveniently update with a model. We separate model via primary keys.
	 * Given model MUST contains primary keys and some fillable fields for updating.
	 *
	 * @param model A db model which contains `DkColumnInfo` annotation in each field.
	 * @param fillable When null is passed, we check with default fillable defined in `DkColumnInfo`.
	 */
	@Override
	public void update(Object model, @Nullable String[] fillable) {
		TheQueryBuilder<M> queryBuilder = newQuery();

		ArrayMap<String, Object> conditions = requireUpdateConditions(model);
		for (int index = conditions.size() - 1; index >= 0; --index) {
			queryBuilder.where(conditions.keyAt(index), conditions.valueAt(index));
		}

		queryBuilder.update(model, fillable);
	}

	@Override
	public void upsert(Object model) {
		upsert(model, null);
	}

	@Override
	public void upsert(Object model, @Nullable String[] fillable) {
		// Refer when impl: https://www.sqlitetutorial.net/sqlite-replace-statement/

		//        INSERT INTO tmp (email, username) VALUES ('mail1', 'name1')
		//        ON CONFLICT (email, username)
		//        DO UPDATE SET email='kkkk3'

		// TODO: impl it

		String insertFields = "`email`, `telno`";
		String insertValues = "'test@g.c', `0923919232`";
		String pkList = "`username`, `email`";
		String updateSet = "`email` = 'test@g.c', `telno` = '098191020020'";

		String upsertQuery = "insert into" +
			" " + OwnGrammarHelper.wrapName(tableName()) +
			" (" + insertFields + ")" +
			" values " + insertValues +
			" on conflict (" + pkList + ")" +
			" do update set " + updateSet;

		execQuery(upsertQuery.trim());
	}

	@Override
	public boolean empty() {
		// Auto-increment PK has 4 equivalent names: `rowid`, `_rowid_`, `oid` and `id` (our curstom pk)
		return rawQuery("select `_rowid_` from " + OwnGrammarHelper.wrapName(tableName()) + " limit 1").getCount() == 0;
	}

	@Override
	public long count() {
		long rowCount = 0;
		Cursor cursor = rawQuery("select count(`_rowid_`) from " + OwnGrammarHelper.wrapName(tableName()));
		if (cursor.moveToFirst()) {
			rowCount = cursor.getLong(0);
		}
		cursor.close();
		return rowCount;
	}

	// region Private

	private ArrayMap<String, Object> requireUpdateConditions(Object model) {
		ArrayMap<String, Object> conditions = new ArrayMap<>();

		Class modelClass = model.getClass();
		List<Field> fields = DkReflectionFinder.getIns().findFields(modelClass, DkColumnInfo.class);

		for (Field field : fields) {
			try {
				DkColumnInfo colInfo = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class));
				if (colInfo.primaryKey()) {
					conditions.put(colInfo.name(), field.get(model));
				}
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		if (conditions.size() == 0) {
			throw new RuntimeException("Invalid model, must contain pk fields");
		}

		return conditions;
	}

	// endregion Private
}
