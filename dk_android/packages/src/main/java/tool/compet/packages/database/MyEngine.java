/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

abstract class MyEngine {
	abstract MyDatabaseConnection connection();

	abstract MyGrammar grammar();
}
