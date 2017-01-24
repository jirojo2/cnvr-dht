package es.upm.dit.cnvr.dht;

import java.util.Scanner;

import org.jgroups.Address;

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
			master.addSelf();
			
			Thread.sleep(2000);
			Scanner reader = new Scanner(System.in);
			
			do {
				System.out.println("Seleccione una acción: ");
				System.out.println(" [1] Introducir dato");
				System.out.println(" [2] Obtener dato");
				System.out.println(" [3] Introducir nodo\n");
				
				String option = reader.next();
				Data data = null;
			
				switch (option) {
					case "1": System.out.println("Introduzca el dato:\n");
							  String dataString = reader.next();
							  data = new Data(dataString);
							  System.out.printf("La calve del dato es %d\n\n", data.getKey());
							  master.putData(data);
							  break;
					case "2": System.out.println("Introduzca la clave del dato que desea obtener:\n");
							  String dataKey = reader.next();
							  int key = Integer.parseInt(dataKey);
							  data = master.getData(key);
							  System.out.printf("El dato obtenido es %s\n\n", data.getValue());
							  break;
					case "3": System.out.println("Añadiendo un nuevo nodo:");
							  DHT other = new DHT();
							  other.addSelf();
							  break;
					default: System.out.println("Seleccione una opción válida.");
							 break;
				}
				System.out.flush();
			} while (true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
