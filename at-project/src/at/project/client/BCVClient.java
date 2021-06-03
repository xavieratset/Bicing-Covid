/*
 * Classe amb la informació necessària de cada 
 * client de l'aplicació BCV
*/


package at.project.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BCVClient {
	
	private int phoneNumber;
	private int telegramToken;
	private String[] subscribedStations;
	private String region;
	
	public BCVClient() {
		super();
	}

	public BCVClient(int phoneNumber, int telegramToken, String[] subscribedStations, String region) {
		super();
		this.phoneNumber = phoneNumber;
		this.telegramToken = telegramToken;
		this.subscribedStations = subscribedStations;
		this.region = region;
	}

	public int getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getTelegramToken() {
		return telegramToken;
	}

	public void setTelegramToken(int telegramToken) {
		this.telegramToken = telegramToken;
	}

	public String[] getSubscribedStations() {
		return subscribedStations;
	}

	public void setSubscribedStations(String[] subscribedStations) {
		this.subscribedStations = subscribedStations;
	}
	
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Override
	public String toString() {
		return "BCVClient [phoneNumber=" + phoneNumber + ", telegramToken=" + telegramToken + ", subscribedStations="
				+ Arrays.toString(subscribedStations) + ", region=" + region + "]\n";
	}

	/*
	 * Funció per passar les estacions del format de la classe
	 * a un set de Integer
	 * input: estacions en tipus String[]
	 * output: estacions en tipus Set<Integer>
	*/
	public Set<Integer> stationsToSet(String[] stations){
		List<String> wordList = Arrays.asList(stations);
		Set<Integer> set = new HashSet<>();
		for(String s : wordList) {
			set.add(Integer.parseInt(s));
		}
		return set;
	}
	
	
	/*
	 * Funció per passar les estacions del format Set<Integer>
	 * a un String[]
	 * input: estacions en tipus Set<Integer>
	 * output: estacions en tipus String[]
	*/
	public String[] stationsToStringArray(Set<Integer> set) {
		List<String> list = new ArrayList<String>();
		for(Integer stationID: set) {
			list.add(String.valueOf(stationID));
		}
		String[] stationsList = list.toArray(new String[0]);
		
		return stationsList;
	}
	
	/*
	 * Funció per passar les estacions del format de la classe
	 * a una llista de Integer
	 * input: estacions en tipus String[]
	 * output: estacions en tipus List<Integer>
	*/
	public List<Integer> stationsToIntList() {
		List<Integer> list = new ArrayList<Integer>();
		for(String stationID: this.subscribedStations) {
			list.add(Integer.valueOf(stationID));
		}
		return list;
	}
	
	/*
	 * Funció per passar les estacions del format Set<Integer>
	 * a un String
	 * input: estacions en tipus Set<Integer>
	 * output: estacions en tipus String
	*/
	public String stationsSetToString(Set<Integer> set) {
		String stationsString = "";
		int i = 1;
		for(Integer stationID: set) {
			stationsString = stationsString + String.valueOf(stationID);
			if (i < set.size()) 
				stationsString = stationsString +",";
			i++;
		}
		
		return stationsString;
	}
}
