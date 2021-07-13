import java.util.ArrayList;

public class Listener extends SQLiteParserBaseListener {

	private ArrayList<Transaction> result = new ArrayList<Transaction>();
	private ArrayList<Schema> schemas = new ArrayList<Schema>();

	// This is needed to know if a sql statement belongs to a transaction or not.
	private boolean inTransaction = false;
	private boolean inSelect = false;
	private boolean inInsert = false;
	private boolean inDelete = false;

	// To store temporarly the collected information from each SQL statement.
	private String tableName = "empty";
	private ArrayList<String> usedColumn = new ArrayList<String>();
	private boolean hasCondition = false;
	private ArrayList<Expression> whereExpr = new ArrayList<Expression>();
	private boolean hasSubSelect = false;
	private int insertedRows = 0;
	private ArrayList<String> values = new ArrayList<String>();

	private boolean afterFunctionExpr = false;
	private boolean firstTableName = false;

	// transactions & operations ID counter.
	private int tID = 1;
	private int opID = 1;

	// ================= Setters and Getters ===================

	public ArrayList<Transaction> getResult() {
		return result;
	}

	public void setResult(ArrayList<Transaction> result) {
		this.result = result;
	}

	public ArrayList<Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(ArrayList<Schema> schemas) {
		this.schemas = schemas;
	}

	// =========================================================

	// This method takes the used table name by a sql statement and returns the
	// complete schema of this table.
	private Schema getMatchedSchema(String tableName) {
		for (int i = 0; i < schemas.size(); i++) {
			if (schemas.get(i).getName().equalsIgnoreCase(tableName))
				return schemas.get(i);
		}
		return null;
	}

	// This method checks if a WHERE clause uses a primary key.
	private boolean usePKey(String pKey, ArrayList<Expression> whereExpr) {
		boolean result = false;
		for (int i = 0; i < whereExpr.size(); i++) {
			if (whereExpr.get(i).getLeftExpr().equalsIgnoreCase(pKey))
				result = true;
		}
		return result;
	}

	// this method reset all variables that store the collected information from the
	// sql statements.
	private void resetAll() {
		tableName = "empty";
		usedColumn.clear();
		hasCondition = false;
		whereExpr.clear();
		hasSubSelect = false;
		insertedRows = 0;
		values.clear();
		afterFunctionExpr = false;
		firstTableName = false;
	}

	// =========================================================

	// This method indicates the beginnig of a transaction. It creates a new empty
	// transaction and switch the inTransaction flag to true.
	@Override
	public void enterBegin_stmt(SQLiteParser.Begin_stmtContext ctx) {
		inTransaction = true;
		Transaction t = new Transaction(tID);
		// reset the operation ID counter for the current transaction.
		opID = 1;
		// increment the transactions ID counter.
		tID++;
		result.add(t);
	}

	// This method indicates the termination of a transaction.
	@Override
	public void enterCommit_stmt(SQLiteParser.Commit_stmtContext ctx) {
		// reset the operation counter when exiting a transaction.
		opID = 1;
		inTransaction = false;
	}

	// ============ enter & exit functions of the 4 main statements ===============

	@Override
	public void enterSelect_core(SQLiteParser.Select_coreContext ctx) {
		if (inTransaction) {
			// check if the insert or delete statement has a sub select statement.
			if (inInsert || inDelete)
				hasSubSelect = true;
			else {
				// switch the flag true only if the select statement belongs to a transaction.
				inSelect = true;
				// check if the select statment has a condition.
				for (int i = 0; i < ctx.getChildCount(); i++) {
					if (ctx.getChild(i).getText().equalsIgnoreCase("where"))
						hasCondition = true;
				}
			}
		}
	}

	// This method converts a select statement to the abstract representation after
	// collecting the needed information, and stores the resulted opration in the
	// current transaction.
	@Override
	public void exitSelect_core(SQLiteParser.Select_coreContext ctx) {
		if (inTransaction && !inInsert && !inDelete) {
			inSelect = false;

			// Get the current transaction, which is the last transaction in result list.
			Transaction t = result.get(result.size() - 1);
			// get the table schema that this statment is using.
			Schema usedSchema = getMatchedSchema(tableName);

			// The object cannot be created if the sql statment works on unknown schema.
			if (usedSchema != null) {
				int schemaSize = usedSchema.getAttributes().size() + 1;
				String PrimaryKey = usedSchema.getpKey();

				Obj currentObj = new Obj(tableName);
				currentObj.setTableSize(schemaSize);

				// if the current object uses all columns, keep usedcolumns set empty "level 1".
				// if the current object doesn't use all attributes in the table.
				if (!usedColumn.contains("*") && usedColumn.size() < schemaSize - 1) {
					// add the used columns to the current object "level 2".
					for (int i = 0; i < usedColumn.size(); i++)
						currentObj.addColumn(usedColumn.get(i));
				}
				// check if the object can be a level 3 object.
				if (hasCondition) {
					// if the WHERE clause uses a primary key, update the object to level 3.
					if (usePKey(PrimaryKey, whereExpr)) {

						for (int i = 0; i < whereExpr.size(); i++) {
							String leftSide = whereExpr.get(i).getLeftExpr();
							String oper = whereExpr.get(i).getOperation();
							// get the expression that uses the primary key.
							if ((leftSide.equalsIgnoreCase(PrimaryKey)) && (oper.equals("="))) {
								currentObj.setpKeyName(whereExpr.get(i).getLeftExpr());
								currentObj.setpKeyValue(whereExpr.get(i).getRightExpr());
								// if the object is level 3 object and uses all columns, make sure that the used
								// columns set of the object is not empty. This is because the previous logic
								// keeps the usedcolumns set empty if the object uses all columns. This must be
								// the case for level 1 object but not for level 3 object.
								if (currentObj.getUsedColumns().size() == 0) {
									for (int k = 0; k < usedColumn.size(); i++)
										currentObj.addColumn(usedColumn.get(k));
								}
							}
						}
					}
				}
				// Then, create the operation and add it to the transaction.
				t.addOperation(opID, 'R', currentObj);
				// increament the operation counter.
				opID++;
			}
			// reset the used variables.
			resetAll();
		}
	}

