/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.Cursor;

import java.util.List;

import static tool.compet.database.MyConst.K_BASIC;

/**
 * Android sqlite query builder.
 */
public class TheSqliteQueryBuilder<M> extends TheQueryBuilder<M> {
	TheSqliteQueryBuilder(DkSqliteDao<M> dao, MyGrammar grammar, String tableName, Class<M> modelClass) {
		super(dao, grammar, tableName, modelClass);
	}

	@Override
	public long lastInsertRowId() {
		long insertRowId = -1;
		Cursor cursor = ((DkSqliteDao) dao).rawQuery("select last_insert_rowid()");
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
