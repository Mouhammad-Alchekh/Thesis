import java.util.ArrayList;
//import java.io.IOException;
//import org.antlr.v4.runtime.*;

public class Listener extends SQLiteParserBaseListener {

	ArrayList<Transaction> result = new ArrayList<Transaction>();

	public ArrayList<Transaction> getResult() {
		System.out.println("Finishing With the Listener");
		return result;
	}

	public void setResult(ArrayList<Transaction> result) {
		this.result = result;
	}

	@Override
	public void enterTransaction_name(SQLiteParser.Transaction_nameContext ctx) {
		// The logic of translating the transactions to the abstract representation
		System.out.println("Enter a transaction");
		
	}

	@Override
	public void exitTransaction_name(SQLiteParser.Transaction_nameContext ctx) {
		// The logic of translating the transactions to the abstract representation
		System.out.println("Exit a transaction");

	}
}
