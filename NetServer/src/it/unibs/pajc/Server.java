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
			 
			//Socket client = server.accept();
			
				
		){
			
			while(true){
				//mette in ascolto il server e fin quando qualcuno NON bussa, rimane in attesa che qualcuno bussi
				//il thread principale si ferma su quella linea, in attesa che un client si connetta e appena si connette questo metodo
				//ritorna  l'oggetto Client
				Socket client = server.accept();
				Protocol p = new Protocol(client);
				Thread clientThread = new Thread(p);
				clientThread.start();
			}
			
			
		} catch (IOException ex) {
			System.err.println("Errore di comunicazione:" + ex);
		}
		
		System.out.println("exit....");
		
	}

}
