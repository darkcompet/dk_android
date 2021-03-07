/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tool.compet.core.type.DkCallback1;
import tool.compet.core.util.DkMaps;
import tool.compet.core.util.DkStrings;

import static tool.compet.packages.database.MyConst.K_AND;
import static tool.compet.packages.database.MyConst.K_ASC;
import static tool.compet.packages.database.MyConst.K_BASIC;
import static tool.compet.packages.database.MyConst.K_DESC;
import static tool.compet.packages.database.MyConst.K_EQ;
import static tool.compet.packages.database.MyConst.K_IN;
import static tool.compet.packages.database.MyConst.K_INNER;
import static tool.compet.packages.database.MyConst.K_IS_NOT_NULL;
import static tool.compet.packages.database.MyConst.K_IS_NULL;
import static tool.compet.packages.database.MyConst.K_LEFT;
import static tool.compet.packages.database.MyConst.K_NOT_IN;
import static tool.compet.packages.database.MyConst.K_NOT_NULL;
import static tool.compet.packages.database.MyConst.K_NULL;
import static tool.compet.packages.database.MyConst.K_OR;
import static tool.compet.packages.database.MyConst.K_RAW;
import static tool.compet.packages.database.MyConst.K_RIGHT;

/**
 * This is base query builder for various query language as sqlite, mysql, postgresql...
 * It receives a database connection, provides a query execution.
 * Caller can build and execute a query from this instead of manual sql.
 *
 * @author darkcompet
 */
public abstract class TheQueryBuilder<M> { // M: model
    /**
     * Insert new row from given key-value map.
     *
     * @param params Map of insert key-value for the table
     * @return Last inserted row id of current connection.
     * @throws RuntimeException When invalid params
     */
    public abstract long insert(Map<String, Object> params);

    protected final MyDatabaseConnection connection;
    protected final MyGrammar grammar;
    protected String tableName;
    protected Class<M> modelClass;

    protected List<MySelection> nullableSelects;
    protected boolean distinct;
    protected List<MyJoin> nullableJoins;
    protected List<MyExpression> nullableWheres;
    protected List<MyOrderBy> nullableOrderBys;
    protected List<MyGroupBy> nullableGroupBys;
    protected List<MyExpression> nullableHavings;
    protected long limit = Long.MIN_VALUE;
    protected long offset = Long.MIN_VALUE;

    // Params for insert or update
    protected Map<String, Object> pairs;

    // Package privated (note that, this class is open for usage, not for create)
    TheQueryBuilder(MyDatabaseConnection connection, MyGrammar grammar, String tableName, Class<M> modelClass) {
        this.connection = connection;
        this.grammar = grammar;
        this.tableName = tableName;
        this.modelClass = modelClass;
    }

