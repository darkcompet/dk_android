/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.DkConst;
import tool.compet.core.DkStrings;

import static tool.compet.database.MyConst.K_AND;
import static tool.compet.database.MyConst.K_BASIC;
import static tool.compet.database.MyConst.K_EQ;
import static tool.compet.database.MyConst.K_IN;
import static tool.compet.database.MyConst.K_IS_NOT_NULL;
import static tool.compet.database.MyConst.K_IS_NULL;
import static tool.compet.database.MyConst.K_NOT_IN;
import static tool.compet.database.MyConst.K_NOT_NULL;
import static tool.compet.database.MyConst.K_NULL;
import static tool.compet.database.MyConst.K_OR;
import static tool.compet.database.MyConst.K_RAW;

class MyJoiner extends MyExpression {
	private List<MyExpression> __expressions;

	MyJoiner(MyGrammar grammar) {
		super(grammar, K_AND, K_BASIC);
	}

	private List<MyExpression> expressions() {
		return __expressions != null ? __expressions : (__expressions = new ArrayList<>());
	}

	MyJoiner on(String first, String second) {
		expressions().add(new MyExpression(grammar, K_AND, K_BASIC, first, K_EQ, second));
		return this;
	}

	MyJoiner on(String first, String operator, String second) {
		expressions().add(new MyExpression(grammar, K_AND, K_BASIC, first, operator, second));
		return this;
	}

	MyJoiner where(String name, Object value) {
		expressions().add(new MyExpression(grammar, K_AND, K_BASIC, name, K_EQ, value));
		return this;
	}

	MyJoiner orWhere(String name, Object value) {
		expressions().add(new MyExpression(grammar, K_OR, K_BASIC, name, K_EQ, value));
		return this;
	}

	MyJoiner where(String name, String operator, Object value) {
		expressions().add(new MyExpression(grammar, K_AND, K_BASIC, name, operator, value));
		return this;
	}

	MyJoiner orWhere(String name, String operator, Object value) {
		expressions().add(new MyExpression(grammar, K_OR, K_BASIC, name, operator, value));
		return this;
	}

	MyJoiner whereNull(String name) {
		expressions().add(new MyExpression(grammar, K_AND, K_NULL, name, K_IS_NULL));
		return this;
	}

	MyJoiner orWhereNull(String name) {
		expressions().add(new MyExpression(grammar, K_OR, K_NULL, name, K_IS_NULL));
		return this;
	}

	MyJoiner whereNotNull(String name) {
		expressions().add(new MyExpression(grammar, K_AND, K_NOT_NULL, name, K_IS_NOT_NULL));
		return this;
	}

	MyJoiner orWhereNotNull(String name) {
		expressions().add(new MyExpression(grammar, K_OR, K_NOT_NULL, name, K_IS_NOT_NULL));
		return this;
	}

	MyJoiner whereIn(String name, Iterable values) {
		expressions().add(new MyExpression(grammar, K_AND, K_IN, name, K_IN, values));
		return this;
	}

	MyJoiner orWhereIn(String name, Iterable values) {
		expressions().add(new MyExpression(grammar, K_OR, K_IN, name, K_IN, values));
		return this;
	}

	MyJoiner whereNotIn(String name, Iterable values) {
		expressions().add(new MyExpression(grammar, K_AND, K_NOT_IN, name, K_NOT_IN, values));
		return this;
	}

	MyJoiner orWhereNotIn(String name, Iterable values) {
		expressions().add(new MyExpression(grammar, K_OR, K_NOT_IN, name, K_NOT_IN, values));
		return this;
	}

	MyJoiner whereRaw(String sql) {
		expressions().add(new MyExpression(grammar, K_AND, K_RAW, sql));
		return this;
	}

	MyJoiner orWhereRaw(String sql) {
		expressions().add(new MyExpression(grammar, K_OR, K_RAW, sql));
		return this;
	}

	/**
	 * Compile multiple join conditions.
	 *
	 * @return for eg,. "`user`.`name` is null and `user`.`age` <= '20'"
	 */
	@Override
	protected String compile() {
		if (expressions().size() == 0) {
			return DkConst.EMPTY_STRING;
		}
		List<String> clauses = new ArrayList<>();

		for (MyExpression exp : expressions()) {
			clauses.add(exp.compile());
		}

		String joinedClauses = DkStrings.join(' ', clauses);
		joinedClauses = joinedClauses.replaceFirst("^(and|or)", "");

		return joinedClauses.trim();
	}
}
