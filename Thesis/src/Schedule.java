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

	private void printSplitSchedule() {
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

	private void printMultiSplitSchedule() {
		// to combine all operations into this string
		String result = " ";

		// To store the positions of all comit operations in the schedule. for each item
		// in this list, the index represents the transaction ID of the commit operation
		// and the value represents the index of the last operation befor commiting.
		ArrayList<Integer> commitPositions = new ArrayList<Integer>();
		for (int i = 0; i < TransactionId.size(); i++) {
			commitPositions.add(0);
		}
		for (int i = 0; i < TransactionId.size() - 1; i++) {
			if (TransactionId.get(i) != TransactionId.get(i + 1))
				commitPositions.set(TransactionId.get(i), i);
		}
		// The last commit will be after the last operation
		commitPositions.set(TransactionId.get(TransactionId.size() - 1), TransactionId.size() - 1);

		for (int i = 0; i < operations.size(); i++) {
			if (result != " ")
				result += ", ";
			Operation op = operations.get(i);
			int tId = TransactionId.get(i);
			result += op.getType() + Integer.toString(tId) + "[" + op.getObject() + "]" + " ";

			if (commitPositions.get(tId) == i)
				result += ", C" + Integer.toString(tId) + " ";
		}
		System.out.println(String.format("%s%s%s%s%s", "S", " = ", "[", result, "]"));
	}

	public void print() {
		if (this.type == "Split Schedule")
			printSplitSchedule();
		if (this.type == "Multi Split Schedule")
			printMultiSplitSchedule();
	}

	// To get the Schedule as a string for printing.
	public String getSchedule2Print() {
		// to combine all operations into this string
		String result = "";

		if (this.type.equals("Split Schedule"))
			result = getSplitSchedule2Print();
		if (this.type.equals("Multi Split Schedule"))
			result = getMultiSplitSchedule2Print();

		return result;
	}

	private String getSplitSchedule2Print() {
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
		result = "S" + " = " + "[" + result + "] \n";

		return result;
	}

	private String getMultiSplitSchedule2Print() {
		// to combine all operations into this string
		String result = " ";

		// To store the positions of all comit operations in the schedule. for each item
		// in this list, the index represents the transaction ID of the commit operation
		// and the value represents the index of the last operation befor commiting.
		ArrayList<Integer> commitPositions = new ArrayList<Integer>();
		for (int i = 0; i < TransactionId.size(); i++) {
			commitPositions.add(0);
		}
		for (int i = 0; i < TransactionId.size() - 1; i++) {
			if (TransactionId.get(i) != TransactionId.get(i + 1))
				commitPositions.set(TransactionId.get(i), i);
		}
		// The last commit will be after the last operation
		commitPositions.set(TransactionId.get(TransactionId.size() - 1), TransactionId.size() - 1);

		for (int i = 0; i < operations.size(); i++) {
			if (result != " ")
				result += ", ";
			Operation op = operations.get(i);
			int tId = TransactionId.get(i);
			result += op.getType() + Integer.toString(tId) + "[" + op.getObject() + "]" + " ";

			if (commitPositions.get(tId) == i)
				result += ", C" + Integer.toString(tId) + " ";
		}
		result = "S" + " = " + "[" + result + "] \n";

		return result;
	}

}
