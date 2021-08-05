import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.io.IOException;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public abstract class Translator {

	// create a dictionary to map the string representation of an object "the key"
	// with a short code "the value".
	private static HashMap<String, String> dicCodes = new HashMap<>();

	// This method converts the string representation of every object in each
	// transaction to a short string code and stores it inside the object container.
	private static void convertObjects(ArrayList<Transaction> result) {
		// Create 3 counters for each object level.
		int lv1 = 1;
		int lv2 = 1;
		int lv3 = 1;

		for (int i = 0; i < result.size(); i++) {
			Transaction t = result.get(i);
			ArrayList<Operation> operations = t.getOperations();

			for (int j = 0; j < operations.size(); j++) {
				Operation op = operations.get(j);
				// Get the string representation of an object.
				String objStringRep = op.convertObject();

				// if the string representation is already encoded, just add this code.
				if (dicCodes.containsKey(objStringRep)) {
					String code = dicCodes.get(objStringRep);
					op.setObject(code);
					t.addUsedObject(code);
				}
				// otherwise, create a new code for this object based on its level.
				else {
					// check the object level and create the code.
					// Then add this code to the dictionary.
					if (op.getObj().isLevel1()) {
						String code = "A" + Integer.toString(lv1);
						lv1++;
						dicCodes.put(objStringRep, code);
						op.setObject(code);
						t.addUsedObject(code);
					}
					// if this happens, the object is a level 2 object.
					else if (op.getObj().isLevel2()) {
						String code = "B" + Integer.toString(lv2);
						lv2++;
						dicCodes.put(objStringRep, code);
						op.setObject(code);
						t.addUsedObject(code);
					}
					// otherwise, the object is a level 3 object.
					else {
						String code = "C" + Integer.toString(lv3);
						lv3++;
						dicCodes.put(objStringRep, code);
						op.setObject(code);
						t.addUsedObject(code);
					}
				}
			}
		}
	}

	// This methode takes a list of transactions and return a list of all used
	// objects in this set along with theire transaction & operation indices.
	private static ArrayList<ObjectInfo> getObjectsInfo(ArrayList<Transaction> result) {

		// To store all objects in every transaction in a single list.
		ArrayList<ObjectInfo> objects = new ArrayList<ObjectInfo>();

		// collect all objects in every transaction and put them in a single list.
		for (int i = 0; i < result.size(); i++) {
			Transaction t = result.get(i);
			ArrayList<Operation> operations = t.getOperations();

			for (int j = 0; j < operations.size(); j++) {
				Obj obj = operations.get(j).getObj();
				objects.add(new ObjectInfo(obj, i, j));
			}
		}
		return objects;
	}

	// This method checks if there is any overlap between any two objects and
	// removes this overlap by manipulating the objects.
	private static void process(ArrayList<Transaction> result) {
		// Get all objects in every transaction and store them in a single list.
		ArrayList<ObjectInfo> objects = getObjectsInfo(result);

		int i = 0;
		boolean reset = false;

		while (i < objects.size() - 1) {
			int j = i + 1;
			while (j < objects.size()) {
				Obj obj1 = objects.get(i).getObj();
				Obj obj2 = objects.get(j).getObj();
				// if both objects are tables "level1" or they are equal, do nothing.
				if ((obj1.isLevel1() && obj2.isLevel1()) || (obj1.isEqual(obj2))) {
					// if 2 given sql statments are already equal, the stored columns names in the
					// generated usedColumns set might not be in the same order. This might cause
					// errors when creating the string representation of the object like these 2
					// string representation "clientnameage clientagename". This is why we need to
					// copy one object to another to make sure they have the same usedColumns set.
					if (obj1.isEqual(obj2))
						obj1.copy(obj2);
					j++;
				}
				// if one object is a table and the other is a set of columns or cells of that
				// same table, make the other object equal to the first one and reset the loop.
				else if (obj1.isLevel1() || obj2.isLevel1()) {
					if (obj1.getTableName().equalsIgnoreCase(obj2.getTableName()) && obj1.isLevel1()) {
						obj2.copy(obj1);
						reset = true;
						break;
					} else if (obj2.getTableName().equalsIgnoreCase(obj1.getTableName()) && obj2.isLevel1()) {
						obj1.copy(obj2);
						reset = true;
						break;
					} else
						j++;
				}
				// if both objects are level 2, check if there is any intersection in the used
				// columns. if there is, take the union of the used columns of both objects and
				// set the result to be the new usedcolumns for both objects and reset the loop.
				else if (obj1.isLevel2() && obj2.isLevel2()) {
					// if the objects belong to different tables, do nothing.
					if (!obj1.getTableName().equalsIgnoreCase(obj2.getTableName()))
						j++;
					else {
						Set<String> intersection = new HashSet<String>(obj1.getUsedColumns());
						Set<String> set2 = new HashSet<String>(obj2.getUsedColumns());
						intersection.retainAll(set2);
						// if there is no intersection, the objects are separated "do nothing".
						if (intersection.size() == 0)
							j++;
						else {
							Set<String> union = new HashSet<String>(obj1.getUsedColumns());
							union.addAll(set2);
							// if the union size is equal to the table size
							if (union.size() == obj1.getTableSize()) {
								// make each object a level 1 object by removing all used columns.
								Set<String> emptyusedColumns = new HashSet<String>();
								obj1.setUsedColumns(emptyusedColumns);
								obj2.copy(obj1);
							} else {
								obj1.setUsedColumns(union);
								obj2.copy(obj1);
							}
							reset = true;
							break;
						}
					}
				}
				// if one object is level 2 and the other is level 3, check the intersection. if
				// there is an intersection, convert the level 3 object to level 2 and do the
				// same logic in the previous step.
				else if (obj1.isLevel2() || obj2.isLevel2()) {
					// if the objects belong to different tables, do nothing.
					if (!obj1.getTableName().equalsIgnoreCase(obj2.getTableName()))
						j++;
					else {
						Set<String> intersection = new HashSet<String>(obj1.getUsedColumns());
						Set<String> set2 = new HashSet<String>(obj2.getUsedColumns());
						intersection.retainAll(set2);
						// if there is no intersection, the objects are separated "do nothing".
						if (intersection.size() == 0)
							j++;
						else {
							// First, make sure that any level 3 object is converted to level 2.
							obj1.setpKeyName("empty");
							obj1.setpKeyValue("empty");
							obj2.setpKeyName("empty");
							obj2.setpKeyValue("empty");
							// Then, compute the union of the used columns.
							Set<String> union = new HashSet<String>(obj1.getUsedColumns());
							union.addAll(set2);
							// if the union size is equal to the table size
							if (union.size() == obj1.getTableSize()) {
								// make each object a level 1 object by removing all used columns.
								Set<String> emptyusedColumns = new HashSet<String>();
								obj1.setUsedColumns(emptyusedColumns);
								obj2.copy(obj1);
							} else {
								obj1.setUsedColumns(union);
								obj2.copy(obj1);
							}
							reset = true;
							break;
						}
					}
				}
				// if both objects are level 3 objects, check the primary key and the columns
				// intersection. if there is an overlapping, compute the new level 3 object by
				// computing the union of the used columns. Then, set each object to be equal to
				// this new object and reset the loop.
				else {
					// if the objects belong to different tables, do nothing.
					if (!obj1.getTableName().equalsIgnoreCase(obj2.getTableName()))
						j++;
					else {
						// if the primary key values are different, do nothing.
						if (!obj1.getpKeyValue().equalsIgnoreCase(obj2.getpKeyValue()))
							j++;
						else {
							Set<String> intersection = new HashSet<String>(obj1.getUsedColumns());
							Set<String> set2 = new HashSet<String>(obj2.getUsedColumns());
							intersection.retainAll(set2);
							// if there is no intersection, the objects are separated "do nothing".
							if (intersection.size() == 0)
								j++;
							else {
								// compute the union of the used columns.
								Set<String> union = new HashSet<String>(obj1.getUsedColumns());
								union.addAll(set2);
								// update both objects to be equal to the new level 3 object.
								obj1.setUsedColumns(union);
								obj2.copy(obj1);
								reset = true;
								break;
							}
						}
					}
				}
			}
			// The end of the inner loop.
			if (reset) {
				i = 0;
				reset = false;
			} else
				i++;
		}

	}

	public static ArrayList<Transaction> translate(String fileName, ArrayList<Schema> schemas) {
		ArrayList<Transaction> result = new ArrayList<Transaction>();

		try {
			// get the input from the file name as a stream of characters.
			CharStream inputStream = CharStreams.fromFileName(fileName);

			// tokenize the input stream using a lexer
			SQLiteLexer sqlLexer = new SQLiteLexer(inputStream);

			// create a token stream from a token source whiche is sql lexer
			CommonTokenStream tokenStream = new CommonTokenStream(sqlLexer);

			// create a parser from a token stream
			SQLiteParser sqlParser = new SQLiteParser(tokenStream);

			// Create a Pare Tree
			ParseTree tree = sqlParser.parse();

			// ========================= Visualize the parse Tree =========================
			
//			JFrame frame = new JFrame("Antlr Parse Tree");
//			frame.setSize(800, 600);
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			TreeViewer viewer = new TreeViewer(Arrays.asList(sqlParser.getRuleNames()), tree);
//			JScrollPane scroll = new JScrollPane(viewer, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
////			viewer.open();
//			viewer.setScale(1.5);
//			frame.add(scroll);
//			frame.setVisible(true);
			
			// ============================================================================

			// Create our custom Listener
			Listener listener = new Listener();
			listener.setSchemas(schemas);

			// Create the ParseTree walker & Walk over the tree.
			ParseTreeWalker walker = new ParseTreeWalker();
			walker.walk(listener, tree);

			result = listener.getResult();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// processing the objects by removing any overlap and converting the string
		// representation of each object to a short string code.
		if (result.size() > 0) {
			process(result);
			convertObjects(result);
			dicCodes.clear();
		}
		return result;
	}
	
	public static ArrayList<Transaction> translate4gui(String sqlCode, ArrayList<Schema> schemas) {
		ArrayList<Transaction> result = new ArrayList<Transaction>();

		// get the input from the string received from the gui interface
		CharStream inputStream = CharStreams.fromString(sqlCode);

		// tokenize the input stream using a lexer
		SQLiteLexer sqlLexer = new SQLiteLexer(inputStream);

		// create a token stream from a token source whiche is sql lexer
		CommonTokenStream tokenStream = new CommonTokenStream(sqlLexer);

		// create a parser from a token stream
		SQLiteParser sqlParser = new SQLiteParser(tokenStream);

		// Create a Pare Tree
		ParseTree tree = sqlParser.parse();

		// ========================= Visualize the parse Tree =========================
		
//			JFrame frame = new JFrame("Antlr Parse Tree");
//			frame.setSize(800, 600);
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			TreeViewer viewer = new TreeViewer(Arrays.asList(sqlParser.getRuleNames()), tree);
//			JScrollPane scroll = new JScrollPane(viewer, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
////			viewer.open();
//			viewer.setScale(1.5);
//			frame.add(scroll);
//			frame.setVisible(true);
		
		// ============================================================================

		// Create our custom Listener
		Listener listener = new Listener();
		listener.setSchemas(schemas);

		// Create the ParseTree walker & Walk over the tree.
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);

		result = listener.getResult();

		// processing the objects by removing any overlap and converting the string
		// representation of each object to a short string code.
		if (result.size() > 0) {
			process(result);
			convertObjects(result);
			dicCodes.clear();
		}
		return result;
	}
}
