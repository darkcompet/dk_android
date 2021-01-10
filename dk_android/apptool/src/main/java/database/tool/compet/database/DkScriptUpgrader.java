/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.log.DkLogs;

public class DkScriptUpgrader {
    private static final String COMMENT = "--";
    private static final char END_SQL = ';';

    private final SQLiteDatabase db;

    public DkScriptUpgrader(SQLiteDatabase db) {
        this.db = db;
    }

    public void upgrade(List<String> lines) {
        for (String sql : parseSqls(lines)) {
            try {
                DkLogs.debug(this, "Upgrade sql: " + sql);
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        }
    }

    private List<String> parseSqls(List<String> lines) {
        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            if ( ! line.trim().startsWith(COMMENT)) {
                sb.append(line).append(' ');
            }
        }

        return nextSql(sb);
    }

    private List<String> nextSql(StringBuilder data) {
        List<String> res = new ArrayList<>();

        int start = 0;

        for (int end = 0, N = data.length(); end < N; ++end) {
            if (data.charAt(end) == END_SQL) {
                res.add(data.substring(start, end));
                start = end + 1;
            }
        }

        return res;
    }
}
