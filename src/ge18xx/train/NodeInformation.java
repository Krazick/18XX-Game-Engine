package ge18xx.train;

import ge18xx.center.RevenueCenter;
import ge18xx.map.Location;
import ge18xx.tiles.Tile;

public class NodeInformation {

	Location location;		//	Location
	boolean corpStation;	//	Operating Corp Station (true or false)
	boolean openFlow;		//	Open Flow for Train Running Through (true or false)
	boolean hasRevenueCenter;	// Has a Revenue Center
	RevenueCenter revenueCenter;	// If a RevenueCenter, it is saved here
	int revenue;			//	Revenue
	int bonus;				//	Bonus (for Cattle or Port)
	
	public NodeInformation (Location aLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter,
				int aRevenue, int aBonus, RevenueCenter aRevenueCenter, int aPhase) {
		setLocation (aLocation);
		setCorpStation (aCorpStation);
		setOpenFlow (aOpenFlow);
		setHasRevenueCenter (aHasRevenueCenter);
		setRevenue (aRevenue);
		setRevenueCenter (aRevenueCenter, aPhase);
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
	
	public boolean isSide () {
		return location.isSide ();
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
	
	public void setRevenueCenter (RevenueCenter aRevenueCenter, int aPhase) {
		revenueCenter = aRevenueCenter;
		if (aRevenueCenter != RevenueCenter.NO_CENTER) {
			setHasRevenueCenter (true);
			setRevenue (aRevenueCenter.getRevenue (aPhase));
		} else {
			setHasRevenueCenter (false);
			setRevenue (0);
		}
	}
	
	public RevenueCenter getRevenueCenter () {
		return revenueCenter;
	}
	
	public boolean hasRevenueCenter () {
		return hasRevenueCenter;
	}
	
	public String getDetail () {
		String tDetail;
		
		tDetail = "[" + getLocationInt ();
		if (revenueCenter != null) {
			tDetail += ": $" + revenueCenter.getRevenueToString ();
			tDetail += " Has Corp Station " + corpStation;
		}
		tDetail += "]";
		
		return tDetail;
	}

	public void applyRCinfo (Tile aTile, Location aLocation, int aPhase, int aCorpID) {
		RevenueCenter tRevenueCenter;
		if (aTile != Tile.NO_TILE) {
			tRevenueCenter = aTile.getCenterAtLocation (aLocation);
			if (tRevenueCenter != RevenueCenter.NO_CENTER) {
				setHasRevenueCenter (true);
				setRevenueCenter (tRevenueCenter, aPhase);
				if (tRevenueCenter.cityHasStation (aCorpID)) {
					setCorpStation (true);
				}
			} else {
				System.err.println ("Can't find Revenue Center at " + aLocation.getLocation ());
			}
		}
		
	}
}
