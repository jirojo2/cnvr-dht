package es.upm.dit.cnvr.dht;

import java.io.Serializable;

import org.jgroups.Address;

public class PacketSetData implements Packet, Serializable {
	public static final int ID = Packet.PACKET_SET_DATA;

	private Address src;
	private Address dst;
	private Integer key;
	private Data data;

	public PacketSetData(Address src, Address dst, Integer key, Data data) {
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

	@Override
	public String toString() {
		return String.format("%s: %d -> %s", dst.toString(), key.toString(), data.toString());
	}
}
