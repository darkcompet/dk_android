/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core4j.DkStrings;
import tool.compet.database4j.OwnGrammarHelper;

public class TheSqliteTableModifier {
	private final String tableName;
	private final List<String> orderedCommands = new ArrayList<>();

	TheSqliteTableModifier(String tableName) {
		this.tableName = tableName;
	}

	public TheSqliteTableModifier addIndex(String colName) {
		String tab_col_idx = OwnGrammarHelper.wrapName(DkStrings.format("%s_%s_idx", tableName, colName));
		String wrapped_tab_name = OwnGrammarHelper.wrapName(tableName);
		String wrapped_col_name = OwnGrammarHelper.wrapName(colName);
		orderedCommands.add(DkStrings.format("create index %s on %s (%s)", tab_col_idx, wrapped_tab_name, wrapped_col_name));
		return this;
	}

	public TheSqliteTableModifier removeIndex(String colName) {
		String index_name = OwnGrammarHelper.wrapName(DkStrings.format("%s_%s_idx", tableName, colName));
		orderedCommands.add(DkStrings.format("drop index if exists %s", index_name));
		return this;
	}

	List<String> compile() {
		return orderedCommands;
	}
}
