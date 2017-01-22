package es.upm.dit.cnvr.dht;

import org.jgroups.Address;

public interface Packet {
	public static final int PACKET_SET_DATA = 0x01;
	public static final int PACKET_STEP = 0x02;
	public static final int PACKET_DATA_REQUEST = 0x03;
	public static final int PACKET_DATA_RESPONSE = 0x04;
	public static final int PACKET_ADD_NODE = 0x05;

	public int getId();

	public Address getDst();
}
