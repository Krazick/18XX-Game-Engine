package ge18xx.train;

import ge18xx.map.Location;
import ge18xx.tiles.Gauge;

public class SegmentInformation {

	Location location;		//	Location
	boolean corpStation;	//	Operating Corp Station (true or false)
	boolean openStation;	//	Open Station (true or false)
	int revenue;			//	Revenue
	int bonus;				//	Bonus (for Cattle or Port)
	Gauge gauge;			//	Track Gauge
	
	public SegmentInformation (Location aLocation, boolean aCorpStation, boolean aOpenStation, int aRevenue, int aBonus, Gauge aGauge) {
		setLocation (aLocation);
		setCorpStation (aCorpStation);
		setOpenStation (aOpenStation);
		setRevenue (aRevenue);
		setBonus (aBonus);
		setGauge (aGauge);
	}

	public Gauge getGauge () {
		return gauge;
	}
	
	public int getBonus () {
		return bonus;
	}
	
	public int getRevenue () {
		return revenue;
	}
	
	public boolean getOpenStation () {
		return openStation;
	}
	
	public boolean getCorpStation () {
		return corpStation;
	}
	
	private void setGauge (Gauge aGauge) {
		gauge = aGauge;
	}

	private void setBonus (int aBonus) {
		bonus = aBonus;
	}

	private void setRevenue (int aRevenue) {
		revenue = aRevenue;
	}

	private void setOpenStation (boolean aOpenStation) {
		openStation = aOpenStation;
	}

	private void setCorpStation (boolean aCorpStation) {
		corpStation = aCorpStation;
	}

	private void setLocation (Location aLocation) {
		location = aLocation;
	}

	public boolean isValid () {
		boolean tIsValid = false;
		
		if (location != null) {
			if (location.getLocation() != Location.NO_LOCATION) {
				tIsValid = true;
			}
		}
		
		return tIsValid;
	}
}
