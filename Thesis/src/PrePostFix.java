import java.util.ArrayList;

// This Class is a container of 2 lists that represent the prefix and the postfix of a transaction based on a split point.
public class PrePostFix {
	private int tId;
	private int splitIndex;
	private ArrayList<Operation> prefix = new ArrayList<Operation>();
	private ArrayList<Operation> postfix = new ArrayList<Operation>();

	public PrePostFix(int tId, int splitIndex) {
		this.tId = tId;
		this.splitIndex = splitIndex;
	}

	public int gettId() {
		return tId;
	}

	public void settId(int tId) {
		this.tId = tId;
	}

	public int getSplitIndex() {
		return splitIndex;
	}

	public void setSplitIndex(int splitIndex) {
		this.splitIndex = splitIndex;
	}

	public ArrayList<Operation> getPrefix() {
		return prefix;
	}

	public void setPrefix(ArrayList<Operation> prefix) {
		this.prefix = prefix;
	}

	public ArrayList<Operation> getPostfix() {
		return postfix;
	}

	public void setPostfix(ArrayList<Operation> postfix) {
		this.postfix = postfix;
	}

	public void addPrefixOp(Operation op) {
		prefix.add(op);
	}

	public void addPostfixOp(Operation op) {
		postfix.add(op);
	}
}
