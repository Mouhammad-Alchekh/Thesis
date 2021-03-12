import java.util.ArrayList;

// This is a wrapper class to wrap 2 lists in one object
public class DoubleList {
	ArrayList<Operation> first = new ArrayList<Operation>();
	ArrayList<Operation> second = new ArrayList<Operation>();
	
	public DoubleList(ArrayList<Operation> first, ArrayList<Operation> second) {
		this.first = first;
		this.second = second;
	}

	public ArrayList<Operation> getFirst() {
		return first;
	}

	public void setFirst(ArrayList<Operation> first) {
		this.first = first;
	}

	public ArrayList<Operation> getSecond() {
		return second;
	}

	public void setSecond(ArrayList<Operation> second) {
		this.second = second;
	}
	
}
