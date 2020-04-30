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

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.List;

import tool.compet.core.util.DkLogs;
import tool.compet.database.helper.DkSqliteSupportedTypes;

/**
 * Support making a sub-sentence of operators, columns, values... for DkQuery.
 * <p></p>
 * Suppose we have a condition:
 * <pre>
 *    {@code (col1 == val1) || ((col2 >= val2) && (col3 <= val3))}
 * </pre>
 * Then corresponding code for it is:
 * <pre>
 *    {@code or(eq(col1, val1), and(gte(col2, val2), lte(col3, val3));}
 * </pre>
 *
 * @author darkcompet (co.vp@kilobytes.com.vn)
 */
public class DkExpress {
   private static DkSqliteSupportedTypes sqliteSupportedTypes = new DkSqliteSupportedTypes();
   private StringBuilder builder = new StringBuilder(64);

   private DkExpress() {
   }

   @NonNull
   @Override
   public String toString() {
      return builder.toString();
   }

   /**
    * Just append key to builder, it does not optimize anything.
    */
   private DkExpress justAppend(String other) {
      builder.append(other);
      return this;
   }

   /**
    * Append a name to builder, it takes care about keyword confliction.
    */
   private DkExpress appendName(String name) {
      if (DkSqliteSupport.isKeyword(name)) {
         builder.append("'").append(name).append("'");
      }
      else {
         builder.append(name);
      }
      return this;
   }

   /**
    * Append a value to builder, it takes care about sql injection.
    * For case val is boolean type, val will be auto-converted to 1/0 when write.
    * when read, caller can query with true/false value, or 1/0 value.
    */
   private DkExpress appendVal(Object val) {
      if (val == null) {
         builder.append("null");
         return this;
      }

      switch (sqliteSupportedTypes.getType(val.getClass())) {
         case DkSqliteSupportedTypes.TYPE_INTEGER:
         case DkSqliteSupportedTypes.TYPE_REAL: {
            builder.append(val);
            return this;
         }
         // For case val is String type, val will be prefix and suffix with ' character
         // and be checked to prevent sql injection before read/write.
         case DkSqliteSupportedTypes.TYPE_STRING: {
            builder.append("'").append(Util.preventInjection((String) val)).append("'");
            return this;
         }
      }
      throw new RuntimeException("Not support append type: " + val.getClass().getName());
   }

   /**
    * @return {@code 'tabName'.'colId'}
    */
   public static String col(String tabName, String colId) {
      return new DkExpress().appendName(tabName).justAppend(".").appendName(colId).toString();
   }

   /**
    * @param expression should not contain any special character which cause sql injection.
    * @return {@code (input)}
    */
   public static String pack(String expression) {
      return new DkExpress().justAppend("(").justAppend(expression).justAppend(")").toString();
   }

   /**
    * @param col can be keyword but should not contain any special character which causes sql injection.
    * @return {@code (col = '' OR col IS NULL)
    */
   public static String isEmpty(String col) {
      return pack(new DkExpress()
         .appendName(col).justAppend(" = '' OR ")
         .appendName(col).justAppend(" IS NULL").toString());
   }

   /**
    * @return {@code (input1) AND (input2) AND ... (inputN)}
    */
   public static String and(String... expressions) {
      DkExpress out = new DkExpress();

      if (expressions == null || expressions.length == 0) {
         return out.toString();
      }
      out.justAppend(pack(expressions[0]));

      for (int i = 1, N = expressions.length; i < N; ++i) {
         out.justAppend("AND ").justAppend(pack(expressions[i]));
      }
      return out.toString();
   }

   /**
    * @return {@code (input1) OR (input2) OR ... (inputN)}
    */
   public static String or(String... expressions) {
      DkExpress out = new DkExpress();

      if (expressions == null || expressions.length == 0) {
         return out.toString();
      }
      out.justAppend(pack(expressions[0]));

      for (int i = 1, N = expressions.length; i < N; ++i) {
         out.justAppend("OR ").justAppend(pack(expressions[i]));
      }
      return out.toString();
   }

   /**
    * Make IN sentence. This method takes casre about sql injection for each value.
    *
    * @return {@code col IN (val1, val2, val3, ..., valN)}
    */
   public static String in(String col, Iterable vals) {
      DkExpress out = new DkExpress();
      Iterator it = vals.iterator();

      if (it.hasNext()) {
         out.appendName(col).justAppend(" IN (").appendVal(it.next());

         while (it.hasNext()) {
            out.justAppend(", ").appendVal(it.next());
         }
         out.justAppend(")");
      }

      return out.toString();
   }

