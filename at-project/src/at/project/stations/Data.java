/*
 * Classe amb la informació del Bicing
 * extreta de la pàgina web https://api.bsmsa.eu/ext/api/bsm/gbfs/v2/en/station_status  
*/

package at.project.stations;

import java.util.List;

public class Data {
	private long last_updated;
	private int ttl;
	private Stations data;

	public Data() {
		super();
	}

	public Data(long last_updated, int ttl, Stations data) {
		super();
		this.last_updated = last_updated;
		this.ttl = ttl;
		this.data = data;
	}

	public long getLast_updated() {
		return last_updated;
	}

	public void setLast_updated(long last_updated) {
		this.last_updated = last_updated;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public Stations getData() {
		return data;
	}

	public void setData(Stations data) {
		this.data = data;
	}

	/*
	 * Mètode toString modificat per mostrar en un format més
	 * llegible la informació de totes les estacions al lloc web
	 */
	@Override
	public String toString() {
		String bicingInfo = "&thinsp;Stations Info:<br>";
		for (int i = 0; i < data.getStations().size(); i++) {
			if (i%2 == 0)
				bicingInfo = bicingInfo + "&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;" + data.getStations().get(i).essentialInfo();
			else
				bicingInfo = bicingInfo + "&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;" + data.getStations().get(i).essentialInfo() + "<br>";
		}
		return bicingInfo;
	}
	
	/*
	 * Mètode per comprovar si una o més dels IDs
	 * introduits per l'usuari no existeixen en les
	 * dades recollides del Bicing
	 */
	public String stationsExist(List<Integer> stations) {
		String missingStations = "";
		int size = 0;
		for(Integer id: stations) {
			size++;
			boolean exists = false;
			for(int i = 0; i <= id; i++) { // recorrem de la posició 0 a la posició id ja que les estacions estan ordenades
				if (this.data.getStations().get(i).getStation_id() == id)
					exists = true;
			}
			if (!exists) {
				missingStations += id.toString();
				if (size < stations.size()) // no afegim coma després de l'última estació que falta
					missingStations += ", ";
			}
		}
		
		return missingStations;
	}
	
	


	
	
}
