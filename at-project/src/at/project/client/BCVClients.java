/*
 * Classe que conté una llista de BCVClient
 * i les funcions adequades per treballar 
 * amb la llista.
*/

package at.project.client;

import java.util.List;

public class BCVClients {
	
	private List<BCVClient> clients;
	
	public BCVClients() {
		super();
	}

	public BCVClients(List<BCVClient> clients) {
		super();
		this.clients = clients;
	}

	public List<BCVClient> getClients() {
		return clients;
	}

	public void setClients(List<BCVClient> clients) {
		this.clients = clients;
	}

	/*
	 * Funció toString modificada per adaptar el format a la pàgina web
	*/
	@Override
	public String toString() {
		String output = "&thinsp;BCVClients: <br>";
		for(int i = 0; i < clients.size(); i++) {
			output = output + "&emsp;" + clients.get(i).toString() + "<br>";
		}
		return output;
	}
	
	/*
	 * Funció per afegir un BCVClient a la llista.
	*/
	public void addClient(BCVClient client) {
		clients.add(client);
	}
	
	
	/*
	 * Funció per buscar i tornar la informació 
	 * d'un client en funció del 
	 * seu número de telèfon.
	*/
	public BCVClient searchClient (int phoneNumber) {
		int i = 0;
		while(clients.get(i).getPhoneNumber() != phoneNumber && i <= clients.size()) {
			i++;
		}
		if (i < clients.size())
			return clients.get(i);
		else
			return new BCVClient(-1,0,null, null); // valors per mostrar error
	}
}
