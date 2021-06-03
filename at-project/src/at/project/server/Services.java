/*
 * Classe amb els m�todes que comuniquen p�gina web
 * amb la nostra aplicaci�.
 */

package at.project.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import at.project.stations.*;
import at.project.client.*;
import at.project.notifier.Message;
import at.project.notifier.Telegram;
import at.project.covid.*;
import at.project.database.DatabaseQueries;


@Path("/item")
public class Services {
	
	static private Data cache = new Data();	// cache on guardem la informaci� de la API del Bicing per no haver-la de cridar cada cop 
	static private List<BCVClient> listClients = new ArrayList<BCVClient>(); // llista amb els clients de l'aplicaci�
	static private BCVClients bcvClients = new BCVClients(); // clients de l'aplicaci�
	static private boolean firstTime = true; // boolea, serveix perqu� en la primera execuci� es cridi la API del Bicing
	static private boolean firstTimeCovid = true; // boolea, serveix perqu� en la primera execuci� es cridi la API del Bicing
	static private Date firstDate = new Date(); // variable on guardarem l'�ltim instant de temps en que hem cridat l'API del Bicing
	static private List<CovidInfo> covidData = new ArrayList<CovidInfo>(); // llista amb tota la informaci� extreta de l'API del Covid
	static private DatabaseQueries queries = new DatabaseQueries(); // variable per crear connexi� amb la base de dades i fer crides
	
	final static Logger logger = Logger.getLogger(Services.class);
	
	/*
	 * M�tode per agafar la informaci� del Bicing
	 * mitjan�ant l'API de Bicing
	 * Es crida mitjan�ant el bot� 'Get Stations Info'.
	*/
	@POST
	@Path("/getAllStations")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateCache() {
		
		Date lastDate = new Date(); // agafem l'instant de temps en el qual es crida la funci�
		
		if(((lastDate.getTime()-firstDate.getTime())/1000) > 120 || firstTime) { // mirem si �s la primera crida o han passat m�s de 120 segons per cridar l'API del Bicing
			try {
				Client client = ClientBuilder.newClient();
				WebTarget getStationsInfo = client.target("https://api.bsmsa.eu/").path("ext/api/bsm/gbfs/v2/en/station_status");
				Data allItems = getStationsInfo.request(
						MediaType.APPLICATION_JSON_TYPE).get(new GenericType<Data>() {});
				cache = allItems;
				firstDate = lastDate;
				firstTime = false;
				
				// comprovem quines estacions falten pel debug
				int cont = 1;
				logger.debug("#stations: " + cache.getData().getStations().size());
				for (int i = 0; i < cache.getData().getStations().size(); i++) {
					if(cache.getData().getStations().get(i).getStation_id() != (i+cont)) {
						logger.debug("station id missing: " + (i+cont));
						cont++;
					}
				}
				
			} catch (Exception e) {
				return Response.status(200).entity("Stations info could not be retrieved.").build();
			}
		}
		
		return Response.status(200).entity(cache.toString()).build();
	}
	
	
	/*
	 * M�tode per crear clients a partir del formulari de la p�gina web.
	 * input: JSON amb tots els camps de la classe BCVClient
	 * output: Response amb la informaci� del client creat o, si no s'ha
	 * 	pogut crear, un missatge informat de l'error que s'ha produ�t.
	 * Es crida mitjan�ant el bot� Submit del quadrat 'Create BCV Client'.
	*/
	@POST
	@Path("/addClient")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createClient(BCVClient client) {
		try {
			
			// Comprovem que el phoneNumber tingui 9 d�gits
			int length = (int) (Math.log10(client.getPhoneNumber()) + 1);
			if(length != 9 ) {
				return Response.status(404).entity("&emsp;ERROR: Phone number must have 9 digits").build();
			}
			// Actualitzem cache per saber quines estacions estan disponibles
			updateCache();
			int stationsSize = cache.getData().getStations().size();
			if(checkStationNumbers(client, stationsSize) == -1)
				return Response.status(404).entity("&emsp;ERROR: Station IDs go from 1 to " + stationsSize).build();
			
			// Comprovacions per evitar duplicacions o que quedi un camp buit
			if(queries.phoneNumberExists(client.getPhoneNumber()))
				return Response.status(404).entity("&emsp;ERROR: Ja existeix un client amb n�mero de tel�fon " + client.getPhoneNumber() + ".").build();
			if(queries.telegramTokenExists(client.getTelegramToken()))
				return Response.status(404).entity("&emsp;ERROR: Ja existeix un client amb telegramToken " + client.getTelegramToken() + ".").build();
			if(client.getTelegramToken() == 0)
				return Response.status(404).entity("&emsp;ERROR: El camp telegramToken no pot estar buit.").build();
			//if(client.getSubscribedStations()[0] == "")
				//return Response.status(404).entity("&emsp;ERROR: El camp Stations to subscribe no pot estar buit.").build();
			
			// Comprovem si les estacions que vol l'usuari estan disponibles
			String stationsCheck = cache.stationsExist(client.stationsToIntList());
			if (!stationsCheck.equals(""))
				return Response.status(404).entity("&emsp;ERROR: Les estacions amb ID " + stationsCheck + " no estan disponibles ara mateix.").build();
			
			// Afegim client a la base de dades
			String subStations = String.join(",",client.getSubscribedStations());
			queries.addClient(client.getPhoneNumber(), client.getTelegramToken(), subStations, client.getRegion());
			
			return Response.status(200).entity(client.toString()).build();
			
		} catch (Exception e) {
			return Response.status(404).build(); 
		}	
	}
	
