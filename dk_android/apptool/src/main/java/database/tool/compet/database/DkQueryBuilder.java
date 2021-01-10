/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tool.compet.core.util.DkStrings;
import tool.compet.core.type.DkCallback;

import static tool.compet.database.MyConst.K_AND;
import static tool.compet.database.MyConst.K_ASC;
import static tool.compet.database.MyConst.K_BASIC;
import static tool.compet.database.MyConst.K_DESC;
import static tool.compet.database.MyConst.K_EQ;
import static tool.compet.database.MyConst.K_IN;
import static tool.compet.database.MyConst.K_INNER;
import static tool.compet.database.MyConst.K_IS_NOT_NULL;
import static tool.compet.database.MyConst.K_IS_NULL;
import static tool.compet.database.MyConst.K_LEFT;
import static tool.compet.database.MyConst.K_NOT_IN;
import static tool.compet.database.MyConst.K_NOT_NULL;
import static tool.compet.database.MyConst.K_NULL;
import static tool.compet.database.MyConst.K_OR;
import static tool.compet.database.MyConst.K_RAW;
import static tool.compet.database.MyConst.K_RIGHT;

/**
 * This class receives a database connection, provides query execution.
 * You can build queries and execute them instead of raw query.
 * <p></p>
 * When you query with customized model, make sure each field in your model was annotated with
 * {@link SerializedName} since we use it to map column in retrieved {@link Cursor} with your model.
 *
 * @author darkcompet
 */
public class DkQueryBuilder<M> { // M: model
    private final MyConnection connection;
    private final String tableName;
    private final Class<M> modelClass;
    private final DkGrammar grammar;

    private List<MySelection> __selects; // don't use directly
    private boolean distinct;
    private List<MyJoin> __joins; // don't use directly
    private List<MyExpression> __wheres; // don't use directly
    private List<MyOrderBy> __orderBys; // don't use directly
    private List<MyGroupBy> __groupBys; // don't use directly
    private List<MyExpression> __havings; // don't use directly
    private long limit = Long.MIN_VALUE;
    private long offset = Long.MIN_VALUE;

    public DkQueryBuilder(MyConnection connection, String tableName, Class<M> modelClass) {
        this.connection = connection;
        this.tableName = tableName;
        this.modelClass = modelClass;
        this.grammar = new DkGrammar();
    }

    public DkQueryBuilder(MyConnection connection, String tableName, Class<M> modelClass, DkGrammar grammar) {
        this.connection = connection;
        this.tableName = tableName;
        this.modelClass = modelClass;
        this.grammar = grammar;
    }

    private List<MySelection> selects() {
        return __selects != null ? __selects : (__selects = new ArrayList<>());
    }
    
    private List<MyJoin> joins() {
        return __joins != null ? __joins : (__joins = new ArrayList<>());
    }
    
    private List<MyExpression> wheres() {
        return __wheres != null ? __wheres : (__wheres = new ArrayList<>());
    }

    private List<MyOrderBy> orderBys() {
        return __orderBys != null ? __orderBys : (__orderBys = new ArrayList<>());
    }

    private List<MyGroupBy> groupBys() {
        return __groupBys != null ? __groupBys : (__groupBys = new ArrayList<>());
    }

    private List<MyExpression> havings() {
        return __havings != null ? __havings : (__havings = new ArrayList<>());
    }

    public DkQueryBuilder<M> select(String... names) {
        for (String name : names) {
            selects().add(new MySelection(grammar, K_BASIC, name));
        }
        return this;
    }

    /**
     * @param subQuery String for eg,. "count(id)"
     * @return this
     */
    public DkQueryBuilder<M> selectRaw(String subQuery) {
        return this.selectRaw(subQuery, null);
    }

    public DkQueryBuilder<M> selectRaw(String subQuery, String alias) {
        selects().add(new MySelection(grammar, K_RAW, subQuery, alias));
        return this;
    }

    public DkQueryBuilder<M> distinct() {
        this.distinct = true;
        return this;
    }

    public DkQueryBuilder<M> leftJoin(String joinTable, String name, String operator, Object value) {
        return registerJoin(K_LEFT, joinTable, name, operator, value);
    }

    public DkQueryBuilder<M> rightJoin(String joinTable, String name, String operator, Object value) {
        return registerJoin(K_RIGHT, joinTable, name, operator, value);
    }

    private DkQueryBuilder<M> join(String joinTable, String name, String operator, Object value) {
        return registerJoin(K_INNER, joinTable, name, operator, value);
    }

