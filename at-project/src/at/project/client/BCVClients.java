/*
 * Classe que cont� una llista de BCVClient
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
	 * Funci� toString modificada per adaptar el format a la p�gina web
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
	 * Funci� per afegir un BCVClient a la llista.
	*/
	public void addClient(BCVClient client) {
		clients.add(client);
	}
	
	
	/*
	 * Funci� per buscar i tornar la informaci� 
	 * d'un client en funci� del 
	 * seu n�mero de tel�fon.
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
