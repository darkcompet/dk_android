/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import tool.compet.core.util.DkStrings;

import static tool.compet.database.MyConst.K_AND;
import static tool.compet.database.MyConst.K_BASIC;

class MyJoin extends MyExpression {
    // Single join on
    String joinType; // left, inner, right
    String joinTable; // user_detail

    // Multiple join on
    DkJoiner joiner;

    private MyJoin(DkGrammar grammar, String joinType, String joinTable) {
        super(grammar, K_BASIC, K_AND); // not need `type` and `logic`, but just set default values
        this.joinTable = joinTable;
        this.joinType = joinType;
    }

    MyJoin(DkGrammar grammar, String joinType, String joinTable, DkJoiner joiner) {
        this(grammar, joinType, joinTable);
        this.joiner = joiner;
    }

    MyJoin(DkGrammar grammar, String joinType, String joinTable, String name, String operator, Object value) {
        this(grammar, joinType, joinTable);
        this.name = name;
        this.operator = operator;
        this.value = value;
    }

    @Override
    protected String compile() {
        String tableName = grammar.wrapName(joinTable);
        String onCondition = joiner != null ? joiner.compile() : super.compileWithoutLogic();
        return DkStrings.format("%s join %s on %s", joinType, tableName, onCondition);
    }
}
