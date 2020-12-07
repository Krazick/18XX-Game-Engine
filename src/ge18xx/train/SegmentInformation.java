package ge18xx.train;

import ge18xx.map.Location;
import ge18xx.tiles.Gauge;

public class SegmentInformation {

	Location location;		//	Location
	boolean corpStation;	//	Operating Corp Station (true or false)
	boolean openFlow;		//	Open Flow for Train Running Through (true or false)
	boolean hasRevenueCenter;	// Has a Revenue Center
	int revenue;			//	Revenue
	int bonus;				//	Bonus (for Cattle or Port)
	Gauge gauge;			//	Track Gauge
	
	public SegmentInformation (Location aLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter,
				int aRevenue, int aBonus, Gauge aGauge) {
		setLocation (aLocation);
		setCorpStation (aCorpStation);
		setOpenFlow (aOpenFlow);
		setHasRevenueCenter (aHasRevenueCenter);
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
	
	public boolean getOpenFLow () {
		return openFlow;
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

	private void setOpenFlow (boolean aOpenFlow) {
		openFlow = aOpenFlow;
	}

	private void setCorpStation (boolean aCorpStation) {
		corpStation = aCorpStation;
	}

	private void setLocation (Location aLocation) {
		location = aLocation;
	}

	public int getLocation () {
		return location.getLocation ();
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

	public void setHasRevenueCenter (boolean aHasRevenueCenter) {
		hasRevenueCenter = aHasRevenueCenter;
	}
	
	public boolean hasRevenueCenter() {
		return hasRevenueCenter;
	}
}
