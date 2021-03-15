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
		System.out.println();
		System.out.println();

		// ====================== Testing on another Example ===========================
		Operation op = new Operation(1, 'W', 'x');
		ArrayList<Edge> test = new ArrayList<Edge>();
		
		Edge e1 = new Edge(1, op, 2, op, 1);
		test.add(e1);
		Edge e2 = new Edge(2, op, 4, op, 2);
		test.add(e2);
		Edge e3 = new Edge(2, op, 7, op, 3);
		test.add(e3);
		Edge e4 = new Edge(1, op, 3, op, 4);
		test.add(e4);
		Edge e5 = new Edge(3, op, 4, op, 5);
		test.add(e5);
		Edge e6 = new Edge(3, op, 5, op, 6);
		test.add(e6);
		Edge e7 = new Edge(4, op, 6, op, 7);
		test.add(e7);
		Edge e8 = new Edge(5, op, 6, op, 8);
		test.add(e8);
		Edge e9 = new Edge(9, op, 8, op, 9);
		test.add(e9);
		
		for (int i = 0; i < test.size(); i++) {
			Edge e = test.get(i);
			e.print();
		}
		System.out.println();
		
		ArrayList<Cycle> cycles2 = Tools.getCycles(test);
		System.out.println("The Possible Cycles Are:");
		for (int i = 0; i < cycles2.size(); i++) {
			int n = i + 1;
			System.out.println("Cycle number " + n);
			cycles2.get(i).print();
		}
	}
}
