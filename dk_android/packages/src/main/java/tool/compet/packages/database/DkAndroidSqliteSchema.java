/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import tool.compet.core.BuildConfig;
import tool.compet.core.log.DkLogs;
import tool.compet.core.type.DkCallback;
import tool.compet.core.util.DkStrings;

/**
 * Android sqlite migration helper. Caller can use this to create, modify table.
 */
public class DkAndroidSqliteSchema extends TheSchema {
    /**
     * Create new table.
     */
    public static void create(SQLiteDatabase db, String tableName, DkCallback<TheSqliteColumnBuilder> builderCb) {
        // Get table definition from caller
        TheSqliteColumnBuilder builder = new TheSqliteColumnBuilder();
        builderCb.call(builder);

        // Build table definition
        List<String> colsDef = builder.compile();

        // Create table
        String query = DkStrings.format("create table if not exists %s (%s)",
            MyGrammarHelper.wrapName(tableName),
            DkStrings.join(", ", colsDef)
        );

        if (BuildConfig.DEBUG) {
            DkLogs.info(DkAndroidSqliteSchema.class, "Create table: %s", query);
        }

        db.execSQL(query);
    }

    /**
     * Modifies table attributes like: index (add, drop), column (add, drop)...
     */
    public static void table(SQLiteDatabase db, String tableName, DkCallback<TheSqliteTableModifier> modifierCb) {
        // Get commands from caller
        TheSqliteTableModifier modifier = new TheSqliteTableModifier(tableName);
        modifierCb.call(modifier);

        // Build and Run sql collection
        for (String query : modifier.compile()) {
            if (BuildConfig.DEBUG) {
                DkLogs.info(DkAndroidSqliteSchema.class, "Modify table: %s", query);
            }
            db.execSQL(query);
        }
    }
}
