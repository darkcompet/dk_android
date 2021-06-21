/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

/**
 * Android sqlite query builder.
 */
public class TheSqliteQueryBuilder<M> extends TheQueryBuilder<M> {
	TheSqliteQueryBuilder(DkSqliteDao<M> dao, MyGrammar grammar, String tableName, Class<M> modelClass) {
		super(dao, grammar, tableName, modelClass);
	}
}
