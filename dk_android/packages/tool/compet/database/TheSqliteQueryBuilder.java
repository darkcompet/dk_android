/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import tool.compet.database4j.OwnGrammar;
import tool.compet.database4j.TheQueryBuilder;

/**
 * Android sqlite query builder.
 */
public class TheSqliteQueryBuilder<M> extends TheQueryBuilder<M> {
	TheSqliteQueryBuilder(DkSqliteDao<M> dao, OwnGrammar grammar, String tableName, Class<M> modelClass) {
		super(dao, grammar, tableName, modelClass);
	}
}
