/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tool.compet.database.DkSqliteType;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DkColumnInfo {
    // column name in table
    String name();

    int type() default DkSqliteType.UNDEFINED;

    // whether this column is primary key
    boolean primaryKey() default false;

    // whether this column is not null
    boolean notNull() default false;

    // whether this column is auto-increment
    boolean autoIncrement() default false;

    // whether this column is indexed to speed up for selection (but down fow insert, update)
    boolean index() default false;
}