	/*
	 * M�tode per extreure tots els clients
	 * output: Response amb la informaci� dels clients o un missatge informat de l'error que s'ha produ�t.
	 * Es crida mitjan�ant el bot� 'Get All Clients'.
	*/
	@GET
	@Path("/getAllClients")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getItems(){
		try {
			bcvClients.setClients(queries.getClients());
			return Response.status(200).entity(bcvClients.toString()).build();
		} catch (Exception e) {
			return Response.status(404).entity("&emsp;ERROR: No s'han pogut recuperar els clients.").build();
		}
	}
	
	/*
	 * M�tode per afegir estacions a un client
	 * input: BCVClient que cont� les estacions a afegir al camp subscribedStations i el phoneNumber del client a modificar
	 * output: Response amb la informaci� del client actualitzada o un missatge informat de l'error que s'ha produ�t.
	 * Es crida mitjan�ant el bot� submit del quadrat 'Add stations to a client'
	*/
	@POST
	@Path("/addStation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addStation(BCVClient updateStations) {
		try {
			
			updateCache();
			
			// Busquem si existeix un client amb aquest phoneNumber
			BCVClient bC = new BCVClient();
			bcvClients.setClients(queries.getClients());
			bC = bcvClients.searchClient(updateStations.getPhoneNumber());
			if(bC.getPhoneNumber() == -1) {
				return Response.status(200).entity("&emsp;ERROR: There is no client with phone number" + updateStations.getPhoneNumber() + ".").build();
			}
			
			// Comprovem si les estacions que vol afegir estan disponibles
			String stationsCheck = cache.stationsExist(updateStations.stationsToIntList());
			if (!stationsCheck.equals(""))
				return Response.status(404).entity("&emsp;ERROR: Les estacions amb ID " + stationsCheck + " no estan disponibles ara mateix.").build();
			
			// Unim les estacions noves amb les antigues mitjan�ant un set, per evitar buscar duplicacions	
			Set<Integer> newStations = bC.stationsToSet(bC.getSubscribedStations());
			newStations.addAll(updateStations.stationsToSet(updateStations.getSubscribedStations()));
			bC.setSubscribedStations(bC.stationsToStringArray(newStations));
			bcvClients.setClients(listClients);
			
			// Afegim les noves estacions del client a la base de dades
			String stationsString = bC.stationsSetToString(newStations);
			queries.updateStations(bC.getPhoneNumber(), stationsString);
			return Response.status(200).entity(bC.toString()).build();
			
		} catch (Exception e) {
			String notFound = "&emsp;ERROR: There is no client with that phone number.";
			return Response.status(404).entity(notFound).build();
		}	
	}
	
