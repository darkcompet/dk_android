/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tool.compet.core.constant.DkConst;
import tool.compet.core.util.DkStrings;
import tool.compet.core.util.DkCollections;
import tool.compet.core.util.DkObjects;
import tool.compet.core.util.DkUtils;

import static tool.compet.database.MyConst.K_IS_NOT_NULL;
import static tool.compet.database.MyConst.K_IS_NULL;

public class DkGrammar {
    // SQLite available operators
    protected Set<String> availableOperators = new HashSet<>(Arrays.asList(
        "=", "<", ">", "<=", ">=", "<>", "!=",
        "like", "not like", "ilike",
        "&", "|", "<<", ">>"
    ));

    protected void fixGrammar(@NonNull MyExpression expression) {
        String myOperator = expression.operator.toLowerCase();
        if (!availableOperators.contains(myOperator)) {
            throw new RuntimeException("Invalid operator: " + expression.operator);
        }
        // Fix null comparasion, for eg,. `user`.`name` = null -> `user`.`name` is null
        if (expression.value == null) {
            switch (myOperator) {
                case "=":
                    expression.operator = K_IS_NULL;
                    break;
                case "!=":
                case "<>":
                    expression.operator = K_IS_NOT_NULL;
                    break;
                default:
                    DkUtils.complain("Invalid operator `%s` for null value", expression.operator);
            }
        }
    }

    /**
     * Prevent confliction between name and keyword.
     *
     * @param name db column name or table name, for eg,. "user.id as user_id", "user.*"
     * @return String for eg,. "`user`.`id` as `user_id`", "`user`.*"
     */
    protected String wrapName(@NonNull String name) {
        if ("*".equals(name)) {
            return "*";
        }
        name = name.trim();
        if (name.contains(" as ")) {
            String[] arr = name.split("\\s+as\\s+");
            return wrapName(arr[0]) + " as " + wrapName(arr[1]);
        }
        if (name.contains(".")) {
            String[] arr = name.split("\\.");
            return wrapName(arr[0]) + '.' + wrapName(arr[1]);
        }
        return '`' + name + '`';
    }

    /**
     * Wrap value to make it can valid when comparasion and prevent injection.
     *
     * @param value must primitive value, for eg,. "leo leo", 1.23
     */
    protected String wrapPrimitiveValue(@Nullable Object value) {
        if (value == null) {
            return null;
        }

        final String singleQuote = "'";
        final String doubleQuote = "''";

        if (value instanceof String) {
            return singleQuote + ((String) value).replace(singleQuote, doubleQuote) + singleQuote;
        }
        return singleQuote + value.toString().replace(singleQuote, doubleQuote) + singleQuote;
    }

    protected String wrapJoinedValues(Iterable values) {
        List<String> items = new ArrayList<>();
        for (Object value : values) {
            items.add(wrapPrimitiveValue(value));
        }
        return DkStrings.join(", ", items);
    }

    protected boolean invalidOperator(String operator) {
        return !availableOperators.contains(operator.toLowerCase());
    }

    /**
     * Generate select list.
     *
     * @param selections Select list
     * @return for eg,. "*", "`id`, `name`, count(id) as `count_id`"
     */
    protected String compileSelects(List<MySelection> selections) {
        // Consider caller wanna get all fields if no selection was specified
        if (DkCollections.isEmpty(selections)) {
            return "*";
        }

        List<String> items = new ArrayList<>();
        for (MySelection selection : selections) {
            items.add(selection.compile());
        }
        return DkStrings.join(", ", items);
    }

    /**
     * Generate insert clause.
     *
     * @param params Insert params
     * @return for eg,. "insert into `user` (`id`, `name`) values ('1', 'leo leo')"
     */
    protected String compileInsertQuery(String tableName, Map<String, Object> params) {
        if (DkObjects.isEmpty(params)) {
            return DkConst.EMPTY_STRING;
        }
        
        List<String> names = new ArrayList<>();
        List<String> values = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            names.add(wrapName(entry.getKey()));
            values.add(wrapPrimitiveValue(entry.getValue()));
        }
        
        String flatten_names = DkStrings.join(", ", names);
        String flatten_values = DkStrings.join(", ", values);
        String insertSet = DkStrings.format("(%s) values (%s)", flatten_names, flatten_values);

