/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Method;

import tool.compet.core.DkStrings;

/**
 * Subclass must implement methods `migrate_X_to_Y()` for upgrading process when call this method,
 * in here, `X` is some version, and `Y` is next of X version (X and Y are integers and not leading with zero).
 * For eg,. subclass should implement methods like: `migrate_0_to_1()`, `migrate_1_to_2()`, `migrate_2_to_3()`, ...
 * with a parameter `SQLiteDatabase db`.
 */
public abstract class DkSqliteMigration {
	/**
	 * For convenience, override this to customize migration method name.
	 * By default, method name should be `migrate_X_to_Y()`, where X, Y is fromVersion, toVersion.
	 *
	 * @param fromVersion For eg,. 0
	 * @param toVersion For eg,. 1
	 * @return Migration method name.
	 */
	protected String calcMigrationMethodName(int fromVersion, int toVersion) {
		return DkStrings.format("migrate_%d_to_%d", fromVersion, toVersion);
	}

	/**
	 * Call this when new database was created.
	 */
	public void onCreate(SQLiteDatabase db) throws Exception {
		migrate(db, 0, 1);
	}

	/**
	 * Call this when new version was updated.
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws Exception {
		migrate(db, oldVersion, newVersion);
	}

	/**
	 * Start migration database.
	 * Because at migration duration (create, upgrade, downgrade), Android framework has
	 * enabled transaction mode, so we should not perform commit or rollback here.
	 *
	 * @param db         Sqlite database instance.
	 * @param oldVersion Old db version.
	 * @param newVersion New db version.
	 * @throws Exception Some error
	 */
	private void migrate(SQLiteDatabase db, int oldVersion, int newVersion) throws Exception {
		Class<?> clazz = getClass();

		for (int version = oldVersion; version < newVersion; ++version) {
			String methodName = calcMigrationMethodName(version, version + 1);
			Method method = clazz.getDeclaredMethod(methodName, SQLiteDatabase.class);
			method.setAccessible(true);
			method.invoke(this, db);
		}
	}
}