    public DkQueryBuilder<M> leftJoin(String joinTable, DkCallback<DkJoiner> joinerCallback) {
        return join(K_LEFT, joinTable, joinerCallback);
    }

    public DkQueryBuilder<M> rightJoin(String joinTable, DkCallback<DkJoiner> joinerCallback) {
        return join(K_RIGHT, joinTable, joinerCallback);
    }

    public DkQueryBuilder<M> join(String joinTable, DkCallback<DkJoiner> joinerCallback) {
        return join(K_INNER, joinTable, joinerCallback);
    }

    private DkQueryBuilder<M> join(String joinType, String joinTable, DkCallback<DkJoiner> joinerCallback) {
        // Send joiner to callback and receive condition from callbacker
        DkJoiner joiner = new DkJoiner(this.grammar);
        joinerCallback.call(joiner);

        joins().add(new MyJoin(grammar, joinType, joinTable, joiner));
        return this;
    }

    private DkQueryBuilder<M> registerJoin(String joinType, String joinTable, String name, String operator, Object value) {
        joins().add(new MyJoin(grammar, joinType, joinTable, name, operator, value));
        return this;
    }

    /**
     * This is equal where, short where for equal.
     * @param name String table column name
     * @param value Object target value which matches with value of the field
     */
    public DkQueryBuilder<M> where(String name, Object value) {
        return registerExpression(new MyExpression(grammar, K_BASIC, K_AND, name, K_EQ, value), wheres());
    }

    public DkQueryBuilder<M> orWhere(String name, Object value) {
        return registerExpression(new MyExpression(grammar, K_BASIC, K_OR, name, K_EQ, value), wheres());
    }

    public DkQueryBuilder<M> where(String name, String operator, Object value) {
        return registerExpression(new MyExpression(grammar, K_BASIC, K_AND, name, operator, value), wheres());
    }

    public DkQueryBuilder<M> orWhere(String name, String operator, Object value) {
        return registerExpression(new MyExpression(grammar, K_BASIC, K_OR, name, operator, value), wheres());
    }

    public DkQueryBuilder<M> whereNull(String name) {
        return registerExpression(new MyExpression(grammar, K_NULL, K_AND, name, K_IS_NULL), wheres());
    }

    public DkQueryBuilder<M> orWhereNull(String name) {
        return registerExpression(new MyExpression(grammar, K_NULL, K_OR, name, K_IS_NULL), wheres());
    }

    public DkQueryBuilder<M> whereNotNull(String name) {
        return registerExpression(new MyExpression(grammar, K_NOT_NULL, K_AND, name, K_IS_NOT_NULL), wheres());
    }

    public DkQueryBuilder<M> orWhereNotNull(String name) {
        return registerExpression(new MyExpression(grammar, K_NOT_NULL, K_OR, name, K_IS_NOT_NULL), wheres());
    }

    public DkQueryBuilder<M> whereIn(String name, Iterable values) {
        return registerExpression(new MyExpression(grammar, K_IN, K_AND, name, K_IN, values), wheres());
    }

    public DkQueryBuilder<M> orWhereIn(String name, Iterable values) {
        return registerExpression(new MyExpression(grammar, K_IN, K_OR, name, K_IN, values), wheres());
    }

    public DkQueryBuilder<M> whereNotIn(String name, Iterable values) {
        return registerExpression(new MyExpression(grammar, K_NOT_IN, K_AND, name, K_NOT_IN, values), wheres());
    }

    public DkQueryBuilder<M> orWhereNotIn(String name, Iterable values) {
        return registerExpression(new MyExpression(grammar, K_NOT_IN, K_OR, name, K_NOT_IN, values), wheres());
    }

    public DkQueryBuilder<M> whereRaw(String sql) {
        return registerExpression(new MyExpression(grammar, K_RAW, K_AND, sql), wheres());
    }

    public DkQueryBuilder<M> orWhereRaw(String sql) {
        return registerExpression(new MyExpression(grammar, K_RAW, K_OR, sql), wheres());
    }

