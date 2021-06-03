/*
 * Classe encarregada de fer les 
 * crides a la base de dades.
*/

package at.project.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import at.project.client.BCVClient;
import at.project.server.Services;

public class DatabaseQueries {

	private DatabaseConnection db = null;
	private Logger logger = Logger.getLogger(Services.class);

	/*
	 * Creem la connexió amb la base de dades.
	*/
	public DatabaseQueries() {
		try {
			db = new DatabaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void finalize() {
		try {
			super.finalize();
			db.disconnectBD();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/*
	 * Funció per crear un client a la base de dades.
	 * input: els camps requerits per crear un usuari
	*/
	public void addClient(int phoneNumber, int telegramToken, String subscribedStations, String region) {
		String query = "INSERT INTO BCVClient (phoneNumber, telegramToken, subscribedStations, region) VALUES (?,?,?,?)";
		PreparedStatement statement = null;
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, phoneNumber);
			statement.setInt(2, telegramToken);
			statement.setString(3, subscribedStations);
			statement.setString(4, region);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Funció per agafar tots els clients de la base de dades.
	 * output: List<BCVClient> amb tots els clients de la base de dades.
	*/
	public List<BCVClient> getClients(){
		String query = "SELECT * FROM BCVClient;";
		PreparedStatement statement = null;
		ResultSet rs = null;
		List<BCVClient> clients = new ArrayList<BCVClient>();
		try {
			statement = db.prepareStatement(query);
			rs = statement.executeQuery();
			while (rs.next()) {
				String stations = rs.getString("subscribedStations");
				String[] stations2 = stations.split(","); // passem String a String[]
				BCVClient client = new BCVClient(rs.getInt("phoneNumber"), rs.getInt("telegramToken"), stations2, rs.getString("region"));
				clients.add(client);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return clients;
	}
	
	/*
	 * Funció per actualitzar (afegir en aquest cas) les estacions d'un usuari
	 * input: phoneNumber de l'usuari i els IDs de les estacions a afegir
	*/
	public void updateStations(int phoneNumber, String stations) {
		String query = "UPDATE BCVClient SET subscribedStations = ? WHERE phoneNumber = ? ;";
		PreparedStatement statement = null;
		try {
			statement = db.prepareStatement(query);
			statement.setString(1,stations);
			statement.setInt(2,phoneNumber);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Funció per canviar la regió d'un client.
	 * input: phoneNumber de l'usuari i nom de la nova regió
	*/
	public void updateRegion(int phoneNumber, String region) {
		String query = "UPDATE BCVClient SET region = ? WHERE phoneNumber = ? ;";
		PreparedStatement statement = null;
		try {
			statement = db.prepareStatement(query);
			statement.setString(1,region);
			statement.setInt(2,phoneNumber);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	} 
	

	/*
	 * Funció per comprovar si un telèfon ja existeix a la base de dades.
	 * Busca si hi ha alguna coincidència entre el phoneNumber
	 * que rep i els de la base de dades.
	 * input: phoneNumber de l'usuari
	 * output: boolea per dir si ha trobat o no un usuari amb aquest telèfon
	*/
	public boolean phoneNumberExists(int phoneNumber) {
		String query = "SELECT phoneNumber FROM BCVClient WHERE phoneNumber = ? ;";
		PreparedStatement statement = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, phoneNumber);
			rs = statement.executeQuery();
			if (rs.next()) {
				result = true;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/*
	 * Funció per comprovar si un telegramToken ja existeix a la base de dades.
	 * Busca si hi ha alguna coincidència entre el phoneNumber
	 * que rep i els de la base de dades.
	 * input: phoneNumber de l'usuari
	 * output: boolea per dir si ha trobat o no un usuari amb aquest telegramToken
	*/
	public boolean telegramTokenExists(int telegramToken) {
		String query = "SELECT telegramToken FROM BCVClient WHERE telegramToken = ? ;";
		PreparedStatement statement = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, telegramToken);
			rs = statement.executeQuery();
			if (rs.next()) {
				result = true;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/*
	 * Funció per buscar un client a la base de dades
	 * i tornar tota la seva informació.
	 * input: phoneNumber de l'usuari
	 * output: BCVClient amb la informació de l'usuari
	 * 		   o null si no el troba
	*/
	public BCVClient getClient(int phoneNumber){
		String query = "SELECT phoneNumber FROM BCVClient WHERE phoneNumber = ?;";
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = db.prepareStatement(query);
			rs = statement.executeQuery();
			if (rs.next()) {
				logger.debug("getClients: stations: " + rs.getString("subscribedStations"));
				String stations = rs.getString("subscribedStations");
				String[] stations2 = stations.split(",");
				logger.debug("getClients: stations replaceAll: " + stations2);
				BCVClient client = new BCVClient(rs.getInt("phoneNumber"), rs.getInt("telegramToken"), stations2, rs.getString("region"));
				return client;
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
