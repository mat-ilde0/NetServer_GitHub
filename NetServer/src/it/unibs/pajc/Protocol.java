package it.unibs.pajc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/*
 * in questo modo nel metodo run si va ad implementare la logica di comunicazione
 */

public class Protocol implements Runnable{
	
	private Socket client;
	private String clientName;
	/*
	 * non li inizializzo nel costruttore perché non ha senso costruire risorse se poi non le uso -> le costruisco nel metodo run perché è lì che le uso
	 */
	private static ArrayList<Protocol> clientList = new ArrayList<>();     //vengono memorizzati tutti i client connessi
	/*
	 * non ci ho messo Socket perchè andrei a memorizzare informazioni di basso livello e non potrei farci cose utili che mi servono.
	 */

	private BufferedReader in;
	private PrintWriter out;	
	/*
	 * Gli devo passare anche il BufferdReader e PrintWriter ? -> no: sono calcolati in funzione del socket del client.
	 */
	public Protocol(Socket client) {
		this.client = client;
		clientList.add(this);
	}

	@Override
	public void run() {
		try{
			
			in = new BufferedReader(
					new InputStreamReader(client.getInputStream()));
				
			out = new PrintWriter(client.getOutputStream(), true);
				
			/*
			 * sono stati creati due stream : uno permette di leggere le informazioni, laltro permette di iviarle a server.
			 */
			
				System.out.println("Client connesso:"+ client.getPort());
					
					boolean converToUpper = true;
					String request;
					String clientName = "";
			
			
				do {
					out.printf("Ciao! Dimmi il tuo nome: ");
					clientName = in.readLine();
					
				}while(clientName.length() < 3);    //se è sotto i tre caratteri non ho un nome valido
				
				out.printf("\nBenvenuto >>> %s <<<\n", clientName);
				
				
				while((request = in.readLine()) != null) {
					//leggere nella linea di comando del server
					System.out.println("Processing request: " + request);
					/*
					 * stringa di risposta che si vuole mandare al client -> per farlo la mando nello stram in output.
					 */
					String command = request.toUpperCase();
									
					if("!U".equals(command))
						converToUpper = true;
					else if("!L".equals(command))
						converToUpper = false;
					//comando per stampare tutti i client connessi al server
					else if("@LIST".equals(command))
						//viewClients();
						listClient(this);
					
					//se incontro questa stringa termino la comunicazione con il server
					if("@QUIT".equals(command))
						break;
					else 
						sendToAll(this, request);

					
					String response = (converToUpper) ? 
							request.toUpperCase(): request.toLowerCase();
				
					
					out.printf("\n%s\n", response);
						
				}
				
				out.printf("Arrivederci %s\n", clientName);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	/*
	 * metodo che trasmette un messaggio mandato da un client a tutti gli altri connessi al server
	 */
	protected void sendToAll(Protocol sender, String message) {
		/*for (Protocol client : clientList) {    
			client.sendMessage(sender, message);
		}*/
		
		//Implementazione attraverso il processing Stream -> equivalente alla precedente.
		clientList.forEach(c -> c.sendMessage(sender, message));
	}
	
	/*
	 * metodo che manda un messaggio al client che invoca il metodo : si scrive semplicemente sull'outputStream
	 */
	protected void sendMessage(Protocol sender, String message) {
		this.out.printf("[%s] : %s", this.clientName, message);
		this.out.flush();
	}
	
	/*
	 * metodo che permette, su richiesta di un client, per vedere tutti i client conessi 
	 */
	/*protected void viewClients() {
		clientList.forEach(c -> this.out.printf("%s", c.clientName));
	}*/  //creato io 
	
	//dal prof
	protected void listClient(Protocol sender) {
		clientList.forEach(c -> this.sendMessage(sender, String.format(" %s ", c.clientName)));
		
		this.sendMessage(sender, "\n\r");
	}

}
