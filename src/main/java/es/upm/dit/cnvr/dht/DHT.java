package es.upm.dit.cnvr.dht;

import java.util.HashMap;

public class DHT {
	public static final int KEYSPACE_SIZE = 64;
	public static final int L = 2;
	
	private Node<DHT> node;
	
	private NeighborVector neighbors;
	private HashMap<Integer, Data> dataset = new HashMap<>();
	
	public DHT() {
		this.node = new Node<DHT>(this);
		this.neighbors = new NeighborVector(this.node.getKey());
	}
	
	DHT forceKey(int key) {
		this.node.forceKey(key);
		return this;
	}
	
	private void putDataHere(Data data) {

		int k = data.getKey();
		
		// respondemos directamente
		dataset.put(k, data);
		
		// replicar en vecinos (realmente enviamos un mensaje a cada nodo vecino)
		for (Node<DHT> neighbor : neighbors) {
			DHT neighborDHT = neighbor.getAddress();
			neighborDHT.dataset.put(k, data);
		}
	}
	
	private boolean isKeyBetween(int a, int b, int x) {
		if (b < a) {
			a -= KEYSPACE_SIZE;
		}
		if (b < x) {
			x -= KEYSPACE_SIZE;
		}
		return a < x && x < b;
	}
	
	public String printNeighbors() {
		if (this.neighbors.isEmpty()) {
			return "[ ]";
		}
		
		StringBuffer buffer = new StringBuffer();
		for (Node<DHT> node : this.neighbors) {
			buffer.append(node.getKey() + ", ");
		}
		return "[ " + buffer.substring(0, buffer.length()-2) + " ]";
	}
	
	public Node<DHT> getNode() {
		return this.node;
	}
	
	public void putData(Data data) {

		int k = data.getKey();
		int l_low = neighbors.firstKey();
		int l_high = neighbors.lastKey();
		
		if (k == node.getKey()) {
			// respondemos directamente
			// y replicamos en vecinos (realmente enviamos un mensaje a cada nodo vecino)
			putDataHere(data);
		}
		else if (neighbors.containsKey(k)) {
			// escurrimos el bulto
			DHT neighborDHT = neighbors.get(k).getAddress();
			neighborDHT.putData(data);
		}
		else if (l_low < k && k < l_high) {
			// escurrimos el bulto
			DHT nearest = this;
			int mindist = Math.abs(node.getKey() - k);
			for (Node<DHT> neighbor : neighbors) {
				int dist = Math.abs(neighbor.getKey() - k);
				if (dist < mindist) {
					nearest = neighbor.getAddress();
					mindist = dist;
				}
			}
			nearest.putDataHere(data);
		}
		else {
			// escurrimos el bulto
			DHT neighborDHT = neighbors.last().getAddress();
			neighborDHT.putData(data);
		}
	}
	
	public Data getData(int key) {

		int l_low = neighbors.firstKey();
		int l_high = neighbors.lastKey();

		if (l_low < key && key < l_high) {
			// si está entre los vecinos... lo tengo yo
			return dataset.get(key);
		}
		else {
			// escurrimos el bulto
			DHT neighborDHT = neighbors.last().getAddress();
			return neighborDHT.getData(key);			
		}
	}
	
	public void addNode(Node<DHT> node) {

		int k = node.getKey();

		if (k == this.node.getKey() || neighbors.containsKey(k)) {
			// no hacemos nada ni avisamos a nadie
			// punto de parada de recursividad
			System.out.println("  no hacemos nada ni avisamos a nadie");
		}
		else if (neighbors.size() < 2*L) {
			// es vecino seguro
			System.out.println("  es vecino seguro");
			neighbors.add(node);
			node.getAddress().addNode(this.node);
			
			// avisamos a cada vecino
			for (Node<DHT> neighbor : neighbors) {
				DHT neighborDHT = neighbor.getAddress();
				neighborDHT.addNode(node);
			}
		}
		else if (isKeyBetween(neighbors.firstKey(), neighbors.lastKey(), k)) {
			// reemplazamos a un vecino, se complica la cosa
			System.out.println("  reemplazamos a un vecino, se complica la cosa");
			neighbors.add(node);
			node.getAddress().addNode(this.node);
			
			// por un lado, habrá que notificar a vecinos ya existentes
			for (Node<DHT> neighbor : neighbors) {
				DHT neighborDHT = neighbor.getAddress();
				System.out.println("    notifico a vecino existente");
				neighborDHT.addNode(node);
			}
			
			/* TODO: Ya los quito en neighbors.add(node)
			// por otro lado hay que decirle a un vecino que deja de ser vecino
			if (neighbors.firstKey() < k) {
				// quitamos l_low
				System.out.println("    quito vecino l_low");
				neighbors.remove(neighbors.firstKey());
			}
			else {
				// quitamos l_high
				System.out.println("    quito vecino l_high");
				neighbors.remove(neighbors.lastKey());
			}
			*/
		}
		else {
			// escurrimos el bulto
			System.out.println("  escurrimos el bulto");
			DHT neighborDHT = neighbors.last().getAddress();
			neighborDHT.addNode(node);	
		}

		System.out.println(String.format("%d -> %s", this.node.getKey(), this.printNeighbors()));
	}

}
