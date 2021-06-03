/*
 * Classe per connectar-se amb la base de dades de MySQL
 * L'usuari ha de canviar els valors de les variables user i password si vol provar el programa
 * per les seves pròpies; o bé crear un usuari de MySQL amb aquests valors
*/

package at.project.database;

import java.sql.*;

public class DatabaseConnection {

	private Connection connection = null;
	
	public DatabaseConnection() throws Exception {
		String user = "test";
		String password="Test_1234";
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		connection=DriverManager.getConnection("jdbc:mysql://localhost/atproject?user="+user+"&password="+password+
				"&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
	}
	
	//execute queries
	
		public PreparedStatement prepareStatement(String query) throws SQLException{
			// Note that this is done using https://www.arquitecturajava.com/jdbc-prepared-statement-y-su-manejo/
			return connection.prepareStatement(query);
		}
		
		public void disconnectBD() throws SQLException{
			connection.close();
		}
	
}
