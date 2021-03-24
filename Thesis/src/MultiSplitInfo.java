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

}
