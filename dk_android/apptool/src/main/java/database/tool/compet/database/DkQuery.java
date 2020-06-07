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

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tool.compet.core.helper.DkTypeHelper;
import tool.compet.core.math.DkMaths;
import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.util.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;
import static tool.compet.database.DkExpress.joinVals;

/**
 * This class receives a SQLiteDatabase instance, provides making, executing a query.
 * Note that, its independent class with table managers, table managers should use this class
 * to do transaction with the database.
 * <p></p>
 * When you query with customized model, make sure each field in your model was annotated with
 * {@link SerializedName} since we use it to map column in retrieved {@link Cursor} with your model.
 *
 * @author darkcompet (co.vp@kilobytes.com.vn)
 */
public class DkQuery implements DkSqliteKeyword {
   private SQLiteDatabase database;
   private StringBuilder builder;

   private DkQuery(SQLiteDatabase database) {
      this.database = database;
      this.builder = new StringBuilder(256);
   }

   /**
    * Caller should take care passing readable or writable SQLiteDatabase object to this.
    */
   public static DkQuery newIns(SQLiteDatabase db) {
      return new DkQuery(db);
   }

   public Cursor raw() throws Exception {
      String sql = builder.toString();
      Cursor cursor;

      try {
         if (DEBUG) {
            DkLogs.log(this, "rawQuery: " + sql);
         }
         database.beginTransaction();
         cursor = database.rawQuery(sql, null);
         database.setTransactionSuccessful();
      }
      finally {
         database.endTransaction();
      }

      return cursor;
   }

   public <M> List<M> raw(Class<M> modelClass) throws Exception {
      List<M> ressult = new ArrayList<>();
      Cursor cursor = raw();

      if (cursor == null) {
         return ressult;
      }
      try {
         if (cursor.moveToFirst()) {
            do {
               ressult.add(row2obj(cursor, modelClass));
            } while (cursor.moveToNext());
         }
      }
      finally {
         cursor.close();
      }

      return ressult;
   }

   public void exe() throws Exception {
      try {
         if (DEBUG) {
            DkLogs.log(this, "exeQuery: " + builder.toString());
         }
         String sql = builder.toString();
         database.beginTransaction();
         database.execSQL(sql);
         database.setTransactionSuccessful();
      }
      finally {
         database.endTransaction();
      }
   }

   private <M> M row2obj(Cursor cursor, Class<M> modelType) throws Exception {
      M model = modelType.newInstance();

      List<Field> fields = DkReflectionFinder.getIns()
         .findFields(modelType, SerializedName.class, true, false);

      if (fields.size() == 0) {
         DkLogs.complain(this, "You must annotate some fields with @SerializedName in %s",
            modelType.getName());
      }

      for (Field field : fields) {
         try {
            String col = field.getAnnotation(SerializedName.class).value();
            String val = cursor.getString(cursor.getColumnIndex(col));
            Class<?> type = field.getType();

            switch (DkTypeHelper.getTypeMasked(type)) {
               case DkTypeHelper.TYPE_BOOLEAN_MASKED: {
                  field.set(model, DkMaths.parseBoolean(val));
                  break;
               }
               case DkTypeHelper.TYPE_SHORT_MASKED: {
                  field.set(model, DkMaths.parseShort(val));
                  break;
               }
               case DkTypeHelper.TYPE_INTEGER_MASKED: {
                  field.set(model, DkMaths.parseInt(val));
                  break;
               }
               case DkTypeHelper.TYPE_LONG_MASKED: {
                  field.set(model, DkMaths.parseLong(val));
                  break;
               }
               case DkTypeHelper.TYPE_FLOAT_MASKED: {
                  field.set(model, DkMaths.parseFloat(val));
                  break;
               }
               case DkTypeHelper.TYPE_DOUBLE_MASKED: {
                  field.set(model, DkMaths.parseDouble(val));
                  break;
               }
               case DkTypeHelper.TYPE_STRING_MASKED: {
                  field.set(model, val);
                  break;
               }
               default: {
                  throw new RuntimeException("Invalid type of field: " + type.getName());
               }
            }
         }
         catch (Exception e) {
            DkLogs.logex(this, e);
         }
      }
      return model;
   }

