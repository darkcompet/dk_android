/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.database.helper;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.util.DkLogs;

public class DkScriptUpgrader {
	private static final char COMMENT = '#';
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
			line = line.trim();

			if (line.length() > 0) {
				if (line.charAt(0) != COMMENT) {
					sb.append(line).append(' ');
				}
			}
		}

		return parseSqls(sb);
	}

	private List<String> parseSqls(StringBuilder data) {
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
