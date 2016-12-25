package es.upm.dit.cnvr.dht;

public class Node<Address> {

	private Address address;
	private Integer key;
	
	public Node(Address address) {
		this.address = address;
	}
	
	void forceKey(int key) {
		this.key = key;
	}
	
	public int getKey() {
		if (key != null) {
			return key.intValue();
		}

		key = address.hashCode() % DHT.KEYSPACE_SIZE;
		if (key < 0) {
			key += DHT.KEYSPACE_SIZE;
		}
		return key;
	}
	
	public Address getAddress() {
		return this.address;
	}
}
