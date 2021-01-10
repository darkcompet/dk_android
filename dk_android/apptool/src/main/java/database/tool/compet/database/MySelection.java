/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import static tool.compet.database.MyConst.K_BASIC;
import static tool.compet.database.MyConst.K_RAW;

class MySelection {
    private final DkGrammar grammar;

    // Common
    String type; // basic, raw

    // For column select
    String name; // columns, alias, ...

    // For raw select (sub query, function...)
    String raw; // function, sub query, ...
    String alias; // user_id, user_name, ...

    MySelection(DkGrammar grammar, String type, String name) {
        this.grammar = grammar;
        this.type = type;
        this.name = name;
    }

    MySelection(DkGrammar grammar, String type, String raw, String alias) {
        this.grammar = grammar;
        this.type = type;
        this.raw = raw;
        this.alias = alias;
    }

    String compile() {
        switch (this.type) {
            case K_BASIC: {
                return grammar.wrapName(this.name);
            }
            case K_RAW: {
                String raw = this.raw;
                if (this.alias != null) {
                    raw = "(" + raw + ") as " + grammar.wrapName(this.alias);
                }
                return raw;
            }
            default: {
                throw new RuntimeException("Invalid type: " + this.type);
            }
        }
    }
}
