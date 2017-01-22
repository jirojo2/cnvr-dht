package es.upm.dit.cnvr.dht;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.jgroups.Address;


import org.junit.Test;

public class NeighborVectorTest {

	@Test
	public void iterationComplete() throws Exception {
		NeighborVector v = new NeighborVector(5);
		v.add(new DHT().forceKey(61).getNode());
		v.add(new DHT().forceKey(3).getNode());
		v.add(new DHT().forceKey(8).getNode());
		v.add(new DHT().forceKey(12).getNode());
		
		Iterator<Node<Address>> iterator = v.iterator();
		assertEquals(61, iterator.next().getKey());
		assertEquals(3, iterator.next().getKey());
		assertEquals(8, iterator.next().getKey());
		assertEquals(12, iterator.next().getKey());
	}
	
	@Test
	public void iterationJustCCW() throws Exception {
		NeighborVector v = new NeighborVector(5);
		v.add(new DHT().forceKey(61).getNode());
		v.add(new DHT().forceKey(3).getNode());
		
		Iterator<Node<Address>> iterator = v.iterator();
		assertEquals(61, iterator.next().getKey());
		assertEquals(3, iterator.next().getKey());
	}
	
	@Test
	public void iterationJustCW() throws Exception {
		NeighborVector v = new NeighborVector(5);
		v.add(new DHT().forceKey(8).getNode());
		v.add(new DHT().forceKey(12).getNode());
		
		Iterator<Node<Address>> iterator = v.iterator();
		assertEquals(8, iterator.next().getKey());
		assertEquals(12, iterator.next().getKey());
	}
	
	@Test
	public void addOnlyL() throws Exception {
		NeighborVector v = new NeighborVector(5);
		v.add(new DHT().forceKey(50).getNode());
		v.add(new DHT().forceKey(61).getNode());
		v.add(new DHT().forceKey(3).getNode());
		v.add(new DHT().forceKey(8).getNode());
		v.add(new DHT().forceKey(12).getNode());
		v.add(new DHT().forceKey(15).getNode());
		
		Iterator<Node<Address>> iterator = v.iterator();
		assertEquals(61, iterator.next().getKey());
		assertEquals(3, iterator.next().getKey());
		assertEquals(8, iterator.next().getKey());
		assertEquals(12, iterator.next().getKey());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void addOnlyCWW() throws Exception {
		NeighborVector v = new NeighborVector(5);
		v.add(new DHT().forceKey(50).getNode());
		v.add(new DHT().forceKey(61).getNode());
		v.add(new DHT().forceKey(3).getNode());
		v.add(new DHT().forceKey(55).getNode());
		
		Iterator<Node<Address>> iterator = v.iterator();

		assertEquals(61, iterator.next().getKey());
		assertEquals(3, iterator.next().getKey());
		assertEquals(50, iterator.next().getKey());
		assertEquals(55, iterator.next().getKey());
		assertFalse(iterator.hasNext());
	}
}
