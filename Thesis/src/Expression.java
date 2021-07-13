
public class Expression {
	private String leftExpr;
	private String operation;
	private String rightExpr;

	public Expression(String leftExpr, String operation, String rightExpr) {
		this.leftExpr = leftExpr;
		this.operation = operation;
		this.rightExpr = rightExpr;
	}

	public String getLeftExpr() {
		return leftExpr;
	}

	public void setLeftExpr(String leftExpr) {
		this.leftExpr = leftExpr;
	}

	public String getRightExpr() {
		return rightExpr;
	}

	public void setRightExpr(String rightExpr) {
		this.rightExpr = rightExpr;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void print() {
		System.out.println("The leftside is: " + leftExpr);
		System.out.println("The operation is: " + operation);
		System.out.println("The rightside is: " + rightExpr);

	}

}
