/*
 * Copyright (c) 2020 DarkCompet. All rights reserved.
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

import androidx.collection.ArraySet;

import java.util.Arrays;

public class DkSqliteSupport {
   // Sqlite 143 keywords
   private static final ArraySet<String> keywords = new ArraySet<>(Arrays.asList(
      "ABORT", "ACTION", "ADD", "AFTER", "ALL", "ALTER", "ANALYZE",
      "AND", "AS", "ASC", "ATTACH", "AUTOINCREMENT", "BEFORE", "BEGIN",
      "BETWEEN", "BY", "CASCADE", "CASE", "CAST", "CHECK", "COLLATE",
      "COLUMN", "COMMIT", "CONFLICT", "CONSTRAINT", "CREATE", "CROSS", "CURRENT",
      "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DATABASE", "DEFAULT", "DEFERRABLE", "DEFERRED",
      "DELETE", "DESC", "DETACH", "DISTINCT", "DO", "DROP", "EACH",
      "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUDE", "EXCLUSIVE", "EXISTS",
      "EXPLAIN", "FAIL", "FILTER", "FIRST", "FOLLOWING", "FOR", "FOREIGN",
      "FROM", "FULL", "GLOB", "GROUP", "GROUPS", "HAVING", "IF",
      "IGNORE", "IMMEDIATE", "IN", "INDEX", "INDEXED", "INITIALLY", "INNER",
      "INSERT", "INSTEAD", "INTERSECT", "INTO", "IS", "ISNULL", "JOIN",
      "KEY", "LAST", "LEFT", "LIKE", "LIMIT", "MATCH", "NATURAL",
      "NO", "NOT", "NOTHING", "NOTNULL", "NULL", "NULLS", "OF",
      "OFFSET", "ON", "OR", "ORDER", "OTHERS", "OUTER", "OVER",
      "PARTITION", "PLAN", "PRAGMA", "PRECEDING", "PRIMARY", "QUERY", "RAISE",
      "RANGE", "RECURSIVE", "REFERENCES", "REGEXP", "REINDEX", "RELEASE", "RENAME",
      "REPLACE", "RESTRICT", "RIGHT", "ROLLBACK", "ROW", "ROWS", "SAVEPOINT",
      "SELECT", "SET", "TABLE", "TEMP", "TEMPORARY", "THEN", "TIES",
      "TO", "TRANSACTION", "TRIGGER", "UNBOUNDED", "UNION", "UNIQUE", "UPDATE",
      "USING", "VACUUM", "VALUES", "VIEW", "VIRTUAL", "WHEN", "WHERE",
      "WINDOW", "WITH", "WITHOUT"
   ));

   public static boolean isKeyword(String name) {
      return keywords.contains(name.toUpperCase());
   }
}
