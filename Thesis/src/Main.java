import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {

	public static void main(String[] args) {
		Transaction t1 = new Transaction(1);
		t1.AddOperation(1, 'R', 'x');
		t1.AddOperation(2, 'W', 'y');
		Transaction t2 = new Transaction(2);
		t2.AddOperation(1, 'R', 'y');
		t2.AddOperation(2, 'W', 'z');
		Transaction t3 = new Transaction(3);
		t3.AddOperation(1, 'R', 'z');
		t3.AddOperation(2, 'R', 'x');
		t3.AddOperation(3, 'W', 'x');
		t3.AddOperation(4, 'W', 'z');

		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(t1);
		transactions.add(t2);
		transactions.add(t3);

		t1.Print();
		t2.Print();
		t3.Print();
		System.out.println();

		// ============= Getting the Edges using the First approach =============

		ArrayList<Edge> edges = Tools.getEdges(transactions);
		for (int i = 0; i < edges.size(); i++) {
			Edge e = edges.get(i);
			e.print();
		}
		System.out.println();

		// ============= Getting the Edges using the second approach =============

//		ArrayList<Edge> list = Tools.getEdges(t1, t2, t3); // 3 iterations
//		for (int i = 0; i < list.size(); i++) {
//			Edge e = list.get(i);
//			e.print();
//		}
//		System.out.println();

		// ======================== Getting Cycles =============================

		ArrayList<Cycle> cycles = Tools.getCycles(edges);
		System.out.println("The Possible Cycles Are:");
		for (int i = 0; i < cycles.size(); i++) {
			int n = i + 1;
			System.out.println("Cycle number " + n);
			cycles.get(i).print();
		}
		System.out.println();

	}
}
