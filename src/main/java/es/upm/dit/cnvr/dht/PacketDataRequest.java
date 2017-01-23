package es.upm.dit.cnvr.dht;

import java.io.Serializable;

import org.jgroups.Address;

public class PacketDataRequest implements Packet, Serializable {
	public static final int ID = Packet.PACKET_DATA_REQUEST;

	private Address src;
	private Address dst;
	private Integer key;

	public PacketDataRequest(Address src, Address dst, Integer key) {
		this.src = src;
		this.dst = dst;
		this.key = key;
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
}
