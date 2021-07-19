
public class Operation {
	private int idO;
	private char type;
	// a unique string represntation of SQL object "or database object" that SQL
	// statments use.
	private String object = "empty";
	// the real SQL object "or database object".
	private Obj obj = new Obj("empty");

	public Operation(int idO, char type, String obj) {
		this.idO = idO;
		this.type = type;
		this.object = obj;
	}

	// An overloaded constructor.
	public Operation(int idO, char type, Obj obj) {
		this.idO = idO;
		this.type = type;
		this.obj = obj;
	}

	public Obj getObj() {
		return obj;
	}

	public void setObj(Obj obj) {
		this.obj = obj;
	}

	public int getId() {
		return idO;
	}

	public void setId(int idO) {
		this.idO = idO;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public void print() {
		System.out.println(String.format("%s%s%s%s", type, "[", object, "]"));
	}
	
	// To get the operation as a string for printing.
	public String getOperation2Print() {
		// to combine all components into this string
		String result = type + "[" + object + "] \n";
		return result;
	}
	
	// check if a given operation is different.
	public boolean isDifferent(Operation op2) {
		boolean result = true;
		// If both operations have the same type and object, they are similar.
		if (this.type == op2.getType() && this.object.equals(op2.getObject()))
			result = false;
		return result;
	}

	// This method generates a string representation of an object from its collected
	// information.
	public String convertObject() {
		String result = obj.getTableName();
		Object[] columns = obj.getUsedColumns().toArray();
		for (int i = 0; i < columns.length; i++)
			result += columns[i];
		if (!obj.getpKeyName().equals("empty")) {
			result += obj.getpKeyName();
			result += obj.getpKeyValue();
		}
		return result;
	}
}
