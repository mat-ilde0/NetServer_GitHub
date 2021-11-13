package it.unibs.pajc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * in questo modo nel metodo run si va ad implementare la logica di comunicazione
 */

public class Protocol implements Runnable{
	
	private Socket client;
	/*
	 * non li inizializzo nel costruttore perché non ha senso costruire risorse se poi non le uso -> le costruisco nel metodo run perché è lì che le uso
	 */
	private BufferedReader in;
	private PrintWriter out;
	
	/*
	 * Gli devo passare anche il BufferdReader e PrintWriter ? -> no: sono calcolati in funzione del socket del client.
	 */
	public Protocol(Socket client) {
		this.client = client;
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
					
				}while(clientName.length() < 3);    //se è sotto i tre caratteri non gho un nome valido
				
				out.printf("\nBenvenuto >>> %s <<<\n", clientName);
				
				
				while((request = in.readLine()) != null) {
					//leggere nella linea di comando del server
					System.out.println("Processing request: " + request);
					/*
					 * stringa di risposta che si vuole mandare al client -> per varlo la mando nello stram in output.
					 */
					String command = request.toUpperCase();
					
					String response = (converToUpper) ? 
							request.toUpperCase(): request.toLowerCase();
				
					if("!TOUPPER".equals(command))
						converToUpper = true;
					else if("!TOLOWER".equals(command))
						converToUpper = false;
					
					//se incontro questa stringa termino la comunicazione con il server
					if("@QUIT".equals(command))
						break;
						
				}
				
				out.printf("Arrivederci %s\n", clientName);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

}
