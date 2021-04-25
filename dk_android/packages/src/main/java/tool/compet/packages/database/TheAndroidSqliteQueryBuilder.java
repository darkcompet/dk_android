/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import android.database.Cursor;

import java.util.List;
import java.util.Map;

import tool.compet.core.BuildConfig;
import tool.compet.core.DkLogs;
import tool.compet.core.DkMaps;

import static tool.compet.packages.database.MyConst.K_BASIC;

/**
 * Android sqlite query builder.
 *
 * @author darkcompet
 */
public class TheAndroidSqliteQueryBuilder<M> extends TheQueryBuilder<M> { // M: model
	TheAndroidSqliteQueryBuilder(MyDatabaseConnection connection, MyGrammar grammar, String tableName, Class<M> modelClass) {
		super(connection, grammar, tableName, modelClass);
	}

	/**
	 * Insert new row from given key-value map.
	 *
	 * @param params Map of insert key-value for the table
	 * @return Last inserted row id of current connection.
	 * @throws RuntimeException When invalid params
	 */
	@Override
	public long insert(Map<String, Object> params) {
		if (DkMaps.isEmpty(params)) {
			throw new RuntimeException("Cannot insert empty record");
		}
		String query = grammar.compileInsertQuery(tableName, params);
		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "Insert query: %s", query);
		}
		MyAndroidSqliteConnection sqliteConnection = (MyAndroidSqliteConnection) connection;
		sqliteConnection.execQuery(query);

		long insertRowId = -1;
		Cursor cursor = sqliteConnection.rawQuery("select last_insert_rowid()");
		if (cursor.moveToFirst()) {
			insertRowId = cursor.getLong(0);
		}
		cursor.close();

		return insertRowId;
	}

	@Override
	public long count() {
		selects().clear();
		selects().add(new MySelection(grammar, K_BASIC, "_rowid_"));
		List<M> rows = get();
		return rows == null ? 0 : rows.size();
	}
}
