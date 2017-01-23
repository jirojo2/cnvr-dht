package es.upm.dit.cnvr.dht;

import java.util.HashMap;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class DHT extends ReceiverAdapter {
	public static final int KEYSPACE_SIZE = 64;
	public static final int L = 2;
	
	private static final String JGROUPS_CLUSTER = "DHT-CNVR";

	private JChannel channel;
	private Address address;

	private Node<Address> node;
	
	private Data requestingData;

	private NeighborVector neighbors;
	private HashMap<Integer, Data> dataset = new HashMap<>();
	
	public DHT() throws Exception {
		channel = new JChannel();
		channel.setReceiver(this);
		channel.connect(JGROUPS_CLUSTER);
		this.address = channel.getAddress();
		this.node = new Node<Address>(address);
		this.neighbors = new NeighborVector(this.node.getKey());
	}

	public void close() {
		channel.close();
	}

	public void viewAccepted(View new_view) {
		System.out.println("** view: " + new_view);
	}

	private void handle(PacketSetData pkt) {
		dataset.put(pkt.getKey(), pkt.getData());
	}

	private void handle(PacketStep pkt) {
		if (pkt.isLocal())
			putDataHere(pkt.getData());
		else
			putData(pkt.getData());
	}

	private void handle(PacketDataRequest pkt) {
		Data data = getData(pkt.getKey());
		if (data != null) {
			requestingData = data;
		}
	}

	private void handle(PacketDataResponse pkt) {
		requestingData = pkt.getData();
	}

	private void handle(PacketAddNode pkt) {
		addNode(pkt.getNode());
	}

	public void receive(Message msg) {
		System.out.println(msg.getSrc() + ": " + msg.getObject());

		Packet pkt = (Packet) msg.getObject();
		if (pkt.getDst() != null && !pkt.getDst().equals(address)) {
			// ignoramos el paquete, no va para nosotros
			return;
		}

		// Packet handler
		switch (pkt.getId()) {
			case PacketSetData.ID:
				handle((PacketSetData) pkt);
				break;
			case PacketStep.ID:
				handle((PacketStep) pkt);
				break;
			case PacketDataRequest.ID:
				handle((PacketDataRequest) pkt);
				break;
			case PacketDataResponse.ID:
				handle((PacketDataResponse) pkt);
				break;
			case PacketAddNode.ID:
				handle((PacketAddNode) pkt);
				break;
			default:
				System.out.println("Unknown packet with ID " + pkt.getId());
		}
	}

	private void send(Packet pkt) {
		try {
			Message msg = new Message(pkt.getDst(), null, pkt);
			channel.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		for (Node<Address> neighbor : neighbors) {
			send(new PacketSetData(address, neighbor.getAddress(), k, data));
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
		for (Node<Address> node : this.neighbors) {
			buffer.append(node.getKey() + ", ");
		}
		return "[ " + buffer.substring(0, buffer.length()-2) + " ]";
	}
	
	public Node<Address> getNode() {
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
			send(new PacketStep(address, neighbors.get(k).getAddress(), k, data));
		}
		else if (l_low < k && k < l_high) {
			// escurrimos el bulto
			Address nearest = address;
			int mindist = Math.abs(node.getKey() - k);
			for (Node<Address> neighbor : neighbors) {
				int dist = Math.abs(neighbor.getKey() - k);
				if (dist < mindist) {
					nearest = neighbor.getAddress();
					mindist = dist;
				}
			}
			send(new PacketStep(address, nearest, k, data, true));
		}
		else {
			// escurrimos el bulto
			send(new PacketStep(address, neighbors.last().getAddress(), k, data));
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
			requestingData = null;
			send(new PacketDataRequest(address, neighbors.last().getAddress(), key));
			while (requestingData == null) {
				try {
					Thread.sleep(200);
				} catch (Exception e) { }
			}
			return requestingData;
		}
	}

	public void addSelf() {
		Address coordinator = channel.getView().getMembers().get(0);
		send(new PacketAddNode(address, coordinator, node));
	}
	
	public void addNode(Node<Address> node) {

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
			send(new PacketAddNode(address, node.getAddress(), this.node));
			
			// avisamos a cada vecino
			for (Node<Address> neighbor : neighbors) {
				send(new PacketAddNode(address, neighbor.getAddress(), node));
			}
		}
		else if (isKeyBetween(neighbors.firstKey(), neighbors.lastKey(), k)) {
			// reemplazamos a un vecino, se complica la cosa
			System.out.println("  reemplazamos a un vecino, se complica la cosa");
			neighbors.add(node);
			send(new PacketAddNode(address, node.getAddress(), this.node));
			
			// por un lado, habrá que notificar a vecinos ya existentes
			for (Node<Address> neighbor : neighbors) {
				System.out.println("    notifico a vecino existente");
				send(new PacketAddNode(address, neighbor.getAddress(), node));
			}
		}
		else {
			// escurrimos el bulto
			System.out.println("  escurrimos el bulto");
			send(new PacketAddNode(address, neighbors.last().getAddress(), node));
		}

		System.out.println(String.format("%d -> %s", this.node.getKey(), this.printNeighbors()));
	}

}
