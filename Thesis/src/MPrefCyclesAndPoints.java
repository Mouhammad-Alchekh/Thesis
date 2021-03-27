import java.util.ArrayList;

// this is a wrapper class to combine the list of multi-prefix conflicts-free cycles with a list of multi-split points.
public class MPrefCyclesAndPoints {
	private ArrayList<Cycle> multiPrefCycles = new ArrayList<Cycle>();
	private ArrayList<MultiSplitInfo> splitInfo = new ArrayList<MultiSplitInfo>();

	public MPrefCyclesAndPoints(ArrayList<Cycle> multiPrefCycles, ArrayList<MultiSplitInfo> splitInfo) {
		this.multiPrefCycles = multiPrefCycles;
		this.splitInfo = splitInfo;
	}

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

	public int size() {
		return multiPrefCycles.size();
	}

	// append another MPrefCyclesAndPoints object to this one.
	public void append(MPrefCyclesAndPoints cp) {
		for (int i = 0; i < cp.size(); i++) {
			multiPrefCycles.add(cp.getMultiPrefCycles().get(i));
			splitInfo.add(cp.getSplitInfo().get(i));
		}
	}
}
