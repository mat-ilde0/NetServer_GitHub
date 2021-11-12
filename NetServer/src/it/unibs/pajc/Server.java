package it.unibs.pajc;

import java.io.*;
import java.io.IOException;
import java.net.*;


public class Server {

	public static void main(String[] args) {

		//su quale porta associo il servizio (scelta a caso)
		int port = 1234;
		
		System.out.println("Avvio del server");
		
		try (
			/*
			 * l'oggetto base su cui si crea il server è il seguente
			 * Si costruisce la risorsa che viene agganciata alla porta definita.
			 */
			ServerSocket server = new ServerSocket(port);
			//mette in ascolto il server e fin quando qualcuno NON bussa, rimane in attesa che qualcuno bussi
			//il thread principale si ferma su quella linea, in attesa che un client si connetta e appena si connette questo metodo
			//ritorna  l'oggetto Client 
			Socket client = server.accept();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(client.getInputStream()));
				
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				
			/*
			 * sono stati creati due stream : uno permette di leggere le informazioni, laltro permette di iviarle a server.
			 */
				
		){
			
			System.out.println("Client connesso:"+ client.getPort());
			String request;
			
			while((request = in.readLine()) != null) {
				//leggere nella linea di comando del server
				System.out.println("Processing request: " + request);
				/*
				 * stringa di risposta che si vuole mandare al client -> per varlo la mando nello stram in output.
				 */
				String response = request.toUpperCase();
				out.println(response);
				
				//se incontro questa stringa termino la comunicazione con il server
				if("@QUIT".equals(response))
					break;
					
			}
				
			
			
		} catch (IOException ex) {
			System.err.println("Errore di comunicazione:" + ex);
		}
		
		System.out.println("exit....");
		
	}

}
