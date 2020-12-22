package ge18xx.train;

import ge18xx.center.RevenueCenter;
import ge18xx.map.Location;
import ge18xx.tiles.Tile;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

public class NodeInformation {
	final static AttributeName AN_LOCATION = new AttributeName ("location");
	final static AttributeName AN_CORP_STATION = new AttributeName ("corpStation");
	final static AttributeName AN_OPEN_FLOW = new AttributeName ("openFlow");
	final static AttributeName AN_HAS_REVENUE_CENTER = new AttributeName ("hasRevenueCenter");
	final static AttributeName AN_REVENUE = new AttributeName ("revenue");
	final static AttributeName AN_BONUS = new AttributeName ("bonus");

	Location location;		//	Location
	boolean corpStation;	//	Operating Corp Station (true or false)
	boolean openFlow;		//	Open Flow for Train Running Through (true or false)
	boolean hasRevenueCenter;	// Has a Revenue Center
	RevenueCenter revenueCenter;	// If a RevenueCenter, it is saved here
	int revenue;			//	Revenue
	int bonus;				//	Bonus (for Cattle or Port)
	
	public NodeInformation (Location aLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter,
				int aRevenue, int aBonus, RevenueCenter aRevenueCenter, int aPhase) {
		setHasRevenueCenter (aHasRevenueCenter);
		setRevenue (aRevenue);
		setRevenueCenter (aRevenueCenter, aPhase);
		setBonus (aBonus);
		setOpenFlow (aOpenFlow);
		setCorpStation (aCorpStation);
		setLocation (aLocation);
	}
	
	public int getBonus () {
		return bonus;
	}
	
	public int getRevenue () {
		return revenue;
	}
	
	public boolean getOpenFlow () {
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

	public void setOpenFlow (boolean aOpenFlow) {
		openFlow = aOpenFlow;
	}

	public void setCorpStation (boolean aCorpStation) {
		corpStation = aCorpStation;
		// If there is a Corporate Station, it is -ALWAYS OPEN-
		if (corpStation) {
			setOpenFlow (true);
		}
	}

	public void setLocation (Location aLocation) {
		location = aLocation;
		// if the Location is a MapCell Side, it is -ALWAYS OPEN-
		if (isSide ()) {
			setOpenFlow (true);
		}
		// Or if there is a Corporate Station, it is -ALWAYS OPEN-
		if (corpStation) {
			setOpenFlow (true);
		}
	}

	public int getLocationInt () {
		return location.getLocation ();
	}
	
	public Location getLocation () {
		return location;
	}
	
	public boolean isSide () {
		boolean tIsSide;
		
		if (location == Location.NO_LOC) {
			tIsSide = false;
		} else {
			tIsSide = location.isSide ();
		}
		
		return tIsSide;
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
	
	public XMLElement getElement (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;
		
		tXMLElement = aXMLDocument.createElement (aElementName);
		tXMLElement.setAttribute (AN_LOCATION, location.getLocation ());
		tXMLElement.setAttribute (AN_CORP_STATION, corpStation);
		tXMLElement.setAttribute (AN_OPEN_FLOW, openFlow);
		tXMLElement.setAttribute (AN_HAS_REVENUE_CENTER, hasRevenueCenter);
		tXMLElement.setAttribute (AN_REVENUE, revenue);
		tXMLElement.setAttribute (AN_BONUS, bonus);

		return tXMLElement;
	}
}
