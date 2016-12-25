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
        System.out.println("  Roberto Paterna Ferrón");
        System.out.println("  José Ignacio Rojo Rivero");
        System.out.println("=============================");
        
        DHT master = new DHT(); // esto en realidad define de forma implicita un nodo
        
        for (int i = 0; i < 20; i++) {
        	DHT otroDHT = new DHT();
        	
        	System.out.println(String.format("Añadimos el nodo %d", otroDHT.getNode().getKey()));
        	master.addNode(otroDHT.getNode());
        	System.out.println(String.format("%d -> %s", otroDHT.getNode().getKey(), otroDHT.printNeighbors()));
        }
    }
}
