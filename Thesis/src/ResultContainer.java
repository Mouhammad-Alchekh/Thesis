import java.util.ArrayList;

public class ResultContainer {

	private boolean hasWarning;
	private String warningInfo;
	private ArrayList<Transaction> result = new ArrayList<Transaction>();
	
	public ResultContainer(boolean hasWarning, String warningInfo, ArrayList<Transaction> result) {
		this.hasWarning = hasWarning;
		this.warningInfo = warningInfo;
		this.result = result;
	}
	
	public boolean getHasWarning() {
		return hasWarning;
	}
	
	public void setHasWarning(boolean hasWarning) {
		this.hasWarning = hasWarning;
	}
	
	public String getWarningInfo() {
		return warningInfo;
	}
	
	public void setWarningInfo(String warningInfo) {
		this.warningInfo = warningInfo;
	}
	
	public ArrayList<Transaction> getResult() {
		return result;
	}
	
	public void setResult(ArrayList<Transaction> result) {
		this.result = result;
	}

}