   /**
    * Support equals sentence. Value will be auto converted.
    *
    * @return If val is not null then return {@code col = val}.
    *    Otherwise return {@code (col = '' OR col IS NULL)}.
    */
   public static String eq(String col, Object val) {
      if (val == null) {
         return isEmpty(col);
      }
      return new DkExpress().appendName(col).justAppend(" = ").appendVal(val).toString();
   }

   /**
    * Support AND of equals sentence. This method takes casre about sql injection for each value.
    *
    * @return {@code col1 = val1 AND col2 = val2 AND ... colN = valN}.
    * If a val is null, then for each column that has null value, expression
    * will be converted to {@code (col = '' OR col IS NULL)}.
    */
   public static String eq(List<String> cols, List<?> vals) {
      if (cols == null || vals == null || cols.size() != vals.size()) {
         throw new RuntimeException("Cols and Vals must be non null and same length");
      }

      DkExpress out = new DkExpress();

      out.justAppend(eq(cols.get(0), vals.get(0)));

      for (int i = 1, N = cols.size(); i < N; ++i) {
         out.justAppend(" AND ").justAppend(eq(cols.get(i), vals.get(i)));
      }

      return out.toString();
   }

   /**
    * Make join columns.
    *
    * @return {@code col1, col2, ..., colN}
    */
   public static String joinCols(Iterable<String> cols) {
      DkExpress out = new DkExpress();
      Iterator<String> colsIt = cols.iterator();

      if (colsIt.hasNext()) {
         out.appendName(colsIt.next());

         while (colsIt.hasNext()) {
            out.justAppend(", ").appendName(colsIt.next());
         }
      }
      return out.toString();
   }

   /**
    * Make join values. This method takes casre about sql injection for each value.
    *
    * @return {@code val1, val2, ..., valN}
    */
   public static String joinVals(Iterable<?> vals) {
      DkExpress out = new DkExpress();
      Iterator<?> valsIt = vals.iterator();

      if (valsIt.hasNext()) {
         out.appendVal(valsIt.next());

         while (valsIt.hasNext()) {
            out.justAppend(", ").appendVal(valsIt.next());
         }
      }
      return out.toString();
   }

   /**
    * Make set for update. This method takes casre about sql injection for each value.
    *
    * @return {@code col1 = val1, col2 = val2, ..., coln = valN}
    */
   public static String set(List<String> cols, List vals) {
      if (cols == null || vals == null || cols.size() != vals.size()) {
         DkLogs.complain(DkExpress.class, "Cols and Vals must be non null and same length");
      }

      DkExpress out = new DkExpress();

      out.appendName(cols.get(0)).justAppend(" = ").appendVal(vals.get(0));

      for (int i = 1, N = vals.size(); i < N; ++i) {
         out.justAppend(", ").appendName(cols.get(i)).justAppend(" = ").appendVal(vals.get(i));
      }
      return out.toString();
   }

   /**
    * Make greater than. This method takes casre about sql injection for given value.
    *
    * @return {@code col > val}
    */
   public static String gt(String col, Object val) {
      return new DkExpress().appendName(col).justAppend(" > ").appendVal(val).toString();
   }

   /**
    * Make greater than or equal. This method takes casre about sql injection for given value.
    *
    * @return {@code col >= val}
    */
   public static String gte(String col, Object val) {
      return new DkExpress().appendName(col).justAppend(" >= ").appendVal(val).toString();
   }

   /**
    * Make less than. This method takes casre about sql injection for given value.
    *
    * @return {@code col < val}
    */
   public static String lt(String col, Object val) {
      return new DkExpress().appendName(col).justAppend(" < ").appendVal(val).toString();
   }

   /**
    * Make less than or equal. This method takes casre about sql injection for given value.
    *
    * @return {@code col <= val}
    */
   public static String lte(String col, Object val) {
      return new DkExpress().appendName(col).justAppend(" <= ").appendVal(val).toString();
   }

   /**
    * @return {@code tab1.col1 = tab2.col2}
    */
   public static String joinOn(String tab1, String col1, String tab2, String col2) {
      return new DkExpress().appendName(tab1).justAppend(".").appendName(col1)
         .justAppend(" = ").appendName(tab2).justAppend(".").appendName(col2).toString();
   }
}
