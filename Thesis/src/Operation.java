
public class Operation {
	private int idO;
	private char type;

	private char object; // To be changed later

	public Operation(int idO, char type, char obj) {
		this.idO = idO;
		this.type = type;
		this.object = obj;
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

	public char getObject() {
		return object;
	}

	public void setObject(char object) {
		this.object = object;
	}

	// check if a given operation is different
	public boolean isDifferent(Operation op2) {
		boolean result = true;
		// If both operations have the same type and object, they are similar.
		if (this.type == op2.getType() && this.object == op2.getObject())
			result = false;
		return result;
	}

}
