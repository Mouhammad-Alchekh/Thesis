import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Transaction {

	private int id;
	// A transaction contains many operations
	// Each operation is an object that has many characteristics
	private ArrayList<Operation> operations = new ArrayList<Operation>();
	// To store the short string codes of the used objects in this transaction.
	private Set<String> usedObjects = new HashSet<String>();

	public Transaction(int id) {
		this.id = id;
	}

	public void addOperation(int idO, char type, String obj) {
		Operation op = new Operation(idO, type, obj);
		operations.add(op);
		// Ignore adding commit operation for the moment
		usedObjects.add(obj);
	}

	public void addOperation(int idO, char type, Obj obj) {
		Operation op = new Operation(idO, type, obj);
		operations.add(op);
	}

	public void addUsedObject(String obj) {
		usedObjects.add(obj);
	}

	public void Print() {
		String result = " "; // to combine all operations into this string
		int size = operations.size();
		for (int i = 0; i < size; i++) {
			if (result != " ") // to add a comma between 2 operations
				result += ", ";
			Operation op = operations.get(i);
			result += op.getType() + Integer.toString(getId()) + "[" + op.getObject() + "]" + " ";
		}
		System.out.println(String.format("%s%s%s%s%s%s%s%s", "T", getId(), " = ", "[", result, ", C", getId(), " ]"));
	}

	// To get the transaction as a string for printing.
	public String getTransaction2Print() {
		String result = " "; // to combine all operations into this string
		int size = operations.size();
		for (int i = 0; i < size; i++) {
			if (result != " ") // to add a comma between 2 operations
				result += ", ";
			Operation op = operations.get(i);
			result += op.getType() + Integer.toString(getId()) + "[" + op.getObject() + "]" + " ";
		}

		result = "T" + Integer.toString(getId()) + " = " + "[" + result + ", C" + Integer.toString(getId()) + " ] \n";
		return result;

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

	public Set<String> getUsedObjects() {
		return usedObjects;
	}

	public int size() {
		return operations.size();
	}

}
