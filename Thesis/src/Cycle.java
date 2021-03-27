import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Cycle {
	private LinkedList<Edge> cycle;

	public Cycle() {
		this.cycle = new LinkedList<Edge>();
	}

	public void addEdge(Edge e) {
		Edge newEdge = new Edge(e.getT1ID(), e.getOp1(), e.getT2ID(), e.getOp2(), e.getId());
		cycle.addLast(newEdge);
	}

	public void addEdgeFront(Edge e) {
		Edge newEdge = new Edge(e.getT1ID(), e.getOp1(), e.getT2ID(), e.getOp2(), e.getId());
		cycle.addFirst(newEdge);
	}

	public void removeFirstEdge() {
		cycle.removeFirst();
	}

	public Edge getFirstEdge() {
		return cycle.getFirst();
	}

	public Edge getLastEdge() {
		return cycle.getLast();
	}

	public Edge getEdge(int i) {
		return cycle.get(i);
	}

	// copy a given cycle to this cycle.
	public void copyCycle(Cycle c2) {
		for (int i = 0; i < c2.size(); i++) {
			Edge e = c2.getEdge(i);
			Edge newEdge = new Edge(e.getT1ID(), e.getOp1(), e.getT2ID(), e.getOp2(), e.getId());
			this.cycle.add(newEdge);
		}
		
	}

	public void replace(int i, Edge e) {
		Edge oldEdge = cycle.get(i);
		// if the new edge is not of the same shape of the previous one
		if (oldEdge.getT1ID() == e.getT2ID() && oldEdge.getT2ID() == e.getT1ID())
			e.flip();
		Edge newEdge = new Edge(e.getT1ID(), e.getOp1(), e.getT2ID(), e.getOp2(), e.getId());
		cycle.set(i, newEdge);
	}

	public LinkedList<Edge> getCycle() {
		return cycle;
	}

	public void setCycle(LinkedList<Edge> cycle) {
		this.cycle = cycle;
	}

	public int size() {
		return cycle.size();
	}

	// check if a given cycle is equal to this cycle.
	public boolean isEqual(Cycle c2) {
		// store the IDs of the used edges in each cycle.
		Set<Integer> firstIDs = new HashSet<Integer>();
		Set<Integer> secondIDs = new HashSet<Integer>();
		// if two cycles don't have the same size, they are not equal.
		if (this.size() != c2.size())
			return false;

		for (int i = 0; i < cycle.size(); i++) {
			firstIDs.add(cycle.get(i).getId());
			secondIDs.add(c2.getEdge(i).getId());
		}
		// if two cycles use the same edges, they are equal.
		firstIDs.removeAll(secondIDs);
		if (firstIDs.size() == 0)
			return true;

		return false;
	}

	public void print() {
		System.out.println("This Cycle is Constructed From:");
		for (int i = 0; i < cycle.size(); i++) {
			cycle.get(i).print();
		}
		System.out.println();
	}

	// This method reorder the seuqnce of the edges in the cycle list and make the
	// edge that is equal to the given edge the first edge in this cycle.
	public void reorder(Edge e) {
		int index = -1;
		for (int i = 0; i < cycle.size(); i++) {
			if (cycle.get(i).getId() == e.getId()) {
				index = i;
				break;
			}
		}
		for (int i = 0; i < index; i++) {
			Edge temp = cycle.get(i);
			cycle.add(temp);
		}
		for (int i = 0; i < index; i++) {
			cycle.removeFirst();
		}

	}

	// reorder the list of edges in this cycle so that the moveing direction in the
	// cycle is the opposite direction now.
	// Ex: T2 -> T3 -> T1 -> T2 will be changed to T2 -> T1 -> T3 -> T2
	public void changeDirection() {
		for (int i = 0; i < cycle.size(); i++) {
			cycle.get(i).flip();
		}
		for (int i = 0; i < cycle.size() / 2; i++) {
			Edge first = cycle.get(i);
			Edge last = cycle.get(cycle.size() - 1 - i);
			cycle.set(i, last);
			cycle.set(cycle.size() - 1 - i, first);
		}
	}
}
