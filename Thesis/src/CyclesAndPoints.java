import java.util.ArrayList;

// this is a wrapper class to combine the list of prefix-write conflicts-free cycles with its split points.
public class CyclesAndPoints {
	private ArrayList<Cycle> cycles = new ArrayList<Cycle>();
	private ArrayList<SplitPoint> splitPoints = new ArrayList<SplitPoint>();

	public CyclesAndPoints(ArrayList<Cycle> cycles, ArrayList<SplitPoint> splitPoints) {
		this.cycles = cycles;
		this.splitPoints = splitPoints;
	}

	public ArrayList<Cycle> getCycles() {
		return cycles;
	}

	public void setCycles(ArrayList<Cycle> cycles) {
		this.cycles = cycles;
	}

	public ArrayList<SplitPoint> getSplitPoints() {
		return splitPoints;
	}

	public void setSplitPoints(ArrayList<SplitPoint> splitPoints) {
		this.splitPoints = splitPoints;
	}
	
	public int size() {
		return cycles.size();
	}
	
	// append another CyclesAndPoints object to this one.
	public void append(CyclesAndPoints cp) {
		for (int i = 0; i < cp.size(); i++) {
			cycles.add(cp.getCycles().get(i));
			splitPoints.add(cp.getSplitPoints().get(i));
		}
	}

}
