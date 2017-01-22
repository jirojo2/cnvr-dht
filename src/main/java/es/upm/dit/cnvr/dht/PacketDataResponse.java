package es.upm.dit.cnvr.dht;

import org.jgroups.Address;

public class PacketDataResponse implements Packet {
	public static final int ID = Packet.PACKET_DATA_RESPONSE;

	private Address src;
	private Address dst;
	private Integer key;
	private Data data;

	public PacketDataResponse(Address src, Address dst, Integer key, Data data) {
		this.src = src;
		this.dst = dst;
		this.key = key;
		this.data = data;
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
}