	/*
	 * M�tode enviar la informaci� de Bicing d'un client per Telegram
	 * input: BCVClient que cont� el phoneNumber del client a notificar
	 * output: Response amb la informaci� de les estacions a les que s'ha subscrit el client
	 * Es crida mitjan�ant el bot� 'Notify Bicing' del quadrat 'Notify Client'.
	*/
	@POST
	@Path("/notifyStationsInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response notifyStationsInfo(BCVClient clientToNotify) {
		try {
			
			updateCache();
			
			// Agafem els clients de la base de dades i busquem si existeix el tel�fon
			BCVClient bC = new BCVClient();
			bcvClients.setClients(queries.getClients());
			bC = bcvClients.searchClient(clientToNotify.getPhoneNumber());
			if(bC.getPhoneNumber() == -1)
				return Response.status(404).entity("&emsp;ERROR: No hi ha cap client amb aquest phoneNumber.").build();
			
			// Recorrem les estacions i creem strings amb els formats adequats a la web i el Telegram, respectivament
			List<Integer> stationsSubscribed = bC.stationsToIntList();
			String info = "";
			String infoTelegram = "";
			String essentialInfo  = "";
			for(Integer stationID: stationsSubscribed) {
				essentialInfo = cache.getData().getStation(stationID).essentialInfo();
				info = info + "&emsp;" + essentialInfo + "<br>";
				infoTelegram = infoTelegram + essentialInfo + "\n";
			}
			
			// Enviem el missatge a trav�s del bot de Telegram, mitjan�ant el telegramToken del client
			Telegram tg = new Telegram();
			Client client = ClientBuilder.newClient();
			Message message = new Message(bC.getTelegramToken(),
					infoTelegram);
			WebTarget targetSendMessage = client.target(tg.getTgURL()).path(tg.getSendMsgRequest());
			targetSendMessage.request().post(Entity.entity(message, MediaType.APPLICATION_JSON_TYPE),
					String.class);
			
			return Response.status(200).entity(info).build();
			
		} catch (Exception e) {
			String notFound = "&emsp;ERROR: The client has not subscribed to any stations.";
			return Response.status(404).entity(notFound).build();
		}
	}
	
	/*
	 * M�tode per agafar la informaci� del Covid
	 * mitjan�ant l'API corresponent
	 * output: Response amb tota la informaci� del Covid
	 * Es crida mitjan�ant el bot� 'Get Covid Info'.
	*/
	@POST
	@Path("/getCovid")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateCovid() {
		
		// Fem que nom�s agafi les dades a trav�s de l'API en la primera execuci� del programa
		if(firstTimeCovid) {
			try {
				
				// Agafem les dades de la web
				Client client = ClientBuilder.newClient();
				WebTarget getStationsInfo = client.target("https://analisi.transparenciacatalunya.cat/").path("resource/jvut-jxu8.json");
				covidData = getStationsInfo.request(
						MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<CovidInfo>>() {});
				firstTimeCovid = false;
				
				// Ordenem les dades en funci� de la variable data_ini, on ordre descendent, de manera que despr�s
				// sabem que la primera inst�ncia que trobem d'una comarca ser� la m�s recent. El sort l'hem tret de:
				// https://stackoverflow.com/questions/16751540/sorting-an-object-arraylist-by-an-attribute-value-in-java/43129819
				covidData.sort((CovidInfo ci1, CovidInfo ci2) -> {
					int comparison = ci1.getData_ini().compareTo(ci2.getData_ini());
					   if (comparison < 0)
					     return 1;
					   if (comparison > 0)
					     return -1;
					   return 0;
					});
				
			} catch (Exception e) {
				return Response.status(404).entity(covidData.toString()).build();
			}
		}
		
		return Response.status(200).entity(covidListToString(covidData)).build(); 
	}
	
