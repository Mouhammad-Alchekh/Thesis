import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Transaction {

	private int id;
	// A transaction contains many operations
	// Each operation is an object that has many characteristics
	private ArrayList<Operation> operations = new ArrayList<Operation>();   // initially ArrayList. May be changed later.
	private Set<Character> usedObjects = new HashSet<Character>();          // To store the used objects in this transaction.

	public Transaction(int id) {
		this.id = id;
	}

	public void AddOperation(int idO, char type, char obj) {
		Operation op = new Operation(idO, type, obj);
		operations.add(op);
		// Ignore adding commit operation for the moment
		usedObjects.add(obj);
	}

	public void Print() {
		String result = " ";                                                // to combine all operations into this string
		int size = operations.size();
		for (int i = 0; i < size; i++) {
			if (result != " ")                                              // to add comma between operations
				result += ", ";
			Operation op = operations.get(i);
			result += op.getType() + Integer.toString(getId()) + "[" + op.getObject() + "]" + " ";
		}
		System.out.println(String.format("%s%s%s%s%s%s", "T", getId(), " = ", "[", result, "]"));

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Operation> getOperations() {
		return operations;
	}

	public void setOperations(ArrayList<Operation> operations) {
		this.operations = operations;
	}

	public Set<Character> getUsedObjects() {
		return usedObjects;
	}

}
