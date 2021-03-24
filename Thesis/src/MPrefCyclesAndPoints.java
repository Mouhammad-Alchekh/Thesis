import java.util.ArrayList;

// this is a wrapper class to combine the list of multi-prefix conflicts-free cycles with a list of multi-split points.
public class MPrefCyclesAndPoints {
	public ArrayList<Cycle> getMultiPrefCycles() {
		return multiPrefCycles;
	}

	public void setMultiPrefCycles(ArrayList<Cycle> multiPrefCycles) {
		this.multiPrefCycles = multiPrefCycles;
	}

	public ArrayList<MultiSplitInfo> getSplitInfo() {
		return splitInfo;
	}

	public void setSplitInfo(ArrayList<MultiSplitInfo> splitInfo) {
		this.splitInfo = splitInfo;
	}

	private ArrayList<Cycle> multiPrefCycles = new ArrayList<Cycle>();
	private ArrayList<MultiSplitInfo> splitInfo = new ArrayList<MultiSplitInfo>();

	public MPrefCyclesAndPoints(ArrayList<Cycle> multiPrefCycles, ArrayList<MultiSplitInfo> splitInfo) {
		this.multiPrefCycles = multiPrefCycles;
		this.splitInfo = splitInfo;
	}
}
