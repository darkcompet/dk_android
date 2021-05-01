/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import tool.compet.core.DkCollections;
import tool.compet.core.DkConst;
import tool.compet.core.DkRunner1;
import tool.compet.core.DkStrings;
import tool.compet.core.reflection.DkReflectionFinder;

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
 * This is base query builder for various query language as sqlite, mysql, postgresql...
 * It receives a database connection, provides a query execution.
 * Caller can build and execute a query from this instead of manual sql.
 *
 * @author darkcompet
 */
public abstract class TheQueryBuilder<M> {
	// Enable this to reduce mistake when query
	private boolean enableStrictMode = true;

	/**
	 * @return Last insert row id.
	 */
	public abstract long lastInsertRowId();

	protected final TheDao<M> dao;
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

	// Package privated (this class is open for usage, not for create)
	TheQueryBuilder(TheDao<M> dao, MyGrammar grammar, String tableName, Class<M> modelClass) {
		this.dao = dao;
		this.grammar = grammar;
		this.tableName = tableName;
		this.modelClass = modelClass;
	}

	// region Build query

	public TheQueryBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public TheQueryBuilder<M> model(Class<M> modelClass) {
		this.modelClass = modelClass;
		return this;
	}

	protected List<MySelection> selects() {
		return nullableSelects != null ? nullableSelects : (nullableSelects = new ArrayList<>());
	}

	protected List<MyJoin> joins() {
		return nullableJoins != null ? nullableJoins : (nullableJoins = new ArrayList<>());
	}

	protected List<MyExpression> wheres() {
		return nullableWheres != null ? nullableWheres : (nullableWheres = new ArrayList<>());
	}

	protected List<MyOrderBy> orderBys() {
		return nullableOrderBys != null ? nullableOrderBys : (nullableOrderBys = new ArrayList<>());
	}

	protected List<MyGroupBy> groupBys() {
		return nullableGroupBys != null ? nullableGroupBys : (nullableGroupBys = new ArrayList<>());
	}

	protected List<MyExpression> havings() {
		return nullableHavings != null ? nullableHavings : (nullableHavings = new ArrayList<>());
	}

	/**
	 * Select one or multiple columns.
	 */
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

	/**
	 * Left join with another table on `first = second` condition.
	 */
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

	public TheQueryBuilder<M> leftJoin(String joinTable, DkRunner1<MyJoiner> joinerCallback) {
		return registerMultipleJoin(K_LEFT, joinTable, joinerCallback);
	}

	public TheQueryBuilder<M> rightJoin(String joinTable, DkRunner1<MyJoiner> joinerCallback) {
		return registerMultipleJoin(K_RIGHT, joinTable, joinerCallback);
	}

	public TheQueryBuilder<M> join(String joinTable, DkRunner1<MyJoiner> joinerCallback) {
		return registerMultipleJoin(K_INNER, joinTable, joinerCallback);
	}

