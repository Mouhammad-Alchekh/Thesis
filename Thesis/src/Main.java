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

		ArrayList<Transaction> example1 = new ArrayList<Transaction>();
		example1.add(t1);
		example1.add(t2);
		example1.add(t3);

		System.out.println("====== Example 1 =======");
		System.out.println();
		t1.Print();
		t2.Print();
		t3.Print();
		System.out.println();

		// ====== Example 2 =======

		Transaction t11 = new Transaction(1);
		t11.AddOperation(1, 'W', 'x');
		t11.AddOperation(2, 'W', 'y');
		Transaction t22 = new Transaction(2);
		t22.AddOperation(1, 'R', 'v');
		t22.AddOperation(2, 'R', 'z');
		t22.AddOperation(3, 'W', 'v');
		t22.AddOperation(4, 'W', 'x');
		Transaction t33 = new Transaction(3);
		t33.AddOperation(1, 'R', 'y');
		t33.AddOperation(2, 'W', 'z');

		ArrayList<Transaction> example2 = new ArrayList<Transaction>();
		example2.add(t33);
		example2.add(t22);
		example2.add(t11);
		

		System.out.println("====== Example 2 =======");
		System.out.println();
		t11.Print();
		t22.Print();
		t33.Print();
		System.out.println();
		System.out.println();
		System.out.println();

		Tools.DecideIsolationLevel(example2);

		// ================== Testing getCycles() on another Example ==================

//		Operation op = new Operation(1, 'W', 'x');
//		ArrayList<Edge> test = new ArrayList<Edge>();
//
//		Edge e1 = new Edge(1, op, 2, op, 1);
//		test.add(e1);
//		Edge e2 = new Edge(1, op, 4, op, 2);
//		test.add(e2);
//		Edge e3 = new Edge(2, op, 5, op, 3);
//		test.add(e3);
//		Edge e4 = new Edge(2, op, 3, op, 4);
//		test.add(e4);
//		Edge e5 = new Edge(3, op, 6, op, 5);
//		test.add(e5);
//		Edge e6 = new Edge(4, op, 5, op, 6);
//		test.add(e6);
//		Edge e7 = new Edge(4, op, 7, op, 7);
//		test.add(e7);
//		Edge e8 = new Edge(5, op, 6, op, 8);
//		test.add(e8);
//		Edge e9 = new Edge(5, op, 8, op, 9);
//		test.add(e9);
//		Edge e10 = new Edge(6, op, 9, op, 10);
//		test.add(e10);
//		Edge e11 = new Edge(7, op, 8, op, 11);
//		test.add(e11);
//		Edge e12 = new Edge(8, op, 9, op, 12);
//		test.add(e12);
//
//		for (int i = 0; i < test.size(); i++) {
//			Edge e = test.get(i);
//			e.print();
//		}
//		System.out.println();
//		System.out.println();
//		System.out.println();
//
//		ArrayList<Cycle> cycles2 = Tools.getCycles(test);
//	
//		System.out.println("The Possible Cycles Are:");
//		for (int i = 0; i < cycles2.size(); i++) {
//			int n = i + 1;
//			System.out.println("Cycle number " + n);
//			cycles2.get(i).print();
//		}

	}
}
