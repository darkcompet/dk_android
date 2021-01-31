/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

/**
 * Base dao for databases.
 */
public abstract class TheDao<M> { // M: table model
    public abstract TheQueryBuilder<M> newQuery();

    public abstract TheQueryBuilder<M> newQuery(String table);

    public abstract <T> TheQueryBuilder<T> newQuery(Class<T> modelClass);

    public abstract <T> TheQueryBuilder<T> newQuery(String table, Class<T> modelClass);

    public abstract M find(long rowid);

    public abstract void delete(long rowid);

    public abstract void clear();

    public abstract void truncate();

    public abstract long insert(Object model);

    public abstract void update(Object model);

    public abstract void upsert(Object model);

    public abstract boolean isEmpty();

    public abstract long count();
}
