import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {

	public static void printTransactions(ArrayList<Transaction> t) {
		for (int i = 0; i < t.size(); i++) {
			t.get(i).Print();
		}
		System.out.println();
	}

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
		
		// ====== Example 2 =======

		Transaction t21 = new Transaction(1);
		t21.AddOperation(1, 'W', 'x');
		t21.AddOperation(2, 'W', 'y');
		Transaction t22 = new Transaction(2);
		t22.AddOperation(1, 'R', 'v');
		t22.AddOperation(2, 'R', 'z');
		t22.AddOperation(3, 'W', 'v');
		t22.AddOperation(4, 'W', 'x');
		Transaction t23 = new Transaction(3);
		t23.AddOperation(1, 'R', 'y');
		t23.AddOperation(2, 'W', 'z');
		ArrayList<Transaction> example2 = new ArrayList<Transaction>();
		example2.add(t23);
		example2.add(t22);
		example2.add(t21);

		// ====== Example 3 =======

		Transaction t31 = new Transaction(1);
		t31.AddOperation(1, 'R', 'x');
		t31.AddOperation(2, 'W', 'y');
		t31.AddOperation(3, 'R', 'z');
		Transaction t32 = new Transaction(2);
		t32.AddOperation(1, 'R', 'y');
		t32.AddOperation(2, 'R', 'v');
		t32.AddOperation(3, 'W', 'a');
		t32.AddOperation(4, 'W', 'd');
		Transaction t33 = new Transaction(3);
		t33.AddOperation(1, 'R', 'b');
		t33.AddOperation(2, 'W', 'c');
		t33.AddOperation(3, 'W', 'v');
		Transaction t34 = new Transaction(4);
		t34.AddOperation(1, 'R', 'm');
		t34.AddOperation(2, 'R', 'd');
		ArrayList<Transaction> example3 = new ArrayList<Transaction>();
		example3.add(t31);
		example3.add(t32);
		example3.add(t33);
		example3.add(t34);
		
		// ====== Example 4 =======
		
		Transaction t41 = new Transaction(1);
		t41.AddOperation(1, 'W', 'x');
		t41.AddOperation(2, 'W', 'y');
		Transaction t42 = new Transaction(2);
		t42.AddOperation(1, 'R', 'z');
		t42.AddOperation(2, 'W', 'm');
		t42.AddOperation(3, 'R', 'y');
		Transaction t43 = new Transaction(3);
		t43.AddOperation(1, 'W', 'm');
		t43.AddOperation(2, 'W', 'x');
		Transaction t44 = new Transaction(4);
		t44.AddOperation(1, 'R', 'a');
		t44.AddOperation(2, 'W', 'b');
		t44.AddOperation(3, 'W', 'c');
		Transaction t45 = new Transaction(5);
		t45.AddOperation(1, 'W', 'd');
		t45.AddOperation(2, 'R', 'a');
		ArrayList<Transaction> example4 = new ArrayList<Transaction>();
		example4.add(t41);
		example4.add(t42);
		example4.add(t43);
		example4.add(t44);
		example4.add(t45);
		

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

	
		
		printTransactions(example4);
		Tools.DecideIsolationLevel(example4);
		
		
		
	}
}
