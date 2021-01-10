/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import tool.compet.core.util.DkStrings;

import static tool.compet.database.MyConst.K_BASIC;
import static tool.compet.database.MyConst.K_IN;
import static tool.compet.database.MyConst.K_NOT_IN;
import static tool.compet.database.MyConst.K_NOT_NULL;
import static tool.compet.database.MyConst.K_NULL;

/**
 * Hold operation info to express comparasion.
 *
 * For eg,. "`user`.`id` <= 100", "`user.name` is not null"
 */
class MyExpression {
    protected final DkGrammar grammar;

    protected String type; // basic, null, notnull, in, notin, raw
    protected String logic; // and, or
    
    protected String name; // user.id as user_id, or raw query
    protected String operator; // =, <, >, ...
    protected Object value; // primitive or iterable

    MyExpression(DkGrammar grammar, String type, String logic) {
        this.grammar = grammar;
        this.type = type;
        this.logic = logic;
    }
    
    MyExpression(DkGrammar grammar, String type, String logic, String name) {
        this(grammar, type, logic);
        this.name = name;
    }

    MyExpression(DkGrammar grammar, String type, String logic, String name, String operator) {
        this(grammar, type, logic, name);
        this.operator = operator;
    }

    MyExpression(DkGrammar grammar, String type, String logic, String name, String operator, Object value) {
        this(grammar, type, logic, name, operator);
        this.value = value;
    }

    /**
     * Compile to build expression (condition) for given info.
     * @return Expression like "and `user`.`name` is not null"
     */
    protected String compile() {
        return logic + ' ' + compileWithoutLogic();
    }

    /**
     * Compile to build expression (condition) for given info without logic-appending.
     * @return Expression like "`user`.`name` is not null"
     */
    protected String compileWithoutLogic() {
        String name = grammar.wrapName(this.name); // user.name as user_name

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
                String joined_values = grammar.wrapJoinedValues((Iterable) value);
                return DkStrings.format("%s %s (%s)", name, operator, joined_values);
            }
            default: {
                throw new RuntimeException("Invalid type: " + type);
            }
        }
    }
}
