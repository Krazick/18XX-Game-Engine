package ge18xx.train;

import ge18xx.center.RevenueCenter;
import ge18xx.map.Location;
import ge18xx.tiles.Tile;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

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
	
	public NodeInformation (boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter,
				int aRevenue, int aBonus) {
		this (Location.NO_LOC, aCorpStation, aOpenFlow, aHasRevenueCenter, aRevenue, aBonus, RevenueCenter.NO_CENTER);
	}

	public NodeInformation copyNode () {
		NodeInformation tNode;
		Location tLocation;
		RevenueCenter tRevenueCenter;
		
		tNode = new NodeInformation (corpStation, openFlow, hasRevenueCenter, revenue, bonus);
		tRevenueCenter = revenueCenter.clone ();
		tLocation = location.clone ();
		tNode.setRevenueCenter (tRevenueCenter);
		tNode.setLocation (tLocation);
		
		return tNode;
	}

	public NodeInformation (Location aLocation, boolean aCorpStation, boolean aOpenFlow, boolean aHasRevenueCenter,
				int aRevenue, int aBonus, RevenueCenter aRevenueCenter) {
		setHasRevenueCenter (aHasRevenueCenter);
		setRevenue (aRevenue);
		setBonus (aBonus);
		setOpenFlow (aOpenFlow);
		setRevenueCenter (aRevenueCenter);
		setCorpStation (aCorpStation);
		setLocation (aLocation);
	}
	
	public NodeInformation (XMLNode aNodeInfoNode) {
		boolean tCorpStation, tHasRevenueCenter, tOpenFlow;
		int tBonus, tLocationInt, tRevenue;
		Location tLocation;

		tCorpStation = aNodeInfoNode.getThisBooleanAttribute (AN_CORP_STATION);
		tOpenFlow = aNodeInfoNode.getThisBooleanAttribute (AN_OPEN_FLOW);
		tHasRevenueCenter = aNodeInfoNode.getThisBooleanAttribute (AN_HAS_REVENUE_CENTER);
		tBonus = aNodeInfoNode.getThisIntAttribute (AN_BONUS);
		tLocationInt = aNodeInfoNode.getThisIntAttribute (AN_LOCATION);
		tRevenue = aNodeInfoNode.getThisIntAttribute (AN_REVENUE);
		tLocation = new Location (tLocationInt);
		
		setHasRevenueCenter (tHasRevenueCenter);
		setRevenue (tRevenue);
		setBonus (tBonus);
		setOpenFlow (tOpenFlow);
		setCorpStation (tCorpStation);
		setLocation (tLocation);
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

	public void setBonus (int aBonus) {
		bonus = aBonus;
	}

	public void setRevenue (int aRevenue) {
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
			setCorpStation (false); // If this is a Side, there is -NEVER- a Corp Station
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
	
	public void setRevenueCenter (RevenueCenter aRevenueCenter) {
		revenueCenter = aRevenueCenter;
		if (aRevenueCenter != RevenueCenter.NO_CENTER) {
			setHasRevenueCenter (true);
			if (aRevenueCenter.isTown () || aRevenueCenter.isDotTown ()) {
				setOpenFlow (true);
			} else if (aRevenueCenter.isCity ()) {
				if (aRevenueCenter.cityHasOpenStation ()) {
					setOpenFlow (true);
				}
			}
		} else {
			setHasRevenueCenter (false);
			setOpenFlow (true);
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
			tDetail += ": $" + revenue;
			tDetail += " CorpStation " + corpStation;
		}
		tDetail +=  " OpenFlow " + openFlow + "]";
		
		return tDetail;
	}

	public void applyRCinfo (Tile aTile, Location aLocation, int aCorpID) {
		RevenueCenter tRevenueCenter;
		
		if (aTile != Tile.NO_TILE) {
			tRevenueCenter = aTile.getCenterAtLocation (aLocation);
			if (tRevenueCenter != RevenueCenter.NO_CENTER) {
				setHasRevenueCenter (true);
				setRevenueCenter (tRevenueCenter);
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

	public void fixRevenueCenter (Tile tTile) {
		RevenueCenter tRevenueCenter;
		
		tRevenueCenter = tTile.getCenterAtLocation (location);
		if (tRevenueCenter != RevenueCenter.NO_CENTER) {
			setRevenueCenter (tRevenueCenter);
		} else {
			tRevenueCenter = tTile.getFirstRevenueCenter ();
			if (tRevenueCenter != RevenueCenter.NO_CENTER) {
				setRevenueCenter (tRevenueCenter);
				location = tRevenueCenter.getLocation ();
			} else {
				setRevenueCenter (tRevenueCenter);
				setLocation (Location.NO_LOC);
			}
		}
	}

//	Location location;		//	Location
//	boolean corpStation;	//	Operating Corp Station (true or false)
//	boolean openFlow;		//	Open Flow for Train Running Through (true or false)
//	boolean hasRevenueCenter;	// Has a Revenue Center
//	RevenueCenter revenueCenter;	// If a RevenueCenter, it is saved here
//	int revenue;			//	Revenue
//	int bonus;				//	Bonus (for Cattle or Port)

	public void updateNode (Tile aNewTile) {
		// If this Location is a NOT a Side, need to update - ie No Revenue Centers on a Side
		if (! isSide ()) {
			fixRevenueCenter (aNewTile);
		}
	}
	
	public void updateRevenue (int aPhase) {
		if (revenueCenter != RevenueCenter.NO_CENTER) {
			setRevenue (revenueCenter.getRevenue (aPhase));
		}
	}
	
	public boolean isSame (NodeInformation aNode) {
		boolean tIsSame = true;
		
		if (location.getLocation () != aNode.getLocationInt ()) {
			tIsSame = false;
		}
		
		return tIsSame;
	}
}
