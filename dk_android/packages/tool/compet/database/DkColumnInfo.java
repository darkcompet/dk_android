/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates this into column which maps with table column for CRUD operations.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DkColumnInfo {
	// Column name (can be real table column or virtual table column)
	String name();

	// Primary key (unique)
	boolean primaryKey() default false;

	// Indicate this column can be upserted (true: can upsert, false: cannot upsert)
	boolean fillable() default false;
}