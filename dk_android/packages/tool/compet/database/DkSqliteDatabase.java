/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tool.compet.core4j.DkStrings;
import tool.compet.database4j.OwnGrammarHelper;
import tool.compet.database4j.TheDatabase;

/**
 * This support handling Sqlite database.
 */
public abstract class DkSqliteDatabase extends SQLiteOpenHelper implements TheDatabase {
	protected final String name;
	protected final int version;

	protected DkSqliteDatabase(Context context, String name, int version) {
		super(context, name, null, version);
		this.name = name;
		this.version = version;
	}

	/**
	 * Delete current app database.
	 */
	public void deleteDatabase(Context context) {
		context.deleteDatabase(name);
	}

	/**
	 * Delete a table from current app database.
	 */
	public void dropTable(String tableName) {
		getWritableDatabase().execSQL("drop table if exists " + OwnGrammarHelper.wrapName(tableName));
	}

	/**
	 * @return Number of table inside current app database.
	 */
	public int tableCount() {
		int tableCount = -1;
		SQLiteDatabase db = getReadableDatabase();
		String query = "select count(*) from `sqlite_master` where `type` = 'table' and `name` != 'android_metadata' and `name` != 'sqlite_sequence'";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				tableCount = cursor.getInt(0);
			}
			cursor.close();
		}
		db.close();

		return tableCount;
	}

	/**
	 * Check existence of given table in current app database.
	 */
	public boolean hasTable(String tableName) {
		String tabName = OwnGrammarHelper.wrapName(tableName);
		String query = DkStrings.format("select count(`_rowid_`) from `sqlite_master` where `name` = %s and `type` = 'table'", tabName);

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		boolean exists = cursor.moveToFirst() && cursor.getInt(0) > 0;

		cursor.close();
		db.close();

		return exists;
	}
}
