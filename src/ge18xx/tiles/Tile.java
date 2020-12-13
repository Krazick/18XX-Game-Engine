package ge18xx.tiles;

//
//  Tile.java
//  Java_18XX
//
//  Created by Mark Smith on 11/4/06.
//  Copyright (c) 2006 __MyCompanyName__. All rights reserved.
//  A simple Java applet
//

import ge18xx.center.Centers;
import ge18xx.center.City;
import ge18xx.center.CityInfo;
import ge18xx.center.RevenueCenter;
import ge18xx.center.RevenueCenterType;
import ge18xx.center.Town;
import ge18xx.center.TownTick;
import ge18xx.company.Corporation;
import ge18xx.company.MapToken;
import ge18xx.company.Token;
import ge18xx.company.TokenCompany;
import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutine3I;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import java.awt.*;

public class Tile implements Comparable<Object>, Cloneable {
	public final static AttributeName AN_NUMBER = new AttributeName ("number");
	public final static AttributeName AN_TILE_NUMBER = new AttributeName ("tileNumber");
	public final static AttributeName AN_TYPE = new AttributeName ("type");
	public final static AttributeName AN_FIXED = new AttributeName ("fixed");
	public final static ElementName EN_TILE = new ElementName ("Tile");
	public static final Tile NO_TILE = null;
	public static final String NO_BASES = "";
	public static final String NO_TOKENS = "";
    public static final int NOT_A_TILE = 0;
    static final int NO_TRACK = 0;
	static final int NO_STATION = 0;
	static final int NO_RC_ID = -1;
	static final boolean ON_TILE = true;
	int number;
	int XCenter;
	int YCenter;
	TileName name;
	TileType type;
	Tracks tracks;
	Centers centers;
	int trainUsingSide [] = new int [6];		// Train Number using the side;
	
	public Tile () {
		this (NOT_A_TILE, TileType.NO_TYPE);
	}

	public Tile (int aNumber, int aType) {
		this (aNumber, TileName.NO_NAME, aType);
	}
	
	public Tile (int aNumber, String aName, int aType) {
		centers = new Centers ();
		setValues (aNumber, aName, aType);
	}
	
	public Tile (Tile aTile) {
		int tLocation;
		Centers tCenters;
		
		setValues (aTile.getNumber (), aTile.getName (), aTile.getTypeInt ());
		tLocation = aTile.getTileNameLocation ();
		setTileNameLocation (tLocation);
		tCenters = aTile.getCenters ();
		centers = new Centers (tCenters, this);
		tracks = aTile.tracks.clone ();
	}
	
	public Tile (XMLNode aNode) {
		int tNumber, tTileTypeID;
		boolean tFixed;
		String tType;
		XMLNodeList tXMLNodeList;
		
		tracks = new Tracks ();
		centers = new Centers ();
		tNumber = aNode.getThisIntAttribute (AN_NUMBER);
		tType = aNode.getThisAttribute (AN_TYPE);
		tFixed = aNode.getThisBooleanAttribute (AN_FIXED);
		name = null;
		tTileTypeID = TileType.getTypeFromName (tType);
		tXMLNodeList = new XMLNodeList (tileParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aNode, TileName.EN_TILE_NAME, Track.EN_TRACK, RevenueCenter.EN_REVENUE_CENTER);
		setValues (tNumber, tTileTypeID);
		centers.setTileType (type);
		type.setFixed (tFixed);
	}
	
	public Track getTrackFromSide (int aSideLocation) {
		Track tTrack;
		
		tTrack = tracks.getTrackFromSide (aSideLocation);
		
		return tTrack;
	}
	
	public void clearTrainUsingSide () {
		for (int tSideIndex = 0; tSideIndex < 6; tSideIndex++) {
			trainUsingSide [tSideIndex] = 0;
		}
	}
	
	public boolean isTrainUsingSide (int aSideIndex) {
		boolean tIsTrainUsingSide = false;
		
		tIsTrainUsingSide = (trainUsingSide [aSideIndex] > 0);
		
		return tIsTrainUsingSide;
	}
	
	public boolean addTrack (Track aTrack) {
		return tracks.add (aTrack);
	}
	