	private TheQueryBuilder<M> registerMultipleJoin(String joinType, String joinTable, DkRunner1<MyJoiner> joinerCallback) {
		// Send joiner to callback and receive condition from callbacker
		MyJoiner joiner = new MyJoiner(grammar);
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
	 *
	 * @param name  String table column name
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

	public TheQueryBuilder<M> whereIn(String name, Iterable<?> values) {
		return registerExpression(new MyExpression(grammar, K_AND, K_IN, name, K_IN, values), wheres());
	}

	public TheQueryBuilder<M> orWhereIn(String name, Iterable<?> values) {
		return registerExpression(new MyExpression(grammar, K_OR, K_IN, name, K_IN, values), wheres());
	}

	public TheQueryBuilder<M> whereNotIn(String name, Iterable<?> values) {
		return registerExpression(new MyExpression(grammar, K_AND, K_NOT_IN, name, K_NOT_IN, values), wheres());
	}

	public TheQueryBuilder<M> orWhereNotIn(String name, Iterable<?> values) {
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
	public TheQueryBuilder<M> offset(long offset) {
		this.offset = offset;
		return this;
	}

	public TheQueryBuilder<M> limit(long limit) {
		this.limit = limit;
		return this;
	}

	// endregion Build query

	// region CRUD

	@Nullable
	public M first() {
		this.limit = 1;
		List<M> rows = this.get();

		return rows == null || rows.size() == 0 ? null : rows.get(0);
	}

	/**
	 * Subclass should override this to perform more efficient count calculation.
	 */
	public long count() {
		return DkCollections.sizeOf(get());
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

		// Maybe should validate the query if possible :D
		String query = DkStrings.join(DkConst.SPACE_CHAR, items).trim();

		return dao.rawQuery(query, modelClass);
	}

	public long insert(Object model) {
		return insert(model, null);
	}

	/**
	 * By default, we only support insert a model which has fields annotated with `DkColumnInfo`.
	 * For trigger before insert, caller can override `onUpdate()` at dao class.
	 *
	 * @param model Must be model which contains fields annotated with `DkColumnInfo`
	 * @param fillable When null is passed, we only filter fields which has fillable in model
	 */
	public long insert(Object model, @Nullable String[] fillable) {
		// Trigger before make insert params
		dao.onInsert(model);

		Map<String, Object> insertParams = requireInsertParams(model, fillable);
		String query = grammar.compileInsertQuery(tableName, insertParams);

		dao.execQuery(query);

		return lastInsertRowId();
	}

	public void update(Object model) {
		update(model, null);
	}

	/**
	 * By default, we only support update a model which has fields annotated with `DkColumnInfo`.
	 * For trigger before update, caller can override `onUpdate()` at dao class.
	 *
	 * @param model Must be model which contains fields annotated with `DkColumnInfo`
	 * @param fillable When null is passed, we only filter fields which has fillable in model
	 */
	public void update(Object model, @Nullable String[] fillable) {
		// Trigger before make update params
		dao.onUpdate(model);

		Map<String, Object> updateParams = requireUpdateParams(model, fillable);
		String whereClause = grammar.compileWheres(wheres());
		if (enableStrictMode && wheres().size() == 0) {
			throw new RuntimeException("Failed since perform update without any condition in strict mode");
		}
		String query = grammar.compileUpdateQuery(tableName, updateParams, whereClause);

		dao.execQuery(query);
	}

	public void delete() {
		String whereClause = grammar.compileWheres(wheres());
		String query = grammar.compileDeleteQuery(tableName, whereClause);

		dao.execQuery(query.trim());
	}

	/**
	 * Execute a query.
	 */
	public void execute(String query) {
		dao.execQuery(query);
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

	// endregion CRUD

	// region Get/Set

	public boolean isEnableStrictMode() {
		return enableStrictMode;
	}

	public void setEnableStrictMode(boolean enableStrictMode) {
		this.enableStrictMode = enableStrictMode;
	}

	// endregion Get/Set

	// region Private

	private Map<String, Object> requireInsertParams(Object model, String[] fillable) {
		Map<String, Object> params = new ArrayMap<>();
		Set<String> fillableCols = (fillable == null) ? null : DkCollections.asSet(fillable);

		Class modelClass = model.getClass();
		List<Field> fields = DkReflectionFinder.getIns().findFields(modelClass, DkColumnInfo.class);

		for (Field field : fields) {
			try {
				DkColumnInfo colInfo = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class));
				// Ignore pk
				if (colInfo.primaryKey()) {
					continue;
				}

				String colName = colInfo.name();

				// Ignore by default fillable at model
				if (fillableCols == null) {
					if (! colInfo.fillable()) continue;
				}
				// Ignore by caller requested fillable
				else {
					if (! fillableCols.contains(colName)) continue;
				}

				Object value = MyGrammarHelper.toDbValue(field.get(model));

				params.put(colName, value);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		if (params.size() == 0) {
			throw new RuntimeException("Must provide fillable columns in model");
		}

		return params;
	}

	private Map<String, Object> requireUpdateParams(Object model, @Nullable String[] fillable) {
		Map<String, Object> params = new ArrayMap<>();
		Set<String> fillableCols = (fillable == null) ? null : DkCollections.asSet(fillable);

		Class modelClass = model.getClass();
		List<Field> fields = DkReflectionFinder.getIns().findFields(modelClass, DkColumnInfo.class);

		// Collect update params from model fields
		for (Field field : fields) {
			try {
				DkColumnInfo colInfo = Objects.requireNonNull(field.getAnnotation(DkColumnInfo.class));
				// Ignore pk
				if (colInfo.primaryKey()) {
					continue;
				}

				String colName = colInfo.name();

				// When caller does not pass fillable, let ignore columns which is not fillable at model definition
				if (fillableCols == null) {
					if (! colInfo.fillable()) continue;
				}
				// Ignore columns which is not in caller requested fillable
				else {
					if (! fillableCols.contains(colName)) continue;
				}

				Object value = MyGrammarHelper.toDbValue(field.get(model));

				params.put(colName, value);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		if (params.size() == 0) {
			throw new RuntimeException("The model must contain fillable fields");
		}

		return params;
	}

	// endregion Private
}
