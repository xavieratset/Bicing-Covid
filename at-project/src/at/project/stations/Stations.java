/*
 * Classe amb una llista de Station
 * i els m�todes necessaris per treballar
 * amb la llista.
 */

package at.project.stations;

import java.util.List;

public class Stations {
	
	private List<Station> stations;
	
	public Stations() {
		super();
	}

	public Stations(List<Station> stations) {
		super();
		this.stations = stations;
	}

	public List<Station> getStations() {
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}

	@Override
	public String toString() {
		return "Stations [stations=" + stations + "]";
	}
	
	/*
	 * Funci� per buscar la informaci� d'una estaci�
	 * a partir del seu id. Si no la troba torna un Station buit.
	 * No es pot fer servir el m�tode get ja implementat
	 * de la classe List ja que com hi ha estacions que no tenen
	 * informaci� disponible, no en tots els casos es compleix
	 * que Station amb station_id X == Stations.get(X).
	 */
	public Station getStation(Integer id) {
		for(Station station: this.stations) {
			if(station.getStation_id() == id)
				return station;
		}
		return new Station();
	}
}
