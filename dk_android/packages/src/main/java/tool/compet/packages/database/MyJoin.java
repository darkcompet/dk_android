/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import tool.compet.core.util.DkStrings;

class MyJoin {
    private final MyGrammar grammar;
    private final String joinType; // left, inner, right
    private final String joinTable; // user_detail

    // A. Single join on
    private String first;
    private String operator;
    private String second;

    // B. Multiple join on
    private DkJoiner joiner;

    // A
    MyJoin(MyGrammar grammar, String joinType, String joinTable, String first, String operator, String second) {
        this.grammar = grammar;
        this.joinType = joinType;
        this.joinTable = joinTable;
        this.first = first;
        this.operator = operator;
        this.second = second;
    }

    // B
    MyJoin(MyGrammar grammar, String joinType, String joinTable, DkJoiner joiner) {
        this.grammar = grammar;
        this.joinType = joinType;
        this.joinTable = joinTable;
        this.joiner = joiner;
    }

    /**
     * For eg,. this builds below clauses:
     * 1. left join `event` on `user`.`id` = `event`.`user_id` and `user`.`rank` >= `event`.`level`
     * 2. right join `event` on `user`.`id` = `event`.`user_id` or `user`.`rank` <= `event`.`level`
     * 3. inner join `event` on `user`.`id` = `event`.`user_id` and `user`.`rank` != `event`.`level`
     */
    String compile() {
        String tableName = grammar.wrapName(joinTable);
        String onCondition = makeCondition();
        return DkStrings.format("%s join %s on %s", joinType, tableName, onCondition);
    }

    private String makeCondition() {
        if (joiner != null) {
            return joiner.compile();
        }
        return grammar.wrapName(first) + ' ' + operator + ' ' + grammar.wrapName(second);
    }
}