	public boolean addTrack (int aEnter, int aExit, int aGaugeType) {
		Gauge baseGauge;
		Gauge tGauge = new Gauge (aGaugeType);
		
		baseGauge = tGauge.getBaseGauge ();
		if (baseGauge != null) {
			addTrack (new Track (aEnter, aExit, baseGauge));
		}
		
		return addTrack (new Track (aEnter, aExit, tGauge));
	}
	
	public void appendTokensState (XMLDocument aXMLDocument, XMLElement aMapCellElement) {
		if (centers != null) {
			centers.appendTokensState (aXMLDocument, aMapCellElement);
		}
	}
	
	public boolean canAllTracksExit (MapCell aThisMapCell, int aTileOrient) {
		return tracks.canAllTracksExit (aThisMapCell, aTileOrient);
	}
	
	public boolean canDeadEndTrack () {
		return type.canDeadEndTrack ();
	}
	
	public boolean cityOnTile () {
		RevenueCenter rc = getRevenueCenter (0);
		
		return (rc.canPlaceStation ());
	}
	
	public boolean cityOrTownOnTile () {
		RevenueCenter rc = getRevenueCenter (0);
		
		return (rc.cityOrTown ());
	}
	
	public void clearAllCityInfoCorporations () {
		centers.clearAllCityInfoCorporations ();
	}
	
	public void clearAllCityInfoMapCells () {
		centers.clearAllCityInfoMapCells ();
	}
	
	public void clearAllCityInfoRevenueCenters () {
		centers.clearAllCityInfoRevenueCenters ();
	}
	
	public void clearAllStations () {
		centers.clearAllStations ();
	}
	
	public void clearStation (int aCorporationId) {
		centers.clearStation (aCorporationId);
	}
	
	public void clearCorporation (Corporation aCorporation) {
		centers.clearCityInfoCorporation (aCorporation);
	}
	
	public MapToken getMapTokenFor (int aCorporationID) {
		return centers.getMapTokenFor (aCorporationID);
	}
	
	public Tile clone () {
		try {
			Tile tTile = (Tile) super.clone ();
			tTile.number = number;
			tTile.XCenter = XCenter;
			tTile.YCenter = YCenter;
			if (name == null) {
				tTile.name = null;
			} else {
				tTile.name = name.clone ();
			}
			tTile.type = type.clone ();
			tTile.tracks = tracks.clone ();
			tTile.centers = centers.clone ();
			
			return tTile;
		} catch (CloneNotSupportedException e) {
			throw new Error ("Tile.clone Not Supported Exception");
		}
	}
	
	public int compareTo (Object oTile) throws ClassCastException {
		if (!(oTile instanceof Tile)) {
			throw new ClassCastException ("A Tile object expected.");
		}
		int oNumber = ((Tile) oTile).getNumber ();
		
		return this.number - oNumber;
	}
	
	public void returnTokens () {
		int tCityCenterCount, tCityCenterIndex, tStationIndex;
		RevenueCenter tRevenueCenter;
		City tCity;
		MapToken tMapToken;
		TokenCompany tTokenCompany;
		
		tCityCenterCount = getCityCenterCount ();
		if (tCityCenterCount > 0) {
			for (tCityCenterIndex = 0; tCityCenterIndex < tCityCenterCount; tCityCenterIndex++) {
				tRevenueCenter = getRevenueCenter (tCityCenterIndex);
				if (tRevenueCenter.isCity ()) {
					tCity = (City) getRevenueCenter (tCityCenterIndex);
					if (tCity.hasToken ()) {
						for (tStationIndex = 0; tStationIndex < tCity.getStationCount (); tStationIndex++) {
							tMapToken = tCity.getToken (tStationIndex);
							if (tMapToken != City.NO_STATION) {
								tTokenCompany = tMapToken.getWhichCompany ();
								System.out.println ("Returning Token for " + tTokenCompany.getAbbrev () + " count before " + tTokenCompany.getTokenCount ());
								tTokenCompany.addMapToken (tMapToken);
								System.out.println ("Returning Token for " + tTokenCompany.getAbbrev () + " count now " + tTokenCompany.getTokenCount ());
							}
						}
					}
				}
			}
		}

	}
	
