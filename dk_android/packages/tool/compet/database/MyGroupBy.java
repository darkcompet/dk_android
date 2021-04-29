/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

class MyGroupBy {
	private final MyGrammar grammar;

	String type;
	String name; // column or raw sql

	MyGroupBy(MyGrammar grammar, String type, String name) {
		this.grammar = grammar;
		this.type = type;
		this.name = name;
	}

	String compile() {
		switch (this.type) {
			case "basic":
				return this.grammar.wrapName(this.name);
			case "raw":
				return this.name;
			default:
				throw new RuntimeException("Invalid type: " + this.type);
		}
	}
}
