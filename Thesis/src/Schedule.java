import java.util.ArrayList;

public class Schedule {
	private ArrayList<Operation> operations = new ArrayList<Operation>();
	// this list contains the transaction id for each operation in the schedule.
	private ArrayList<Integer> TransactionId = new ArrayList<Integer>();
	private String type;

	public Schedule(String type) {
		this.type = type;
	}

	public void AddOperation(Operation op, int Tid) {
		operations.add(op);
		TransactionId.add(Tid);
	}

	public ArrayList<Operation> getOperations() {
		return operations;
	}

	public void setOperations(ArrayList<Operation> operations) {
		this.operations = operations;
	}

	public ArrayList<Integer> getTransactionId() {
		return TransactionId;
	}

	public void setTransactionId(ArrayList<Integer> transactionId) {
		TransactionId = transactionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int size() {
		return operations.size();
	}
	public void print() {
		// to combine all operations into this string
		String result = " ";
		boolean passSplitPoint = false;

		for (int i = 0; i < operations.size(); i++) {
			// to add a comma between 2 operations
			if (result != " ")
				result += ", ";
			Operation op = operations.get(i);
			result += op.getType() + Integer.toString(TransactionId.get(i)) + "[" + op.getObject() + "]" + " ";

			if (i < operations.size() - 1) {
				// This is the point of prefix b where we split the schedule.
				if ((TransactionId.get(i) != TransactionId.get(i + 1)) && !passSplitPoint) {
					passSplitPoint = true;
					continue;
				}
				// After passing the spilt point, add a commit when the next id is different.
				if ((TransactionId.get(i) != TransactionId.get(i + 1)) && passSplitPoint)
					result += ", C" + Integer.toString(TransactionId.get(i)) + " ";
			}
			if (i == operations.size() - 1) {
				result += ", C" + Integer.toString(TransactionId.get(i)) + " ";
			}
		}
		System.out.println(String.format("%s%s%s%s%s", "S", " = ", "[", result, "]"));
	}

}
