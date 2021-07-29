/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import tool.compet.BuildConfig;
import tool.compet.core.DkLogcats;
import tool.compet.core4j.DkRunner1;
import tool.compet.core4j.DkStrings;
import tool.compet.database4j.OwnGrammarHelper;
import tool.compet.database4j.TheSchema;

/**
 * Android sqlite migration helper. Use to create, modify table.
 * For eg,. add index, remove index, add column, drop column...
 */
public class DkSqliteSchemaHelper extends TheSchema {
	/**
	 * Create new table.
	 */
	public static void create(SQLiteDatabase db, String tableName, DkRunner1<TheSqliteColumnBuilder> builderCb) {
		// Get table definition from caller
		TheSqliteColumnBuilder builder = new TheSqliteColumnBuilder();
		builderCb.run(builder);

		// Build table definition
		List<String> colsDef = builder.compile();

		// Create table
		String query = DkStrings.format("create table if not exists %s (%s)",
			OwnGrammarHelper.wrapName(tableName),
			DkStrings.join(", ", colsDef)
		);

		if (BuildConfig.DEBUG) {
			DkLogcats.info(DkSqliteSchemaHelper.class, "Create table: %s", query);
		}

		db.execSQL(query);
	}

	/**
	 * Modifies table attributes like: index (add, drop), column (add, drop)...
	 */
	public static void table(SQLiteDatabase db, String tableName, DkRunner1<TheSqliteTableModifier> modifierCb) {
		// Get commands from caller
		TheSqliteTableModifier modifier = new TheSqliteTableModifier(tableName);
		modifierCb.run(modifier);

		// Build and Run sql collection
		for (String query : modifier.compile()) {
			if (BuildConfig.DEBUG) {
				DkLogcats.info(DkSqliteSchemaHelper.class, "Modify table: %s", query);
			}
			db.execSQL(query);
		}
	}
}
