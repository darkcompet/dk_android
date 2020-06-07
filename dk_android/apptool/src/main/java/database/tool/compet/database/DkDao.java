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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.util.DkLogs;
import tool.compet.core.util.DkStrings;
import tool.compet.database.annotation.DkColumnInfo;
import tool.compet.database.helper.DkSqliteSupportedTypes;

import static tool.compet.database.DkExpress.eq;

/**
 * Data access object, you can execute commands to the table which be associated with this.
 * <p></p>
 * Subclass must provide tableName and modelType for us.
 * <p></p>
 * Override onCreate(), onUpgrade() to hear upgrading events.
 * <p></p>
 * This will use {@link DkQuery} to make a SQL sentence, but maybe you have to
 * write a complex query manually.
 */
public abstract class DkDao<T extends DkTableModel> implements DkTableSchema {
	protected String tableName;
	protected Class<T> modelClass;

	protected abstract String getTableName();
	protected abstract Class<T> getModelType();

	public abstract SQLiteDatabase getReadableDatabase();
	public abstract SQLiteDatabase getWritableDatabase();

	protected DkDao() {
		tableName = getTableName();
		modelClass = getModelType();
	}

	protected DkQuery newReader() {
		return DkQuery.newIns(getReadableDatabase());
	}

	protected DkQuery newWriter() {
		return DkQuery.newIns(getWritableDatabase());
	}

	public void onCreate(SQLiteDatabase db) throws Exception {
		StringBuilder creationScript = new StringBuilder();
		DkSqliteSupportedTypes typeHelper = new DkSqliteSupportedTypes();

		creationScript.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

		List<String> definitions = new ArrayList<>();

		List<Field> fields = DkReflectionFinder.getIns()
			.findFields(modelClass, SerializedName.class, true, false);

		for (Field field : fields) {
			String definition = field.getAnnotation(SerializedName.class).value();

			switch (typeHelper.getType(field.getType())) {
				case DkSqliteSupportedTypes.TYPE_STRING: {
					definition += " TEXT";
					break;
				}
				case DkSqliteSupportedTypes.TYPE_REAL: {
					definition += " REAL";
					break;
				}
				case DkSqliteSupportedTypes.TYPE_INTEGER: {
					definition += " INTEGER";
					break;
				}
				default: {
					throw new RuntimeException(DkStrings.format("Field %s.%s has type %s is not supported",
						modelClass.getName(), field.getName(), field.getType().getName()));
				}
			}

			if (field.isAnnotationPresent(DkColumnInfo.class)) {
				DkColumnInfo descriptionInfo = field.getAnnotation(DkColumnInfo.class);

				if (descriptionInfo.primaryKey()) {
					definition += " PRIMARY KEY";
				}
				if (descriptionInfo.notNull()) {
					definition += " NOT NULL";
				}
				if (descriptionInfo.autoIncrement()) {
					definition += " AUTOINCREMENT";
				}
			}

			definitions.add(definition);
		}

		creationScript.append(DkStrings.join(", ", definitions));
		creationScript.append(")");

		DkQuery.newIns(db).setSql(creationScript.toString()).exe();
	}

	public void onUpgrade(SQLiteDatabase db) throws Exception {
	}

	public void dropTable() throws Exception {
		newWriter().setSql("DROP TABLE IF EXISTS " + tableName).exe();
	}

	public boolean isEmpty() throws Exception {
		return getRowCount() == 0;
	}

	public void clear() throws Exception {
		newWriter().deleteFrom(tableName).exe();
	}

	public boolean isExists() throws Exception {
		Cursor cursor = newReader()
			.selectFrom("sqlite_master")
			.where(DkExpress.and(
				eq("name", tableName),
				eq("type", "table")))
			.raw();

		return cursor != null && cursor.getCount() > 0 && cursor.moveToFirst() && cursor.getInt(0) > 0;
	}

	public int getRowCount() throws Exception {
		return getCount(newReader().selectCount(tableName, COL_ID).raw());
	}

	public List<T> query(String id) throws Exception {
		return newReader()
			.selectFrom(tableName)
			.where(eq(COL_ID, id))
			.raw(modelClass);
	}

	public List<T> queryAll(DkExpress whereClause) throws Exception {
		return newReader()
			.selectFrom(tableName)
			.where(whereClause)
			.raw(modelClass);
	}

	public List<T> queryAll(String whereClause) throws Exception {
		return newReader()
				.selectFrom(tableName)
				.where(whereClause)
				.raw(modelClass);
	}

	public List<T> queryAll() throws Exception {
		return newReader().selectFrom(tableName).raw(modelClass);
	}

	public void update(DkTableModel model) throws Exception {
		update(model, eq(COL_ID, model.getId()).toString());
	}

	public void update(DkTableModel model, String whereClause) throws Exception {
		List<String> names = new ArrayList<>();
		List<Object> values = new ArrayList<>();

		List<Field> fields = DkReflectionFinder.getIns()
			.findFields(this.modelClass, SerializedName.class, true, false);

		for (Field field : fields) {
			if (!field.isAnnotationPresent(DkColumnInfo.class) ||
				!field.getAnnotation(DkColumnInfo.class).primaryKey()) {
				// update except primary key
				names.add(field.getAnnotation(SerializedName.class).value());
				values.add(field.get(model));
			}
		}

		newWriter()
			.update(tableName, names, values, whereClause)
			.exe();
	}

	public void insert(T model) throws Exception {
		if (model.getId() == null) {
			DkLogs.complain(this, "Must issue ID first.");
		}

		List<String> names = new ArrayList<>();
		List<Object> values = new ArrayList<>();

		List<Field> fields = DkReflectionFinder.getIns()
			.findFields(this.modelClass, SerializedName.class, true, false);

		for (Field field : fields) {
			names.add(field.getAnnotation(SerializedName.class).value());
			values.add(field.get(model));
		}

		newWriter()
			.insertInto(tableName, names, values)
			.exe();
	}

	public void upsert(T model) throws Exception {
		Cursor cursor = newReader()
			.selectCount(tableName, "*")
			.where(eq(COL_ID, model.getId()))
			.raw();

		if (getCount(cursor) > 0) {
			update(model);
		}
		else {
			insert(model);
		}
	}

	public int getCount(Cursor cursor) {
		int rowCnt = 0;

		if (cursor != null) {
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				rowCnt = cursor.getInt(0);
			}
			cursor.close();
		}

		return rowCnt;
	}

	public void deleteRows(String... ids) throws Exception {
		deleteRows(Arrays.asList(ids));
	}

	public void deleteRows(List<String> ids) throws Exception {
		if (ids != null && ids.size() > 0) {
			newWriter()
				.deleteFrom(tableName)
				.where(DkExpress.in(COL_ID, ids))
				.exe();
		}
	}
}
