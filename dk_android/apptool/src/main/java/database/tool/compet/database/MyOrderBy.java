/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

class MyOrderBy {
    private final DkGrammar grammar;

    String type;
    String name; // column or raw sql
    String direction;

    MyOrderBy(DkGrammar grammar, String type, String name, String direction) {
        this.grammar = grammar;
        this.type = type;
        this.name = name;
        this.direction = direction;
    }

    String compile() {
        switch (this.type) {
            case "basic":
                return this.grammar.wrapName(this.name) + ' ' + this.direction;
            case "raw":
                return this.name;
            default:
                throw new RuntimeException("Invalid type: " + this.type);
        }
    }
}
