/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Base dao for access databases.
 */
public abstract class TheDao<M> { // M: table model
	// Each dao map with one table
	public abstract String tableName();

	// Each dao map with one model of table
	public abstract Class<M> modelClass();

	public abstract TheQueryBuilder<M> newQuery();

	public abstract TheQueryBuilder<M> newQuery(String table);

	public abstract <T> TheQueryBuilder<T> newQuery(Class<T> modelClass);

	public abstract <T> TheQueryBuilder<T> newQuery(String table, Class<T> modelClass);

	public abstract List<M> rawQuery(String query, Class<M> modelClass);

	public abstract void execQuery(String query);

	public abstract M find(long rowid);

	public abstract void delete(long rowid);

	public abstract void clear();

	public abstract void truncate();

	// Trigger before insert
	public void onInsert(Object model) {
		if (model instanceof DkModel) {
			((DkModel) model).onInsert();
		}
	}
	
	public abstract long insert(Object model);

	public abstract long insert(Object model, @Nullable String[] fillable);

	public abstract long lastInsertRowId();

	// Trigger before update
	public void onUpdate(Object model) {
		if (model instanceof DkModel) {
			((DkModel) model).onUpdate();
		}
	}
	
	public abstract void update(Object model);

	public abstract void update(Object model, @Nullable String[] fillable);

	public abstract void upsert(Object model);

	public abstract void upsert(Object model, @Nullable String[] fillable);

	public abstract boolean empty();

	public abstract long count();
}
