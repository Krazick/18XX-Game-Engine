package ge18xx.center;

//
//  Centers.java
//  Game_18XX
//
//  Created by Mark Smith on 3/2/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import ge18xx.company.Corporation;
import ge18xx.company.MapToken;
import ge18xx.company.Token;
import ge18xx.company.TokenCompany;
import ge18xx.company.TokenStack;
import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Feature2;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileName;
import ge18xx.tiles.TileType;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class Centers implements Cloneable {
	public static final int UNSPECIFIED_ID = -1;
	List<RevenueCenter> centers;
	
	public Centers () {
		centers = new LinkedList<RevenueCenter> ();
	}
	
	public Centers (Centers aCenters, Tile aTile) {
		RevenueCenter tNewCenter;
		Revenues tRevenues;
		CityInfo tCityInfo;
		int tType;
		int tID;
		int tLocation;
		int tRevenueValue;
		String tName;
		List<RevenueCenter> tCenters;
	
		centers = new LinkedList<RevenueCenter> ();
		tCenters = aCenters.getCenters ();
		for (RevenueCenter tCenter : tCenters) {
			tType = tCenter.getTypeToInt ();
			tID = tCenter.getID ();
			tLocation = tCenter.getLocationToInt ();
			tRevenueValue = tCenter.getRevenue (0);
			tName = tCenter.getName ();
			tNewCenter = setupRevenueCenter (tType, tID, tLocation, tName, tRevenueValue, aTile.getTheTileType ());
			tRevenues = tCenter.getRevenues ();
			tCityInfo = tCenter.getCityInfo ();
			tNewCenter.setCityInfo (tCityInfo);
			tNewCenter.setRevenues (tRevenues);
			add (tNewCenter);
		}
	}
	
	private Centers (List<RevenueCenter> aCenters) {
		centers = aCenters;
	}
	
	public boolean add (RevenueCenter aCenter) {
		return centers.add (aCenter);
	}
	
	public void appendCenters (XMLElement aXMLElement, XMLDocument aXMLDocument) {
		XMLElement tCenterElement;
		
		for (RevenueCenter center : centers) {
			if (center != null) {
				tCenterElement = center.createElement (aXMLDocument);
				aXMLElement.appendChild (tCenterElement);
			}
		}
	}
	
	public void appendTokensState (XMLDocument aXMLDocument, XMLElement aMapCellElement) {
		for (RevenueCenter tCenter : centers) {
			tCenter.appendTokensState (aXMLDocument, aMapCellElement);
		}
	}

	public void appendCorporationBases (XMLDocument aXMLDocument, XMLElement aMapCellElement) {
		for (RevenueCenter tCenter : centers) {
			tCenter.appendCorporationBase (aXMLDocument, aMapCellElement);
		}
	}
	
	public void clearAllCityInfoCorporations () {
		for (RevenueCenter tCenter: centers) {
			tCenter.clearCityInfoCorporation ();
		}
	}
	
	public void clearAllCityInfoMapCells () {
		for (RevenueCenter tCenter: centers) {
			tCenter.clearCityInfoMapCell ();
		}
	}
	
	public void clearAllCityInfoRevenueCenters () {
		for (RevenueCenter tCenter: centers) {
			tCenter.clearCityInfoRevenueCenter ();
		}
	}
	
	public void clearAllStations () {
		City tCity;
		for (RevenueCenter tCenter: centers) {
			if (tCenter.isCity ()) {
				tCity = (City) tCenter;
				tCity.clearAllStations ();
			}
		}
	}
	
	public void clearCorporation () {
		for (RevenueCenter tCenter: centers) {
			tCenter.clearCorporation ();
		}
	}

	public void clearCityInfoCorporation (Corporation aCorporation) {
		for (RevenueCenter tCenter: centers) {
			tCenter.clearCityInfoCorporation (aCorporation);
		}
	}
	
	public void returnStation (TokenCompany aTokenCompany) {
		City tCity;
		
		for (RevenueCenter tCenter: centers) {
			if (tCenter.isCity ()) {
				tCity = (City) tCenter;
				tCity.returnStation (aTokenCompany);
			}
		}
	}
	
	public void clearStation (int aCorporationId) {
		City tCity;
		
		for (RevenueCenter tCenter: centers) {
			if (tCenter.isCity ()) {
				tCity = (City) tCenter;
				tCity.clearStation (aCorporationId);
			}
		}
	}
	
	public MapToken getMapTokenFor (int aCorporationID) {
		MapToken tMapToken;
		City tCity;
		
		tMapToken = (MapToken) TokenStack.NO_TOKEN;
		for (RevenueCenter tCenter: centers) {
			if (tCenter.isCity ()) {
				tCity = (City) tCenter;
				tMapToken = tCity.getMapTokenFor (aCorporationID);
			}
		}
		
		return tMapToken;
	}

	public Centers clone () {
		List<RevenueCenter> tCentersCopy = new LinkedList<RevenueCenter> ();
		for (RevenueCenter tCenter : centers) {
			if (tCenter != null) {
				tCentersCopy.add (tCenter.clone ());
			} else {
				tCentersCopy.add (null);
			}
		}
		
		return new Centers (tCentersCopy);
	}
	
	public XMLElement createElement (XMLDocument aXMLDocument, int aIndex) {
		XMLElement tXMLElement;
		RevenueCenter tCenter;
		
		tCenter = centers.get (aIndex);
		tXMLElement = tCenter.createElement (aXMLDocument);
		
		return tXMLElement;
	}
	
	public void draw (Graphics g, int XCenter, int YCenter, Hex aHex, boolean aOnTile, Feature2 aSelectedFeature) {
		draw (g, XCenter, YCenter, MapCell.NO_ORIENTATION, aHex, aOnTile, aSelectedFeature);
	}
	
	public void draw (Graphics g, int XCenter, int YCenter, int aTileOrient, Hex aHex, boolean aOnTile, Feature2 aSelectedFeature) {
		for (RevenueCenter tRevenueCenter : centers) {
			tRevenueCenter.draw (g, XCenter, YCenter, aTileOrient, aHex, aOnTile, aSelectedFeature);
		}
	}
	
	public void loadStationsStates (XMLNode aMapCellNode) {
		for (RevenueCenter tRevenueCenter : centers) {
			tRevenueCenter.loadStationsStates (aMapCellNode);
		}
	}

	public int getStationIndex (int aCorporationID) {
		int tStationIndex;
		int tCenterIndex, tCenterCount;
		RevenueCenter tCenter;
		
		tStationIndex = UNSPECIFIED_ID;
		tCenterCount = centers.size ();
		for (tCenterIndex = 0; tCenterIndex < tCenterCount; tCenterIndex++) {
			tCenter = centers.get (tCenterIndex);
			if (tCenter.cityHasStation (aCorporationID)){
				tStationIndex = tCenterIndex;
			}
		}
		
		return tStationIndex;
	}
	
	public RevenueCenter get (int aCenterIndex) {
		RevenueCenter tCenter;
		int tSize;
		
		tSize = centers.size ();
		if (tSize > 0) {
			if (aCenterIndex <= tSize) {
				tCenter = centers.get (aCenterIndex);
			} else {
				tCenter = null;
			}
		} else {
			tCenter = null;
		}
		
		return tCenter;
	}
	
	public RevenueCenter getRCWithBaseForCorp (Corporation aCorporation) {
		RevenueCenter tCenter = RevenueCenter.NO_CENTER;
		
		for (RevenueCenter tRevenueCenter : centers) {
			if (tRevenueCenter.withBaseForCorp (aCorporation)) {
				tCenter = tRevenueCenter;
			}
		}
		
		return tCenter;
		
	}

	public RevenueCenter getCenterAtLocation (Location aLocation) {
		RevenueCenter tCenter = RevenueCenter.NO_CENTER;
		int tRCLocationInt;
		int tLocationInt;
		
		tLocationInt = aLocation.getLocation ();
		for (RevenueCenter tRevenueCenter : centers) {
			tRCLocationInt = tRevenueCenter.getLocationToInt ();
			if (tLocationInt == tRCLocationInt) {
				tCenter = tRevenueCenter;
			}
		}
		
		return tCenter;
	}
	
	public int getCenterCount () {
		return (centers.size ());
	}
	
	public List<RevenueCenter> getCenters () {
		return centers;
	}
	
	public int getCityCenterCount () {
		int tCityCount = 0;
		
		for (RevenueCenter tCenter : centers) {
			if (tCenter instanceof City) {
				tCityCount++;
			}
		}
		
		return tCityCount;
	}
	public CityInfo getCityInfo () {
		CityInfo tCityInfo = null;
		
		for (RevenueCenter tCenter : centers) {
			tCityInfo = tCenter.getCityInfo ();
		}
		
		return tCityInfo;
	}
	
	public String getCityName () {
		CityInfo tCityInfo;
		String tCityName = "";
		
		tCityInfo = getCityInfo ();
		if (tCityInfo != null) {
			tCityName = tCityInfo.getName ();
		}
		
		return tCityName;
	}
	
	public RevenueCenter getRCContainingPoint (Point aPoint, Hex aHex, int XCenter, int YCenter, int aTileOrient) {
		RevenueCenter tRevenueCenter = null;
		
		for (RevenueCenter tRC : centers) {
			if (tRC.containingPoint (aPoint, aHex, XCenter, YCenter, aTileOrient)) {
				tRevenueCenter = tRC;
			}
		}
		
		return tRevenueCenter;
	}
	
	public int getRevenueCenterID () {
		int tRevenueCenterID = RevenueCenter.NO_ID;
		
		for (RevenueCenter tRevenueCenter : centers) {
			tRevenueCenterID = tRevenueCenter.getID ();
		}
		
		return tRevenueCenterID;
	}
	
	public RevenueCenter getSelectedRevenueCenter (Feature2 aSelectedFeature2, int aTileOrient) {
		RevenueCenter tRevenueCenter;
		Location tLocation;
		
		tRevenueCenter = null;
		if (centers.size () > 0) {
			for (RevenueCenter tCenter : centers) {
				tLocation = tCenter.getLocation ();
				tLocation = tLocation.rotateLocation (aTileOrient);
				if (tCenter.isSingleSelected (tLocation, aSelectedFeature2)) {
					tRevenueCenter = tCenter;
				}
			}
		}
		
		return tRevenueCenter;
	}
	
	public String getToolTip () {
		String tToolTip = "";
		String tSuperToolTip = "";
		String tCompanyHomes;
		String tCompanyDestinations;
		String tPreviousAbbrev;
		String tCorporationAbbrev;
		String tCityName;
		String tRCType;
		RevenueCenter tCenter;
		String tTokenAbbrev;

		tCenter = centers.get (0);
		if (tCenter != null) {
			tCityName = tCenter.getCityName ();
			if (tCityName != null) {
				if (tCityName.length () > 0) {
					tToolTip += "City: " + tCityName + "<br>";
				}
			}
		}
		tCompanyHomes = "";
		tCompanyDestinations = "";
		tPreviousAbbrev = null;
		tRCType = "";
		
		for (RevenueCenter tRC : centers) {
			if (tSuperToolTip.length () == 0) {
				tSuperToolTip = tRC.getToolTip ();
			}
			tTokenAbbrev = "";
			if (tRC.isTown ()) {
				if (tRCType.equals ("")) {
					tRCType = "Town";
				} else {
					tRCType = "Two Towns";
				}
			} else if (tRC.isCity ()) {
				tRCType = "City";
				tTokenAbbrev = tRC.getTokenToolTip ();
			}
			if (! ("".equals (tTokenAbbrev))) {
				tToolTip += "Tokens: "+ tTokenAbbrev + "<br>";
			}
			tCorporationAbbrev = tRC.getHomeCompanyAbbrev ();
			if (tCorporationAbbrev != null) {
				if (! tCorporationAbbrev.equals (tPreviousAbbrev)) {
					tCompanyHomes += "Home for: " + tCorporationAbbrev + "<br>";
					tPreviousAbbrev = tCorporationAbbrev;
				}
			}
			tCorporationAbbrev = tRC.getDestCompanyAbbrev ();
			if (tCorporationAbbrev != null) {
				tCompanyDestinations += "Destination for: " + tCorporationAbbrev + "<br>";
			}
		}
		if (tRCType.length () > 0) {
			tToolTip += "RC Type: " + tRCType + "<br>";
		}
		tToolTip += tCompanyHomes;
		tToolTip += tSuperToolTip;
		tToolTip += tCompanyDestinations;
		
		return tToolTip;
	}
	
	public int getTypeCount () {
		int tTypeCount;
		
		tTypeCount = 0;
		for (RevenueCenter tRC : centers) {
			if ((tRC.isTown ()) || (tRC.isDotTown ())) {
				tTypeCount++;
			}
			if (tRC.isCity ()) {
				if (! tRC.isDestination ()) {
					tTypeCount += 10;
				}
			}
		}
		return tTypeCount;
	}
	
	public boolean hasStation (int aCorpID) {
		boolean tHasStation = false;
		
		for (RevenueCenter tRC : centers) {
			if (tRC.cityHasStation (aCorpID)) {
				tHasStation = true;
			}
		}
		
		return tHasStation;
	}
	
	public boolean hasAnyStation () {
		boolean tHasAnyStation = false;
		
		for (RevenueCenter tRC : centers) {
			if (tRC.cityHasAnyStation ()) {
				tHasAnyStation = true;
			}
		}
		
		return tHasAnyStation;
	}
	
	public boolean hasAnyCorporationBase () {
		boolean tHasAnyCorporationBase = false;
		
		for (RevenueCenter tRC : centers) {
			if (tRC.hasAnyCorporationBase ()) {
				tHasAnyCorporationBase = true;
			}
		}
		
		return tHasAnyCorporationBase;
	}
	
	public boolean hasStation (Token aToken) {
		boolean tHasStation = false;
		
		for (RevenueCenter tRC : centers) {
			if (tRC.cityHasStation (aToken)) {
				tHasStation = true;
			}
		}
		
		return tHasStation;
	}
	
	public boolean isEmpty () {
		return centers.isEmpty ();
	}
	
	public void printlog () {
		System.out.println ("Revenue Centers Log Printout:");
		for (RevenueCenter tCenter : centers) {
			tCenter.printlog ();
		}
	}
	
	public void setCorporationBase (Corporation aBaseCorporation, Location aNewCityLocation) {
		for (RevenueCenter tCenter : centers) {
			if (tCenter.isAtLocation (aNewCityLocation)) {
				if ((tCenter instanceof City) || (tCenter instanceof PrivateRailwayCenter)) {
					tCenter.setCorporation (aBaseCorporation);
				}
			}
		}
	}
	
	public void copyCityInfo (Tile aTile) {
		int tCenterCount, tCenterIndex;
		RevenueCenter tCenter;
		CityInfo tCityInfo;
		
		tCenterCount = centers.size ();
		for (tCenterIndex = 0; tCenterIndex < tCenterCount; tCenterIndex++) {
			tCenter = centers.get (tCenterIndex);
			tCityInfo = tCenter.getCityInfo ();
			if (tCityInfo != null) {
				tCityInfo.setRevenueCenter (tCenter);
				aTile.setCityInfo (tCityInfo);
			}
		}
	}

	public void setCityInfo (CityInfo aCityInfo) {
		
		if (aCityInfo != null) {
			for (RevenueCenter tRevenueCenter : centers) {
				tRevenueCenter.setCityInfo (aCityInfo);
			}
		}
	}
	
	public void setMapCell (MapCell aMapCell) {
		for (RevenueCenter tCenter : centers) {
			tCenter.setMapCell (aMapCell);
		}
	}

	public void setTileType (TileType aTileType) {
		for (RevenueCenter tCenter : centers) {
			tCenter.setTileType (aTileType);
		}
	}
	
	public RevenueCenter setupRevenueCenter (int aType, int aLocation, int aValue, TileType aTileType) {
		return setupRevenueCenter (aType, UNSPECIFIED_ID, aLocation, TileName.NO_NAME2, aValue, aTileType);
	}
	
	public RevenueCenter setupRevenueCenter (int aType, int aID, int aLocation, int aValue, TileType aTileType) {
		return setupRevenueCenter (aType, aID, aLocation, TileName.NO_NAME2, aValue, aTileType);
	}
	
	public RevenueCenter setupRevenueCenter (int aType, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
		RevenueCenter tRevenueCenter;
		RevenueCenterType tRevenueCenterType = new RevenueCenterType (aType);
		int tStationCount = tRevenueCenterType.getStationCount ();
		
		if (tRevenueCenterType.isDotTown()) {
			tRevenueCenter = new Town (aType, aID, aLocation, aName, aValue, aTileType);
		} else if (tRevenueCenterType.isTown()) {
			tRevenueCenter = new TownTick (aType, aID, aLocation, aName, aValue, aTileType);
		} else if (tRevenueCenterType.isCity()) {
			tRevenueCenter = new City (aType, tStationCount, aID, aLocation, aName, aValue, aTileType);
		} else {
			tRevenueCenter = new City (aType, tStationCount, aID, aLocation, aName, aValue, aTileType);
		}
		
		return (tRevenueCenter);
	}

	public int size () {
		return centers.size ();
	}
	
	public String toString () {
		return centers.toString ();
	}

	public boolean hasTown() {
		boolean tHasTown = false;
		
		for (RevenueCenter tCenter : centers) {
			if (tCenter.isTown ()) {
				tHasTown = true;
			}
		}
		
		return tHasTown;
	}

	public RevenueCenter getCenterAtLocation (int aLocation) {
		int tRCLocationInt;
		RevenueCenter tCenter = RevenueCenter.NO_CENTER;
		
		for (RevenueCenter tRevenueCenter : centers) {
			tRCLocationInt = tRevenueCenter.getLocationToInt ();
			if (aLocation == tRCLocationInt) {
				tCenter = tRevenueCenter;
			}
		}
		
		return tCenter;

	}
}