	public String getCorporationBases () {
		String tCorporationBases = NO_BASES, tCorporationBase;
		int tCityCenterCount, tCityIndex;
		RevenueCenter tRevenueCenter;
		City tCity;
		String tAbbrev;
		
		tCityCenterCount = getCityCenterCount ();
		if (tCityCenterCount > 0) {
			for (tCityIndex = 0; tCityIndex < tCityCenterCount; tCityIndex++) {
				tRevenueCenter = getRevenueCenter (tCityIndex);
				if (tRevenueCenter.isCity ()) {
					tCity = (City) getRevenueCenter (tCityIndex);
					if (tCity.isCorporationBase ()) {
						tAbbrev = tCity.getHomeCompanyAbbrev ();
						tCorporationBase = tAbbrev + "," + tCityIndex;
						if (!(tCorporationBases.equals (NO_BASES))) {
							tCorporationBases += ";";
						}
						tCorporationBases += tCorporationBase;
					}
				}
			}
		}
		
		return tCorporationBases;
	}

	public City getCityAt (int aCityIndex) {
		City tCity = City.NO_CITY;
		int tCityCenterCount;
		
		tCityCenterCount = getCityCenterCount ();
		if (tCityCenterCount > 0) {
			tCity = (City) getRevenueCenter (aCityIndex);
		}
		
		return tCity;
	}
	
	public String getPlacedTokens () {
		String tPlacedTokens = NO_TOKENS, tAPlacedToken;
		int tCityCenterCount, tCityCenterIndex, tStationIndex;
		RevenueCenter tRevenueCenter;
		City tCity;
		MapToken tMapToken;
		String tAbbrev;
		
		tCityCenterCount = getCityCenterCount ();
		if (tCityCenterCount > 0) {
			for (tCityCenterIndex = 0; tCityCenterIndex < tCityCenterCount; tCityCenterIndex++) {
				tRevenueCenter = getRevenueCenter (tCityCenterIndex);
				if (tRevenueCenter.isCity ()) {
					tCity = (City) getRevenueCenter (tCityCenterIndex);
					if (tCity.hasToken ()) {
						for (tStationIndex = 0; tStationIndex < tCity.getStationCount (); tStationIndex++) {
							tMapToken = tCity.getToken (tStationIndex);
							if (tMapToken != City.NO_STATION) {
								tAbbrev = tMapToken.getCorporationAbbrev ();
								tAPlacedToken = tAbbrev + "," + tStationIndex + "," + tCityCenterIndex;
								if (!(tPlacedTokens.equals (NO_TOKENS))) {
									tPlacedTokens += ";";
								}
								tPlacedTokens += tAPlacedToken;
							}
						}
					}
				}
			}
		}
		
		return tPlacedTokens;
	}
		
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tElement;
		XMLElement tTrackElement;
		XMLElement tCenterElement;
		XMLElement tNameElement;
		int tIndex;
		int tTrackCount;
		int tCenterCount;
		
		tElement = aXMLDocument.createElement (EN_TILE);
		tElement.setAttribute (AN_NUMBER, number);
		tElement.setAttribute (AN_TYPE, getTypeName ());
		tNameElement = name.createElement (aXMLDocument);
		if (tNameElement != null) {
			tElement.appendChild (tNameElement);
		}
		
		tTrackCount = tracks.size ();
		if (tTrackCount > 0) {
			for (tIndex = 0; tIndex < tTrackCount; tIndex++) {
				tTrackElement = tracks.createElement (aXMLDocument, tIndex);
				tElement.appendChild (tTrackElement);
			}
		}
		
		tCenterCount = centers.size ();
		if (tCenterCount > 0) {
			for (tIndex = 0; tIndex < tCenterCount; tIndex++) {
				tCenterElement = centers.createElement (aXMLDocument, tIndex);
				tElement.appendChild (tCenterElement);
			}
		}
		
