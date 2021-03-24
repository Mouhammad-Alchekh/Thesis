import java.util.LinkedList;

public class Cycle {
	private LinkedList<Edge> cycle;

	public Cycle() {
		this.cycle = new LinkedList<Edge>();
	}

	public void addEdge(Edge e) {
		cycle.addLast(e);
	}

	public void addEdgeFront(Edge e) {
		cycle.addFirst(e);
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

	public void copyCycle(Cycle c2) {
		LinkedList<Edge> edges2 = c2.getCycle();
		this.cycle.addAll(edges2);
	}

	public void replace(int i, Edge e) {
		Edge oldEdge = cycle.get(i);
		// if the new edge is not of the same shape of the previous one
		if (oldEdge.getT1ID() == e.getT2ID() && oldEdge.getT2ID() == e.getT1ID())
			e.flip();
		cycle.set(i, e);
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

	public boolean isEqual(Cycle c2) {

		if (this.size() != c2.size())
			return false;

		for (int i = 0; i < cycle.size(); i++) {
			Edge first = cycle.get(i);
			Edge second = c2.getCycle().get(i);
			if (first.getId() != second.getId())
				return false;
		}

		return true;
	}

	public void print() {
		System.out.println("This Cycle is Constructed From:");
		for (int i = 0; i < cycle.size(); i++) {
			cycle.get(i).print();
		}
		System.out.println();
	}

	// This method reorder the seuqnce of the edges in the cycle list and make the
	// given edge the first edge in this cycle.
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
}
