/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.Cursor;

import androidx.collection.ArrayMap;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import tool.compet.core.constant.DkConst;
import tool.compet.core.helper.DkTypeHelper;
import tool.compet.core.log.DkLogs;
import tool.compet.core.math.DkMaths;
import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.util.DkStrings;

class MyDaoHelper {
    private static Map<Class<?>, Integer> class2type;

    static <M> M row2obj(Cursor cursor, Class<M> modelType) {
        M model;
        try {
            model = modelType.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        DkReflectionFinder reflectionFinder = DkReflectionFinder.getInstalledIns();
        List<Field> fields = reflectionFinder.findFields(modelType, DkColumnInfo.class, true, false);

        if (fields.size() == 0) {
            DkLogs.complain(MyDaoHelper.class, "You must annotate some fields with @DkColumnInfo in %s", modelType.getName());
        }

        for (Field field : fields) {
            try {
                String column = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class)).name();
                String value = cursor.getString(cursor.getColumnIndex(column));
                Class<?> type = field.getType();

                switch (DkTypeHelper.getTypeMasked(type)) {
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
                DkLogs.error(MyDaoHelper.class, e);
            }
        }
        return model;
    }

    static String calcColumnDefinition(DkColumnInfo columnInfo, Field field) {
        String definition = DkConst.EMPTY_STRING;

        // Add type
        switch (columnInfo.type()) {
            case DkSqliteType.UNDEFINED: {
                definition += ' ' + calcColumnTypeNameWhenUndefined(field);
                break;
            }
            case DkSqliteType.INTEGER: {
                definition += " integer";
                break;
            }
            case DkSqliteType.TEXT: {
                definition += " text";
                break;
            }
            case DkSqliteType.REAL: {
                definition += " real";
                break;
            }
            case DkSqliteType.BLOB: {
                definition += " blob";
                break;
            }
            default: {
                throw new RuntimeException("Invalid type: " + columnInfo.type());
            }
        }

        // Add properties
        if (columnInfo.primaryKey()) {
            definition += " primary key";
        }
        if (columnInfo.notNull()) {
            definition += " not null";
        }
        if (columnInfo.autoIncrement()) {
            definition += " autoincrement";
        }

        return definition;
    }

    private static int calcColumnTypeWhenUndefined(Field field) {
        Integer type = class2type().get(field.getType());
        return type == null ? -1 : type;
    }

    private static String calcColumnTypeNameWhenUndefined(Field field) {
        switch (calcColumnTypeWhenUndefined(field)) {
            case DkSqliteType.INTEGER: {
                return "integer";
            }
            case DkSqliteType.TEXT: {
                return "text";
            }
            case DkSqliteType.REAL: {
                return "real";
            }
            case DkSqliteType.BLOB: {
                return "blob";
            }
            default: {
                throw new RuntimeException(DkStrings.format("Field %s has type %s which is not supported",
                    field.getName(), field.getType().getName()));
            }
        }
    }

    private static Map<Class<?>, Integer> class2type() {
        if (class2type == null) {
            class2type = new ArrayMap<>();
        }

        class2type.put(boolean.class, DkSqliteType.INTEGER);
        class2type.put(Boolean.class, DkSqliteType.INTEGER);
        class2type.put(short.class, DkSqliteType.INTEGER);
        class2type.put(Short.class, DkSqliteType.INTEGER);
        class2type.put(int.class, DkSqliteType.INTEGER);
        class2type.put(Integer.class, DkSqliteType.INTEGER);
        class2type.put(long.class, DkSqliteType.INTEGER);
        class2type.put(Long.class, DkSqliteType.INTEGER);

        class2type.put(String.class, DkSqliteType.TEXT);

        class2type.put(float.class, DkSqliteType.REAL);
        class2type.put(Float.class, DkSqliteType.REAL);
        class2type.put(double.class, DkSqliteType.REAL);
        class2type.put(Double.class, DkSqliteType.REAL);

        return class2type;
    }
}