	@Override
	public void enterInsert_stmt(SQLiteParser.Insert_stmtContext ctx) {
		if (inTransaction) {
			// switch the flag true only if the insert statement belongs to a transaction.
			inInsert = true;
			firstTableName = true;

			// This flag is needed to count how many closed brackets "inserted rows" there
			// are in this insert statement. These rows are set after the keyword VALUES.
			boolean afterValues = false;
			// check how many inseted rows there are in the insert statement.
			for (int i = 0; i < ctx.getChildCount(); i++) {
				if (ctx.getChild(i).getText().equalsIgnoreCase("values"))
					afterValues = true;
				if (afterValues) {
					// an inseterd row values can be detected if the closed bracket is the last
					// child or if we encounter this pattern " ) , ( "
					if (ctx.getChild(i).getText().equalsIgnoreCase(")")) {
						if (i == ctx.getChildCount() - 1)
							insertedRows++;
						else if (i != ctx.getChildCount() - 2) {
							if (ctx.getChild(i + 2).getText().equalsIgnoreCase("("))
								insertedRows++;
						}
					}
				}
			}
		}
	}

	// This method converts an insert statement to the abstract representation.
	@Override
	public void exitInsert_stmt(SQLiteParser.Insert_stmtContext ctx) {
		if (inTransaction) {
			inInsert = false;
			// Get the current transaction, which is the last transaction in result list.
			Transaction t = result.get(result.size() - 1);
			// get the table schema that this statment is using.
			Schema usedSchema = getMatchedSchema(tableName);

			// The object cannot be created if the sql statement works on unknown schema.
			if (usedSchema != null) {
				int schemaSize = usedSchema.getAttributes().size() + 1;
				String PrimaryKey = usedSchema.getpKey();
				boolean finish = false;

				// To know how many values we insert per row.
				int valuesSetSize = 0;

				// check if the object can be a level 3 object.
				if (!hasSubSelect) {
					if (usedColumn.contains(PrimaryKey)) {
						int pKeyIndex = usedColumn.indexOf(PrimaryKey);
						// extract each primary key name,value from each set of values, that belongs to
						// a single row, and create the matched object.
						if (insertedRows != 0) {
							valuesSetSize = values.size() / insertedRows;
							for (int i = pKeyIndex; i < values.size(); i = i + valuesSetSize) {
								Obj currentObj = new Obj(tableName);
								currentObj.setTableSize(schemaSize);
								currentObj.addColumns(usedSchema.getAttributes());
								currentObj.setpKeyName(PrimaryKey);
								currentObj.setpKeyValue(values.get(i));

								t.addOperation(opID, 'W', currentObj);
								opID++;
							}
							finish = true;
						}
					}
				}
				// if we cannont create level 3 objects
				if (!finish) {
					// consider the object a level 1 object.
					Obj lv1Object = new Obj(tableName);
					lv1Object.setTableSize(schemaSize);
					t.addOperation(opID, 'W', lv1Object);
					opID++;
				}
			}
			// reset the used variables.
			resetAll();
		}
	}

	@Override
	public void enterDelete_stmt(SQLiteParser.Delete_stmtContext ctx) {
		if (inTransaction) {
			// switch the flag true only if the delete statement belongs to a transaction.
			inDelete = true;

			// check if the delete statment has a condition.
			for (int i = 0; i < ctx.getChildCount(); i++) {
				if (ctx.getChild(i).getText().equalsIgnoreCase("where"))
					hasCondition = true;
			}
		}
	}

