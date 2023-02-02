package ge18xx.tiles;

import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Point;

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
import ge18xx.map.Edge;
import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.map.MapGraph;
import ge18xx.map.Vertex;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutine3I;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

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
		int tNumber;
		int tTileTypeID;
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

	public Track getTrackFromStartToEnd (int aStartLocation, int aEndLocation) {
		Track tTrack;

		tTrack = tracks.getTrackFromStartToEnd (aStartLocation, aEndLocation);

		return tTrack;
	}

	public Track getTrackFromSide (int aSideLocation) {
		Track tTrack;

		tTrack = tracks.getTrackFromSide (aSideLocation);

		return tTrack;
	}

	public boolean addCenter (RevenueCenter aRevenueCenter) {
		return (centers.add (aRevenueCenter));
	}
	
	public boolean removeCenter (RevenueCenter aRevenueCenter) {
		return (centers.remove (aRevenueCenter));
	}
	
	public void removeTemporaryCenters () {
		centers.removeTemporaryCenters ();
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
		if (hasCenters ()) {
			centers.appendTokensState (aXMLDocument, aMapCellElement);
		}
	}

	public void appendCorporationBases (XMLDocument aXMLDocument, XMLElement aMapCellElement) {
		if (hasCenters ()) {
			centers.appendCorporationBases (aXMLDocument, aMapCellElement);
		}
	}

	public boolean canAllTracksExit (MapCell aThisMapCell, int aTileOrient) {
		return tracks.canAllTracksExit (aThisMapCell, aTileOrient);
	}

	/**
	 * Clear the Specified Train from all tracks on the Tile
	 *
	 * @param aTrainNumber The Train Number to clear
	 */
	public void clearTrain (int aTrainNumber) {
		tracks.clearTrain (aTrainNumber);
	}

	/**
	 * Clear All Trains from every Track on the Tile
	 *
	 */
	public void clearAllTrains () {
		tracks.clearAllTrains ();
	}

	public boolean canDeadEndTrack () {
		return type.canDeadEndTrack ();
	}

	public boolean cityOnTile () {
		RevenueCenter rc = getRevenueCenter (0);

		return rc.canPlaceStation ();
	}

	public boolean cityOrTownOnTile () {
		RevenueCenter rc = getRevenueCenter (0);

		return rc.cityOrTown ();
	}

	public void clearAll () {
		clearAllCityInfoCorporations ();
		clearAllCityInfoMapCells ();
		clearAllCityInfoRevenueCenters ();
		clearAllStations ();
		clearAllTrains ();
	}

	private void clearAllCityInfoCorporations () {
		centers.clearAllCityInfoCorporations ();
	}

	private void clearAllCityInfoMapCells () {
		centers.clearAllCityInfoMapCells ();
	}

	private void clearAllCityInfoRevenueCenters () {
		centers.clearAllCityInfoRevenueCenters ();
	}

	public void clearAllStations () {
		centers.clearAllStations ();
	}

	public void returnStation (TokenCompany aTokenCompany) {
		centers.returnStation (aTokenCompany);
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

	@Override
	public Tile clone () {
		try {
			Tile tTile = (Tile) super.clone ();
			tTile.number = number;
			tTile.XCenter = XCenter;
			tTile.YCenter = YCenter;
			if (name == TileName.NO_TILE_NAME) {
				tTile.name = TileName.NO_TILE_NAME;
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

	@Override
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
					if (tCity.cityHasAnyStation ()) {
						for (tStationIndex = 0; tStationIndex < tCity.getStationCount (); tStationIndex++) {
							tMapToken = tCity.getToken (tStationIndex);
							if (tMapToken != MapToken.NO_MAP_TOKEN) {
								tTokenCompany = tMapToken.getWhichCompany ();
								System.out.println ("Returning Token for " + tTokenCompany.getAbbrev ()
										+ " count before " + tTokenCompany.getTokenCount ());
//								tTokenCompany.addMapToken (tMapToken);
								tTokenCompany.setTokenUsed (tMapToken, false);
								System.out.println ("Returning Token for " + tTokenCompany.getAbbrev () + " count now "
										+ tTokenCompany.getTokenCount ());
							}
						}
					}
				}
			}
		}

	}

	public int getCorporationHomeCount () {
		int tBaseCount = 0;
		int tCityCenterCount, tCityIndex;
		RevenueCenter tRevenueCenter;
		City tCity;

		tCityCenterCount = getCityCenterCount ();
		if (tCityCenterCount > 0) {
			for (tCityIndex = 0; tCityIndex < tCityCenterCount; tCityIndex++) {
				tRevenueCenter = getRevenueCenter (tCityIndex);
				if (tRevenueCenter.isCity ()) {
					tCity = (City) getRevenueCenter (tCityIndex);
					if (tCity.isCorporationBase ()) {
						tBaseCount++;
					}
				}
			}
		}

		return tBaseCount;
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
		int tCityCenterCount;
		int tCityCenterIndex;
		int tStationIndex;
		int tTokenIndex;
		RevenueCenter tRevenueCenter;
		City tCity;
		MapToken tMapToken;
		String tAbbrev;

		// a single Placed Token is identified by:
		// Company Abbrev, Station Index, City Center Index, Token Index
		
		tCityCenterCount = getCityCenterCount ();
		if (tCityCenterCount > 0) {
			for (tCityCenterIndex = 0; tCityCenterIndex < tCityCenterCount; tCityCenterIndex++) {
				tRevenueCenter = getRevenueCenter (tCityCenterIndex);
				if (tRevenueCenter.isCity ()) {
					tCity = (City) getRevenueCenter (tCityCenterIndex);
					if (tCity.cityHasAnyStation ()) {
						for (tStationIndex = 0; tStationIndex < tCity.getStationCount (); tStationIndex++) {
							tMapToken = tCity.getToken (tStationIndex);
							if (tMapToken != MapToken.NO_MAP_TOKEN) {
								tAbbrev = tMapToken.getCorporationAbbrev ();
								tTokenIndex = tMapToken.getTokenIndex ();
								tAPlacedToken = tAbbrev + "," + tStationIndex + "," + tCityCenterIndex + "," + tTokenIndex;
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
		if (name != TileName.NO_TILE_NAME) {
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

	public RevenueCenter getRunThroughCenter () {
		return centers.getRunThroughCenter ();
	}

	public RevenueCenter getCenterAtLocation (Location aLocation) {
		return centers.getRevenueCenterAtLocation (aLocation);
	}

	public RevenueCenter getRCWithBaseForCorp (Corporation aCorporation) {
		return centers.getRCWithBaseForCorp (aCorporation);
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

	public String getName () {
		if (name == TileName.NO_TILE_NAME) {
			return TileName.NO_NAME2;
		} else {
			return (name.getName ());
		}
	}

	public int getNumber () {
		return (number);
	}

	public String getNumberToString () {
		return (Integer.valueOf (number).toString ());
	}

	public RevenueCenter getRCContainingPoint (Point aPoint, Hex aHex, int XCenter, int YCenter, int aTileOrient) {
		return centers.getRCContainingPoint (aPoint, aHex, XCenter, YCenter, aTileOrient);
	}

	/**
	 * Get the number of Usable Revenue Centers (that are NOT Destinations)
	 * 
	 * @return the Count of Centers that can be used by Tokens
	 * 
	 */
	public int getRevenueCenterCount () {
		RevenueCenter tRevenueCenter;
		int tUseableRevenueCenters;
		int tRCIndex;
		int tRCCount;
		
		tUseableRevenueCenters = 0;
		tRCCount = centers.size ();
		
		for (tRCIndex = 0; tRCIndex < tRCCount; tRCIndex++) {
			tRevenueCenter = centers.get (tRCIndex);
			if (! tRevenueCenter.isDestination ()) {
				tUseableRevenueCenters++;
			}
		}
		
		return tUseableRevenueCenters;
	}

	public RevenueCenter getRevenueCenter (int aCenterIndex) {
		return centers.get (aCenterIndex);
	}

	/**
	 * Find the RevenueCenter on the Tile that has a Track connecting to the
	 * Location provided
	 *
	 * @param aOtherLocation The remote location that should have track connecting
	 *                       to this RevenueCenter
	 * @return NO_CENTER if no RevenueCenter is found with track connecting to the
	 *         location provided
	 */
	public RevenueCenter findRevenueCenterConnectingTo (int aOtherLocation) {
		RevenueCenter tRevenueCenter;
		RevenueCenter tFoundRevenueCenter;
		int tLocation;
		Track tTrack;
		int tCenterIndex;
		int tCenterCount;

		tFoundRevenueCenter = RevenueCenter.NO_CENTER;
		tCenterCount = getRevenueCenterCount ();
		if (tCenterCount > 0) {
			for (tCenterIndex = 0; tCenterIndex < tCenterCount; tCenterIndex++) {
				tRevenueCenter = centers.get (tCenterIndex);
				tLocation = tRevenueCenter.getLocation ().getLocation ();
				tTrack = tracks.getTrackFromStartToEnd (tLocation, aOtherLocation);
				if (tTrack != Track.NO_TRACK) {
					tFoundRevenueCenter = tRevenueCenter;
				}
			}
		}

		return tFoundRevenueCenter;
	}

	public String getRevenueValue (int aPhase) {
		RevenueCenter tRevenueCenter;
		int tRCIndex;
		String tRevenueValue = "";

		for (tRCIndex = 0; tRCIndex < getRevenueCenterCount (); tRCIndex++) {
			if (tRevenueValue.equals ("")) {
				tRevenueCenter = getRevenueCenter (tRCIndex);
				if (tRevenueCenter != RevenueCenter.NO_CENTER) {
					tRevenueValue = tRevenueCenter.getRevenueToString (aPhase);
				}
			}
		}

		return tRevenueValue;
	}

	public RevenueCenter getSelectedRevenueCenter (Feature2 aSelectedFeature2, int aTileOrient) {
		RevenueCenter tRevenueCenter;

		if (hasCenters ()) {
			tRevenueCenter = centers.getSelectedRevenueCenter (aSelectedFeature2, aTileOrient);
		} else {
			tRevenueCenter = RevenueCenter.NO_CENTER;
		}

		return tRevenueCenter;
	}

	public int getTileNameLocation () {
		int tLocation = Location.NO_LOCATION;

		if (name != TileName.NO_TILE_NAME) {
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

	public String getToolTip (int aPhase) {
		String tTip = "";

		tTip += "Tile: " + getTypeName () + " " + getNumberToString () + "<br>";
		tTip += "Revenue: " + getRevenueValue (aPhase) + "<br>";
		if (hasCenters ()) {
			tTip += centers.getToolTip ();
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
		return type.getType ();
	}

	public String getTypeName () {
		return type.getName ();
	}

	public int getX () {
		return XCenter;
	}

	public int getY () {
		return YCenter;
	}

	private boolean hasCenters () {
		boolean tHasCenters = false;

		if (centers != Centers.NO_CENTERS) {
			if (centers.size () > 0) {
				tHasCenters = true;
			}
		}

		return tHasCenters;
	}

	public boolean hasAnyStation () {
		if (hasCenters ()) {
			return centers.hasAnyStation ();
		} else {
			return false;
		}
	}

	public boolean hasAnyCorporationBase () {
		if (hasCenters ()) {
			return centers.hasAnyCorporationBase ();
		} else {
			return false;
		}
	}

	public boolean hasStation (Token aToken) {
		if (hasCenters ()) {
			return centers.hasStation (aToken);
		} else {
			return false;
		}
	}

	public boolean hasStation (int aCorpID) {
		if (hasCenters ()) {
			return centers.hasStation (aCorpID);
		} else {
			return false;
		}
	}

	public Location getLocationWithStation (int aCorpID, int aTileOrient) {
		Location tLocationWithStation;
		int tStationIndex;
		RevenueCenter tRevenueCenter;

		tLocationWithStation = Location.NO_LOC;
		if (hasCenters ()) {
			tStationIndex = getStationIndex (aCorpID);
			if (tStationIndex != Centers.UNSPECIFIED_ID) {
				tRevenueCenter = centers.get (tStationIndex);
				if (tRevenueCenter != RevenueCenter.NO_CENTER) {
					tLocationWithStation = tRevenueCenter.getLocation ();
					tLocationWithStation = tLocationWithStation.unrotateLocation (aTileOrient);
				}
			}
		}

		return tLocationWithStation;
	}

	public int getStationIndex (int aCorpID) {
		int tStationIndex;
		
		if (hasCenters ())
			tStationIndex = centers.getStationIndex (aCorpID);
		else {
			tStationIndex = Centers.UNSPECIFIED_ID;
		}
		
		return tStationIndex;
	}

	public void loadStationsStates (XMLNode aMapCellNode) {
		if (hasCenters ()) {
			centers.loadStationsStates (aMapCellNode);
		}
	}

	public void paintComponent (Graphics g, int Xc, int Yc, int aTileOrient, Hex aHex,
			Feature2 aSelectedFeature, boolean aTileIsSelected) {
		int tOldX, tOldY;

		tOldX = getX ();
		tOldY = getY ();
		setXY (Xc, Yc);
		paintComponent (g, aTileOrient, aHex, aSelectedFeature, aTileIsSelected);
		setXY (tOldX, tOldY);
	}

	public void paintComponent (Graphics g, Hex aHex) {
		paintComponent (g, 0, aHex, new Feature2 (), false);
	}

	public void paintComponent (Graphics g, int aTileOrient, Hex aHex, Feature2 aSelectedFeature,
				boolean aTileIsSelectable) {
		Paint tHexPaint;

		tHexPaint = type.getPaint (aTileIsSelectable);

		aHex.paintHex (g, XCenter, YCenter, tHexPaint);

		tracks.draw (g, XCenter, YCenter, aTileOrient, aHex, tHexPaint, aSelectedFeature);
		centers.draw (g, XCenter, YCenter, aTileOrient, aHex, ON_TILE, aSelectedFeature);

		if (name != TileName.NO_TILE_NAME) {
			name.draw (g, XCenter, YCenter, aTileOrient, aHex);
		}
	}

	public void printlog () {
		System.out.println ("Tile Number " + number);
		if (name != TileName.NO_TILE_NAME) {
			name.printlog ();
		}
		if (type != TileType.NO_TILE_TYPE) {
			type.printlog ();
		}
		System.out.print ("Tile ");
		tracks.printlog ();
		System.out.print ("Tile ");
		centers.printlog ();
	}

	public void setCorporationHome (Corporation aHomeCorporation, Location aNewCityLocation) {
		centers.setCorporationHome (aHomeCorporation, aNewCityLocation);
	}

	public boolean removeHome (Corporation aHomeCorporation, Location aNewCityLocation) {
		boolean tHomeRemoved;
		
		tHomeRemoved = centers.removeHome (aHomeCorporation, aNewCityLocation);
		
		return tHomeRemoved;
	}

	public void setCityInfo (CityInfo aCityInfo) {
		if (aCityInfo != CityInfo.NO_CITY_INFO) {
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
				tTileRevenueCenter.setCorporationHome (tCorporation);
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
		if (name != TileName.NO_TILE_NAME) {
			name.setLocation (aLocation);
		}
	}

	public void setValues (int aNumber, String aName, int aType) {
		tracks = new Tracks ();
		setValues (aNumber, aType);
		name = new TileName (aName);
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

	ParsingRoutine3I tileParsingRoutine = new ParsingRoutine3I () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			name = new TileName (aChildNode);
		}

		@Override
		public void foundItemMatchKey2 (XMLNode aChildNode) {
			Track tSegment;

			tSegment = new Track (aChildNode);
			tracks.add (tSegment);
		}

		@Override
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
				if (tMapToken0 != MapToken.NO_MAP_TOKEN) {
					tMapToken0.printlog ();
				}
				if (tMapToken1 != MapToken.NO_MAP_TOKEN) {
					tMapToken1.printlog ();
				}
				centers.clearAllStations ();
				if (tMapToken1 != MapToken.NO_MAP_TOKEN) {
					tCity0.placeStation (tMapToken1, aMapCell);
				}
				if (tMapToken0 != MapToken.NO_MAP_TOKEN) {
					tCity1.placeStation (tMapToken0, aMapCell);
				}
			}
		}
	}

	public boolean hasTown () {
		return centers.hasTown ();
	}

	public Track getConnectingTrackBetween (Location aThisLocation, Location aThatLocation) {
		Track tTrack;
		int tStartLocation, tEndLocation;

		tStartLocation = aThisLocation.getLocation ();
		tEndLocation = aThatLocation.getLocation ();

		tTrack = tracks.getTrackFromStartToEnd (tStartLocation, tEndLocation);

		return tTrack;
	}

	public boolean hasConnectingTrackBetween (Location aThisLocation, Location aThatLocation) {
		boolean tHasConnectingTrackBetween = true;
		Track tTrack;

		tTrack = getConnectingTrackBetween (aThisLocation, aThatLocation);
		if (tTrack == Track.NO_TRACK) {
			tHasConnectingTrackBetween = false;
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
		if (tTrack == Track.NO_TRACK) {
			tGauge = tTrack.getGauge ();
		}

		return tGauge;
	}

	public int getTrackCount () {
		int tTrackCount = 0;

		tTrackCount = tracks.size ();

		return tTrackCount;
	}

	public int getTrackCountFromSide (Location aLocation) {
		int tTrackCount = 0;

		tTrackCount = tracks.getTrackCountFromSide (aLocation);

		return tTrackCount;
	}

	public int getTrackIndexBetween (Location aStartLocation, Location aEndLocation) {
		int tTrackIndex;

		tTrackIndex = tracks.getTrackIndexBetween (aStartLocation, aEndLocation);

		return tTrackIndex;
	}

	public Track getTrackByIndex (int aNextTrackIndex) {
		Track tTrack;

		tTrack = tracks.getTrack (aNextTrackIndex);

		return tTrack;
	}

	public Track getTrackFromStartByIndex (Location aStartLocation, int aNextTrackIndex) {
		Track tTrack;

		tTrack = tracks.getTrackFromStartByIndex (aStartLocation, aNextTrackIndex);

		return tTrack;
	}

	public boolean areLocationsConnected (Location aLocation, int aRemoteLocationIndex) {
		return tracks.areLocationsConnected (aLocation, aRemoteLocationIndex);
	}

	// ---------------------------------------------------------------------------//
	// Map Graph Stuff after this point
	public void fillMapGraph (MapGraph aMapGraph, int aTileOrient, MapCell aMapCell) {
		int tTrackCount;
		int tTrackIndex;
		Vertex tStartVertex;
		Vertex tEndVertex;
		Track tTrack;
		Location tStartLocation;
		Location tEndLocation;
		Edge tEdge;

		tTrackCount = tracks.size ();
		for (tTrackIndex = 0; tTrackIndex < tTrackCount; tTrackIndex++) {
			tTrack = getTrackByIndex (tTrackIndex);
			tStartLocation = tTrack.getEnterLocation ();
			tEndLocation = tTrack.getExitLocation ();
			tStartLocation = tStartLocation.rotateLocation (aTileOrient);
			tEndLocation = tEndLocation.rotateLocation (aTileOrient);
			tStartVertex = new Vertex (aMapCell, tStartLocation);
			tEndVertex = new Vertex (aMapCell, tEndLocation);
			tEdge = new Edge (tTrack, tStartVertex, tEndVertex);
			addVertexAndEdge (aMapGraph, tStartVertex, tEdge);
			addVertexAndEdge (aMapGraph, tEndVertex, tEdge);
			addNeighborVertexAndEdge (aMapGraph, tStartVertex);
			addNeighborVertexAndEdge (aMapGraph, tEndVertex);
		}
	}

	private void addNeighborVertexAndEdge (MapGraph aMapGraph, Vertex aVertex) {
		Vertex tNeighborVertex;
		MapCell tNeighborMapCell;
		MapCell tMapCell;
		Location tNeighborLocation;
		Location tLocation;
		int tNeighborLoc;
		Edge tSide2SideEdge;

		if (aVertex.isOnSide ()) {
			tMapCell = aVertex.getMapCell ();
			tLocation = aVertex.getLocation ();
			if (! tMapCell.isBlockedSide (tLocation.getLocation ())) {
				tNeighborMapCell = tMapCell.getNeighbor (tLocation.getLocation ());
				if (tNeighborMapCell != MapCell.NO_MAP_CELL) {
					if (tNeighborMapCell.isSelectable ()) {
						tNeighborLoc = tMapCell.getSideFromNeighbor (tNeighborMapCell);
						tNeighborLocation = new Location (tNeighborLoc);
						tNeighborVertex = new Vertex (tNeighborMapCell, tNeighborLocation);
						tSide2SideEdge = new Edge (Track.NO_TRACK, aVertex, tNeighborVertex);
						aVertex.addEdge (tSide2SideEdge);
						addVertexAndEdge (aMapGraph, tNeighborVertex, tSide2SideEdge);
					}
				}
			}
		}

	}

	public void addVertexAndEdge (MapGraph aMapGraph, Vertex aVertex, Edge aEdge) {
		Vertex tFoundVertex;
		String tVertexID;

		if (aMapGraph.containsVertex (aVertex)) {
			tVertexID = aVertex.getID ();
			tFoundVertex = aMapGraph.getVertexWithID (tVertexID);
			aEdge.replaceVertex (tFoundVertex);
			tFoundVertex.addEdge (aEdge);
		} else {
			aVertex.addEdge (aEdge);
			aMapGraph.addVertex (aVertex);
		}
	}

	public int compareType (Tile aTile) {
		int tTypeDiff;
		TileType tType;

		tType = aTile.getType ();
		tTypeDiff = type.compareType (tType);

		return tTypeDiff;
	}

	public int compareNumber (Tile aTile) {
		int tNumberDiff;
		int tTileNumber;

		tTileNumber = aTile.getNumber ();
		tNumberDiff = number - tTileNumber;

		return tNumberDiff;
	}
}
