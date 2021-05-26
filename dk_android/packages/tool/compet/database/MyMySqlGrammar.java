/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Base grammar for making sql sentence.
 */
public class MyMySqlGrammar extends MyGrammar {
	// SQLite available operators
	private final Set<String> availableOperators = new HashSet<>(Arrays.asList(
		"=", "<", ">", "<=", ">=", "<>", "!=",
		"like", "not like", "ilike",
		"&", "|", "<<", ">>"
	));

	@Override
	protected Set<String> availableOperators() {
		return availableOperators;
	}
}