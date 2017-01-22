package es.upm.dit.cnvr.dht;

import org.jgroups.Address;

public class PacketAddNode implements Packet {
	public static final int ID = Packet.PACKET_ADD_NODE;

	private Address src;
	private Address dst;
	private Node<Address> node;

	public PacketAddNode(Address src, Address dst, Node<Address> node) {
		this.src = src;
		this.dst = dst;
		this.node = node;
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

	public Node<Address> getNode() {
		return node;
	}
}