   /**
    * Set new query. The query built before calling this method will be cleared.
    */
   public DkQuery setSql(String query) {
      if (builder.length() > 0) {
         builder = new StringBuilder(query.length());
      }
      builder.append(query);
      return this;
   }

   /**
    * Just append given text.
    */
   private DkQuery justAppend(String text) {
      builder.append(text);
      return this;
   }

   /**
    * Append given text and a space next.
    */
   private DkQuery appendNext(String text) {
      builder.append(text).append(" ");
      return this;
   }

   /**
    * Like {@link DkQuery#appendNext(String)} but it takes care about keyword confliction.
    */
   private DkQuery appendName(String name) {
      builder.append("'").append(name).append("'").append(" ");
      return this;
   }

   public DkQuery select(String... cols) {
      return appendNext(SELECT).appendNext(DkExpress.joinCols(Arrays.asList(cols)));
   }

   public DkQuery from(String tableName) {
      return appendNext(FROM).appendName(tableName);
   }

   public DkQuery selectFrom(String tableName, String... cols) {
      return selectFrom(false, tableName, cols);
   }

   public DkQuery selectFrom(boolean isDistinct, String tableName, String... cols) {
      if (isDistinct) {
         appendNext(SELECT).appendNext(DISTINCT);
      }
      if (cols == null || cols.length == 0) {
         appendNext(SELECT).appendNext("*");
      }
      else {
         appendNext(SELECT).appendNext(DkExpress.joinCols(Arrays.asList(cols)));
      }

      return appendNext(FROM).appendName(tableName);
   }

   /**
    * @param col can be asterisk or column name. If col is asterisk, then
    *            row will be counted. Otherwise non-null-value of the row
    *            will be counted.
    */
   public DkQuery selectCount(String tableName, String col) {
      return appendNext("SELECT COUNT(")
         .justAppend(col).appendNext(")")
         .appendNext(FROM)
         .appendName(tableName);
   }

   public DkQuery innerJoin(String tableName, String joinOnCondition) {
      return appendNext(INNER_JOIN).appendName(tableName)
         .appendNext(ON).appendNext(joinOnCondition);
   }

   public DkQuery update(String tableName, List<String> cols, List<Object> vals, String whereClause) {
      return appendNext(UPDATE).appendName(tableName)
         .appendNext(SET).appendNext(DkExpress.set(cols, vals))
         .appendNext(WHERE).appendNext(whereClause);
   }

   public DkQuery insertInto(String tableName, List<String> cols, List<Object> vals) {
      if (cols == null || vals == null || cols.size() != vals.size()) {
         DkLogs.complain(this, "Cols and Vals must be non null and same size");
      }

      return appendNext("INSERT INTO").appendName(tableName)
         .justAppend("(").justAppend(DkExpress.joinCols(cols)).appendNext(")")
         .appendNext("VALUES (").justAppend(joinVals(vals)).appendNext(")");
   }

   public DkQuery deleteFrom(String tableName) {
      return appendNext("DELETE FROM").appendName(tableName);
   }

   public DkQuery dropTable(String tableName) {
      return appendNext("DROP TABLE IF EXISTS").appendName(tableName);
   }

   public DkQuery where(String whereClause) {
      return appendNext(WHERE).appendNext(whereClause);
   }

   public DkQuery where(DkExpress where) {
      return appendNext(WHERE).appendNext(where.toString());
   }

   public DkQuery orderby(boolean isAsc, String... sortCols) {
      if (sortCols != null && sortCols.length > 0) {
         appendNext(ORDER_BY)
            .appendNext(DkExpress.joinCols(Arrays.asList(sortCols)))
            .appendNext(isAsc ? ASC : DESC);
      }
      return this;
   }

   public DkQuery limit(int capacity) {
      return appendNext(LIMIT).appendNext(String.valueOf(capacity));
   }

   @NonNull
   public String toString() {
      return builder.toString();
   }

   /**
    * Validate the correctness of sql query.
    */
   public DkQuery validate() {
      String sql = builder.toString();
      throw new RuntimeException("Invalid sql");
   }
}
