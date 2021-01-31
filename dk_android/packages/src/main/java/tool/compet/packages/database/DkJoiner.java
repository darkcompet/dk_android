/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.constant.DkConst;
import tool.compet.core.util.DkStrings;

import static tool.compet.packages.database.MyConst.K_AND;
import static tool.compet.packages.database.MyConst.K_BASIC;
import static tool.compet.packages.database.MyConst.K_EQ;
import static tool.compet.packages.database.MyConst.K_IN;
import static tool.compet.packages.database.MyConst.K_IS_NOT_NULL;
import static tool.compet.packages.database.MyConst.K_IS_NULL;
import static tool.compet.packages.database.MyConst.K_NOT_IN;
import static tool.compet.packages.database.MyConst.K_NOT_NULL;
import static tool.compet.packages.database.MyConst.K_NULL;
import static tool.compet.packages.database.MyConst.K_OR;
import static tool.compet.packages.database.MyConst.K_RAW;

class DkJoiner extends MyExpression {
    private List<MyExpression> __expressions;
    
    DkJoiner(MyGrammar grammar) {
        super(grammar, K_AND, K_BASIC);
    }
    
    private List<MyExpression> expressions() {
        return __expressions != null ? __expressions : (__expressions = new ArrayList<>());
    }

    public DkJoiner on(String first, String second) {
        expressions().add(new MyExpression(grammar, K_AND, K_BASIC, first, K_EQ, second));
        return this;
    }

    public DkJoiner on(String first, String operator, String second) {
        expressions().add(new MyExpression(grammar, K_AND, K_BASIC, first, operator, second));
        return this;
    }

    public DkJoiner where(String name, Object value) {
        expressions().add(new MyExpression(grammar, K_AND, K_BASIC, name, K_EQ, value));
        return this;
    }

    public DkJoiner orWhere(String name, Object value) {
        expressions().add(new MyExpression(grammar, K_OR, K_BASIC, name, K_EQ, value));
        return this;
    }

    public DkJoiner where(String name, String operator, Object value) {
        expressions().add(new MyExpression(grammar, K_AND, K_BASIC, name, operator, value));
        return this;
    }

    public DkJoiner orWhere(String name, String operator, Object value) {
        expressions().add(new MyExpression(grammar, K_OR, K_BASIC, name, operator, value));
        return this;
    }

    public DkJoiner whereNull(String name) {
        expressions().add(new MyExpression(grammar, K_AND, K_NULL, name, K_IS_NULL));
        return this;
    }

    public DkJoiner orWhereNull(String name) {
        expressions().add(new MyExpression(grammar, K_OR, K_NULL, name, K_IS_NULL));
        return this;
    }

    public DkJoiner whereNotNull(String name) {
        expressions().add(new MyExpression(grammar, K_AND, K_NOT_NULL, name, K_IS_NOT_NULL));
        return this;
    }

    public DkJoiner orWhereNotNull(String name) {
        expressions().add(new MyExpression(grammar, K_OR, K_NOT_NULL, name, K_IS_NOT_NULL));
        return this;
    }

    public DkJoiner whereIn(String name, Iterable values) {
        expressions().add(new MyExpression(grammar, K_AND, K_IN, name, K_IN, values));
        return this;
    }

    public DkJoiner orWhereIn(String name, Iterable values) {
        expressions().add(new MyExpression(grammar, K_OR, K_IN, name, K_IN, values));
        return this;
    }

    public DkJoiner whereNotIn(String name, Iterable values) {
        expressions().add(new MyExpression(grammar, K_AND, K_NOT_IN, name, K_NOT_IN, values));
        return this;
    }

    public DkJoiner orWhereNotIn(String name, Iterable values) {
        expressions().add(new MyExpression(grammar, K_OR, K_NOT_IN, name, K_NOT_IN, values));
        return this;
    }

    public DkJoiner whereRaw(String sql) {
        expressions().add(new MyExpression(grammar, K_AND, K_RAW, sql));
        return this;
    }

    public DkJoiner orWhereRaw(String sql) {
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
