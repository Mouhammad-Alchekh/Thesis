
public class Edge {
	private int id;
	private int t1ID;
	private Operation op1;
	private int t2ID;
	private Operation op2;

	public Edge(int t1, Operation op1, int t2, Operation op2, int id) {
		this.t1ID = t1;
		this.op1 = op1;
		this.t2ID = t2;
		this.op2 = op2;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getT1ID() {
		return t1ID;
	}

	public void setT1ID(int t1id) {
		t1ID = t1id;
	}

	public Operation getOp1() {
		return op1;
	}

	public void setOp1(Operation op1) {
		this.op1 = op1;
	}

	public int getT2ID() {
		return t2ID;
	}

	public void setT2ID(int t2id) {
		t2ID = t2id;
	}

	public Operation getOp2() {
		return op2;
	}

	public void setOp2(Operation op2) {
		this.op2 = op2;
	}

	public void print() {
		char type1 = op1.getType();
		char type2 = op2.getType();
		char obj1 = op1.getObject();
		char obj2 = op1.getObject();

		System.out.println(String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s", "( ", "T", t1ID, " , ", type1, t1ID,
				"[", obj1, "]", " , ", type2, t2ID, "[", obj2, "]", " , ", "T", t2ID,  " )", "     ID = ", id));
	}

	public void flip() {
		int temp1 = this.t1ID;
		Operation temp2 = this.op1;

		this.t1ID = this.t2ID;
		this.t2ID = temp1;

		this.op1 = this.op2;
		this.op2 = temp2;
	}
}
