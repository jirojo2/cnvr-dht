package es.upm.dit.cnvr.dht;

import org.jgroups.Address;

public class PacketStep implements Packet {
	public static final int ID = Packet.PACKET_STEP;

	private Address src;
	private Address dst;
	private Integer key;
	private Data data;
	private Boolean local;

	public PacketStep(Address src, Address dst, Integer key, Data data) {
		this.src = src;
		this.dst = dst;
		this.key = key;
		this.data = data;
		this.local = false;
	}

	public PacketStep(Address src, Address dst, Integer key, Data data, Boolean local) {
		this(src, dst, key, data);
		this.local = local;
	}

	public int getId() {
		return ID;
	}

	public Address getSrc() {
		return src;
	}

	public Address getDst() {
		return dst;
	}

	public Integer getKey() {
		return key;
	}

	public Data getData() {
		return data;
	}

	public Boolean isLocal() {
		return local;
	}

	@Override
	public String toString() {
		return String.format("%s: %d -> %s", dst.toString(), key.toString(), data.toString());
	}
}
