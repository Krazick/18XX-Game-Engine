package ge18xx.train;

import ge18xx.center.RevenueCenter;
import ge18xx.map.Location;

public class NodeInformation {

	Location location;		//	Location
	boolean corpStation;	//	Operating Corp Station (true or false)
	boolean openFlow;		//	Open Flow for Train Running Through (true or false)
	boolean hasRevenueCenter;	// Has a Revenue Center
	RevenueCenter revenueCenter;	// If a RevenueCenter, it is saved here
	int revenue;			//	Revenue
	int bonus;				//	Bonus (for Cattle or Port)
	
	public NodeInformation (Location aLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter,
				int aRevenue, int aBonus, RevenueCenter aRevenueCenter) {
		setLocation (aLocation);
		setCorpStation (aCorpStation);
		setOpenFlow (aOpenFlow);
		setHasRevenueCenter (aHasRevenueCenter);
		setRevenueCenter (aRevenueCenter);
		setRevenue (aRevenue);
		setBonus (aBonus);
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

	public void setLocation (Location aLocation) {
		location = aLocation;
	}

	public int getLocationInt () {
		return location.getLocation ();
	}
	
	public Location getLocation () {
		return location;
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
	
	public void setRevenueCenter (RevenueCenter aRevenueCenter) {
		revenueCenter = aRevenueCenter;
	}
	
	public RevenueCenter getRevenueCenter () {
		return revenueCenter;
	}
	
	public boolean hasRevenueCenter () {
		return hasRevenueCenter;
	}
}
