/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 本クラスを継承するサブクラスはSingletonクラスであるべきです。
 */
public abstract class DkSQLiteOpenHelper extends SQLiteOpenHelper {
	protected final String name;
	protected final int version;

	protected DkSQLiteOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
		this.name = name;
		this.version = version;
	}

	public void deleteDatabase(Context context) {
		context.deleteDatabase(name);
	}
}
