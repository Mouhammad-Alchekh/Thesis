import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class Tools {

	// ====================== This Part is for getEdges =========================

	// this method takes a set of objects and a list of operations and return only
	// the operations that use objects from that set.
	private static ArrayList<Operation> findOperations(ArrayList<Operation> operations, Set<Character> intersection) {
		ArrayList<Operation> group = new ArrayList<Operation>();
		Operation temp;

		for (int i = 0; i < operations.size(); i++) {
			temp = operations.get(i);
			if (intersection.contains(temp.getObject()))
				group.add(temp);
		}

		return group;
	}

	// an overloaded version of the previous method.
	// in this case it takes 2 set of objects and returns 2 wrapped lists as a
	// DoubleList object.
	private static DoubleList findOperations(ArrayList<Operation> operations, Set<Character> intersection1,
			Set<Character> intersection2) {
		ArrayList<Operation> group1 = new ArrayList<Operation>();
		ArrayList<Operation> group2 = new ArrayList<Operation>();
		Operation temp;

		for (int i = 0; i < operations.size(); i++) {
			temp = operations.get(i);
			if (intersection1.contains(temp.getObject()))
				group1.add(temp);
			if (intersection2.contains(temp.getObject()))
				group2.add(temp);
		}

		DoubleList result = new DoubleList(group1, group2);
		return result;
	}

	// this method takes 2 groups of operations and find all possible edges that can
	// be constructed from them.
	private static ArrayList<Edge> findEdges(Transaction t1, ArrayList<Operation> group1, Transaction t2,
			ArrayList<Operation> group2, int idCounter) {
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
					if (temp1.getType() == 'W' || temp2.getType() == 'W') {
						idCounter++;
						result.add(new Edge(t1.getId(), temp1, t2.getId(), temp2, idCounter));
					}
				}
			}
		}

		return result;
	}

	// This method takes 2 transactions and return a list of all edges between them.
	private static ArrayList<Edge> getEdges(Transaction t1, Transaction t2, int idCounter) {
		ArrayList<Edge> result = new ArrayList<Edge>();

		// create a new set and store a copy the set we get from a transaction inside it
		// "To avoid unexpected errors".
		Set<Character> set1 = new HashSet<Character>(t1.getUsedObjects());
		Set<Character> set2 = new HashSet<Character>(t2.getUsedObjects());
		Set<Character> intersection = new HashSet<Character>(t1.getUsedObjects());
		// find the intersection between set1 and set2
		intersection.retainAll(set2);

		if (intersection.size() != 0) {
			ArrayList<Operation> operations1 = t1.getOperations();
			ArrayList<Operation> operations2 = t2.getOperations();

			// To store operations from t1 that use the objects in the union set
			ArrayList<Operation> group1 = new ArrayList<Operation>();
			// To store operations from t2 that use the objects in the union set
			ArrayList<Operation> group2 = new ArrayList<Operation>();

			// one iteration over the first transaction
			group1 = findOperations(operations1, intersection);

			// one iteration over the second transaction
			group2 = findOperations(operations2, intersection);

			result = findEdges(t1, group1, t2, group2, idCounter);
		}

		return result;
	}

	// An overloaded method of getEdges with a better efficiency.
	public static ArrayList<Edge> getEdges(Transaction t1, Transaction t2, Transaction t3) {
		ArrayList<Edge> result = new ArrayList<Edge>();
		int edgeIdCounter = 0;

		Set<Character> set1 = new HashSet<Character>(t1.getUsedObjects());
		Set<Character> set2 = new HashSet<Character>(t2.getUsedObjects());
		Set<Character> set3 = new HashSet<Character>(t3.getUsedObjects());
		Set<Character> intersection12 = new HashSet<Character>(t1.getUsedObjects());
		Set<Character> intersection13 = new HashSet<Character>(t1.getUsedObjects());
		Set<Character> intersection23 = new HashSet<Character>(t2.getUsedObjects());

		intersection12.retainAll(set2); // set1 and set2
		intersection13.retainAll(set3); // set1 and set3
		intersection23.retainAll(set3); // set2 and set3

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
		if (intersection12.size() != 0 || intersection13.size() != 0) {
			DoubleList doubleList1 = findOperations(operations1, intersection12, intersection13);
			group12 = doubleList1.getFirst();
			group13 = doubleList1.getSecond();
		}

		// one iteration over the second transaction
		if (intersection12.size() != 0 || intersection23.size() != 0) {
			DoubleList doubleList2 = findOperations(operations2, intersection12, intersection23);
			group21 = doubleList2.getFirst();
			group23 = doubleList2.getSecond();
		}

		// one iteration over the third transaction
		if (intersection13.size() != 0 || intersection23.size() != 0) {
			DoubleList doubleList3 = findOperations(operations3, intersection13, intersection23);
			group31 = doubleList3.getFirst();
			group32 = doubleList3.getSecond();
		}

		ArrayList<Edge> result1 = findEdges(t1, group12, t2, group21, edgeIdCounter);
		int lastIndex = result1.size() - 1;
		//continue the Id counter from the last id in the previous list
		edgeIdCounter = result1.get(lastIndex).getId();

		ArrayList<Edge> result2 = findEdges(t1, group13, t3, group31, edgeIdCounter);
		lastIndex = result2.size() - 1;
		edgeIdCounter = result2.get(lastIndex).getId();

		ArrayList<Edge> result3 = findEdges(t2, group23, t3, group32, edgeIdCounter);
		result.addAll(result1);
		result.addAll(result2);
		result.addAll(result3);

		return result;
	}

	// An overloaded method of getEdges that takes a list of transactions.
	public static ArrayList<Edge> getEdges(ArrayList<Transaction> t) {
		ArrayList<Edge> result = new ArrayList<Edge>();
		ArrayList<Edge> temp = new ArrayList<Edge>();
		int edgeIdCounter = 0;

		for (int i = 0; i < t.size() - 1; i++) {
			for (int j = i + 1; j < t.size(); j++) {
				temp = getEdges(t.get(i), t.get(j), edgeIdCounter);
				// set the counter to be the id of the last edge from the previous list of edges
				int last = temp.size() - 1;
				edgeIdCounter = temp.get(last).getId();

				result.addAll(temp);
			}
		}
		return result;
	}

	// ====================== This Part is for getCycles =========================

	// This method checks if two edges can be connected with each other.
	private static boolean isConnectable(Edge e1, Edge e2) {
		return (e1.getT1ID() == e2.getT1ID() || e1.getT1ID() == e2.getT2ID() || e1.getT2ID() == e2.getT1ID()
				|| e1.getT2ID() == e2.getT2ID());

	}

	// This method checks if a given edge can be connected to any link.
	private static boolean checkConnection(ArrayList<Cycle> links, Edge e) {
		for (int i = 0; i < links.size(); i++) {

			LinkedList<Edge> currentLink = links.get(i).getCycle();

			for (int j = 0; j < currentLink.size(); j++) {
				Edge currentEdge = currentLink.get(j);
				if (isConnectable(currentEdge, e))
					return true;
			}
		}

		return false;
	}

	// This method checks if 2 edges are similar "not identical".
	// 2 edges are similar if they have different id and are connected to the same 2
	// transactions.
	private static boolean isSimilar(Edge e1, Edge e2) {

		// check if the edges are different.
		if (e1.getId() == e2.getId())
			return false;

		if (e1.getT1ID() == e2.getT1ID() && e1.getT2ID() == e2.getT2ID())
			return true;

		if (e1.getT1ID() == e2.getT2ID() && e1.getT2ID() == e2.getT1ID())
			return true;

		return false;
	}

	// This method checks if a given link of edges has a similar edge to a given
	// edge.
	private static boolean hasSimilar(Cycle link, Edge e) {
		for (int i = 0; i < link.size(); i++) {
			if (isSimilar(link.getEdge(i), e))
				return true;
		}
		return false;
	}

	// This method connect a given edge to every link that can be connected to.
	private static void connectEdge(ArrayList<Cycle> links, Edge e) {
		for (int i = 0; i < links.size(); i++) {

			Cycle currentLink = links.get(i);
			Edge start = currentLink.getFirstEdge();
			Edge end = currentLink.getLastEdge();

			// if the link has a similar edge, do nothing.
			if (hasSimilar(currentLink, e))
				continue;

			if (isConnectable(start, e)) {
				// to keep edges in cycles ordered in a correct way
				if (e.getT2ID() != start.getT1ID())
					e.flip();
				// connect the new edge
				currentLink.addEdgeFront(e);

			} else if (isConnectable(end, e)) {
				if (e.getT1ID() != end.getT2ID())
					e.flip();
				currentLink.addEdge(e);
			}
		}
	}

	// This method takes a list of links and an edge and detects any split that can
	// be done on any link of this list based on that edge.
	// and then modify the given list of links by adding 2 additional links that
	// are created from this split
	private static void findSplit(ArrayList<Cycle> links, Edge e) {
		int splitPoint = 0;

		// iterate over each link of the given list of links
		for (int i = 0; i < links.size(); i++) {
			LinkedList<Edge> currentLink = links.get(i).getCycle();

			// if the link has a similar edge, do nothing.
			if (hasSimilar(links.get(i), e))
				continue;

			// detect split on a small link of 2 edges.
			if (currentLink.size() == 2) {
				if (currentLink.get(0).getT2ID() == e.getT1ID() || currentLink.get(0).getT2ID() == e.getT2ID()) {
					// Create the first link
					Cycle first = new Cycle();
					first.addEdge(currentLink.get(0));
					// Create the second link
					Cycle second = new Cycle();
					second.addEdge(currentLink.get(1));
					// add the new links to the list of links
					links.add(first);
					links.add(second);
				}

			}
			// detect split on a link that has more than 2 edges.
			// the loop is to find the split Point.
			// A split point cannot be at the start or at the end.
			for (int j = 1; j < currentLink.size() - 1; j++) {
				if (isConnectable(currentLink.get(j), e))
					splitPoint = j;
				break;
			}

			// if there is a split point
			if (splitPoint != 0) {
				Cycle first = new Cycle();
				Cycle second = new Cycle();

				// Create the first link
				for (int k = 0; k < splitPoint; k++) {
					first.addEdge(currentLink.get(k));
				}
				// Create the second link
				for (int l = splitPoint; l < currentLink.size(); l++) {
					second.addEdge(currentLink.get(l));
				}

				links.add(first);
				links.add(second);
				splitPoint = 0;
			}
		}
	}

	// delete repeated links
	private static void deleteDouble(ArrayList<Cycle> links) {
		int i = 0;
		boolean reset = false;

		// nested loop to get each two pairs of links.
		// This nested loop will reset after deleting a link.
		// This nested loop will end only when deleting nothing.
		while (i < links.size() - 1) {
			int j = i + 1;
			while (j < links.size()) {

				// if two links are similar, delete one of them.
				if (links.get(i).isEqual(links.get(j))) {
					links.remove(j);
					// after deletion, reset the procedure.
					reset = true;
					break;
				}
				j++;
			}
			// to reset, first make i=-1 and the next increment i++ will make it i=0.
			if (reset) {
				i = -1;
				reset = false;
			}
			i++;
		}
	}

	// This method detect if there is any cycle from a given list of links and
	// return a list of found cycles.
	private static ArrayList<Cycle> detectCycle(ArrayList<Cycle> links) {
		ArrayList<Cycle> result = new ArrayList<Cycle>();

		for (int i = 0; i < links.size(); i++) {
			Cycle current = links.get(i);
			Edge start = current.getFirstEdge();
			Edge end = current.getLastEdge();

			// if the start = end , a link is a cycle.
			if (start.getT1ID() == end.getT2ID()) {
				Cycle newCycle = new Cycle();
				newCycle.copyCycle(current);
				result.add(newCycle);
				// after adding the cycle we need to delete the first edge.
				current.removeFirstEdge();
			}
		}
		return result;
	}

	// this method detect and get all tiny cycles that can be constructed from a
	// given set of edges. A tiny cycle is constructed from 2 edges.
	private static ArrayList<Cycle> getTinyCycles(ArrayList<Edge> edges) {
		ArrayList<Cycle> result = new ArrayList<Cycle>();

		for (int i = 0; i < edges.size() - 1; i++) {
			for (int j = i + 1; j < edges.size(); j++) {
				if (isSimilar(edges.get(i), edges.get(j))) {
					Cycle tinyCycle = new Cycle();
					tinyCycle.addEdge(edges.get(i));
					// to make sure that the 2 edges are ordered correctly.
					if (edges.get(i).getT2ID() != edges.get(j).getT1ID())
						edges.get(j).flip();
					tinyCycle.addEdge(edges.get(j));

					result.add(tinyCycle);
				}
			}
		}
		return result;
	}

	// This method takes a list of cycles and an edge as an input.
	// if any cycle in this list has a similar edge, it creates a new cycle by
	// replacing the found edge with the new given edge. and then add this new cycle
	// to the list.
	private static void addReplaceable(ArrayList<Cycle> result, Edge e) {
		int replacePoint = 0;
		boolean canReplace = false;

		// iterate over the list of cycles to get each cycle
		for (int i = 0; i < result.size(); i++) {
			Cycle currentCycle = result.get(i);

			// for each cycle iterate over its edges to check if any of its edges is similar
			// to the given edge
			for (int j = 0; j < currentCycle.size(); j++) {
				Edge currentEdge = currentCycle.getEdge(j);
				// check if the cycle has a similar edge
				if (isSimilar(currentEdge, e)) {
					replacePoint = j;
					canReplace = true;
					break;
				}
			}
			// if the cycle has a similar edge, create the new cycle
			if (canReplace) {
				Cycle newCycle = new Cycle();
				newCycle.copyCycle(currentCycle);
				newCycle.replace(replacePoint, e);

				result.add(newCycle);
				canReplace = false;
			}
		}
	}

	// This method takes a list of edges and return all possible cycles that can be
	// constructed from them
	public static ArrayList<Cycle> getCycles(ArrayList<Edge> edges) {
		// A list that will contain all possible cycles
		ArrayList<Cycle> result = new ArrayList<Cycle>();
		// a link is a sequence of edges while a cycle is a link in which start = end.
		ArrayList<Cycle> links = new ArrayList<Cycle>();

		// iterate over all edges
		for (int i = 0; i < edges.size(); i++) {
			Edge currentEdge = edges.get(i);

			// if there exist no link
			if (links.size() == 0) {
				// Create the first link
				Cycle link = new Cycle();
				link.addEdge(currentEdge);
				links.add(link);

			} else {
				// if the edge cannot be connected to any link
				if (!checkConnection(links, currentEdge)) {
					// Create a new link
					Cycle link = new Cycle();
					link.addEdge(currentEdge);
					links.add(link);
				} else {
					// find all split links based on current edge and add them to links list
					findSplit(links, currentEdge);
					// connect the current edge to each link that can be connected to
					connectEdge(links, currentEdge);
					// find new cycles from the current list of links
					ArrayList<Cycle> newCycles = detectCycle(links);
					// Add the found cycles to the result list
					result.addAll(newCycles);
					// delete any repeated link
					deleteDouble(links);
					// find new cycles by replacing the current edge with any similar edge in any
					// cycle. this method is needed because if a link has a similar edge this link
					// will not be split and it will not connect the given edge. this might lead
					// to missing some cycles. this method will compensate that.
					addReplaceable(result, currentEdge);
				}
			}
		}
		for (int i = 0; i < edges.size(); i++) {
			Edge currentEdge = edges.get(i);
			// find new cycles by replacing the current edge with any similar edge in any
			// cycle. this method is needed because if a link has a similar edge this link
			// will not be split and it will not connect the given edge. this might lead
			// to missing some cycles. this method will compensate that.
			addReplaceable(result, currentEdge);
		}

		// do a second iteration over the given set of edges to make sure that all tiny
		// cycles are added.
		ArrayList<Cycle> tinyCycles = getTinyCycles(edges);
		result.addAll(tinyCycles);

		// delete duplicate cycles
		deleteDouble(result);

		return result;
	}
}
