/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

/**
 * Manage selection clause.
 */
class MySelection {
	private final MyGrammar grammar;

	// Common
	String type; // basic, raw

	// A. For basic column select
	String name; // columns, alias, ...

	// B. For raw select (sub query, function...)
	String raw; // function, sub query, ...
	String alias; // user_id, user_name, ...

	// A
	MySelection(MyGrammar grammar, String type, String name) {
		this.grammar = grammar;
		this.type = type;
		this.name = name;
	}

	// B
	MySelection(MyGrammar grammar, String type, String raw, String alias) {
		this.grammar = grammar;
		this.type = type;
		this.raw = raw;
		this.alias = alias;
	}

	String compile() {
		return grammar.compileSelect(this);
	}
}