        String query = DkStrings.format("insert into %s %s",
            wrapName(tableName),
            insertSet
        );
        return query.trim();
    }

    /**
     * Generate `update` clause.
     *
     * @param params Update params
     * @return for eg,. "update `user` set `id` = 1, `name` = 'leo leo'"
     */
    protected String compileUpdateQuery(String tableName, Map<String, Object> params, String whereClause) {
        StringBuilder updateSetBuilder = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                updateSetBuilder.append(", ");
            }
            String name = wrapName(entry.getKey());
            String value = wrapPrimitiveValue(entry.getValue());

            updateSetBuilder.append(name).append(" = ").append(value);
        }
        String query = DkStrings.format("update %s set %s %s",
            wrapName(tableName),
            updateSetBuilder.toString(),
            whereClause
        );
        return query.trim();
    }

    /**
     * Generate `where` clause.
     *
     * @param wheres Where condition list
     * @return for eg,. "where id = 1 and name is null"
     */
    protected String compileWheres(List<MyExpression> wheres) {
        return compileBoolExpression("where", wheres);
    }

    /**
     * Express bool operation from bool keywords (and, or), name, value and operator.
     *
     * @param prefix `where` or `on`
     * @param expressions List of bool operation
     * @return for eg,. "where `name` = 'hehe' and `age` = '20'", "on `id` = '100'"
     */
    protected String compileBoolExpression(String prefix, List<MyExpression> expressions) {
        // Nothing is compiled, so return empty string
        if (DkCollections.isEmpty(expressions)) {
            return DkConst.EMPTY_STRING;
        }
        List<String> items = new ArrayList<>();
        for (MyExpression expression : expressions) {
            items.add(expression.compile());
        }
        String clause = DkStrings.join(' ', items).trim();
        return prefix + ' ' + clause.replaceFirst("^(and|or)", DkConst.EMPTY_STRING).trim();
    }

    /**
     * Generate `join` clause.
     *
     * @param joinInfos List of join info.
     * @return for eg,. left join `user_detail` on `user`.`id` = `user_detail`.`user_id`
     *                  right join `user_city` on `user`.`id` = `user_city`.`user_id`
     *                  inner join `user_main` on `user`.`id` = `user_main`.`user_id`
     */
    protected String compileJoins(List<MyJoin> joinInfos) {
        if (DkCollections.isEmpty(joinInfos)) {
            return DkConst.EMPTY_STRING;
        }
        List<String> joinClauses = new ArrayList<>();
        for (MyJoin joinInfo : joinInfos) {
            joinClauses.add(joinInfo.compile());
        }
        return DkStrings.join(' ', joinClauses);
    }

    /**
     * Generate `group by` clause.
     *
     * @param groupBys List of group by
     * @return for eg,. "group by `id`, `name`, `age`"
     */
    protected String compileGroupBys(List<MyGroupBy> groupBys) {
        if (DkCollections.isEmpty(groupBys)) {
            return DkConst.EMPTY_STRING;
        }
        List<String> groupByList = new ArrayList<>();
        for (MyGroupBy groupBy : groupBys) {
            groupByList.add(groupBy.compile());
        }
        return "group by " + DkStrings.join(", ", groupByList);
    }

    /**
     * Generate `having` clause.
     *
     * @param havings Having where condition list
     * @return for eg,. "having count(id) < 10"
     */
    protected String compileHaving(List<MyExpression> havings) {
        return compileBoolExpression("having", havings);
    }

    /**
     * Generate `order by` clause.
     *
     * @param orderBys for eg,.
     * @return for eg,. "order by `id` asc, `name` desc"
     */
    protected String compileOrderBys(List<MyOrderBy> orderBys) {
        if (DkCollections.isEmpty(orderBys)) {
            return DkConst.EMPTY_STRING;
        }
        List<String> orderByList = new ArrayList<>();
        for (MyOrderBy orderBy : orderBys) {
            orderByList.add(orderBy.compile());
        }
        return "order by " + DkStrings.join(", ", orderByList);
    }

    /**
     * Generate `limit` clause.
     *
     * @param limit Max item count to get
     * @return for eg,. "limit 10"
     */
    protected String compileLimit(long limit) {
        if (limit == Long.MIN_VALUE) {
            return DkConst.EMPTY_STRING;
        }
        return "limit " + limit;
    }

    /**
     * Generate `offset` clause.
     *
     * @param offset Position where take it
     * @return for eg,. "offset 12"
     */
    protected String compileOffset(long offset) {
        if (offset == Long.MIN_VALUE) {
            return DkConst.EMPTY_STRING;
        }
        return "offset " + offset;
    }

    /**
     * Generate `distinct` clause.
     *
     * @param distinct Indicate the query has distinct
     * @return for eg,. "distinct"
     */
    protected String compileDistinct(boolean distinct) {
        return distinct ? "distinct" : DkConst.EMPTY_STRING;
    }

    protected String compileDelete(String tableName, String whereClause) {
        return DkStrings.format("delete from %s %s",
            wrapName(tableName),
            whereClause
        );
    }
}
