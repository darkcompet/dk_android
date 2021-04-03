/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

abstract class MyAndroidSqliteConnection extends MyDatabaseConnection {
	protected abstract SQLiteDatabase getReadableDatabase();

	protected abstract SQLiteDatabase getWritableDatabase();

	// Android sqlite own method
	protected Cursor rawQuery(String query) {
		if (DEBUG) {
			DkLogs.info(this, "rawQuery: " + query);
		}
		return getReadableDatabase().rawQuery(query, null);
	}

	@Override
	protected <M> List<M> rawQuery(String query, Class<M> modelClass) {
		List<M> result = new ArrayList<>();
		Cursor cursor = this.rawQuery(query);

		if (cursor == null) {
			return result;
		}
		try {
			if (cursor.moveToFirst()) {
				do {
					result.add(MyAndroidSqliteHelper.row2obj(cursor, modelClass));
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
	protected void execQuery(String query) {
		if (DEBUG) {
			DkLogs.info(this, "execQuery: " + query);
		}
		getWritableDatabase().execSQL(query);
	}
}
