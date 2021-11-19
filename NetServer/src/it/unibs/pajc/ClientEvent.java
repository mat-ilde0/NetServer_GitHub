package it.unibs.pajc;

import java.util.ArrayList;

/*
 * parser delle stringhe mandate dai client
 */

public class ClientEvent {
	
	Protocol sender;    //oggetto da cui si riceve il messaggio da progettare
	String command;
	ArrayList<String> parameters = new ArrayList<>();
	
	/*
	 * Un costruttore di questo tipo interpreta i comandi passati e necessita di fare lui tutta l'attività di parsing del messaggio -> non è il massimo.
	 * dichiarandolo come privato impedisco che venga usato dall'esterno -> si vuole passare dal parser. 
	 */
	private ClientEvent(Protocol sender, String command, ArrayList<String> parameters) {
		this.sender = sender;
		this.command = command;
		this.parameters = parameters;
	}
	
	/*
	 * questo perché si vuole fare un ExecutorService per i messaggi -> si vuole costruire la classe ma senza passare dal costruttore
	 * Questo è un metodo che mi restituisce un ClientEvent.
	 */
	public static ClientEvent parse(Protocol sender, String message) {
		String command = null;
		ArrayList<String> parameters = new ArrayList<>();
		
		//fare il parsing ...
		if(message.startsWith("@")) {
			
			String[] tokens = message.split(":");
			command = tokens[0];
			
			for(int i = 1; i < tokens.length; i++) {
				parameters.add(tokens[i]);
			}
			
			
		}else {
			parameters.add(message);
		}
		
		return new ClientEvent(sender, command, parameters);

	}
	
	/*
	 * visto che il messaggio è l'ultimo parametro viene passato e voglio evitare di controllare il numero di parametri allora lo chiamo in questo modo
	 */
	public String getLastParameter() {
		return parameters.size() > 0 ? parameters.get(parameters.size() - 1) : "";
		
	}
	
}
