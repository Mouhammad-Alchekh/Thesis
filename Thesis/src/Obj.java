import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// This class is a container of the information that describes a specific SQL object "database object"
public class Obj {
	private String tableName;
	private int tableSize = 0;
	private Set<String> usedColumns = new HashSet<String>();
	private String pKeyName = "empty";
	private String pKeyValue = "empty";

	public Obj(String tableName) {
		this.tableName = tableName;
	}

	// The object is level 1 if it is a whole table.
	public boolean isLevel1() {
		return (usedColumns.size() == 0);
	}

	// The object is level 2 if it is a set of columns.
	public boolean isLevel2() {
		return (usedColumns.size() > 0 && pKeyName.equals("empty"));
	}

	// The object is level 3 if it is a set of cells.
	public boolean isLevel3() {
		return (usedColumns.size() > 0 && !pKeyName.equals("empty"));
	}

	// This method checks if a given object is equal to this object.
	public boolean isEqual(Obj obj2) {
		boolean result = true;

		// if the objects have different table names, they are not equal.
		if (!this.tableName.equals(obj2.getTableName()))
			result = false;
		// if the objects have different used columns size, they are not equal.
		if (this.usedColumns.size() != obj2.usedColumns.size())
			result = false;
		// if the objects have equal non-zero used columns size
		if ((this.usedColumns.size() == obj2.usedColumns.size()) && (this.usedColumns.size() != 0)) {
			// compute the intersection of the used columns.
			Set<String> intersection = new HashSet<String>(this.getUsedColumns());
			Set<String> set1 = new HashSet<String>(this.getUsedColumns());
			Set<String> set2 = new HashSet<String>(obj2.getUsedColumns());
			intersection.retainAll(set2);
			// if the intersection size is not equal to the set1 size, the used columns are
			// not exactly the same for both objects and the objects are not equal.
			if (intersection.size() != set1.size())
				result = false;
		}
		// if the objects have different Pkey name, they are not equal.
		if (!this.pKeyName.equals(obj2.getpKeyName()))
			result = false;
		// if the objects have the same Pkey name but different values, they are not
		// equal.
		if (this.pKeyName.equals(obj2.getpKeyName())) {
			if (!this.pKeyValue.equals(obj2.getpKeyValue()))
				result = false;
		}

		return result;
	}

	public void addColumn(String Column) {
		usedColumns.add(Column);
	}

	public void addColumns(ArrayList<String> columns) {
		for (int i = 0; i < columns.size(); i++)
			usedColumns.add(columns.get(i));
	}

	// Copy a given object to this object.
	public void copy(Obj obj2) {
		setTableName(obj2.getTableName());
		setTableSize(obj2.getTableSize());
		setUsedColumns(obj2.getUsedColumns());
		setpKeyName(obj2.getpKeyName());
		setpKeyValue(obj2.getpKeyValue());
	}

	public void print() {
		Object[] columns = usedColumns.toArray();
		if (usedColumns.isEmpty() && pKeyName.equals("empty")) {
			System.out.println("This object is a level 1 object");
			System.out.println("Table Name is: " + tableName);
			System.out.println("Table Size is: " + tableSize);
		} else if (pKeyName.equals("empty")) {
			System.out.println("This object is a level 2 object");
			System.out.println("Table Name is: " + tableName);
			System.out.println("Table Size is: " + tableSize);
			System.out.println("The used Columns are: ");
			for (int i = 0; i < columns.length; i++)
				System.out.println(columns[i]);

		} else {
			System.out.println("This object is a level 3 object");
			System.out.println("Table Name is: " + tableName);
			System.out.println("Table Size is: " + tableSize);
			System.out.println("The used Columns are:");
			for (int i = 0; i < columns.length; i++)
				System.out.println(columns[i]);
			System.out.println("The primary key name and value are:");
			System.out.println(pKeyName + " = " + pKeyValue);
		}
	}

	// To get the object as a string for printing.
	public String getObj2Print() {
		String result = "";

		Object[] columns = usedColumns.toArray();
		if (usedColumns.isEmpty() && pKeyName.equals("empty")) {
			result += "This object is a level 1 object \n";
			result += "Table Name is: " + tableName + " \n";
			result += "Table Size is: " + Integer.toString(tableSize) + " \n";
		} else if (pKeyName.equals("empty")) {
			result += "This object is a level 2 object \n";
			result += "Table Name is: " + tableName + " \n";
			result += "Table Size is: " + Integer.toString(tableSize) + " \n";
			result += "The used Columns are:  \n";
			for (int i = 0; i < columns.length; i++)
				result += columns[i] + " \n";

		} else {
			result += "This object is a level 3 object \n";
			result += "Table Name is: " + tableName + " \n";
			result += "Table Size is: " + Integer.toString(tableSize) + " \n";
			result += "The used Columns are:  \n";
			for (int i = 0; i < columns.length; i++)
				result += columns[i] + " \n";
			result += "The primary key name and value are: \n";
			result += pKeyName + " = " + pKeyValue + "\n";
		}
		return result;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getTableSize() {
		return tableSize;
	}

	public void setTableSize(int tableSize) {
		this.tableSize = tableSize;
	}

	public Set<String> getUsedColumns() {
		return usedColumns;
	}

	public void setUsedColumns(Set<String> usedColumns) {
		this.usedColumns = usedColumns;
	}

	public String getpKeyName() {
		return pKeyName;
	}

	public void setpKeyName(String pKeyName) {
		this.pKeyName = pKeyName;
	}

	public String getpKeyValue() {
		return pKeyValue;
	}

	public void setpKeyValue(String pKeyValue) {
		this.pKeyValue = pKeyValue;
	}

}
