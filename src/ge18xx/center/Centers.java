package ge18xx.center;

import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

//
//  Centers.java
//  Game_18XX
//
//  Created by Mark Smith on 3/2/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import ge18xx.company.Corporation;
import ge18xx.company.MapToken;
import ge18xx.company.ShareCompany;
import ge18xx.company.Token;
import ge18xx.company.TokenCompany;
import ge18xx.company.TokenInfo.TokenType;
import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.round.action.CloseCompanyAction;
import ge18xx.round.action.RemoveDestinationsAction;
import ge18xx.round.action.ReplaceTokenAction;
import ge18xx.tiles.Feature2;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileName;
import ge18xx.tiles.TileType;

import geUtilities.GUI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class Centers implements Cloneable {
	public static final int UNSPECIFIED_ID = -1;
	public static final String NO_CITY_NAME2 = "";
	public static final String NO_CITY_NAME = null;
	public static final Centers NO_CENTERS = null;
	List<RevenueCenter> centers;

	public Centers () {
		centers = new LinkedList<> ();
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

		centers = new LinkedList<> ();
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

	public boolean add (int aIndex, RevenueCenter aRevenueCenter) {
		boolean tCenterAdded;
		
		tCenterAdded = shouldAddCenter (aRevenueCenter);
		if (tCenterAdded) {
			centers.add (aIndex, aRevenueCenter);
		}
		
		return tCenterAdded;
	}
	
	public boolean add (RevenueCenter aRevenueCenter) {
		boolean tCenterAdded;
		
		tCenterAdded = shouldAddCenter (aRevenueCenter);

		if (tCenterAdded) {
			tCenterAdded = centers.add (aRevenueCenter);
		}
		
		return tCenterAdded;
	}

	public boolean shouldAddCenter (RevenueCenter aRevenueCenter) {
		boolean tCenterAdded;
		RevenueCenterType tProvidedCenterType;
		RevenueCenterType tRevenueCenterType;
		int tType;
		int tProvidedType;
		int tProvidedLocation;
		int tLocation;
		
		tProvidedCenterType = aRevenueCenter.getRevenueCenterType ();
		tProvidedType = tProvidedCenterType.getType ();
		tProvidedLocation = aRevenueCenter.getLocationToInt ();
		tCenterAdded = true;
		for (RevenueCenter tRevenueCenter : centers) {
			tRevenueCenterType = tRevenueCenter.getRevenueCenterType ();
			tType = tRevenueCenterType.getType ();
			tLocation = tRevenueCenter.getLocationToInt ();
			if (tProvidedType == tType) {
				if (tLocation == tProvidedLocation) {
					tCenterAdded = false;
				}
			}
			
		}
		
		return tCenterAdded;
	}

	public boolean remove (RevenueCenter aCenter) {
		return centers.remove (aCenter);
	}
	
	public void removeTemporaryCenters () {
		int tCenterIndex;
		int tCenterCount;
		RevenueCenter tRevenueCenter;
		
		tCenterCount = size ();
		for (tCenterIndex = 0; tCenterIndex < tCenterCount; tCenterIndex++) {
			tRevenueCenter = centers.get (tCenterIndex);
			if (tRevenueCenter.isTemporary ()) {
				remove (tRevenueCenter);
				tCenterIndex--;
				tCenterCount--;
			}
		}
	}

	public void appendCenters (XMLElement aXMLElement, XMLDocument aXMLDocument) {
		XMLElement tCenterElement;

		for (RevenueCenter center : centers) {
			if (center != RevenueCenter.NO_CENTER) {
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
		for (RevenueCenter tCenter : centers) {
			tCenter.clearCorporation ();
		}
	}

	public void clearAllCityInfoMapCells () {
		for (RevenueCenter tCenter : centers) {
			tCenter.clearCityInfoMapCell ();
		}
	}

	public void clearAllCityInfoRevenueCenters () {
		for (RevenueCenter tCenter : centers) {
			tCenter.clearCityInfoRevenueCenter ();
		}
	}

	public void clearAllStations () {
		City tCity;
		for (RevenueCenter tCenter : centers) {
			if (tCenter.isCity ()) {
				tCity = (City) tCenter;
				tCity.clearAllStations ();
			}
		}
	}

	public void clearCorporation () {
		for (RevenueCenter tCenter : centers) {
			tCenter.clearCorporation ();
		}
	}

	public boolean clearCityInfoCorporation (Corporation aCorporation) {
		boolean tClearedCorporation;

		tClearedCorporation = false;
		for (RevenueCenter tCenter : centers) {
			tClearedCorporation = tCenter.clearCityInfoCorporation (aCorporation);
		}
		
		return tClearedCorporation;
	}

	public void returnStation (TokenCompany aTokenCompany) {
		City tCity;

		for (RevenueCenter tCenter : centers) {
			if (tCenter.isCity ()) {
				tCity = (City) tCenter;
				tCity.returnStation (aTokenCompany);
			}
		}
	}

	public void clearStation (int aCorporationId) {
		City tCity;

		for (RevenueCenter tCenter : centers) {
			if (tCenter.isCity ()) {
				tCity = (City) tCenter;
				tCity.clearStation (aCorporationId);
			}
		}
	}

	public MapToken getMapTokenFor (int aCorporationID) {
		MapToken tMapToken;
		City tCity;

		tMapToken = (MapToken) Token.NO_TOKEN;
		for (RevenueCenter tCenter : centers) {
			if (tCenter.isCity ()) {
				tCity = (City) tCenter;
				tMapToken = tCity.getMapTokenFor (aCorporationID);
			}
		}

		return tMapToken;
	}

	@Override
	public Centers clone () {
		List<RevenueCenter> tCentersCopy = new LinkedList<> ();
		for (RevenueCenter tCenter : centers) {
			if (tCenter != RevenueCenter.NO_CENTER) {
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

	public void draw (Graphics aGraphics, int aXCenter, int aYCenter, Hex aHex, boolean aOnTile, 
						Feature2 aSelectedFeature) {
		draw (aGraphics, aXCenter, aYCenter, MapCell.NO_ORIENTATION, aHex, aOnTile, aSelectedFeature);
	}

	public void draw (Graphics aGraphics, int aXCenter, int aYCenter, int aTileOrient, Hex aHex, boolean aOnTile,
			Feature2 aSelectedFeature) {
		for (RevenueCenter tRevenueCenter : centers) {
			tRevenueCenter.draw (aGraphics, aXCenter, aYCenter, aTileOrient, aHex, aOnTile, aSelectedFeature);
		}
	}

	public void loadBaseStates (XMLNode aMapCellNode) {
		for (RevenueCenter tRevenueCenter : centers) {
			tRevenueCenter.loadBaseStates (aMapCellNode);
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
			if (tCenter.cityHasStation (aCorporationID)) {
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
			if (aCenterIndex == tSize) {
				tCenter = centers.get (tSize - 1);
			} else if (aCenterIndex < tSize) {
				tCenter = centers.get (aCenterIndex);
			} else {
				tCenter = RevenueCenter.NO_CENTER;
			}
		} else {
			tCenter = RevenueCenter.NO_CENTER;
		}

		return tCenter;
	}

	public RevenueCenter getRCWithBaseForCorp (Corporation aCorporation) {
		RevenueCenter tCenter;

		tCenter = RevenueCenter.NO_CENTER;
		for (RevenueCenter tRevenueCenter : centers) {
			if (tCenter == RevenueCenter.NO_CENTER) {
				if (tRevenueCenter.withBaseForCorp (aCorporation)) {
					if (! tRevenueCenter.isDestination ()) {
						tCenter = tRevenueCenter;
					}
				}
			}
		}

		return tCenter;
	}
	
	public RevenueCenter getRunThroughCenter () {
		RevenueCenter tCenter;
		
		tCenter = RevenueCenter.NO_CENTER;
		for (RevenueCenter tRevenueCenter : centers) {
			if (tRevenueCenter.isARunThroughCity ()) {
				tCenter = tRevenueCenter;
			}
		}
		
		return tCenter;
	}

	public RevenueCenter getRevenueCenterAtLocation (Location aLocation) {
		RevenueCenter tCenter = RevenueCenter.NO_CENTER;
		int tRCLocationInt;
		int tLocationInt;

		tCenter = RevenueCenter.NO_CENTER;
		tLocationInt = aLocation.getLocation ();
		if ((tLocationInt >= Location.DEAD_END0_LOC) && (tLocationInt <= Location.DEAD_END5_LOC)) {
			tLocationInt = Location.DEAD_END_LOC;
		}
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
		CityInfo tCityInfo = CityInfo.NO_CITY_INFO;

		for (RevenueCenter tCenter : centers) {
			tCityInfo = tCenter.getCityInfo ();
		}

		return tCityInfo;
	}

	public String getCityName () {
		CityInfo tCityInfo;
		String tCityName = NO_CITY_NAME;

		tCityInfo = getCityInfo ();
		if (tCityInfo != CityInfo.NO_CITY_INFO) {
			tCityName = tCityInfo.getName ();
		}

		return tCityName;
	}

	public RevenueCenter getRCContainingPoint (Point aPoint, Hex aHex, int XCenter, int YCenter, int aTileOrient) {
		RevenueCenter tRevenueCenter;

		tRevenueCenter = RevenueCenter.NO_CENTER;
		for (RevenueCenter tRC : centers) {
			if (tRC.containingPoint (aPoint, aHex, XCenter, YCenter, aTileOrient)) {
				tRevenueCenter = tRC;
			}
		}

		return tRevenueCenter;
	}

	public int getRevenueCenterID () {
		int tRevenueCenterID;

		tRevenueCenterID = RevenueCenter.NO_ID;
		for (RevenueCenter tRevenueCenter : centers) {
			tRevenueCenterID = tRevenueCenter.getID ();
		}

		return tRevenueCenterID;
	}

	public RevenueCenter getSelectedRevenueCenter (Feature2 aSelectedFeature2, int aTileOrient) {
		RevenueCenter tRevenueCenter;
		Location tLocation;

		tRevenueCenter = RevenueCenter.NO_CENTER;
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
		String tToolTip = GUI.NO_TOOL_TIP;
		String tSuperToolTip = GUI.NO_TOOL_TIP;
		String tCompanyHomes;
		String tCompanyDestinations;
		String tPreviousAbbrev;
		String tCorporationAbbrev;
		String tCityName;
		String tRCType;
		RevenueCenter tCenter;
		String tTokenAbbrev;

		tCenter = centers.get (0);
		if (tCenter != RevenueCenter.NO_CENTER) {
			tCityName = tCenter.getCityName ();
			if (tCityName != null) {
				if (tCityName.length () > 0) {
					tToolTip += "City: " + tCityName + "<br>";
				}
			}
		}
		tCompanyHomes = GUI.EMPTY_STRING;
		tCompanyDestinations = GUI.EMPTY_STRING;
		tPreviousAbbrev = null;
		tRCType = GUI.EMPTY_STRING;

		for (RevenueCenter tRC : centers) {
			if (tSuperToolTip.length () == 0) {
				tSuperToolTip = tRC.getToolTip ();
			}
			tTokenAbbrev = GUI.EMPTY_STRING;
			if (tRC.isTown ()) {
				if (tRCType.equals (GUI.EMPTY_STRING)) {
					tRCType = "Town";
				} else {
					tRCType = "Two Towns";
				}
			} else if (tRC.isCity ()) {
				tRCType = "City";
				tTokenAbbrev = tRC.getTokenToolTip ();
			}
			if (!("".equals (tTokenAbbrev))) {
				tToolTip += "Tokens: " + tTokenAbbrev + "<br>";
			}
			tCorporationAbbrev = tRC.getHomeCompanyAbbrev ();
			if (tCorporationAbbrev != null) {
				if (!tCorporationAbbrev.equals (tPreviousAbbrev)) {
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
				if (!tRC.isDestination ()) {
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
		System.out.println ("Revenue Centers Log Printout:");	// PRINTLOG method
		for (RevenueCenter tCenter : centers) {
			tCenter.printlog ();
		}
	}

	public boolean  setCorporationHome (Corporation aCorporation, Location aNewCityLocation) {
		boolean tCorporationHomeSet;
		
		tCorporationHomeSet = false;
		for (RevenueCenter tCenter : centers) {
			if (tCenter.isAtLocation (aNewCityLocation)) {
				if ((tCenter instanceof City) || (tCenter instanceof PrivateRailwayCenter)) {
					tCorporationHomeSet = tCenter.setCorporationHome (aCorporation);
				}
			}
		}
		
		return tCorporationHomeSet;
	}

	public boolean removeHome (Corporation aCorporation, Location aLocation) {
		boolean tHomeRemoved;
		
		tHomeRemoved = false;
		for (RevenueCenter tCenter : centers) {
			if (tCenter.isAtLocation (aLocation)) {
				if ((tCenter instanceof City) || (tCenter instanceof PrivateRailwayCenter)) {
					tHomeRemoved = tCenter.removeHome (aCorporation);
				}
			}
		}
		
		return tHomeRemoved;
	}

	public void copyCityInfo (Tile aTile) {
		RevenueCenter tDestinationCity;
		RevenueCenter tTileRevenueCenter;
		RevenueCenter tCenter;
		CityInfo tCityInfo;
		CityInfo tTileCityInfo;
		int tCenterCount;
		
		tCenterCount = centers.size ();
		for (int tCenterIndex = 0; tCenterIndex < tCenterCount; tCenterIndex++) {
			tCenter = centers.get (tCenterIndex);
			tCityInfo = tCenter.getCityInfo ();
			if (tCenter.isDestination ()) {
				tDestinationCity = tCenter.clone ();
				tDestinationCity.setTemporary (true);
				aTile.addCenter (0, tDestinationCity);
			} else {
				tTileRevenueCenter = aTile.getCenterAt (tCenterIndex);
				if (tTileRevenueCenter != RevenueCenter.NO_CENTER) {
					tTileCityInfo = tTileRevenueCenter.getCityInfo ();
					if (tTileCityInfo != CityInfo.NO_CITY_INFO) {
						tTileCityInfo.copyCityInfo (tCityInfo);
						tTileCityInfo.setRevenueCenter (tTileRevenueCenter);
					}
				}
			}
		}
	}

	public void setCityInfo (CityInfo aCityInfo) {

		if (aCityInfo != CityInfo.NO_CITY_INFO) {
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

	public RevenueCenter setupRevenueCenter (int aType, int aID, int aLocation, String aName, int aValue,
			TileType aTileType) {
		RevenueCenter tRevenueCenter;
		RevenueCenterType tRevenueCenterType = new RevenueCenterType (aType);
		int tStationCount = tRevenueCenterType.getStationCount ();

		if (tRevenueCenterType.isDotTown ()) {
			tRevenueCenter = new Town (aType, aID, aLocation, aName, aValue, aTileType);
		} else if (tRevenueCenterType.isTown ()) {
			tRevenueCenter = new TownTick (aType, aID, aLocation, aName, aValue, aTileType);
		} else if (tRevenueCenterType.isCity ()) {
			tRevenueCenter = new City (aType, tStationCount, aID, aLocation, aName, aValue, aTileType);
		} else {
			tRevenueCenter = new City (aType, tStationCount, aID, aLocation, aName, aValue, aTileType);
		}

		return (tRevenueCenter);
	}

	public int size () {
		return centers.size ();
	}

	@Override
	public String toString () {
		return centers.toString ();
	}

	public boolean hasTown () {
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
	
	public void removeMapTokens (TokenCompany aTokenCompany, String aMapCellID, 
								CloseCompanyAction aCloseCompanyAction) {
		int tTokenCompanyID;
		City tCity;
		
		tTokenCompanyID = aTokenCompany.getID ();
		for (RevenueCenter tRevenueCenter : centers) {
			if (tRevenueCenter.cityHasStation (tTokenCompanyID)) {
				tCity = (City) tRevenueCenter;
				if (tCity.withBaseForCorp (aTokenCompany)) {
					tCity.returnStation (aTokenCompany);
				}
//				aCloseCompanyAction.addRemoveMapTokenEffect
			}
		}
	}

	public void removeMapToken (TokenCompany aFoldingCompany, MapCell aMapCell, Tile aTile,
					ReplaceTokenAction aReplaceTokenAction, int aRevenueCenterIndex, City aCity) {
		int tTokenIndex;
		MapToken tMapToken;
		TokenType tTokenType;
		
		tMapToken = aCity.returnStation (aFoldingCompany);
		tTokenType = tMapToken.getTokenType ();
		tTokenIndex = aFoldingCompany.getTokenIndex (tMapToken);
		aReplaceTokenAction.addRemoveTokenEffect (aFoldingCompany, aMapCell, aTile, aRevenueCenterIndex,
				tTokenType, tTokenIndex);
	}
	
	public void clearBaseCorporation (TokenCompany aFoldingCompany, MapCell aMapCell, Tile aTile,
					ReplaceTokenAction aReplaceTokenAction, int aRevenueCenterIndex, City aCity) {
		boolean tCorporationCleared;
		
		tCorporationCleared = clearBaseCorporation (aCity, aFoldingCompany);
		if (tCorporationCleared) {
			aReplaceTokenAction.addClearCorporationEffect (aFoldingCompany, aMapCell, aTile, aRevenueCenterIndex);
		}
	}
	
	public boolean clearBaseCorporation (City aCity, TokenCompany aCorporation) {
		boolean tCorporationCleared;
		
		tCorporationCleared = aCity.clearCorporation (aCorporation);

		return tCorporationCleared;
	}
	
	public boolean clearHomeCorporation (TokenCompany aCorporation) {
		int tRevenueCenterIndex;
		int tRevenueCenterCount;
		RevenueCenter tRevenueCenter;
		City tCity;
		boolean tCleared;
		
		tRevenueCenterCount = centers.size ();
		tCleared = false;
		for (tRevenueCenterIndex = 0; tRevenueCenterIndex < tRevenueCenterCount; tRevenueCenterIndex++) {
			tRevenueCenter = centers.get (tRevenueCenterIndex);
			tCity = (City) tRevenueCenter;
			tCleared = clearBaseCorporation (tCity, aCorporation);
		}

		return tCleared;
	}
			
	public void layMapToken (TokenCompany aFoldingCompany, MapToken aMapToken, MapCell aMapCell, Tile aTile,
			ReplaceTokenAction aReplaceTokenAction, int aRevenueCenterIndex, City aCity) {
		TokenType tTokenType;
		int tTokenIndex;
		TokenCompany tTokenCompany;
		
		aCity.placeStation (aMapToken);
		tTokenType = aMapToken.getTokenType ();
		tTokenCompany = aMapToken.getTokenCompany ();
		tTokenIndex = tTokenCompany.getTokenIndex (aMapToken);
		aReplaceTokenAction.addLayTokenEffect (tTokenCompany, aMapCell, aTile, aRevenueCenterIndex,
							tTokenType, tTokenIndex);
	}
	
	public void replaceMapToken (String [] aMapCellInfo, MapToken aNewMapToken, TokenCompany aFoldingCompany, 
								MapCell aMapCell, Tile aTile, ReplaceTokenAction aReplaceTokenAction) {
		int tTokenCompanyID;
		int tNewTokenCompanyID;
		int tRevenueCenterCount;
		int tRevenueCenterIndex;
		boolean tReplaceToken;
		RevenueCenter tRevenueCenter;
		City tCity;
		
		tTokenCompanyID = aFoldingCompany.getID ();
		tNewTokenCompanyID = aNewMapToken.getCorporationID ();
		tRevenueCenterCount = centers.size ();
		tReplaceToken = true;
		if (aMapCellInfo.length > 4) {
			if (aMapCellInfo [4].equals ("X")) {
				tReplaceToken = false;
			}
		}

		for (tRevenueCenterIndex = 0; tRevenueCenterIndex < tRevenueCenterCount; tRevenueCenterIndex++) {
			tRevenueCenter = centers.get (tRevenueCenterIndex);
			if (tRevenueCenter.cityHasStation (tTokenCompanyID)) {
				tCity = (City) tRevenueCenter;
				if (tCity.cityHasStation (tTokenCompanyID)) {
					removeMapToken (aFoldingCompany, aMapCell, aTile, aReplaceTokenAction, tRevenueCenterIndex, tCity);
					clearBaseCorporation (aFoldingCompany, aMapCell, aTile, aReplaceTokenAction, 
										tRevenueCenterIndex, tCity);
					if (tReplaceToken) {
						if (! this.hasStation (tNewTokenCompanyID)) {
							aNewMapToken.setConnectedSides (aMapCell, tCity.getLocation ());
							layMapToken (aFoldingCompany, aNewMapToken, aMapCell, aTile, aReplaceTokenAction,
										tRevenueCenterIndex, tCity);
						} else {
							System.out.println ("Company ID " + tNewTokenCompanyID + " Already has a token on the Tile");
						}
					}
				}
			}
		}
	}
	
	/**
	 * Remove One Destination found at the specified Location
	 * 
	 * @param aDestinationLocation The Location that needs to have the Destination removed
	 * @param aAbbrev The abbreviation of the company that has the destination to be removed
	 */
	public void removeDestination (Location aDestinationLocation, MapCell aMapCell, ShareCompany aShareCompany, 
						RemoveDestinationsAction aRemoveDestinationsAction) {
		int tRevenueCenterCount;
		int tRevenueCenterIndex;
		int tRemoveCenterIndex;
		int NO_MATCH_FOUND = -1;
		RevenueCenter tRevenueCenter;

		tRevenueCenterCount = centers.size ();
		tRemoveCenterIndex = NO_MATCH_FOUND;
		for (tRevenueCenterIndex = 0; tRevenueCenterIndex < tRevenueCenterCount; tRevenueCenterIndex++) {
			tRevenueCenter = centers.get (tRevenueCenterIndex);
			if (tRevenueCenter.isDestination ()) {
				if (tRevenueCenter.getLocation ().isSameLocationValue (aDestinationLocation)) {
					tRemoveCenterIndex = tRevenueCenterIndex;
				}
			}
		}
		if (tRemoveCenterIndex != NO_MATCH_FOUND) {
			centers.remove (tRemoveCenterIndex);
			aRemoveDestinationsAction.addRemoveDestinationEffect (aShareCompany, aMapCell, aDestinationLocation);
		}
	}
	
	public void replaceDestination (Location aDestinationLocation, MapCell aMapCell) {
		City tDestinationCity;
		int tLocation;
		
		tLocation = aDestinationLocation.getLocation ();
		tDestinationCity = new City (RevenueCenterType.DESTINATION_CITY, 1, RevenueCenter.NO_ID, tLocation,
				RevenueCenter.NO_NAME, RevenueCenter.NO_VALUE, TileType.NO_TILE_TYPE);
		tDestinationCity.setMapCell (aMapCell);
		add (tDestinationCity);
	}
}
