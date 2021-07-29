/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import androidx.collection.ArraySet;

import java.util.Arrays;
import java.util.Set;

import tool.compet.database4j.OwnGrammar;

/**
 * Base grammar for making sql sentence.
 */
public class MySqliteGrammar extends OwnGrammar {
	// SQLite available operators
	private final Set<String> availableOperators = new ArraySet<>(Arrays.asList(
		"=", "<", ">", "<=", ">=", "<>", "!=",
		"like", "not like", "ilike",
		"&", "|", "<<", ">>"
	));

	@Override
	protected Set<String> availableOperators() {
		return availableOperators;
	}
}
