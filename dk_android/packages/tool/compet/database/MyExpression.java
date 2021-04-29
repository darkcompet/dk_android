/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import java.util.List;

import tool.compet.core.DkStrings;

import static tool.compet.database.MyConst.K_BASIC;
import static tool.compet.database.MyConst.K_IN;
import static tool.compet.database.MyConst.K_NOT_IN;
import static tool.compet.database.MyConst.K_NOT_NULL;
import static tool.compet.database.MyConst.K_NULL;

/**
 * Hold operation info to express comparasion.
 * <p>
 * For eg,. "`user`.`id` <= 100",
 * "`user`.`name` is not null"
 */
class MyExpression {
	protected final MyGrammar grammar;

	protected String logic; // and, or
	protected String type; // basic, null, not null, in, not in, raw
	protected String name; // user.id as user_id, or raw query
	protected String operator; // =, <, >, ...
	protected Object value; // primitive or iterable

	MyExpression(MyGrammar grammar, String logic, String type) {
		this.grammar = grammar;
		this.logic = logic;
		this.type = type;
	}

	MyExpression(MyGrammar grammar, String logic, String type, String name) {
		this(grammar, logic, type);
		this.name = name;
	}

	MyExpression(MyGrammar grammar, String logic, String type, String name, String operator) {
		this(grammar, logic, type, name);
		this.operator = operator;
	}

	MyExpression(MyGrammar grammar, String logic, String type, String name, String operator, Object value) {
		this(grammar, logic, type, name, operator);
		this.value = value;
	}

	/**
	 * Compile to build expression (condition) for given info.
	 *
	 * @return Expression like "and `user`.`name` is not null"
	 */
	protected String compile() {
		return logic + ' ' + compileWithoutLogic();
	}

	private String compileWithoutLogic() {
		String name = grammar.wrapName(this.name); // user.name as user_name
		Object value = MyGrammarHelper.toDbValue(this.value);

		switch (type) {
			case K_BASIC: {
				return DkStrings.format("%s %s %s", name, operator, grammar.wrapPrimitiveValue(value));
			}
			case K_NULL:
			case K_NOT_NULL: {
				return DkStrings.format("%s %s", name, operator);
			}
			case K_IN:
			case K_NOT_IN: {
				List<String> values = grammar.wrapPrimitiveValueList((Iterable<?>) value);
				return DkStrings.format("%s %s (%s)", name, operator, DkStrings.join(", ", values));
			}
			default: {
				throw new RuntimeException("Invalid type: " + type);
			}
		}
	}
}
