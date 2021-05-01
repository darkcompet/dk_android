/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Method;

import tool.compet.core.DkStrings;

public abstract class DkSqliteMigration {
	/**
	 * Call this when new database was created.
	 */
	protected void onCreate(SQLiteDatabase db) throws Exception {
	}

	/**
	 * Call this when new version was updated.
	 * <p>
	 * Subclass must implement methods `migrate_X_to_Y()` for upgrading process when call this method,
	 * in here, `X` is some version, and `Y` is next of X version (X and Y are integers and not leading with zero).
	 *
	 * @param db         Sqlite database instance.
	 * @param oldVersion Old db version.
	 * @param newVersion New db version.
	 * @throws Exception Some error
	 */
	protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws Exception {
		Class<?> clazz = getClass();

		for (int version = oldVersion; version < newVersion; ++version) {
			String methodName = DkStrings.format("migrate_%d_to_%d", version, version + 1);
			Method method = clazz.getDeclaredMethod(methodName);
			method.setAccessible(true);
			method.invoke(this, db);
		}
	}
}
