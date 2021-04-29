/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates this into column which is field in database for convenience CRUD operations.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DkColumnInfo {
	// column name (can be real table column or virtual table column)
	String name();

	// primary key (unique)
	boolean primaryKey() default false;

	// indicate this column can be updated
	boolean fillable() default false;
}