	// This method converts a delete statement to the abstract representation.
	@Override
	public void exitDelete_stmt(SQLiteParser.Delete_stmtContext ctx) {
		if (inTransaction) {
			inDelete = false;

			// Get the current transaction, which is the last transaction in result list.
			Transaction t = result.get(result.size() - 1);
			// get the table schema that this statment is using.
			Schema usedSchema = getMatchedSchema(tableName);

			// The object cannot be created if the sql statment works on unknown schema.
			if (usedSchema != null) {
				int schemaSize = usedSchema.getAttributes().size() + 1;
				String PrimaryKey = usedSchema.getpKey();

				Obj currentObj = new Obj(tableName);
				currentObj.setTableSize(schemaSize);

				if (hasCondition && !hasSubSelect) {
					// if the WHERE clause uses a primary key, update the object to level 3.
					if (usePKey(PrimaryKey, whereExpr)) {

						for (int i = 0; i < whereExpr.size(); i++) {
							String leftSide = whereExpr.get(i).getLeftExpr();
							String oper = whereExpr.get(i).getOperation();

							// get the expression that uses the primary key.
							if ((leftSide.equalsIgnoreCase(PrimaryKey)) && (oper.equals("="))) {
								// And update the object to level 3 by setting the pKey name and value.
								currentObj.setpKeyName(whereExpr.get(i).getLeftExpr());
								currentObj.setpKeyValue(whereExpr.get(i).getRightExpr());
								// delete statment will remove the entier row, so we need to set the used
								// columns by the object to be all attributes columns in the schema.
								currentObj.addColumns(usedSchema.getAttributes());
							}
						}
					}
				}
				// Then, create the operation and add it to the transaction.
				t.addOperation(opID, 'W', currentObj);
				// increament the operation counter.
				opID++;
			}
			// reset the used variables.
			resetAll();
		}
	}
	// ============================================================================

	// This method gets al coulmun names that a transaction-select-statement uses.
	// And store these column names inside the usedColumn list.
	@Override
	public void enterResult_column(SQLiteParser.Result_columnContext ctx) {
		if (inSelect)
			usedColumn.add(ctx.getText());
	}

	// This methode gets the table name that the current select statement reads from
	// and stores this name in the tableName variable.
	@Override
	public void enterTable_or_subquery(SQLiteParser.Table_or_subqueryContext ctx) {
		if (inSelect)
			tableName = ctx.getText();
	}

	// This method checks if the current visited expression is a WHERE expression.
	// If so, it will check the type of the expression. If it is a composite
	// expression, it will decompose it. Otherwise, it store the resultd expression
	// in whereExpr list. Each expression consists of 3 variables for leftside,
	// rightside and operation values.
	@Override
	public void enterExpr(SQLiteParser.ExprContext ctx) {
		// The accepted expression after WHERE keyword should have 3 children and no sub
		// select statement. Otherwise, it will not be useful to create level 3 object.
		if (inSelect && hasCondition && ctx.getChildCount() == 3 && !hasSubSelect) {
			// to skip the expressions that have parentheses.
			boolean skip = false;
			for (int i = 0; i < ctx.getChildCount(); i++) {
				if (ctx.getChild(0).getText().equals("("))
					skip = true;
			}
			if (!skip) {
				if (ctx.getChild(1).getText().equals("OR") || ctx.getChild(1).getText().equals("AND")) {
					// pass the expression if it is a composite expression.
				} else {
					whereExpr.add(new Expression(ctx.getChild(0).getText(), ctx.getChild(1).getText(),
							ctx.getChild(2).getText()));
				}
			}
		}

		// The accepted expression after WHERE keyword should have 3 children and no sub
		// select statement. Otherwise, it will not be useful to create level 3 object.
		if (inDelete && hasCondition && ctx.getChildCount() == 3 && !hasSubSelect) {
			// to skip the expressions that have parentheses.
			boolean skip = false;
			for (int i = 0; i < ctx.getChildCount(); i++) {
				if (ctx.getChild(0).getText().equals("("))
					skip = true;
			}
			if (!skip) {
				if (ctx.getChild(1).getText().equals("OR") || ctx.getChild(1).getText().equals("AND")) {
					// pass the expression if it is a composite expression.
				} else {
					whereExpr.add(new Expression(ctx.getChild(0).getText(), ctx.getChild(1).getText(),
							ctx.getChild(2).getText()));
				}
			}
		}

		// This is for insert statement to collect all inserted VALUES.
		if (inInsert) {
			if (afterFunctionExpr)
				afterFunctionExpr = false;
			else
				values.add(ctx.getText());
		}
	}

	// This method gets the table name that insert statement uses.
	@Override
	public void enterTable_name(SQLiteParser.Table_nameContext ctx) {
		if (inInsert && firstTableName) {
			tableName = ctx.getText();
			firstTableName = false;
		}

	}

	// This method gets al coulmun names that a insert statement uses.
	@Override
	public void enterColumn_name(SQLiteParser.Column_nameContext ctx) {
		if (inInsert)
			usedColumn.add(ctx.getText());
	}

	// this method is needed to know if an expression belongs to a function or not.
	@Override
	public void enterFunction_name(SQLiteParser.Function_nameContext ctx) {
		if (inInsert) {
			afterFunctionExpr = true;
		}
	}

	// This method gets the table name for the delete statement.
	@Override
	public void enterQualified_table_name(SQLiteParser.Qualified_table_nameContext ctx) {
		if (inDelete)
			tableName = ctx.getText();
	}
}
