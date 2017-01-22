package es.upm.dit.cnvr.dht;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args)
	{
		System.out.println("=============================");
		System.out.println("======= CNVR - DHT ==========");
		System.out.println("=============================");
		System.out.println("  Roberto Paterna Ferrón     ");
		System.out.println("  José Ignacio Rojo Rivero   ");
		System.out.println("=============================");
		
		try {
			DHT master = new DHT(); // esto en realidad define de forma implicita un nodo
			master.addNode(master.getNode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
