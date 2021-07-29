/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import tool.compet.core.DkLogcats;
import tool.compet.core4j.DkMaths;
import tool.compet.core4j.DkTypeHelper;
import tool.compet.core4j.DkUtils;
import tool.compet.reflection4j.DkReflectionFinder;
import tool.compet.database4j.DkColumnInfo;

class MySqliteHelper {
	/**
	 * Convert row data in given `cursor` to object which has type is given model class.
	 *
	 * @throws RuntimeException When something happen
	 */
	static <M> M row2obj(Cursor cursor, Class<M> modelClass) {
		M model;
		try {
			model = modelClass.newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		List<Field> fields = DkReflectionFinder.getIns().findFields(modelClass, DkColumnInfo.class);
		if (fields.size() == 0) {
			DkUtils.complainAt(MySqliteHelper.class, "Must annotate some fields with `@DkColumnInfo` in `%s`", modelClass.getName());
		}

		for (Field field : fields) {
			try {
				String colName = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class)).name();
				int colIndex = cursor.getColumnIndex(colName);

				// Field is in model but NOT found in db -> caller does not query it
				if (colIndex < 0) {
					continue;
				}

				String value = cursor.getString(cursor.getColumnIndex(colName));
				Class<?> type = field.getType();

				switch (DkTypeHelper.typeMasked(type)) {
					case DkTypeHelper.TYPE_BOOLEAN_MASKED: {
						field.set(model, DkMaths.parseBoolean(value));
						break;
					}
					case DkTypeHelper.TYPE_SHORT_MASKED: {
						field.set(model, DkMaths.parseShort(value));
						break;
					}
					case DkTypeHelper.TYPE_INTEGER_MASKED: {
						field.set(model, DkMaths.parseInt(value));
						break;
					}
					case DkTypeHelper.TYPE_LONG_MASKED: {
						field.set(model, DkMaths.parseLong(value));
						break;
					}
					case DkTypeHelper.TYPE_FLOAT_MASKED: {
						field.set(model, DkMaths.parseFloat(value));
						break;
					}
					case DkTypeHelper.TYPE_DOUBLE_MASKED: {
						field.set(model, DkMaths.parseDouble(value));
						break;
					}
					case DkTypeHelper.TYPE_STRING_MASKED: {
						field.set(model, value);
						break;
					}
					default: {
						throw new RuntimeException("Invalid type: " + type.getName());
					}
				}
			}
			catch (Exception e) {
				DkLogcats.error(MySqliteHelper.class, e);
			}
		}
		return model;
	}
}