    public TheQueryBuilder table(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public TheQueryBuilder<M> model(Class<M> modelClass) {
        this.modelClass = modelClass;
        return this;
    }

    private List<MySelection> selects() {
        return nullableSelects != null ? nullableSelects : (nullableSelects = new ArrayList<>());
    }
    
    private List<MyJoin> joins() {
        return nullableJoins != null ? nullableJoins : (nullableJoins = new ArrayList<>());
    }
    
    private List<MyExpression> wheres() {
        return nullableWheres != null ? nullableWheres : (nullableWheres = new ArrayList<>());
    }

    private List<MyOrderBy> orderBys() {
        return nullableOrderBys != null ? nullableOrderBys : (nullableOrderBys = new ArrayList<>());
    }

    private List<MyGroupBy> groupBys() {
        return nullableGroupBys != null ? nullableGroupBys : (nullableGroupBys = new ArrayList<>());
    }

    private List<MyExpression> havings() {
        return nullableHavings != null ? nullableHavings : (nullableHavings = new ArrayList<>());
    }

    public TheQueryBuilder<M> select(String... names) {
        for (String name : names) {
            selects().add(new MySelection(grammar, K_BASIC, name));
        }
        return this;
    }

    /**
     * @param subQuery String for eg,. "count(id) as user_id"
     * @return this
     */
    public TheQueryBuilder<M> selectRaw(String subQuery) {
        return selectRaw(subQuery, null);
    }

    public TheQueryBuilder<M> selectRaw(String subQuery, String alias) {
        selects().add(new MySelection(grammar, K_RAW, subQuery, alias));
        return this;
    }

    public TheQueryBuilder<M> distinct() {
        distinct = true;
        return this;
    }

    public TheQueryBuilder<M> leftJoin(String joinTable, String first, String second) {
        return registerSingleJoin(K_LEFT, joinTable, first, "=", second);
    }

    public TheQueryBuilder<M> leftJoin(String joinTable, String first, String operator, String second) {
        return registerSingleJoin(K_LEFT, joinTable, first, operator, second);
    }

    public TheQueryBuilder<M> rightJoin(String joinTable, String first, String second) {
        return registerSingleJoin(K_RIGHT, joinTable, first, "=", second);
    }

    public TheQueryBuilder<M> rightJoin(String joinTable, String first, String operator, String second) {
        return registerSingleJoin(K_RIGHT, joinTable, first, operator, second);
    }

    public TheQueryBuilder<M> join(String joinTable, String first, String second) {
        return registerSingleJoin(K_INNER, joinTable, first, "=", second);
    }

    public TheQueryBuilder<M> join(String joinTable, String first, String operator, String second) {
        return registerSingleJoin(K_INNER, joinTable, first, operator, second);
    }

    public TheQueryBuilder<M> leftJoin(String joinTable, DkCallback1<DkJoiner> joinerCallback) {
        return registerMultipleJoin(K_LEFT, joinTable, joinerCallback);
    }

    public TheQueryBuilder<M> rightJoin(String joinTable, DkCallback1<DkJoiner> joinerCallback) {
        return registerMultipleJoin(K_RIGHT, joinTable, joinerCallback);
    }

    public TheQueryBuilder<M> join(String joinTable, DkCallback1<DkJoiner> joinerCallback) {
        return registerMultipleJoin(K_INNER, joinTable, joinerCallback);
    }

    private TheQueryBuilder<M> registerMultipleJoin(String joinType, String joinTable, DkCallback1<DkJoiner> joinerCallback) {
        // Send joiner to callback and receive condition from callbacker
        DkJoiner joiner = new DkJoiner(grammar);
        joinerCallback.run(joiner);

        joins().add(new MyJoin(grammar, joinType, joinTable, joiner));

        return this;
    }

    private TheQueryBuilder<M> registerSingleJoin(String joinType, String joinTable, String first, String operator, String second) {
        joins().add(new MyJoin(grammar, joinType, joinTable, first, operator, second));

        return this;
    }

    /**
     * This is equal where, short where for equal.
     * @param name String table column name
     * @param value Object target value which matches with value of the field
     */
    public TheQueryBuilder<M> where(String name, Object value) {
        return registerExpression(new MyExpression(grammar, K_AND, K_BASIC, name, K_EQ, value), wheres());
    }

    public TheQueryBuilder<M> orWhere(String name, Object value) {
        return registerExpression(new MyExpression(grammar, K_OR, K_BASIC, name, K_EQ, value), wheres());
    }

    public TheQueryBuilder<M> where(String name, String operator, Object value) {
        return registerExpression(new MyExpression(grammar, K_AND, K_BASIC, name, operator, value), wheres());
    }

    public TheQueryBuilder<M> orWhere(String name, String operator, Object value) {
        return registerExpression(new MyExpression(grammar, K_OR, K_BASIC, name, operator, value), wheres());
    }

    public TheQueryBuilder<M> whereNull(String name) {
        return registerExpression(new MyExpression(grammar, K_AND, K_NULL, name, K_IS_NULL), wheres());
    }

    public TheQueryBuilder<M> orWhereNull(String name) {
        return registerExpression(new MyExpression(grammar, K_OR, K_NULL, name, K_IS_NULL), wheres());
    }

    public TheQueryBuilder<M> whereNotNull(String name) {
        return registerExpression(new MyExpression(grammar, K_AND, K_NOT_NULL, name, K_IS_NOT_NULL), wheres());
    }

    public TheQueryBuilder<M> orWhereNotNull(String name) {
        return registerExpression(new MyExpression(grammar, K_OR, K_NOT_NULL, name, K_IS_NOT_NULL), wheres());
    }

    public TheQueryBuilder<M> whereIn(String name, Iterable values) {
        return registerExpression(new MyExpression(grammar, K_AND, K_IN, name, K_IN, values), wheres());
    }

    public TheQueryBuilder<M> orWhereIn(String name, Iterable values) {
        return registerExpression(new MyExpression(grammar, K_OR, K_IN, name, K_IN, values), wheres());
    }

    public TheQueryBuilder<M> whereNotIn(String name, Iterable values) {
        return registerExpression(new MyExpression(grammar, K_AND, K_NOT_IN, name, K_NOT_IN, values), wheres());
    }

    public TheQueryBuilder<M> orWhereNotIn(String name, Iterable values) {
        return registerExpression(new MyExpression(grammar, K_OR, K_NOT_IN, name, K_NOT_IN, values), wheres());
    }

    public TheQueryBuilder<M> whereRaw(String sql) {
        return registerExpression(new MyExpression(grammar, K_AND, K_RAW, sql), wheres());
    }

    public TheQueryBuilder<M> orWhereRaw(String sql) {
        return registerExpression(new MyExpression(grammar, K_OR, K_RAW, sql), wheres());
    }

    /**
     * Register expression for where, join...
     *
     * @param exp Where condition
     * @return query builder
     */
    private TheQueryBuilder<M> registerExpression(MyExpression exp, List<MyExpression> expressions) {
        // Trim passing params
        exp.name = exp.name.trim();
        exp.operator = exp.operator.trim();

        // Validation
        if (grammar.invalidOperator(exp.operator)) {
            throw new RuntimeException("Invalid operator: " + exp.operator);
        }

        // Fix grammar
        grammar.fixGrammar(exp);

        // Register expression
        expressions.add(exp);

        return this;
    }

    public TheQueryBuilder<M> groupBy(String... names) {
        for (String name : names) {
            groupBys().add(new MyGroupBy(grammar, K_BASIC, name));
        }
        return this;
    }

    public TheQueryBuilder<M> groupByRaw(String sql) {
        groupBys().add(new MyGroupBy(grammar, K_RAW, sql));

        return this;
    }

    public TheQueryBuilder<M> orderBy(String name) {
        return orderBy(K_BASIC, name, K_ASC);
    }

    public TheQueryBuilder<M> orderBy(String name, String direction) {
        return orderBy(K_BASIC, name, direction);
    }

    public TheQueryBuilder<M> orderByAsc(String name) {
        return orderBy(K_BASIC, name, K_ASC);
    }

    public TheQueryBuilder<M> orderByDesc(String name) {
        return orderBy(K_BASIC, name, K_DESC);
    }

    public TheQueryBuilder<M> orderByRaw(String sql) {
        return orderBy(K_RAW, sql, K_ASC);
    }

    public TheQueryBuilder<M> orderByRaw(String sql, String direction) {
        return orderBy(K_RAW, sql, direction);
    }

    public TheQueryBuilder<M> orderByRawAsc(String sql) {
        return orderBy(K_RAW, sql, K_ASC);
    }

    public TheQueryBuilder<M> orderByRawDesc(String sql) {
        return orderBy(K_RAW, sql, K_DESC);
    }

    private TheQueryBuilder<M> orderBy(String type, String name, String direction) {
        orderBys().add(new MyOrderBy(grammar, type, name, direction));

        return this;
    }

    public TheQueryBuilder<M> having(String name, String operator, Object value) {
        return registerExpression(new MyExpression(grammar, K_AND, K_BASIC, name, operator, value), havings());
    }

    public TheQueryBuilder<M> orHaving(String name, String operator, Object value) {
        return registerExpression(new MyExpression(grammar, K_OR, K_BASIC, name, operator, value), havings());
    }

    public TheQueryBuilder<M> havingRaw(String sql) {
        return registerExpression(new MyExpression(grammar, K_AND, K_RAW, sql), havings());
    }

    public TheQueryBuilder<M> orHavingRaw(String sql) {
        return registerExpression(new MyExpression(grammar, K_OR, K_RAW, sql), havings());
    }

    public TheQueryBuilder<M> limit(long limit) {
        this.limit = limit;
        return this;
    }

    public TheQueryBuilder<M> offset(long offset) {
        this.offset = offset;

        return this;
    }

    @Nullable
    public M first() {
        this.limit = 1;
        List<M> rows = this.get();

        return rows == null || rows.size() == 0 ? null : rows.get(0);
    }

    public List<M> get() {
        MyGrammar grammar = this.grammar;
        String[] all = {
            "select",
            grammar.compileDistinct(distinct),
            grammar.compileSelects(nullableSelects),
            "from",
            grammar.wrapName(tableName),
            grammar.compileJoins(nullableJoins),
            grammar.compileWheres(nullableWheres),
            grammar.compileGroupBys(nullableGroupBys),
            grammar.compileHaving(nullableHavings),
            grammar.compileOrderBys(nullableOrderBys),
            grammar.compileLimit(limit),
            grammar.compileOffset(offset)
        };
        List<String> items = new ArrayList<>();
        for (String s : all) {
            if (s != null && s.length() > 0) {
                items.add(s);
            }
        }
        String query = DkStrings.join(' ', items);

        return connection.rawQuery(query.trim(), modelClass);
    }

    /**
     * Call this to set pair (key-value) for insert or update.
     */
    public TheQueryBuilder<M> set(String key, Object value) {
        if (pairs == null) {
            pairs = new ArrayMap<>();
        }
        pairs.put(key, value);

        return this;
    }

    public long insert() {
        return insert(pairs);
    }

    public void update() {
        update(pairs);
    }

    /**
     * @throws RuntimeException When invalid update
     */
    public void update(Map<String, Object> params) {
        if (DkMaps.isEmpty(params)) {
            throw new RuntimeException("Cannot update empty record");
        }
        String whereClause = grammar.compileWheres(wheres());
        String query = grammar.compileUpdateQuery(tableName, params, whereClause);

        connection.execQuery(query);
    }

    public void delete() {
        String whereClause = grammar.compileWheres(wheres());
        String query = grammar.compileDeleteQuery(tableName, whereClause);

        connection.execQuery(query.trim());
    }

    public long count() {
        return 0;
    }

    /**
     * Execute a query.
     */
    public void execute(String query) {
        connection.execQuery(query);
    }

    /**
     * Validate the correctness of sql query.
     */
    public TheQueryBuilder<M> validateSql() {
        throw new RuntimeException("Invalid sql");
    }

    /**
     * Validate the correctness of build query.
     */
    public TheQueryBuilder<M> validateQuery() {
        throw new RuntimeException("Invalid sql");
    }
}
