/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import java.util.Map;

import tool.compet.core.DkMaps;

/**
 * Mysql query builder.
 *
 * @author darkcompet
 */
public class TheMysqlQueryBuilder<M> extends TheQueryBuilder<M> { // M: model
	TheMysqlQueryBuilder(MyDatabaseConnection connection, MyGrammar grammar, String tableName, Class<M> modelClass) {
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
		return -1;
	}
}
