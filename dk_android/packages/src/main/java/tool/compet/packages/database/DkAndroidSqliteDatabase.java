/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tool.compet.core.DkStrings;

/**
 * This support handling Sqlite database.
 */
public abstract class DkAndroidSqliteDatabase extends SQLiteOpenHelper {
	protected final String name;
	protected final int version;

	/**
	 * Be called when a query was fired and db was not existed.
	 * <p>
	 * At this time, db was locked, so caller can perform any creation query here within current thread.
	 *
	 * @param db readable and writable db instance
	 */
	@Override
	public abstract void onCreate(SQLiteDatabase db);

	/**
	 * Be called when a query was fired and new version was declared.
	 * <p>
	 * At this time, db was locked, so caller can perform any updates here within current thread.
	 * If something goes wrong, just throw exception, so all changes will be rollbacked automatically,
	 * and db will stil in old version.
	 *
	 * @param db         readable and writable db instance
	 * @param oldVersion old db version
	 * @param newVersion new db version
	 */
	@Override
	public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

	protected DkAndroidSqliteDatabase(Context context, String name, int version) {
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
		getWritableDatabase().execSQL("drop table if exists " + MyGrammarHelper.wrapName(tableName));
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
				;
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
		tableName = MyGrammarHelper.wrapName(tableName);
		String query = DkStrings.format("select count(`_rowid_`) from `sqlite_master` where `name` = %s and `type` = 'table'", tableName);

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		boolean exists = cursor.moveToFirst() && cursor.getInt(0) > 0;

		cursor.close();
		db.close();

		return exists;
	}
}
