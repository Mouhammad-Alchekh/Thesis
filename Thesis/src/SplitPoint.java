
public class SplitPoint {
	int tIndex;
	int opIndex;
	int tId;

	public SplitPoint(int tIndex, int opIndex, int tId) {
		this.tIndex = tIndex;
		this.opIndex = opIndex;
		this.tId = tId;
	}

	public int gettId() {
		return tId;
	}

	public void settId(int tId) {
		this.tId = tId;
	}

	public int gettIndex() {
		return tIndex;
	}

	public void settIndex(int tIndex) {
		this.tIndex = tIndex;
	}

	public int getOpIndex() {
		return opIndex;
	}

	public void setOpIndex(int opIndex) {
		this.opIndex = opIndex;
	}

	public void print(Schedule splitSchedule) {
		Operation op1 = splitSchedule.getOperations().get(opIndex);
		Operation op2 = op1;
		for (int i = tIndex + 1; i < splitSchedule.size(); i++) {
			if (splitSchedule.getTransactionId().get(i) == tId) {
				op2 = splitSchedule.getOperations().get(i);
				break;
			}
		}
		System.out.println("T" + Integer.toString(tId) + " On Operations (" + op1.getType() + Integer.toString(tId)
				+ "[" + op1.getObject() + "]" + ", " + op2.getType() + Integer.toString(tId) + "[" + op2.getObject()
				+ "]" + ")");
	}

	// To get the split point as a string for printing.
	public String getSplitPoint2Print(Schedule splitSchedule) {
		Operation op1 = splitSchedule.getOperations().get(opIndex);
		Operation op2 = op1;
		for (int i = tIndex + 1; i < splitSchedule.size(); i++) {
			if (splitSchedule.getTransactionId().get(i) == tId) {
				op2 = splitSchedule.getOperations().get(i);
				break;
			}
		}
		
		// to combine all operations into this string
		String result = "T" + Integer.toString(tId) + " On Operations (" + op1.getType() + Integer.toString(tId)
		+ "[" + op1.getObject() + "]" + ", " + op2.getType() + Integer.toString(tId) + "[" + op2.getObject()
		+ "]" + ") \n";

		return result;
	}

}
