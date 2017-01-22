package es.upm.dit.cnvr.dht;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.jgroups.Address;

public class NeighborVector implements Iterable<Node<Address>> {

	private LinkedList<Node<Address>> cwLeaf = new LinkedList<>();
	private LinkedList<Node<Address>> ccwLeaf = new LinkedList<>();
	private int reference;
	
	public NeighborVector(int reference) {
		super();
		this.reference = reference;
	}
	
	private int calculateDistanceCW(int a, int b) {
		int d = b - a;
		if (d < 0) {
			d += DHT.KEYSPACE_SIZE;
		}
		return d;
	}
	
	private int calculateDistanceCCW(int a, int b) {
		int d = a - b;
		if (d < 0) {
			d += DHT.KEYSPACE_SIZE;
		}
		return d;
	}
	
	private int calculateMinDistance(int a, int b) {
		return Math.min(calculateDistanceCCW(a, b), calculateDistanceCW(a, b));
	}
	
	public boolean isEmpty() {
		return cwLeaf.isEmpty() && ccwLeaf.isEmpty();
	}
	
	public int firstKey() {
		return ccwLeaf.getLast().getKey();
	}
	
	public Node<Address> first() {
		return ccwLeaf.getLast();
	}

	public int lastKey() {
		return cwLeaf.getLast().getKey();
	}
	
	public Node<Address> last() {
		return cwLeaf.getLast();
	}
	
	public boolean containsKey(int k) {
		for (Node<Address> node : this) {
			if (node.getKey() == k) {
				return true;
			}
		}
		return false;
	}
	
	public Node<Address> get(int k) {
		for (Node<Address> node : this) {
			if (node.getKey() == k) {
				return node;
			}
		}
		return null;		
	}
	
	public int size() {
		return cwLeaf.size() + ccwLeaf.size();
	}
	
	public boolean add(Node<Address> node) {
		// Necesitamos saber dónde lo añadimos
		// Tenemos dos segmentos, CCW y CW, cada uno de longitud DHT.L
		
		int minDistance = calculateMinDistance(reference, node.getKey());
		boolean cw = minDistance == calculateDistanceCW(reference, node.getKey());
		boolean cwFull = cwLeaf.size() == DHT.L;
		boolean ccwFull = ccwLeaf.size() == DHT.L;
		boolean added = false;
		
		// Comprobamos la dirección
		if (cw && cwFull) {
			if (calculateDistanceCW(reference, lastKey()) > calculateDistanceCW(reference, node.getKey()))
				;
			else if (calculateDistanceCW(reference, lastKey()) < calculateDistanceCW(reference, node.getKey()))
				cw = false;
			else if (ccwFull)
				return false;
		}
		else if (!cw && ccwFull) {
			if (calculateDistanceCCW(reference, firstKey()) > calculateDistanceCCW(reference, node.getKey()))
				;
			else if (calculateDistanceCCW(reference, firstKey()) < calculateDistanceCCW(reference, node.getKey()))
				cw = true;
			else if (cwFull)
				return false;
		}
		
		// Suponiendo que el vector actual está lleno (2*L)
		if (cw) {
			int dist = calculateDistanceCW(reference, node.getKey());
			int d = Integer.MAX_VALUE;
			ListIterator<Node<Address>> i = cwLeaf.listIterator();
			while (i.hasNext()) {
				int idx = i.nextIndex();
				Node<Address> n = i.next();
				d = calculateDistanceCW(n.getKey(), node.getKey());
				if (dist < d) {
					cwLeaf.add(idx, node);
					added = true;
					break;
				}
			}
			
			if (!added && (!cwFull || d > dist)) {
				cwLeaf.add(node);
				added = true;
			}
			
			if (cwFull)
				add(cwLeaf.removeLast());
		}
		else {
			int dist = calculateDistanceCCW(reference, node.getKey());
			int d = Integer.MAX_VALUE;
			ListIterator<Node<Address>> i = ccwLeaf.listIterator();
			while (i.hasNext()) {
				int idx = i.nextIndex();
				Node<Address> n = i.next();
				d = calculateDistanceCCW(n.getKey(), node.getKey());
				if (dist < d) {
					ccwLeaf.add(idx, node);
					added = true;
					break;
				}
			}
			
			if (!added && (!ccwFull || d > dist)) {
				ccwLeaf.add(node);
				added = true;
			}
			
			if (ccwFull)
				add(ccwLeaf.removeLast());
		}
		
		return added;
	}

	@Override
	public Iterator<Node<Address>> iterator() {
		// Iterate over two segments
		// First segment (CCW) is reversed, the second (CW) is normal
		LinkedList<Iterator<Node<Address>>> iterators = new LinkedList<>();
		if (!ccwLeaf.isEmpty())
			iterators.add(ccwLeaf.descendingIterator());
		if (!cwLeaf.isEmpty())
			iterators.add(cwLeaf.iterator());
		return new IteratorAggregator<>(iterators);
	}
	
	private class IteratorAggregator<E> implements Iterator<E> {
		LinkedList<Iterator<E>> internalIterators = new LinkedList<>();
		Iterator<E> crtIterator = null;

		public IteratorAggregator(LinkedList<Iterator<E>> iterators) {
			internalIterators.addAll(iterators);
		}

		@Override
		public boolean hasNext() {
			return (crtIterator != null && crtIterator.hasNext()) ||
					(!internalIterators.isEmpty() && internalIterators.getFirst().hasNext()) ;
		}

		@Override
		public E next() {
			if (crtIterator != null && crtIterator.hasNext()) {
				return crtIterator.next();
			}

			if (!internalIterators.isEmpty()) {
				crtIterator = internalIterators.pollFirst();
				return crtIterator.next();
			}

			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
