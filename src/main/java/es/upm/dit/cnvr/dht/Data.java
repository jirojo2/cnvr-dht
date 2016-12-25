package es.upm.dit.cnvr.dht;

public class Data {
	
	private Object value;
	private Integer key;
	
	public Data(Object value) {
		this.value = value;
	}
	
	public int getKey() {
		if (key != null) {
			return key.intValue();
		}

		key = value.hashCode() % DHT.KEYSPACE_SIZE;
		if (key < 0) {
			key += DHT.KEYSPACE_SIZE;
		}
		return key;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
}
