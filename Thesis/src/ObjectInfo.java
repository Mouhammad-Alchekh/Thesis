// This class is a warapper class to store a given object along with its transaction & operation indices.
public class ObjectInfo {
	private Obj obj;
	// To store the transaction index of the transaction that holds this object.
	private int tIndex;
	// To store the operation index of the operation that holds this object.
	private int opIndex;

	public ObjectInfo(Obj obj, int tIndex, int opIndex) {
		this.obj = obj;
		this.tIndex = tIndex;
		this.opIndex = opIndex;
	}

	public Obj getObj() {
		return obj;
	}

	public void setObj(Obj obj) {
		this.obj = obj;
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

}