    /**
     * Register expression for where, join...
     *
     * @param exp Where condition
     * @return query builder
     */
    private DkQueryBuilder<M> registerExpression(MyExpression exp, List<MyExpression> expressions) {
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

    public DkQueryBuilder<M> groupBy(String... names) {
        for (String name : names) {
            groupBys().add(new MyGroupBy(this.grammar, K_BASIC, name));
        }
        return this;
    }

    public DkQueryBuilder<M> groupByRaw(String sql) {
        groupBys().add(new MyGroupBy(this.grammar, K_RAW, sql));
        return this;
    }

    public DkQueryBuilder<M> orderBy(String name) {
        return orderBy(K_BASIC, name, K_ASC);
    }

    public DkQueryBuilder<M> orderBy(String name, String direction) {
        return orderBy(K_BASIC, name, direction);
    }

    public DkQueryBuilder<M> orderByAsc(String name) {
        return orderBy(K_BASIC, name, K_ASC);
    }

    public DkQueryBuilder<M> orderByDesc(String name) {
        return orderBy(K_BASIC, name, K_DESC);
    }

    public DkQueryBuilder<M> orderByRaw(String sql) {
        return orderBy(K_RAW, sql, K_ASC);
    }

    public DkQueryBuilder<M> orderByRaw(String sql, String direction) {
        return orderBy(K_RAW, sql, direction);
    }

    public DkQueryBuilder<M> orderByRawAsc(String sql) {
        return orderBy(K_RAW, sql, K_ASC);
    }

    public DkQueryBuilder<M> orderByRawDesc(String sql) {
        return orderBy(K_RAW, sql, K_DESC);
    }

    private DkQueryBuilder<M> orderBy(String type, String name, String direction) {
        orderBys().add(new MyOrderBy(grammar, type, name, direction));
        return this;
    }

    public DkQueryBuilder<M> having(String name, String operator, Object value) {
        return registerExpression(new MyExpression(grammar, K_BASIC, K_AND, name, operator, value), havings());
    }

    public DkQueryBuilder<M> orHaving(String name, String operator, Object value) {
        return registerExpression(new MyExpression(grammar, K_BASIC, K_OR, name, operator, value), havings());
    }

    public DkQueryBuilder<M> havingRaw(String sql) {
        return registerExpression(new MyExpression(grammar, K_RAW, K_AND, sql), havings());
    }

    public DkQueryBuilder<M> orHavingRaw(String sql) {
        return registerExpression(new MyExpression(grammar, K_RAW, K_OR, sql), havings());
    }

    public DkQueryBuilder<M> limit(long limit) {
        this.limit = limit;
        return this;
    }

    public DkQueryBuilder<M> offset(long offset) {
        this.offset = offset;
        return this;
    }

    // CRUD: create
    public void insert(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            throw new RuntimeException("Cannot insert empty record");
        }
        String query = grammar.compileInsertQuery(this.tableName, params);
        this.connection.executeQuery(query);
    }

    public M first() {
        this.limit = 1;
        List<M> rows = get();
        return rows == null || rows.size() == 0 ? null : rows.get(0);
    }

    // CRUD: read
    public List<M> get() {
        String[] all = {
            "select",
            grammar.compileDistinct(this.distinct),
            grammar.compileSelects(this.__selects),
            "from",
            grammar.wrapName(this.tableName),
            grammar.compileJoins(this.__joins),
            grammar.compileWheres(this.__wheres),
            grammar.compileGroupBys(this.__groupBys),
            grammar.compileHaving(this.__havings),
            grammar.compileOrderBys(this.__orderBys),
            grammar.compileLimit(this.limit),
            grammar.compileOffset(this.offset)
        };
        List<String> items = new ArrayList<>();
        for (String s : all) {
            if (s != null && s.length() > 0) {
                items.add(s);
            }
        }
        String query = DkStrings.join(' ', items);

        return this.connection.rawQuery(query.trim(), this.modelClass);
    }

    // CRUD: update
    public void update(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            throw new RuntimeException("Cannot update empty record");
        }
        String whereClause = grammar.compileWheres(this.wheres());
        String query = grammar.compileUpdateQuery(this.tableName, params, whereClause);
        this.connection.executeQuery(query);
    }

    // CRUD: delete
    public void delete() {
        String whereClause = grammar.compileWheres(this.wheres());
        String query = grammar.compileDelete(this.tableName, whereClause);
        this.connection.executeQuery(query.trim());
    }

    public long count() {
        return 0;
    }

    /**
     * Execute raw query.
     */
    public void execute(String query) {
        this.connection.executeQuery(query);
    }

    /**
     * Validate the correctness of sql query.
     */
    public DkQueryBuilder<M> validateSql() {
        throw new RuntimeException("Invalid sql");
    }

    /**
     * Validate the correctness of build query.
     */
    public DkQueryBuilder<M> validateQuery() {
        throw new RuntimeException("Invalid sql");
    }
}