	/*
	 * M�tode per canviar la regi� d'un client
	 * input: BCVClient que cont� la nova regi� al camp Region i el phoneNumber del client a modificar
	 * output: Response amb la informaci� del client actualitzada o un missatge informat de l'error que s'ha produ�t.
	 * Es crida mitjan�ant el bot� 'Submit' del quadrat 'Change region'.
	 * 
	*/
	@POST
	@Path("/changeRegion")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response changeRegion(BCVClient updatedRegion) {
		try {
			
			// Comprovem que existeixi el phoneNumber
			BCVClient bC = new BCVClient();
			bcvClients.setClients(queries.getClients());
			bC = bcvClients.searchClient(updatedRegion.getPhoneNumber());
			if(bC.getPhoneNumber() == -1) {
				return Response.status(404).entity("&emsp;ERROR: There is no client with phone number" + updatedRegion.getPhoneNumber() + ".").build();
			}
			
			//Actualitzem regi� al a base de dades
			bC.setRegion(updatedRegion.getRegion());
			bcvClients.setClients(listClients);
			queries.updateRegion(bC.getPhoneNumber(), updatedRegion.getRegion());
			
			return Response.status(200).entity(bC.toString()).build();
			
		} catch (Exception e) {
			String notFound = "An error took place while trying to change region.";
			return Response.status(404).entity(notFound).build();
		}	
	}
	
	/*
	 * M�tode enviar la informaci� del Covid d'un client per Telegram
	 * input: BCVClient que cont� el phoneNumber del client a notificar
	 * output: Response amb la informaci� del Covid de la regi� del client
	 * Es crida mitjan�ant el bot� 'Notify Covid' del quadrat 'Notify Client'.
	*/
	@POST
	@Path("/notifyCovidInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response notifyCovidInfo(BCVClient clientToNotify) {
		try {
			
			updateCovid();

			// Comprovem que existeix el client
			BCVClient bC = new BCVClient();
			bcvClients.setClients(queries.getClients());
			bC = bcvClients.searchClient(clientToNotify.getPhoneNumber());
			if(bC.getPhoneNumber() == -1)
				return Response.status(404).entity("&emsp;ERROR: No hi ha cap client amb aquest phoneNumber.").build();
			
			// Busquem la informaci� m�s recent de la comarca del client i li enviem
			for(CovidInfo info: covidData) {
				if (info.getNom().equals(bC.getRegion())) {
					Telegram tg = new Telegram();
					Client client = ClientBuilder.newClient();
					Message message = new Message(bC.getTelegramToken(),
							info.telegramInfo());
					WebTarget targetSendMessage = client.target(tg.getTgURL()).path(tg.getSendMsgRequest());
					targetSendMessage.request().post(Entity.entity(message, MediaType.APPLICATION_JSON_TYPE),
							String.class);
					
					return Response.status(200).entity(info.toString()).build();
					
				}
			}
			
			return Response.status(404).entity("No information about that region has been found").build();
			
		} catch (Exception e) {
			String notFound = "The client has not subscribed to any region.";
			return Response.status(404).entity(notFound).build();
		}
	}
	
	/*
	 * Funci� per comprovar si el client posa una estaci� amb un ID
	 * m�s gran que el n�mero d'estacions
	 * input: BCVClient amb el subscribedStations i el n�mero total d'estacions disponibles
	 * output: int per indicar si tot est� correcte o hi ha un ID massa gran
	 */
	public int checkStationNumbers(BCVClient client, int size) {
		for(String stationID: client.getSubscribedStations()) {
			if (Integer.parseInt(stationID) > size)
				return -1;
		}
		return 0;
		
	}
	
	/*
	 * Funci� per transformar una variable de tipus List<CovidInfo>
	 * a un String amb tota la informaci�.
	 * input: List<CovidInfo>
	 * output: String amb la informaci� Covid de l'input
	 */
	public String covidListToString (List<CovidInfo> covid) {
		String info = "&thinsp;Informaci� Covid: <br>";
		for(CovidInfo c : covid) {
			info = info + c.toString();
		}
		return info;
	}
}
