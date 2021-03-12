import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {

	// this method takes a set of objects and a list of operations and return only
	// the operations that use objects from that set.
	static ArrayList<Operation> findOperations(ArrayList<Operation> operations, Set<Character> union) {
		ArrayList<Operation> group = new ArrayList<Operation>();
		Operation temp;

		for (int i = 0; i < operations.size(); i++) {
			temp = operations.get(i);
			if (union.contains(temp.getObject()))
				group.add(temp);
		}

		return group;
	}

	// an overloaded version of the previous method.
	// in this case it takes 2 set of objects and returns 2 wrapped lists as a
	// DoubleList object.
	static DoubleList findOperations(ArrayList<Operation> operations, Set<Character> union1, Set<Character> union2) {
		ArrayList<Operation> group1 = new ArrayList<Operation>();
		ArrayList<Operation> group2 = new ArrayList<Operation>();
		Operation temp;

		for (int i = 0; i < operations.size(); i++) {
			temp = operations.get(i);
			if (union1.contains(temp.getObject()))
				group1.add(temp);
			if (union2.contains(temp.getObject()))
				group2.add(temp);
		}

		DoubleList result = new DoubleList(group1, group2);
		return result;
	}

	// this method takes 2 groups of operations and find all possible edges that can
	// be constructed from them.
	static ArrayList<Edge> findEdges(Transaction t1, ArrayList<Operation> group1, Transaction t2,
			ArrayList<Operation> group2) {
		ArrayList<Edge> result = new ArrayList<Edge>();
		Operation temp1;
		Operation temp2;

		// Nested list to find the edges from the 2 lists of group1 and group2
		for (int i = 0; i < group1.size(); i++) {
			temp1 = group1.get(i);

			for (int j = 0; j < group2.size(); j++) {
				temp2 = group2.get(j);
				// To have an edge, both operations must work on the same object.
				if (temp1.getObject() == temp2.getObject()) {
					// An edge must contain at least one writing operation.
					if (temp1.getType() == 'W' || temp2.getType() == 'W')
						result.add(new Edge(t1.getId(), temp1, t2.getId(), temp2));
				}
			}
		}

		return result;
	}

	// This method takes 2 transactions and return a list of all edges between them.
	static ArrayList<Edge> getEdges(Transaction t1, Transaction t2) {
		ArrayList<Edge> result = new ArrayList<Edge>();

		// create a new set and store a copy the set we get from a transaction inside it
		// "To avoid unexpected errors".
		Set<Character> set1 = new HashSet<Character>(t1.getUsedObjects());
		Set<Character> set2 = new HashSet<Character>(t2.getUsedObjects());
		Set<Character> union = new HashSet<Character>(t1.getUsedObjects());
		// find the intersection between set1 and set2
		union.retainAll(set2);

		if (union.size() != 0) {
			ArrayList<Operation> operations1 = t1.getOperations();
			ArrayList<Operation> operations2 = t2.getOperations();

			// To store operations from t1 that use the objects in the union set
			ArrayList<Operation> group1 = new ArrayList<Operation>();
			// To store operations from t2 that use the objects in the union set
			ArrayList<Operation> group2 = new ArrayList<Operation>();

			// one iteration over the first transaction
			group1 = findOperations(operations1, union);

			// one iteration over the second transaction
			group2 = findOperations(operations2, union);

			result = findEdges(t1, group1, t2, group2);
		}

		return result;
	}

	// An overloaded method of getEdges with a better efficiency.
	static ArrayList<Edge> getEdges(Transaction t1, Transaction t2, Transaction t3) {
		ArrayList<Edge> result = new ArrayList<Edge>();

		Set<Character> set1 = new HashSet<Character>(t1.getUsedObjects());
		Set<Character> set2 = new HashSet<Character>(t2.getUsedObjects());
		Set<Character> set3 = new HashSet<Character>(t3.getUsedObjects());
		Set<Character> union12 = new HashSet<Character>(t1.getUsedObjects());
		Set<Character> union13 = new HashSet<Character>(t1.getUsedObjects());
		Set<Character> union23 = new HashSet<Character>(t2.getUsedObjects());

		union12.retainAll(set2); // set1 and set2
		union13.retainAll(set3); // set1 and set3
		union23.retainAll(set3); // set2 and set3

		ArrayList<Operation> operations1 = t1.getOperations();
		ArrayList<Operation> operations2 = t2.getOperations();
		ArrayList<Operation> operations3 = t3.getOperations();

		ArrayList<Operation> group12 = new ArrayList<Operation>();
		ArrayList<Operation> group21 = new ArrayList<Operation>();
		ArrayList<Operation> group13 = new ArrayList<Operation>();
		ArrayList<Operation> group31 = new ArrayList<Operation>();
		ArrayList<Operation> group23 = new ArrayList<Operation>();
		ArrayList<Operation> group32 = new ArrayList<Operation>();

		// one iteration over the first transaction
		if (union12.size() != 0 || union13.size() != 0) {
			DoubleList doubleList1 = findOperations(operations1, union12, union13);
			group12 = doubleList1.getFirst();
			group13 = doubleList1.getSecond();
		}

		// one iteration over the second transaction
		if (union12.size() != 0 || union23.size() != 0) {
			DoubleList doubleList2 = findOperations(operations2, union12, union23);
			group21 = doubleList2.getFirst();
			group23 = doubleList2.getSecond();
		}

		// one iteration over the third transaction
		if (union13.size() != 0 || union23.size() != 0) {
			DoubleList doubleList3 = findOperations(operations3, union13, union23);
			group31 = doubleList3.getFirst();
			group32 = doubleList3.getSecond();
		}

		ArrayList<Edge> result1 = findEdges(t1, group12, t2, group21);
		ArrayList<Edge> result2 = findEdges(t1, group13, t3, group31);
		ArrayList<Edge> result3 = findEdges(t2, group23, t3, group32);
		result.addAll(result1);
		result.addAll(result2);
		result.addAll(result3);

		return result;
	}

	// An overloaded method of getEdges that takes a list of transactions. 
	static ArrayList<Edge> getEdges(ArrayList<Transaction> t) {
		ArrayList<Edge> result = new ArrayList<Edge>();
		ArrayList<Edge> temp = new ArrayList<Edge>();

		for (int i = 0; i < t.size() - 1; i++) {
			for (int j = i + 1; j < t.size(); j++) {
				temp = getEdges(t.get(i), t.get(j));
				result.addAll(temp);
			}
		}
		return result;
	}

	// =========================== Main ================================
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

		// ============= Getting the Edges using the first approach ==============

		ArrayList<Edge> list1 = getEdges(t1, t2); // 2 iterations
		ArrayList<Edge> list2 = getEdges(t1, t3); // 2 iterations
		ArrayList<Edge> list3 = getEdges(t2, t3); // 2 iterations
		ArrayList<Edge> total = new ArrayList<Edge>(); // 6 iterations
		total.addAll(list1);
		total.addAll(list2);
		total.addAll(list3);

		for (int i = 0; i < total.size(); i++) {
			Edge e = total.get(i);
			e.print();
		}
		System.out.println();

		// ============= Getting the Edges using the second approach =============

		ArrayList<Edge> list = getEdges(t1, t2, t3); // 3 iterations
		for (int i = 0; i < list.size(); i++) {
			Edge e = list.get(i);
			e.print();
		}
		System.out.println();
		
		// ============= Getting the Edges using the third approach =============

		ArrayList<Edge> edges = getEdges(transactions);
		for (int i = 0; i < edges.size(); i++) {
			Edge e = edges.get(i);
			e.print();
		}
		
	}
}
