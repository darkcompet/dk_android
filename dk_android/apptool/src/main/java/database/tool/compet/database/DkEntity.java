/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

/**
 * All entities should extend this entity to work with the library.
 */
public abstract class DkEntity {
    public long id;
    public long created_at;
    public long updated_at;
}
