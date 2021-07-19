import java.util.ArrayList;

public class MultiSplitInfo {
	private boolean containsConflict;
	private int tPrimeIndex;
	ArrayList<PrePostFix> prePostFix = new ArrayList<PrePostFix>();

	public MultiSplitInfo(boolean containsConflict, int tPrimeIndex, ArrayList<PrePostFix> prePostFix) {
		this.containsConflict = containsConflict;
		this.tPrimeIndex = tPrimeIndex;
		this.prePostFix = prePostFix;
	}

	public boolean getContainsConflict() {
		return containsConflict;
	}

	public void setContainsConflict(boolean containsConflict) {
		this.containsConflict = containsConflict;
	}

	public int gettPrimeIndex() {
		return tPrimeIndex;
	}

	public void settPrimeIndex(int tPrimeIndex) {
		this.tPrimeIndex = tPrimeIndex;
	}

	public ArrayList<PrePostFix> getPrePostFix() {
		return prePostFix;
	}

	public void setPrePostFix(ArrayList<PrePostFix> prePostFix) {
		this.prePostFix = prePostFix;
	}

	// print all split points from T until T Prime
	public void printSplitPoints() {
		for (int i = 0; i <= tPrimeIndex; i++) {
			PrePostFix current = prePostFix.get(i);
			String tid = Integer.toString(current.gettId());
			// get the last operation in the prefix.
			Operation op1 = current.getPrefix().get(current.getPrefix().size() - 1);
			// get the last operation in the postfix.
			Operation op2 = current.getPostfix().get(0);
			System.out.println("T" + tid + " On Operations (" + op1.getType() + tid + "[" + op1.getObject() + "]" + ", "
					+ op2.getType() + tid + "[" + op2.getObject() + "]" + ")");
		}
	}

	// To get the multi split points as a string for printing.
	public String getSplitPoints2Print() {
		String result = "";

		for (int i = 0; i <= tPrimeIndex; i++) {
			PrePostFix current = prePostFix.get(i);
			String tid = Integer.toString(current.gettId());
			// get the last operation in the prefix.
			Operation op1 = current.getPrefix().get(current.getPrefix().size() - 1);
			// get the last operation in the postfix.
			Operation op2 = current.getPostfix().get(0);

			result += "T" + tid + " On Operations (" + op1.getType() + tid + "[" + op1.getObject() + "]" + ", "
					+ op2.getType() + tid + "[" + op2.getObject() + "]" + ") \n";
		}

		return result;
	}

}
