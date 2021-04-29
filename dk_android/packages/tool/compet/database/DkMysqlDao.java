/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Data access object, you can execute commands to the table which be associated with this.
 */
public abstract class DkMysqlDao<M> extends TheDao<M> { // M: table model
	// Database
	protected abstract SQLiteDatabase getDatabase();

	// Each dao map with one table
	protected abstract String tableName();

	// Each dao map with one model of table
	protected abstract Class<M> modelClass();

	private final MyMySqlConnection connection = new MyMySqlConnection() {
		@Override
		void getDatabase() {
		}
	};
	private final MySqliteGrammar grammar = new MySqliteGrammar();

	@Override
	public TheQueryBuilder<M> newQuery() {
		return new TheMysqlQueryBuilder<>(connection, grammar, tableName(), modelClass());
	}

	@Override
	public TheQueryBuilder<M> newQuery(String table) {
		return new TheMysqlQueryBuilder<>(connection, grammar, table, modelClass());
	}

	@Override
	public <T> TheQueryBuilder<T> newQuery(Class<T> modelClass) {
		return new TheMysqlQueryBuilder<>(connection, grammar, tableName(), modelClass);
	}

	@Override
	public <T> TheQueryBuilder<T> newQuery(String table, Class<T> modelClass) {
		return new TheMysqlQueryBuilder<>(connection, grammar, table, modelClass);
	}
}
