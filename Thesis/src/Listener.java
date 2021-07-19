import java.util.ArrayList;

public class Listener extends SQLiteParserBaseListener {

	private ArrayList<Transaction> result = new ArrayList<Transaction>();
	private ArrayList<Schema> schemas = new ArrayList<Schema>();

	// This is needed to know if a sql statement belongs to a transaction or not.
	private boolean inTransaction = false;
	private boolean inSelect = false;
	private boolean inInsert = false;
	private boolean inDelete = false;
	private boolean inUpdate = false;

	// To store temporarly the collected information from each SQL statement.
	private String tableName = "empty";
	private String subSelectTableName = "empty";
	private String joinedTableName = "empty";
	private ArrayList<String> usedColumn = new ArrayList<String>();
	private ArrayList<String> usedColumn4Reading = new ArrayList<String>();
	private ArrayList<String> joinedUsedColumn = new ArrayList<String>();
	private boolean hasCondition = false;
	private boolean hasJoin = false;
	private ArrayList<Expression> whereExpr = new ArrayList<Expression>();
	private boolean hasSubSelect = false;
	private int insertedRows = 0;
	private ArrayList<String> values = new ArrayList<String>();

	private boolean inTableOrSubquery = false;
	private boolean inJoin = false;
	private boolean inResultColumn = false;
	private boolean hasOR = false;
	private boolean afterFunctionExpr = false;
	private boolean firstTableName = false;
	private boolean inQualifiedTableName = false;
	private boolean inSubSelect = false;
	private boolean inExpression = false;
	private boolean finishSetting = false;
	String indication = "empty";

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
		subSelectTableName = "empty";
		joinedTableName = "empty";
		usedColumn.clear();
		usedColumn4Reading.clear();
		joinedUsedColumn.clear();
		hasCondition = false;
		hasJoin = false;
		whereExpr.clear();
		hasSubSelect = false;
		insertedRows = 0;
		values.clear();
		inTableOrSubquery = false;
		inJoin = false;
		inResultColumn = false;
		hasOR = false;
		afterFunctionExpr = false;
		firstTableName = false;
		inQualifiedTableName = false;
		inSubSelect = false;
		inExpression = false;
		finishSetting = false;
		indication = "empty";
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
			if (inInsert || inDelete || inUpdate || inTableOrSubquery) {
				hasSubSelect = true;
				inSubSelect = true;
			} else {
				// switch the flag true only if the select statement belongs to a transaction.
				inSelect = true;
				// check if the select statment has a condition.
				for (int i = 0; i < ctx.getChildCount(); i++) {
					if (ctx.getChild(i).getText().equalsIgnoreCase("where"))
						hasCondition = true;
					if (ctx.getChild(i).getText().contains("JOIN"))
						hasJoin = true;
				}
			}
		}
	}

	// This method converts a select statement to the abstract representation after
	// collecting the needed information, and stores the resulted opration in the
	// current transaction.
	@Override
	public void exitSelect_core(SQLiteParser.Select_coreContext ctx) {
		// To create an operation that represents the sub-select statement.
		if (inSubSelect) {
			inSubSelect = false;
			// Get the current transaction, which is the last transaction in result list.
			Transaction t = result.get(result.size() - 1);
			// get the table schema that this statement is using.
			Schema usedSchema = getMatchedSchema(subSelectTableName);
			// The object cannot be created if the sql statement works on unknown schema.
			if (usedSchema != null) {
				int schemaSize = usedSchema.getAttributes().size() + 1;
				// create level 1 object because it is a SubSelect statement.
				Obj currentObj = new Obj(subSelectTableName);
				currentObj.setTableSize(schemaSize);
				// Then, create the operation and add it to the transaction.
				t.addOperation(opID, 'R', currentObj);
				// increament the operation counter and reset the SubSelect table name.
				opID++;
				subSelectTableName = "empty";
			}
		}
		// To create an operation that represents the main-select statement.
		if (inTransaction && !inInsert && !inDelete && !inUpdate && !inTableOrSubquery) {
			inSelect = false;

			// Get the current transaction, which is the last transaction in result list.
			Transaction t = result.get(result.size() - 1);
			// get the table schema that this statement is using.
			Schema usedSchema = getMatchedSchema(tableName);

			// The object cannot be created if the sql statement works on unknown schema.
			if (usedSchema != null) {
				int schemaSize = usedSchema.getAttributes().size() + 1;
				String PrimaryKey = usedSchema.getpKey();

				Obj currentObj = new Obj(tableName);
				currentObj.setTableSize(schemaSize);

				// if the current object uses all columns, keep usedcolumns set empty "level 1".
				if (!usedColumn.contains("*") && usedColumn.size() < schemaSize - 1) {
					// if the current object doesn't use all attributes in the table.
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
							// get the expression that uses the primary key.
							if (leftSide.equalsIgnoreCase(PrimaryKey)) {
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
			// if the select statement has a join, create an aditional reading operation.
			if (hasJoin) {
				Schema usedSchema4Join = getMatchedSchema(joinedTableName);
				if (usedSchema4Join != null) {
					int schemaSize4Join = usedSchema4Join.getAttributes().size() + 1;
					Obj object4Join = new Obj(joinedTableName);
					object4Join.setTableSize(schemaSize4Join);
					if (!joinedUsedColumn.contains("*") && joinedUsedColumn.size() < schemaSize4Join - 1) {
						for (int i = 0; i < joinedUsedColumn.size(); i++)
							object4Join.addColumn(joinedUsedColumn.get(i));
					}
					t.addOperation(opID, 'R', object4Join);
					opID++;
				}
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

							// get the expression that uses the primary key.
							if (leftSide.equalsIgnoreCase(PrimaryKey)) {
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

	@Override
	public void enterUpdate_stmt(SQLiteParser.Update_stmtContext ctx) {
		if (inTransaction) {
			// switch the flag true only if the update statement belongs to a transaction.
			inUpdate = true;

			// check if the update statment has a condition.
			for (int i = 0; i < ctx.getChildCount(); i++) {
				if (ctx.getChild(i).getText().equalsIgnoreCase("where")) {
					hasCondition = true;
					// By using this indication, we can know if an expression belongs to the setting
					// part "after SET and before WHERE" or not.
					indication = ctx.getChild(i - 1).getText();
				}
				if (ctx.getChild(i).getText().equalsIgnoreCase("from"))
					indication = ctx.getChild(i - 1).getText();
			}
		}
	}

	@Override
	public void exitUpdate_stmt(SQLiteParser.Update_stmtContext ctx) {
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

				// First we check if we have read columns to create a read operation.
				if (usedColumn4Reading.size() != 0) {
					Obj currentObj = new Obj(tableName);
					currentObj.setTableSize(schemaSize);
					// if the current object uses all columns, keep usedcolumns set empty "level 1".
					if (usedColumn4Reading.size() < schemaSize - 1) {
						// if the current object doesn't use all attributes in the table.
						// add the used columns to the current object "level 2".
						for (int i = 0; i < usedColumn4Reading.size(); i++)
							currentObj.addColumn(usedColumn4Reading.get(i));
					}
					// check if the object can be a level 3 object.
					if (hasCondition) {
						// if the WHERE clause uses a primary key, update the object to level 3.
						if (usePKey(PrimaryKey, whereExpr)) {
							for (int i = 0; i < whereExpr.size(); i++) {
								String leftSide = whereExpr.get(i).getLeftExpr();
								// get the expression that uses the primary key.
								if (leftSide.equalsIgnoreCase(PrimaryKey)) {
									currentObj.setpKeyName(whereExpr.get(i).getLeftExpr());
									currentObj.setpKeyValue(whereExpr.get(i).getRightExpr());
									// if the object uses all columns, make sure to add these columns because it is
									// a level 3 now "not level 1".
									if (currentObj.getUsedColumns().size() == 0)
										currentObj.addColumns(usedSchema.getAttributes());
								}
							}
						}
					}
					t.addOperation(opID, 'R', currentObj);
					opID++;
				}

				// Now we create the writing operation, which uses the same logic.
				Obj currentObj = new Obj(tableName);
				currentObj.setTableSize(schemaSize);
				// check if it can be level 2.
				if (!usedColumn.contains("*") && usedColumn.size() < schemaSize - 1 && !hasSubSelect) {
					for (int i = 0; i < usedColumn.size(); i++)
						currentObj.addColumn(usedColumn.get(i));
				}
				// check if it can be level 3.
				if (hasCondition && !hasSubSelect) {
					if (usePKey(PrimaryKey, whereExpr)) {
						for (int i = 0; i < whereExpr.size(); i++) {
							String leftSide = whereExpr.get(i).getLeftExpr();
							// get the expression that uses the primary key.
							if (leftSide.equalsIgnoreCase(PrimaryKey)) {
								currentObj.setpKeyName(whereExpr.get(i).getLeftExpr());
								currentObj.setpKeyValue(whereExpr.get(i).getRightExpr());
								// if the object uses all columns, make sure to add these columns because it is
								// a level 3 now "not level 1".
								if (currentObj.getUsedColumns().size() == 0)
									currentObj.addColumns(usedSchema.getAttributes());
							}
						}
					}
				}
				t.addOperation(opID, 'W', currentObj);
				opID++;
			}
			// reset the used variables.
			resetAll();
		}
	}

	// ============================================================================

	// This method helps getting coulmun names and table names that a
	// select statement uses.
	@Override
	public void enterResult_column(SQLiteParser.Result_columnContext ctx) {
		if (inSelect)
			inResultColumn = true;
	}

	@Override
	public void exitResult_column(SQLiteParser.Result_columnContext ctx) {
		if (inSelect)
			inResultColumn = false;
	}

	// This methode helps getting the table name that the current select statement,
	// which has no join, reads from.
	@Override
	public void enterTable_or_subquery(SQLiteParser.Table_or_subqueryContext ctx) {
		if (inSelect && !inSubSelect && !hasJoin)
			inTableOrSubquery = true;
		if (inSubSelect)
			subSelectTableName = ctx.getText();
	}

	@Override
	public void exitTable_or_subquery(SQLiteParser.Table_or_subqueryContext ctx) {
		if (inSelect && !inSubSelect && !hasJoin)
			inTableOrSubquery = false;
	}

	// This method extract the different expressions in the statements.
	@Override
	public void enterExpr(SQLiteParser.ExprContext ctx) {
		// This part checks if the current visited expression is a WHERE expression.
		// If so, it will check the type of the expression. If it is a composite
		// expression, it will decompose it. Otherwise, it store the resultd expression
		// in whereExpr list. Each expression consists of 3 variables for leftside,
		// rightside and operation values
		if (inSelect || inDelete || inUpdate) {
			// The accepted expression after WHERE keyword should have 3 children.
			// Otherwise, it will not be useful to create level 3 object.
			if (hasCondition && ctx.getChildCount() == 3) {

				// ignore the expressions that have parentheses "Sub Statement" or inside
				// a sub select.
				if (!ctx.getChild(0).getText().equals("(") && !inSubSelect && !inJoin) {
					// pass the expression if it is a composite expression.
					if (ctx.getChild(1).getText().equals("OR") || ctx.getChild(1).getText().equals("AND")) {
						// if the expressions after WHERE are compined with "OR", we don't need these
						// expressions because they will never lead to a single record even if one of
						// them uses the primary key.
						if (ctx.getChild(1).getText().equals("OR"))
							hasOR = true;
					} else {
						// Only expressions that use "=" are helpful for our cases.
						if (ctx.getChild(1).getText().equals("=") && !hasOR)
							whereExpr.add(new Expression(ctx.getChild(0).getText(), ctx.getChild(1).getText(),
									ctx.getChild(2).getText()));
					}
				}
			}
		}

		if (inUpdate && !finishSetting)
			inExpression = true;

		// This is for insert statement to collect all inserted VALUES, which are
		// expressions of lenght 1 or 4.
		if (inInsert) {
			if (afterFunctionExpr)
				afterFunctionExpr = false;
			else
				values.add(ctx.getText());
		}
	}

	@Override
	public void exitExpr(SQLiteParser.ExprContext ctx) {
		if (inUpdate) {
			if (ctx.getText().equalsIgnoreCase(indication))
				finishSetting = true;

			inExpression = false;
		}
	}

	// This method gets the table name that a statement uses.
	@Override
	public void enterTable_name(SQLiteParser.Table_nameContext ctx) {
		// To get the table name for a select statement whether it is directly written
		// after the keyword FROM or inferred from a sub-query after the keyword FROM.
		if (inSelect && inTableOrSubquery)
			tableName = ctx.getText();

		// To get the table name of the main table that is joined with another select.
		if (inSelect && inResultColumn && hasJoin && tableName.equalsIgnoreCase("empty"))
			tableName = ctx.getText();
		// To get the table name of the joined table.
		if (inSelect && inResultColumn && hasJoin && !tableName.equalsIgnoreCase("empty")
				&& joinedTableName.equalsIgnoreCase("empty")) {
			if (!ctx.getText().equalsIgnoreCase(tableName))
				joinedTableName = ctx.getText();
		}
		// To get the table name of the insert statement.
		if (inInsert && firstTableName) {
			tableName = ctx.getText();
			firstTableName = false;
		}
		// To get the table name of the delete or update statements.
		if (inQualifiedTableName) {
			tableName = ctx.getText();
			inQualifiedTableName = false;
		}
	}

	// This method gets al coulmun names that a statement uses.
	@Override
	public void enterColumn_name(SQLiteParser.Column_nameContext ctx) {
		// To collect the used columns in select statement that has no join.
		if (inSelect && inResultColumn && !hasJoin)
			usedColumn.add(ctx.getText());

		// To collect the used columns in the main table & joined table in a select
		// statement that has join.
		if (inSelect && hasJoin) {
			// column_name node in a select statement that has join will always belongs to a
			// parent that has 3 childern.
			if (ctx.getParent().getChild(0).getText().equalsIgnoreCase(tableName)) {
				if (!usedColumn.contains(ctx.getText()))
					usedColumn.add(ctx.getText());
			}
			if (ctx.getParent().getChild(0).getText().equalsIgnoreCase(joinedTableName)) {
				if (!joinedUsedColumn.contains(ctx.getText()))
					joinedUsedColumn.add(ctx.getText());
			}
		}

		// To collect the used columns in insert statement.
		if (inInsert && !inSubSelect)
			usedColumn.add(ctx.getText());

		// To collect the used columns for writing operation in update statement.
		if (inUpdate && !finishSetting && !inExpression && !inSubSelect)
			usedColumn.add(ctx.getText());
		// To collect the used columns for reading operation in update statement.
		if (inUpdate && !finishSetting && inExpression && !inSubSelect)
			usedColumn4Reading.add(ctx.getText());
	}

	@Override
	public void enterJoin_clause(SQLiteParser.Join_clauseContext ctx) {
		if (inSelect)
			inJoin = true;
	}

	@Override
	public void exitJoin_clause(SQLiteParser.Join_clauseContext ctx) {
		if (inSelect)
			inJoin = false;
	}

	// this method is needed to know if an expression belongs to a function or not.
	@Override
	public void enterFunction_name(SQLiteParser.Function_nameContext ctx) {
		if (inInsert) {
			afterFunctionExpr = true;
		}
	}

	// This method gets the table name of the delete or update statements.
	@Override
	public void enterQualified_table_name(SQLiteParser.Qualified_table_nameContext ctx) {
		if (inDelete || inUpdate)
			// this flag is needed to extract only the table_name from the Qualified table
			// name node. Because this node can have more info beside table_name.
			inQualifiedTableName = true;
	}
}