		return tElement;
	}
	
	public void drawName (Graphics g, int Xc, int Yc, int aTileOrient, Hex aHex) {
		if (name != null) {
			name.draw (g, Xc, Yc, aTileOrient, aHex);
		}
	}
	
	public boolean isFixedTile () {
		return type.isFixed ();
	}
	
	public boolean isTrackOnSide (int aSide) {
		return tracks.isTrackOnSide (aSide);
	}
	
	public boolean isTrackToSide (int aSide) {
		return tracks.isTrackToSide (aSide);
	}
	
	public RevenueCenter getCenterAtLocation (Location aLocation) {
		return centers.getCenterAtLocation (aLocation);
	}
	
	public RevenueCenter getCenterAtLocation (int aLocation) {
		return centers.getCenterAtLocation (aLocation);
	}
	
	public int getCenterCount () {
		return centers.getCenterCount ();
	}
	
	public int getCityCenterCount () {
		return centers.getCityCenterCount ();
	}
	
	public Centers getCenters () {
		return centers;
	}
	
	public Color getColor () {
		return (type.getColor());
	}
	
	public String getName () {
		if (name == null) {
			return TileName.NO_NAME2;
		} else {
			return (name.getName ());
		}
	}
	
	public int getNumber () {
		return (number);
	}
	
	public String getNumberToString () {
		return (new Integer (number).toString ());
	}
	
	public RevenueCenter getRCContainingPoint (Point aPoint, Hex aHex, int XCenter, int YCenter, int aTileOrient) {
		return centers.getRCContainingPoint (aPoint, aHex, XCenter, YCenter, aTileOrient);
	}
	
	public int getRevenueCenterCount () {
		return centers.size ();
	}
	
	public RevenueCenter getRevenueCenter (int aCenterIndex) {
		return centers.get (aCenterIndex);
	}
	
	public String getRevenueValue () {
		RevenueCenter tRevenueCenter;
		int tRCIndex;
		String tRevenueValue = "";
		
		// TODO: For Red-Off Board tiles, check the Phase, and retrieve the correct Revenue Value from the choices
		for (tRCIndex = 0; tRCIndex < getRevenueCenterCount (); tRCIndex++) {
			if (tRevenueValue.equals ("")) {
				tRevenueCenter = getRevenueCenter (tRCIndex);
				if (tRevenueCenter != null) {
					tRevenueValue = tRevenueCenter.getRevenueToString ();
				}
			}
		}
		
		return tRevenueValue;
	}
	
	public RevenueCenter getSelectedRevenueCenter (Feature2 aSelectedFeature2, int aTileOrient) {
		RevenueCenter tRevenueCenter;
		
		if (centers.size () > 0) {
			tRevenueCenter = centers.getSelectedRevenueCenter (aSelectedFeature2, aTileOrient);
		} else {
			tRevenueCenter = null;
		}
		
		return tRevenueCenter;
	}
	
	public int getTileNameLocation () {
		int tLocation = Location.NO_LOCATION;
		
		if (name != null) {
			tLocation = name.getLocationToInt ();
		}
		
		return tLocation;
	}
	
	public TileName getTileName () {
		return name;
	}
	
	public int getTileType () {
		return type.getType ();
	}
	
	public TileType getTheTileType () {
		return type;
	}
	
	public String getToolTip () {
		String tTip = "";
		
		tTip += "Tile: " + getTypeName () + " " + getNumberToString () + "<br>";
		tTip += "Revenue: " + getRevenueValue () + "<br>";
		if (centers != null) {
			if (centers.size () > 0) {
				tTip += centers.getToolTip ();
			}
		}
		tTip += tracks.getToolTip ();
		
		return tTip;
	}
	
	public TileType getType () {
		return type;
	}
	
	public int getTypeCount () {
		return centers.getTypeCount ();
	}
	
	public int getTypeInt () {
		return (type.getType ());
	}
	
	public String getTypeName () {
		return (type.getName());
	}
	
	public int getX () {
		return XCenter;
	}
	
	public int getY () {
		return YCenter;
	}
	
	public boolean hasAnyStation () {
		if (centers != null) {
			return centers.hasAnyStation ();
		} else {
			return false;
		}
	}
	
	public boolean hasStation (Token aToken) {
		if (centers != null) {
			return centers.hasStation (aToken);
		} else {
			return false;
		}
	}
	
	public boolean hasStation (int aCorpID) {
		if (centers != null) {
			return centers.hasStation (aCorpID);
		} else {
			return false;
		}
	}
	
	public int getStationIndex (int aCorpID) {
		if (centers != null)
			return centers.getStationIndex (aCorpID);
		else {
			return Centers.UNSPECIFIED_ID;
		}
	}
	
	public void loadStationsStates (XMLNode aMapCellNode) {
		if (centers != null) {
			centers.loadStationsStates (aMapCellNode);
		}
	}

	public void paintComponent (Graphics g, int Xc, int Yc, int aTileOrient, Hex aHex, Feature2 aSelectedFeature) {
		int tOldX, tOldY;
		
		tOldX = getX ();
		tOldY = getY ();
		setXY (Xc, Yc);
		paintComponent (g, aTileOrient, aHex, aSelectedFeature);
		setXY (tOldX, tOldY);
	}
	
	public void paintComponent (Graphics g, Hex aHex) {
		paintComponent (g, 0, aHex, new Feature2 ());
	}
	
	public void paintComponent (Graphics g, int aTileOrient, Hex aHex, Feature2 aSelectedFeature) {
		Color tHexColor = type.getColor ();
		aHex.paintHex (g, XCenter, YCenter, tHexColor);
		
		tracks.draw (g, XCenter, YCenter, aTileOrient, aHex, tHexColor, aSelectedFeature);
		centers.draw (g, XCenter, YCenter, aTileOrient, aHex, ON_TILE, aSelectedFeature);

		if (name != null) {
			name.draw (g, XCenter, YCenter, aTileOrient, aHex);
		}
	}
	
	public void printlog () {
		System.out.println ("Tile Number " + number);
		if (name != null) {
			name.printlog ();
		}
		if (type != null) {
			type.printlog ();
		}
		System.out.print ("Tile ");
		tracks.printlog ();
		System.out.print ("Tile ");
		centers.printlog ();
	}
	
	public void setCorporationBase (Corporation aBaseCorporation, Location aNewCityLocation) {
		centers.setCorporationBase (aBaseCorporation, aNewCityLocation);
	}
	
	public void setCityInfo (CityInfo aCityInfo) {
		if (aCityInfo != null) {
			centers.setCityInfo (aCityInfo);
		}
	}
	
	public void setMapCell (MapCell aMapCell) {
		centers.setMapCell (aMapCell);
	}
	
	public void setRevenueCenters (Centers aCenters) {
		int tCenterCount, tCenterIndex;
		RevenueCenter tRevenueCenter, tTileRevenueCenter;
		Corporation tCorporation;
		
		tCenterCount = aCenters.size ();
		if (tCenterCount > 0) {
			for (tCenterIndex = 0; tCenterIndex < tCenterCount; tCenterIndex++) {
				tRevenueCenter = getRevenueCenter (tCenterIndex);
				tCorporation = tRevenueCenter.getCorporation ();
				tTileRevenueCenter = getRevenueCenter (tCenterIndex);
				tTileRevenueCenter.setCorporation (tCorporation);
			}
		}
	}

	public boolean setRevenueLocation (int aCenterID, int aLocation) {
		RevenueCenter tRevenueCenter;
		boolean tAdded;
		
		if (centers.isEmpty ()) {
			tAdded = false;
		} else {
			tRevenueCenter = getRevenueCenter (aCenterID);
			tRevenueCenter.setRevenueLocation (aLocation);
			tAdded = true;
		}
		
		return tAdded;
	}

	public boolean setStation (Token aStation) {
		return (setStation (aStation, 0));
	}
	
	public boolean setStation (Token aStation, int aIndex) {
		RevenueCenter rc = getRevenueCenter (aIndex);
		
		return (rc.setStation (aStation));
	}
	
	public void setTileNameLocation (int aLocation) {
		if (name != null) {
			name.setLocation (aLocation);
		}
	}
	
	public void setValues (int aNumber, String aName, int aType) {
		tracks = new Tracks ();
		setValues (aNumber, aType);
		name = new TileName (aName);
		clearTrainUsingSide ();
	}
	
	public void setValues (int aNumber, int aType) {
		number = aNumber;
		type = new TileType (aType, false);
	}
	
	public void setXY (int aXc, int aYc) {
		XCenter = aXc;
		YCenter = aYc;
	}
	
	public boolean stationsOnTile () {
		return hasAnyStation ();
	}
	
	ParsingRoutine3I tileParsingRoutine  = new ParsingRoutine3I ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			name = new TileName (aChildNode);
		}

		public void foundItemMatchKey2 (XMLNode aChildNode) {
			Track tSegment;
			
			tSegment = new Track (aChildNode);
			tracks.add (tSegment);
		}
		
		public void foundItemMatchKey3 (XMLNode aChildNode) {
			String tRCType;
			RevenueCenter tCenter;
			
			tRCType = aChildNode.getThisAttribute (AN_TYPE);
			if (RevenueCenterType.isDotTown (tRCType)) {
				tCenter = new Town (aChildNode);
			} else if (RevenueCenterType.isTown (tRCType)) {
				tCenter = new TownTick (aChildNode);
			} else if (RevenueCenterType.isCity (tRCType)) {
				tCenter = new City (aChildNode);
			} else {
				tCenter = new City (aChildNode);
			}
			centers.add (tCenter);
		}
	};
	
	public void swapTokens (MapCell aMapCell) {
		MapToken tMapToken0, tMapToken1;
		City tCity0, tCity1;
		
		if (getCenterCount () == 2) {
			if (hasAnyStation ()) {
				tCity0 = (City) centers.get (0);
				tCity1 = (City) centers.get (1);
				tMapToken0 = tCity0.getToken (0);
				tMapToken1 = tCity1.getToken (0);
				if (tMapToken0 != City.NO_STATION) {
					tMapToken0.printlog ();
				}
				if (tMapToken1 != City.NO_STATION) { 
					tMapToken1.printlog ();
				}
				centers.clearAllStations ();
				if (tMapToken1 != City.NO_STATION) {
					tCity0.placeStation (tMapToken1, aMapCell);
				}
				if (tMapToken0 != City.NO_STATION) { 
					tCity1.placeStation (tMapToken0, aMapCell);
				}
			}
		}
	}

	public boolean hasTown () {
		return centers.hasTown ();
	}


	public Track getConnectingTrackBetween (Location aThisLocation, Location aThatLocation) {
		Track tFoundTrack = Track.NO_TRACK;
		int tTrackIndex;
		Track tTrack;
		Location tEnterLocation, tExitLocation;
		
		for (tTrackIndex = 0; tTrackIndex < tracks.size (); tTrackIndex++) {
			tTrack = tracks.get (tTrackIndex);
			tEnterLocation = tTrack.getEnterLocation ();
			tExitLocation = tTrack.getExitLocation ();
			if ((tEnterLocation.equals (aThisLocation)) && 
				(tExitLocation.equals (aThatLocation))) {
				tFoundTrack = tTrack;
			} else if ((tEnterLocation.equals (aThatLocation)) && 
					(tExitLocation.equals (aThisLocation))) {
				tFoundTrack = tTrack;
			}
		}
		
		return tFoundTrack;
	}
	
	public boolean hasConnectingTrackBetween (Location aThisLocation, Location aThatLocation) {
		boolean tHasConnectingTrackBetween = false;
		Track tTrack;
		
		tTrack = getConnectingTrackBetween (aThisLocation, aThatLocation);
		if (tTrack == Track.NO_TRACK)  {
			tHasConnectingTrackBetween = true;
		}
		
		return tHasConnectingTrackBetween;
	}

	public boolean isSideUsed (Location aSide) {
		boolean tIsSideUsed = false;
		Track tTrack;
		
		for (int tTrackIndex = 0; tTrackIndex < tracks.size (); tTrackIndex++) {
			tTrack = tracks.get (tTrackIndex);
			if (tTrack.isTrackToSide (aSide.getLocation ())) {
				if (tTrack.isTrackUsed ()) {
					tIsSideUsed = true;
				}
			}
		}
		
		return tIsSideUsed;
	}

	public Gauge getGauge (Location aThisLocation, Location aThatLocation) {
		Gauge tGauge = new Gauge ();
		Track tTrack;
		
		tTrack = getConnectingTrackBetween (aThisLocation, aThatLocation);
			if (tTrack == Track.NO_TRACK)  {
				tGauge = tTrack.getGauge ();
			}

		return tGauge;
	}
}
