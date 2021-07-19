import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class Tools {

	// ====================== This Part is for getEdges =========================

	// this method takes a set of objects and a list of operations and return only
	// the operations that use objects from that set.
	private static ArrayList<Operation> findOperations(ArrayList<Operation> operations, Set<String> intersection) {
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
	private static DoubleList findOperations(ArrayList<Operation> operations, Set<String> intersection1,
			Set<String> intersection2) {
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
		Set<String> set1 = new HashSet<String>(t1.getUsedObjects());
		Set<String> set2 = new HashSet<String>(t2.getUsedObjects());
		Set<String> intersection = new HashSet<String>(t1.getUsedObjects());
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

		Set<String> set1 = new HashSet<String>(t1.getUsedObjects());
		Set<String> set2 = new HashSet<String>(t2.getUsedObjects());
		Set<String> set3 = new HashSet<String>(t3.getUsedObjects());
		Set<String> intersection12 = new HashSet<String>(t1.getUsedObjects());
		Set<String> intersection13 = new HashSet<String>(t1.getUsedObjects());
		Set<String> intersection23 = new HashSet<String>(t2.getUsedObjects());

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
		// continue the Id counter from the last id in the previous list
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
				if (temp.size() > 0) {
					int last = temp.size() - 1;
					edgeIdCounter = temp.get(last).getId();
					result.addAll(temp);
				}
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
			// Create a new edge and copy the given edge into.
			// This is it to prevent any problem caused by pointers.
			Edge tempEdge = new Edge(e.getT1ID(), e.getOp1(), e.getT2ID(), e.getOp2(), e.getId());

			// if the link has a similar edge, do nothing.
			if (hasSimilar(currentLink, tempEdge))
				continue;

			if (currentLink.size() == 1) {
				Edge start = currentLink.getFirstEdge();
				// connecting an edge with a link of size 1 in a correct way
				if (start.getT1ID() == tempEdge.getT2ID()) {
					currentLink.addEdgeFront(tempEdge);
				} else if (start.getT1ID() == tempEdge.getT1ID()) {
					tempEdge.flip();
					currentLink.addEdgeFront(tempEdge);
				} else if (start.getT2ID() == tempEdge.getT1ID()) {
					currentLink.addEdge(tempEdge);
				} else if (start.getT2ID() == tempEdge.getT2ID()) {
					tempEdge.flip();
					currentLink.addEdge(tempEdge);
				}

			} else {
				Edge start = currentLink.getFirstEdge();
				Edge end = currentLink.getLastEdge();

				if (start.getT1ID() == tempEdge.getT2ID()) {
					currentLink.addEdgeFront(tempEdge);
				} else if (start.getT1ID() == tempEdge.getT1ID()) {
					// to keep edges in cycles ordered in a correct way
					tempEdge.flip();
					currentLink.addEdgeFront(tempEdge);
				} else if (end.getT2ID() == tempEdge.getT1ID()) {
					currentLink.addEdge(tempEdge);
				} else if (end.getT2ID() == tempEdge.getT2ID()) {
					tempEdge.flip();
					currentLink.addEdge(tempEdge);
				}
			}
		}
	}

	// This method takes a list of links and an edge and detects any split that can
	// be done on any link of this list based on that edge.
	// and then modify the given list of links by adding 2 additional links that
	// are created from this split
	private static void findSplit(ArrayList<Cycle> links, Edge e) {
		int splitPoint = 0;
		ArrayList<Cycle> newLinks = new ArrayList<Cycle>();

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
					newLinks.add(first);
					newLinks.add(second);
				}

			}
			// detect split on a link that has more than 2 edges.
			// the loop is to find the split Point.
			// A split point cannot be at the start or at the end.
			for (int j = 1; j < currentLink.size() - 1; j++) {
				if (isConnectable(currentLink.get(j), e)) {
					splitPoint = j;
					break;
				}
			}

			// if there is a split point
			if (splitPoint != 0) {
				Cycle first = new Cycle();
				Cycle second = new Cycle();

				// if the mutual node is a left node in the found edge
				// Ex: the edge at the split point is T2 <-> T4 and the given edge is T2 <-> T8
				if (currentLink.get(splitPoint).mutualNodeOnLeft(e)) {
					// Create the first link
					for (int k = 0; k < splitPoint; k++) {
						first.addEdge(currentLink.get(k));
					}
					// Create the second link
					for (int l = splitPoint; l < currentLink.size(); l++) {
						second.addEdge(currentLink.get(l));
					}

					// if the mutual node is a right node in the found edge
					// Ex: the edge at the split point is T4 <-> T2 and the given edge is T2 <-> T8
				} else {
					for (int k = 0; k <= splitPoint; k++) {
						first.addEdge(currentLink.get(k));
					}
					for (int l = splitPoint + 1; l < currentLink.size(); l++) {
						second.addEdge(currentLink.get(l));
					}
				}

				newLinks.add(first);
				newLinks.add(second);
				splitPoint = 0;
			}
		}
		links.addAll(newLinks);
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

	// This method deletes cycles that are constructed from interleaved cycles
	// Ex: "T4" <-> T1 <-> T2 <-> T3 <-> "T4" <-> T7 <-> T6 <-> T5 <-> "T4"
	private static void deleteInterleaved(ArrayList<Cycle> links) {
		ArrayList<Integer> toBeDeleted = new ArrayList<Integer>();
		boolean interleaved = false;

		// first step: detect all interleaved cycles
		for (int i = 0; i < links.size(); i++) {
			Cycle currentCycle = links.get(i);

			for (int j = 0; j < currentCycle.size() - 1; j++) {
				int tId1 = currentCycle.getEdge(j).getT1ID();
				for (int k = j + 1; k < currentCycle.size(); k++) {
					int tId2 = currentCycle.getEdge(k).getT1ID();
					// if a node is used more than once in a cycle, the cycle is interleaved.
					if (tId1 == tId2) {
						interleaved = true;
						break;
					}
				}
				if (interleaved) {
					toBeDeleted.add(i);
					interleaved = false;
					break;
				}
			}
		}
		// second step: delete the found interleaved cycles
		for (int i = toBeDeleted.size() - 1; i >= 0; i--) {
			int index = toBeDeleted.get(i);
			links.remove(index);
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
				// create a new cycle and copy the found cycle into it and then add it.This is
				// to prevent the cycle to be changed later due to the nature of pointers.
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
	private static void detectBySimilar(ArrayList<Cycle> result, Edge e) {
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
				}
			}
		}
		for (int i = 0; i < edges.size(); i++) {
			Edge currentEdge = edges.get(i);
			/**
			 * find new cycles by replacing the current edge with any similar edge in any
			 * cycle. this method is needed because if a link has a similar edge this link
			 * will not be split and it will not connect the given edge. this might lead to
			 * missing some cycles. this method will compensate that.
			 **/
			detectBySimilar(result, currentEdge);
		}
		// do another iteration over the given set of edges to make sure that all tiny
		// cycles are added.
		ArrayList<Cycle> tinyCycles = getTinyCycles(edges);
		result.addAll(tinyCycles);

		// delete duplicate cycles
		deleteDouble(result);
		// delete interleaved cycles
		deleteInterleaved(result);

		return result;
	}

	// =============== This Part is for Deciding The Isolation Level ===============

	// This method returns all Non-Trivial cycles from a list of cycles.
	private static ArrayList<Cycle> getNonTrivialCycles(ArrayList<Cycle> cycles) {
		ArrayList<Cycle> result = new ArrayList<Cycle>();

		for (int i = 0; i < cycles.size(); i++) {
			// get each cycle and iterate over it
			Cycle currentCycle = cycles.get(i);

			for (int j = 0; j < currentCycle.size(); j++) {
				// for each iteration, get a consecutive pairs of edges.
				int k = j + 1;
				if (j == currentCycle.size() - 1)
					// to compare the first and the last edges. They are connected to each other.
					k = 0;
				Edge currentEdge = currentCycle.getEdge(j);
				Edge nextEdge = currentCycle.getEdge(k);
				// Check the condition of Non Trivial Cycle
				if (currentEdge.getOp2().isDifferent(nextEdge.getOp1())) {
					Cycle newCycle = new Cycle();
					newCycle.copyCycle(currentCycle);
					result.add(newCycle);
					// break looping over the cycle after satisfying the condition of Non Trivial
					// Cycle.
					break;
				}
			}
		}
		return result;
	}

	// this method checks if a transaction has a write operation that conflicts with
	// an operation in the given list writeOp.
	private static boolean hasWriteConflict(Transaction t, ArrayList<Operation> writeOp) {

		for (int i = 0; i < t.size(); i++) {
			if (writeOp.contains(t.getOperations().get(i))) {
				return true;
			}
		}
		return false;
	}

	// This method checks every transaction in a given list of transactions to see
	// if it has a write operation that conflicts with any operation in the given
	// writeOp list.
	private static boolean containsWriteConflict(ArrayList<Operation> writeOp, ArrayList<Transaction> t) {
		for (int k = 0; k < t.size(); k++) {
			if (hasWriteConflict(t.get(k), writeOp))
				return true;
		}
		return false;
	}

	// This method takes a transaction ID and a list of transactions as an input and
	// return the index of the transaction that has the same ID.
	private static int getTransactionPosition(int id, ArrayList<Transaction> t) {
		for (int k = 0; k < t.size(); k++) {
			if (t.get(k).getId() == id)
				return k;
		}
		return -1;
	}

	// This method returns all write operations in a transactions that happen before
	// a specific index.
	private static ArrayList<Operation> getprefixWriteOps(int index, Transaction t) {
		ArrayList<Operation> result = new ArrayList<Operation>();
		for (int k = 0; k <= index; k++) {
			if (t.getOperations().get(k).getType() == 'W')
				result.add(t.getOperations().get(k));
		}
		return result;
	}

	// This method checks if a consecutive 2 edges in a cycle can make it
	// transferable or not, and it will return a list of information about where the
	// transfer point is. if the cycle is not transferable, the returned list will
	// be empty.
	private static ArrayList<Integer> trantransferableInfo(Edge currentEdge, Edge nextEdge, ArrayList<Transaction> t) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		// These are needed to check if a cycle is transferable.
		int tId = currentEdge.getT2ID();
		Operation op1 = currentEdge.getOp2();
		Operation op2 = nextEdge.getOp1();
		// get the corresponding transaction of the two operations
		int tIndex = getTransactionPosition(tId, t);
		// get the index of each operation
		int index1 = t.get(tIndex).getOperations().indexOf(op1);
		int index2 = t.get(tIndex).getOperations().indexOf(op2);
		// if this happens, the cycle is transferable
		if (index2 < index1) {
			// add the info about the tranfer point to the result and return it.
			result.add(tIndex);
			result.add(index2);
			result.add(tId);
			return result;
		}
		return result;
	}

	// This method returns a list prefix-write conflict-free cycles for a set of
	// transactions along with a list that contains a split point for each cycle.
	private static CyclesAndPoints getCyclesAndPoints(ArrayList<Cycle> cycles, ArrayList<Transaction> t) {
		ArrayList<Cycle> result = new ArrayList<Cycle>();
		ArrayList<SplitPoint> splitPoints = new ArrayList<SplitPoint>();
		boolean containsWriteConflict = false;

		for (int i = 0; i < cycles.size(); i++) {
			// get each cycle and iterate over it
			Cycle currentCycle = cycles.get(i);

			for (int j = 0; j < currentCycle.size(); j++) {
				// for each iteration, get a consecutive pairs of edges.
				int k = j + 1;
				if (j == currentCycle.size() - 1)
					// to compare the first and the last edges. They are connected to each other.
					k = 0;
				Edge currentEdge = currentCycle.getEdge(j);
				Edge nextEdge = currentCycle.getEdge(k);

				ArrayList<Integer> transferInfo = trantransferableInfo(currentEdge, nextEdge, t);
				// if this happens, the cycle is transferable
				if (transferInfo.size() != 0) {
					int tIndex = transferInfo.get(0);
					int index2 = transferInfo.get(1);
					int tId = transferInfo.get(2);
					// get all write operations in the prefix part of the transaction
					ArrayList<Operation> writeOp = getprefixWriteOps(index2, t.get(tIndex));
					// check if the cycle is prefix-write conflict-free.
					containsWriteConflict = containsWriteConflict(writeOp, t);
					// if there is no conflict, the cycle is a prefix-write conflict-free
					if (!containsWriteConflict) {
						SplitPoint splitP = new SplitPoint(tIndex, index2, tId);
						splitPoints.add(splitP);
						Cycle newCycle = new Cycle();
						newCycle.copyCycle(currentCycle);
						result.add(newCycle);
						break;
					}
				}
			}
			containsWriteConflict = false;
		}
		CyclesAndPoints combined = new CyclesAndPoints(result, splitPoints);
		return combined;
	}

	// This method returns all split schedules that can be constructed from a given
	// list of prefix-write conflict-free cycles.
	private static ArrayList<Schedule> getSplitSchedules(ArrayList<Cycle> cycles, ArrayList<SplitPoint> splitPoints,
			ArrayList<Transaction> t) {
		ArrayList<Schedule> result = new ArrayList<Schedule>();

		// the size of cycles list and the size of splitPoints list are equal.
		for (int i = 0; i < cycles.size(); i++) {
			Cycle currentCycle = cycles.get(i);
			Schedule newSchedule = new Schedule("Split Schedule");

			int splitTIndex = splitPoints.get(i).gettIndex();
			Transaction splitTransaction = t.get(splitTIndex);
			int splitPosition = splitPoints.get(i).getOpIndex();

			Set<Integer> usedTransactions = new HashSet<Integer>();
			usedTransactions.add(splitTransaction.getId());

			// First Step: add all operations in the prefix part of the split transaction to
			// the schedule
			for (int j = 0; j <= splitPosition; j++) {
				Operation op = splitTransaction.getOperations().get(j);
				newSchedule.AddOperation(op, splitTransaction.getId());
			}
			// Second Step: add all transactions that are part of the cycle
			for (int j = 0; j < currentCycle.size(); j++) {
				int Tid = currentCycle.getEdge(j).getT1ID();
				if (Tid == splitTransaction.getId())
					continue;
				int tIndex = getTransactionPosition(Tid, t);
				Transaction currentTransaction = t.get(tIndex);
				usedTransactions.add(Tid);
				// add all operations in the current transaction to the schedule
				for (int k = 0; k < currentTransaction.size(); k++) {
					Operation op = currentTransaction.getOperations().get(k);
					newSchedule.AddOperation(op, Tid);
				}
			}
			// Third Step: add all operations in the postfix part of the split transaction
			// to the schedule
			for (int j = splitPosition + 1; j < splitTransaction.size(); j++) {
				Operation op = splitTransaction.getOperations().get(j);
				newSchedule.AddOperation(op, splitTransaction.getId());
			}
			// Last Step: add the rest transactions
			for (int j = 0; j < t.size(); j++) {
				Transaction currentTransaction = t.get(j);
				if (usedTransactions.contains(currentTransaction.getId()))
					continue;
				for (int k = 0; k < currentTransaction.size(); k++) {
					Operation op = currentTransaction.getOperations().get(k);
					newSchedule.AddOperation(op, currentTransaction.getId());
				}
			}
			result.add(newSchedule);
		}
		return result;
	}

	// This method takes a cycle that is transferable on its first edge and return
	// the index of T prime, which is the last transaction we can open consecutively
	// in this cycle starting from the first transaction.
	private static int getInitialTPrimeIndex(Cycle c, ArrayList<Transaction> t) {
		// at the begining "T" is equal to "T prime".
		int tPrimeIndex = 0;
		// check the next transactions if they can be opened.
		for (int i = 0; i < c.size() - 1; i++) {
			Edge currentE = c.getEdge(i);
			Edge nextE = c.getEdge(i + 1);
			// if the cycle is transferable on the next 2 edges, then it is transferable on
			// the next transaction. And this transaction can be opened.
			if (trantransferableInfo(currentE, nextE, t).size() != 0) {
				tPrimeIndex++;
				// if the cycle is not transferable on the next transaction, stop.
			} else {
				break;
			}
		}
		return tPrimeIndex;
	}

	// This method takes a cycle that is transferable on its first edge and return
	// a list of split indices of all transactions from T to the furthest T prime.
	private static ArrayList<Integer> getMultiSplitPositions(Cycle c, ArrayList<Transaction> t) {
		ArrayList<Integer> result = new ArrayList<Integer>();

		// First, add the split position of the first transaction. because the cycle is
		// transferable on its first transaction.
		ArrayList<Integer> firstTransferInfo = trantransferableInfo(c.getEdge(c.size() - 1), c.getEdge(0), t);
		result.add(firstTransferInfo.get(1));

		// check the next transactions if they can be opened.
		for (int i = 0; i < c.size() - 1; i++) {
			Edge currentE = c.getEdge(i);
			Edge nextE = c.getEdge(i + 1);
			// if the cycle is transferable on the next 2 edges, then it is transferable on
			// the next transaction. And this transaction can be opened.
			ArrayList<Integer> transferInfo = trantransferableInfo(currentE, nextE, t);
			if (transferInfo.size() != 0) {
				// add the index on the operation that determines the slpit point.
				result.add(transferInfo.get(1));
				// if the cycle is not transferable on the next transaction, stop.
			} else {
				break;
			}
		}
		return result;
	}

	// This method returns the prefix and the postfix of each transaction that can
	// be oppened starting from "T" until the furthest "T Prime"
	private static ArrayList<PrePostFix> getPrePostFix(Cycle c, int tPrimeIndex, ArrayList<Integer> MultiSplitIndices,
			ArrayList<Transaction> t) {

		ArrayList<PrePostFix> result = new ArrayList<PrePostFix>();

		for (int i = 0; i <= tPrimeIndex; i++) {
			int tId = c.getEdge(i).getT1ID();
			int tIndex = getTransactionPosition(tId, t);
			int splitPosition = MultiSplitIndices.get(i);

			Transaction temp = t.get(tIndex);
			PrePostFix prefixPostfix = new PrePostFix(tId, splitPosition);

			for (int j = 0; j <= splitPosition; j++) {
				prefixPostfix.addPrefixOp(temp.getOperations().get(j));
			}
			for (int j = splitPosition + 1; j < temp.size(); j++) {
				prefixPostfix.addPostfixOp(temp.getOperations().get(j));
			}
			result.add(prefixPostfix);
		}
		return result;
	}

	// this method checks if two lists have any conflict "write-write" or
	// "write-read".
	private static boolean containsConflict(ArrayList<Operation> first, ArrayList<Operation> second) {

		for (int i = 0; i < first.size(); i++) {
			Operation op1 = first.get(i);

			for (int j = 0; j < second.size(); j++) {
				Operation op2 = second.get(j);
				if ((op1.getObject() == op2.getObject()) && ((op1.getType() == 'W') || (op2.getType() == 'W')))
					return true;
			}
		}
		return false;
	}

	// This method checks if a cycle is a multi-prefix conflict-free cycle and
	// return an object that contains information about that including the index of
	// T prime and prefix & postfix of the transactions that can be oppened.
	private static MultiSplitInfo checkConflict(Cycle c, ArrayList<Transaction> t) {
		// at the begining get the furthest index of (T prime).
		int tPrimeIndex = getInitialTPrimeIndex(c, t);
		// get the split position of every transaction from "T" to "T Prime".
		ArrayList<Integer> MultiSplitIndices = getMultiSplitPositions(c, t);
		// get the prefix & postfix part of every transaction from "T" to "T Prime".
		ArrayList<PrePostFix> prePostFix = getPrePostFix(c, tPrimeIndex, MultiSplitIndices, t);

		boolean containsConflict = false;
		boolean breakFirstCondition = false;
		boolean breakSecondCondition = false;
		boolean breakThirdCondition = false;

		// there is always a number k > 0 such that the first k transactions occurring
		// in C are open. When tPrimeIndex = 0 , k = 1.
		while (tPrimeIndex >= 0) {

			for (int index = 0; index <= tPrimeIndex; index++) {
				// for each prefix, get its write operations and check the three conditions.
				int tId = prePostFix.get(index).gettId();
				int splitIndex = prePostFix.get(index).getSplitIndex();
				int tIndex = getTransactionPosition(tId, t);
				ArrayList<Operation> currentPrefixWriteOp = getprefixWriteOps(splitIndex, t.get(tIndex));

				// check the first condition
				for (int i = index + 1; i <= tPrimeIndex; i++) {
					ArrayList<Operation> prefixJ = prePostFix.get(i).getPrefix();
					if (containsConflict(currentPrefixWriteOp, prefixJ)) {
						breakFirstCondition = true;
						break;
					}
				}
				// check the second condition.
				for (int i = tPrimeIndex + 1; i < c.size(); i++) {
					// get the each transaction that happens after T Prime
					tId = c.getEdge(i).getT1ID();
					tIndex = getTransactionPosition(tId, t);
					Transaction tJ = t.get(tIndex);
					if (containsConflict(currentPrefixWriteOp, tJ.getOperations())) {
						breakSecondCondition = true;
						break;
					}
				}
				// check the third condition
				for (int i = 0; i < index; i++) {
					ArrayList<Operation> postfixJ = prePostFix.get(i).getPostfix();
					if (containsConflict(currentPrefixWriteOp, postfixJ)) {
						breakThirdCondition = true;
						break;
					}
				}
				// If any of the three conditions was not satisfied, there is a conflict.
				if (breakFirstCondition || breakSecondCondition || breakThirdCondition) {
					containsConflict = true;
					break;
				}
			}
			// after checking the three conditions, if they are all satisfied and there is
			// no conflict, the cycle is multi-prefix conflict-free. Stop checking and save
			// the cycle.
			if (!containsConflict)
				break;
			// if the three conditions are not satisfied, check again with another T Prime
			// index.
			tPrimeIndex--;
		}
		MultiSplitInfo multiSplitInfo = new MultiSplitInfo(containsConflict, tPrimeIndex, prePostFix);
		return multiSplitInfo;
	}

	// This method returns a list multi-prefix conflict-free cycles for a set of
	// transactions along with a list of multi-split information for each cycle.
	private static MPrefCyclesAndPoints getMultiPrefCyclesAndPoints(ArrayList<Cycle> cycles, ArrayList<Transaction> t) {
		ArrayList<Cycle> multiPrefCycles = new ArrayList<Cycle>();
		ArrayList<MultiSplitInfo> multiSplitInfo = new ArrayList<MultiSplitInfo>();
		boolean containsConflict = true;

		for (int i = 0; i < cycles.size(); i++) {
			// get each cycle and iterate over it
			Cycle currentCycle = cycles.get(i);

			for (int j = 0; j < currentCycle.size(); j++) {
				// for each iteration, get a consecutive pairs of edges.
				int k = j + 1;
				if (j == currentCycle.size() - 1)
					// to compare the first and the last edges. They are connected to each other.
					k = 0;
				Edge currentEdge = currentCycle.getEdge(j);
				Edge nextEdge = currentCycle.getEdge(k);

				ArrayList<Integer> transferInfo = trantransferableInfo(currentEdge, nextEdge, t);
				// if this happens, the cycle is transferable
				if (transferInfo.size() != 0) {
					// first, reorder the cycle to make the first edge the edge that starts with the
					// transaction that has the transfer point.
					currentCycle.reorder(nextEdge);

					MultiSplitInfo splitInfo = checkConflict(currentCycle, t);
					containsConflict = splitInfo.getContainsConflict();

					// if there is no conflict, the cycle is a prefix-write conflict-free
					if (!containsConflict) {
						Cycle newCycle = new Cycle();
						newCycle.copyCycle(currentCycle);
						multiPrefCycles.add(newCycle);
						multiSplitInfo.add(splitInfo);
						break;
					}
				}
			}
			containsConflict = true;
		}
		MPrefCyclesAndPoints result = new MPrefCyclesAndPoints(multiPrefCycles, multiSplitInfo);
		return result;
	}

	// This method takes a list of multi-prefix conflict-free cycles and returns a
	// multi-split schedule for each cycle.
	private static ArrayList<Schedule> getMultiSplitSchedules(ArrayList<Cycle> cycles, ArrayList<MultiSplitInfo> info,
			ArrayList<Transaction> t) {
		ArrayList<Schedule> result = new ArrayList<Schedule>();

		// the size of cycles list and the size of multiSplitInfo list are equal.
		for (int i = 0; i < cycles.size(); i++) {
			Cycle currentCycle = cycles.get(i);
			MultiSplitInfo currentSplitInfo = info.get(i);
			Schedule newSchedule = new Schedule("Multi Split Schedule");
			Set<Integer> usedTransactions = new HashSet<Integer>();

			int TPrimeIndex = currentSplitInfo.gettPrimeIndex();

			// First Step: add all operations in the prefix parts.
			for (int j = 0; j <= TPrimeIndex; j++) {
				PrePostFix currentT = currentSplitInfo.getPrePostFix().get(j);
				ArrayList<Operation> prefixTJ = currentT.getPrefix();
				usedTransactions.add(currentT.gettId());
				for (int k = 0; k < prefixTJ.size(); k++) {
					newSchedule.AddOperation(prefixTJ.get(k), currentT.gettId());
				}
			}
			// Second Step: add all closed transactions
			for (int j = TPrimeIndex + 1; j < currentCycle.size(); j++) {
				int Tid = currentCycle.getEdge(j).getT1ID();
				int tIndex = getTransactionPosition(Tid, t);
				Transaction currentT = t.get(tIndex);
				usedTransactions.add(Tid);
				// add all operations in the current transaction to the schedule
				for (int k = 0; k < currentT.size(); k++) {
					Operation op = currentT.getOperations().get(k);
					newSchedule.AddOperation(op, Tid);
				}
			}
			// Third Step: add all operations in the postfix parts
			for (int j = 0; j <= TPrimeIndex; j++) {
				PrePostFix currentT = currentSplitInfo.getPrePostFix().get(j);
				ArrayList<Operation> postfixTJ = currentT.getPostfix();
				for (int k = 0; k < postfixTJ.size(); k++) {
					newSchedule.AddOperation(postfixTJ.get(k), currentT.gettId());
				}
			}
			// Last Step: add the rest transactions
			for (int j = 0; j < t.size(); j++) {
				Transaction currentT = t.get(j);
				if (usedTransactions.contains(currentT.getId()))
					continue;
				for (int k = 0; k < currentT.size(); k++) {
					Operation op = currentT.getOperations().get(k);
					newSchedule.AddOperation(op, currentT.getId());
				}
			}
			result.add(newSchedule);
		}
		return result;
	}

	// This method takes a list of cycles and change the direction of each cycle.
	private static void counterDirection(ArrayList<Cycle> cycles) {
		for (int i = 0; i < cycles.size(); i++) {
			cycles.get(i).changeDirection();
		}
	}
	// ============================

	// This version will print the results directly on the console.
	public static void DecideIsolationLevel(ArrayList<Transaction> t) {
		ArrayList<Edge> edges = getEdges(t);
		ArrayList<Cycle> allCycles = getCycles(edges);

		ArrayList<Cycle> NonTrivialCycles = getNonTrivialCycles(allCycles);

		// get Prefix-Write Conflict-Free cycles along with their split points.
		CyclesAndPoints prefWriteCombined = getCyclesAndPoints(NonTrivialCycles, t);
		// we need also to check the Non-trivial cycles from the opposite direction.
		counterDirection(NonTrivialCycles);
		CyclesAndPoints prefWriteCombined2 = getCyclesAndPoints(NonTrivialCycles, t);
		prefWriteCombined.append(prefWriteCombined2);

		ArrayList<SplitPoint> splitPoints = prefWriteCombined.getSplitPoints();
		ArrayList<Cycle> prefWConfFreeCycles = prefWriteCombined.getCycles();
		ArrayList<Schedule> splitSchedules = getSplitSchedules(prefWConfFreeCycles, splitPoints, t);

		// get multi-prefix Conflict-Free cycles along with multi-spit information.
		MPrefCyclesAndPoints multiPrefCombined = getMultiPrefCyclesAndPoints(NonTrivialCycles, t);
		// we need also to check the Non-trivial cycles from the opposite direction.
		counterDirection(NonTrivialCycles);
		MPrefCyclesAndPoints multiPrefCombined2 = getMultiPrefCyclesAndPoints(NonTrivialCycles, t);
		multiPrefCombined.append(multiPrefCombined2);

		ArrayList<MultiSplitInfo> multiSplitInfo = multiPrefCombined.getSplitInfo();
		ArrayList<Cycle> multiPrefCycles = multiPrefCombined.getMultiPrefCycles();
		ArrayList<Schedule> multiSplitSchedules = getMultiSplitSchedules(multiPrefCycles, multiSplitInfo, t);

		// ====== Showing Results ======
		System.out.println();
		System.out.println("Decide Isolation Level Result:");
		System.out.println();
		System.out.println("============================");
		System.out.println("Number of Non-Trivial Cycles = " + NonTrivialCycles.size());
		System.out.println("Number of Prefix-Write Conflict-Free Cycles = " + prefWConfFreeCycles.size());
		System.out.println("Number of Multi-Prefix Conflict-Free Cycles = " + multiPrefCycles.size());
		System.out.println();

		if (NonTrivialCycles.isEmpty()) {
			System.out.println("<< The given set of transactions is ALLOWED under NO ISOLATION level >>");
			System.out.println("===============================================================================");
			System.out.println();
		} else {
			System.out.println("<< The given set of transactions is NOT ALLOWED under NO ISOLATION level >>");
			System.out.println("===============================================================================");
			System.out.println();
		}

		if (prefWConfFreeCycles.isEmpty()) {
			System.out.println("<< The given set of transactions is ALLOWED under READ UNCOMMITTED level >>");
			System.out.println("===============================================================================");
			System.out.println();
		} else {
			System.out.println("<< The given set of transactions is NOT ALLOWED under READ UNCOMMITTED level >>");
			System.out.println();
			System.out.println("The Prefix-Write Conflict-Free Cycles Are:");
			for (int i = 0; i < prefWConfFreeCycles.size(); i++) {
				System.out.println("--------------------");
				System.out.println("Cycle number: " + Integer.toString(i + 1));
				prefWConfFreeCycles.get(i).print();
				System.out.println("The Split Point for this Cycle is:");
				splitPoints.get(i).print(splitSchedules.get(i));
				System.out.println();
				System.out.println("The Found Split Schedule for this Cycle is:");
				splitSchedules.get(i).print();
			}
			System.out.println("===============================================================================");
			System.out.println();
		}

		if (multiPrefCycles.isEmpty()) {
			System.out.println("<< The given set of transactions is ALLOWED under READ COMMITTED level >>");
			System.out.println("===============================================================================");
			System.out.println();
		} else {
			System.out.println("<< The given set of transactions is NOT ALLOWED under READ COMMITTED level >>");
			System.out.println();
			System.out.println("The Multi-Prefix Conflict-Free Cycles Are:");
			for (int i = 0; i < multiPrefCycles.size(); i++) {
				System.out.println("--------------------");
				System.out.println("Cycle number: " + Integer.toString(i + 1));
				multiPrefCycles.get(i).print();
				System.out.println("The Split Points for this Cycle are:");
				multiSplitInfo.get(i).printSplitPoints();
				System.out.println();
				System.out.println("The Found Multi-Split Schedule for this Cycle is:");
				multiSplitSchedules.get(i).print();
			}
			System.out.println("===============================================================================");
			System.out.println();
		}
	}

	// This version will return the results as a single string.
	public static String DecideIsolationLevel2(ArrayList<Transaction> t) {
		String result = "\nDecide Isolation Level Result: \n";
		result += " \n";
		result += "====================================== \n";

		ArrayList<Edge> edges = getEdges(t);
		ArrayList<Cycle> allCycles = getCycles(edges);

		ArrayList<Cycle> NonTrivialCycles = getNonTrivialCycles(allCycles);

		// get Prefix-Write Conflict-Free cycles along with their split points.
		CyclesAndPoints prefWriteCombined = getCyclesAndPoints(NonTrivialCycles, t);
		// we need also to check the Non-trivial cycles from the opposite direction.
		counterDirection(NonTrivialCycles);
		CyclesAndPoints prefWriteCombined2 = getCyclesAndPoints(NonTrivialCycles, t);
		prefWriteCombined.append(prefWriteCombined2);

		ArrayList<SplitPoint> splitPoints = prefWriteCombined.getSplitPoints();
		ArrayList<Cycle> prefWConfFreeCycles = prefWriteCombined.getCycles();
		ArrayList<Schedule> splitSchedules = getSplitSchedules(prefWConfFreeCycles, splitPoints, t);

		// get multi-prefix Conflict-Free cycles along with multi-spit information.
		MPrefCyclesAndPoints multiPrefCombined = getMultiPrefCyclesAndPoints(NonTrivialCycles, t);
		// we need also to check the Non-trivial cycles from the opposite direction.
		counterDirection(NonTrivialCycles);
		MPrefCyclesAndPoints multiPrefCombined2 = getMultiPrefCyclesAndPoints(NonTrivialCycles, t);
		multiPrefCombined.append(multiPrefCombined2);

		ArrayList<MultiSplitInfo> multiSplitInfo = multiPrefCombined.getSplitInfo();
		ArrayList<Cycle> multiPrefCycles = multiPrefCombined.getMultiPrefCycles();
		ArrayList<Schedule> multiSplitSchedules = getMultiSplitSchedules(multiPrefCycles, multiSplitInfo, t);

		// ====== Showing Results ======
		result += "Number of Non-Trivial Cycles = " + Integer.toString(NonTrivialCycles.size()) + " \n";
		result += "Number of Prefix-Write Conflict-Free Cycles = " + Integer.toString(prefWConfFreeCycles.size())
				+ " \n";
		result += "Number of Multi-Prefix Conflict-Free Cycles = " + Integer.toString(multiPrefCycles.size()) + " \n";
		result += " \n";

		if (NonTrivialCycles.isEmpty()) {
			result += "<< The given set of transactions is ALLOWED under NO ISOLATION level >> \n";
			result += "====================================================================== \n";
			result += " \n";
		} else {
			result += "<< The given set of transactions is NOT ALLOWED under NO ISOLATION level >> \n";
			result += "====================================================================== \n";
			result += " \n";
		}

		if (prefWConfFreeCycles.isEmpty()) {
			result += "<< The given set of transactions is ALLOWED under READ UNCOMMITTED level >> \n";
			result += "====================================================================== \n";
			result += " \n";
		} else {
			result += "<< The given set of transactions is NOT ALLOWED under READ UNCOMMITTED level >> \n";
			result += " \n";
			result += "The Prefix-Write Conflict-Free Cycles Are: \n";
			for (int i = 0; i < prefWConfFreeCycles.size(); i++) {
				result += "-------------------- \n";
				result += "Cycle number: " + Integer.toString(i + 1) + " \n";
				result += prefWConfFreeCycles.get(i).getCycle2Print();
				result += "The Split Point for this Cycle is: \n";
				result += splitPoints.get(i).getSplitPoint2Print(splitSchedules.get(i));
				result += " \n";
				result += "The Found Split Schedule for this Cycle is: \n";
				result += splitSchedules.get(i).getSchedule2Print();
			}
			result += "====================================================================== \n";
			result += " \n";
		}

		if (multiPrefCycles.isEmpty()) {
			result += "<< The given set of transactions is ALLOWED under READ COMMITTED level >> \n";
			result += "====================================================================== \n";
			result += " \n";
		} else {
			result += "<< The given set of transactions is NOT ALLOWED under READ COMMITTED level >> \n";
			result += " \n";
			result += "The Multi-Prefix Conflict-Free Cycles Are: \n";
			for (int i = 0; i < multiPrefCycles.size(); i++) {
				result += "-------------------- \n";
				result += "Cycle number: " + Integer.toString(i + 1) + " \n";
				result += multiPrefCycles.get(i).getCycle2Print();
				result += "The Split Points for this Cycle are: \n";
				result += multiSplitInfo.get(i).getSplitPoints2Print();
				result += " \n";
				result += "The Found Multi-Split Schedule for this Cycle is: \n";
				result += multiSplitSchedules.get(i).getSchedule2Print();

			}
			result += "====================================================================== \n";
			result += " \n";
		}
		return result;
	}

}
