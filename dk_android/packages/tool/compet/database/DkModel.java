/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

/**
 * Subclass should extends this to listen trigger upsert from DAO.
 */
public abstract class DkModel {
	// Trigger before create insert-params
	public abstract void onInsert();

	// Trigger before create update-params
	public abstract void onUpdate();
}
