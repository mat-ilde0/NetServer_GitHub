package it.unibs.pajc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/*
 * in questo modo nel metodo run si va ad implementare la logica di comunicazione
 */

public class Protocol implements Runnable{
	
	private static HashMap<String, Consumer<ClientEvent>> commandMap; //questo in realtà è statico
	static {
		commandMap = new HashMap<>();
		commandMap.put("@LIST", e -> e.sender.listClient(e.sender));
		commandMap.put("@ALL", e -> e.sender.sendToAll(e.sender, e.getLastParameter()));
		commandMap.put("@TIME", e -> e.sender.sendMessage(e.sender, LocalDateTime.now().toString()));
		/*
		 * in questo caso si vuole terminare l'esecuzione del client -> si crea un metodo close che chiude il client
		 */
		commandMap.put("@QUIT", e -> e.sender.close());
		commandMap.put("@default@", e -> e.sender.sendMessage(e.sender, e.getLastParameter()));



	}
	
	private Boolean isRunning = true;
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
					
					ClientEvent event = ClientEvent.parse(this, request);
					Consumer<ClientEvent> commandExe = event.command != null ?
							commandMap.get(event.command.toUpperCase()) : 
								commandMap.get("@default@"); 

					
					commandExe.accept(event);
					
						
				}
				
				out.printf("Arrivederci %s\n", clientName);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			clientList.remove(this);
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
		this.out.printf("[%s] : %s", sender.clientName, message);
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
		clientList.forEach(c -> sendMessage(c, String.format(" %s ", c.clientName)));
		
		this.sendMessage(sender, "\n\r");
	}
	
	/*
	 * metodo che chiude il client -> la cosa più comoda è di introdurre una variabile booleana e finché è vera il client continua ad andare e si chiude 
	 * quando queesta diventa falsa
	 */
	public void close() {
		isRunning = false;
	}

}
