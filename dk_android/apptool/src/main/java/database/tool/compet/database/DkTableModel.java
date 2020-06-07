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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import tool.compet.core.security.DkUidGenerator;
import tool.compet.core.util.DkLogs;
import tool.compet.database.annotation.DkColumnInfo;

/**
 * All schema of each table inside database should extend this abstract schema.
 * Note that, we don't support Blob type, so assure type of each field
 * is one of: Integer, Real or String (see {@link tool.compet.database.helper.DkSqliteSupportedTypes}).
 */
public abstract class DkTableModel implements DkTableSchema {
	@Expose
	@SerializedName(COL_ID)
	@DkColumnInfo(primaryKey = true, notNull = true)
	private String id;

	public void issueId() {
		if (id != null) {
			DkLogs.complain(this, "ID was already issued.");
		}

		id = DkUidGenerator.generateRandomKey();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id == null) {
			DkLogs.complain(this, "Cannot set ID with null value.");
		}
		this.id = id;
	}
}
