package ge18xx.center;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;

//
//  City.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//
/*
 *		City Java Source
 *
 */

import ge18xx.company.Corporation;
import ge18xx.company.MapToken;
import ge18xx.company.TokenCompany;
import ge18xx.company.Tokens;
import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Feature2;
import ge18xx.tiles.TileType;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.ParsingRoutineIO;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;

public class City extends RevenueCenter implements Cloneable {
	public static final AttributeName AN_NUMBER = new AttributeName ("number");
	public static final AttributeName AN_COMPANY = new AttributeName ("company");
	public static final AttributeName AN_STATION_INDEX = new AttributeName ("stationIndex");
	public static final ElementName CorporationBase = new ElementName ("CorporationBase");
	public static final ElementName EN_CORPORATE_STATION = new ElementName ("CorporateStation");
	public static final City NO_CITY = null;
	public static final int NO_STATIONS = 0;
	public static final int NOT_VALID_STATION = -1;
	int stationCount;
	MapToken corpStations [];

	public City (XMLNode aNode) {
		super (aNode);
		int tNumber;
		
		tNumber = aNode.getThisIntAttribute (AN_NUMBER);
		setStationCount (tNumber);
	}

	public City (City aCity) {
		int tRevenueCount;
		int tRCType;
		int tLocation;
		int tRevenueValue;
		int tRevenuePhase;
		int tRevenueIndex;

		tRevenueCount = aCity.getRevenueCount ();
		tRCType = aCity.type.getType ();
		tLocation = aCity.location.getLocation ();

		if (tRevenueCount == 1) {
			tRevenueValue = aCity.getRevenue (Revenue.ALL_PHASES);
			setValues (tRCType, aCity.id, tLocation, aCity.name, tRevenueValue);
		} else {
			tRevenueValue = aCity.getRevenueValueIndex (1);
			tRevenuePhase = aCity.getRevenuePhaseIndex (1);
			setValues (tRCType, aCity.id, tLocation, aCity.name, tRevenueValue, tRevenuePhase);
			for (tRevenueIndex = 2; tRevenueIndex < tRevenueCount; tRevenueIndex++) {
				tRevenueValue = aCity.getRevenueValueIndex (tRevenueIndex);
				tRevenuePhase = aCity.getRevenuePhaseIndex (tRevenueIndex);
				addRevenue (tRevenueValue, tRevenuePhase);
			}
		}
		setStationCount (aCity.stationCount);
		setRevenueLocation (aCity.revenues.getLocation ());
	}

	public City (int aType, int aStationCount, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
		super (aType, aID, aLocation, aName, aValue, aTileType);
		setStationCount (aStationCount);
	}

	public City (RevenueCenterType aRCType, int aStationCount, int aID, int aLocation, String aName, int aValue,
			TileType aTileType) {
		super (aRCType, aID, aLocation, aName, aValue, aTileType);
		setStationCount (aStationCount);
	}

	@Override
	public void appendTokensState (XMLDocument aXMLDocument, XMLElement aMapCellElement) {
		int tIndex;
		int tTokenIndex;
		String tAbbrev;
		String tMapCellID;
		XMLElement tXMLTokenState;
		MapToken tMapToken;
		TokenCompany tTokenCompany;

		for (tIndex = 0; tIndex < stationCount; tIndex++) {
			if (hasMapTokenAtStation (tIndex)) {
				tMapToken = getMapTokenAtStation (tIndex);
				tTokenCompany = getTokenCompanyForStation (tIndex);
				tAbbrev = tTokenCompany.getAbbrev ();
				tTokenIndex = tTokenCompany.getTokenIndex (tMapToken);
				tMapCellID = corpStations [tIndex].getMapCellID ();
				tXMLTokenState = aXMLDocument.createElement (EN_CORPORATE_STATION);
				tXMLTokenState.setAttribute (Corporation.AN_ABBREV, tAbbrev);
				tXMLTokenState.setAttribute (AN_STATION_INDEX, tIndex);
				tXMLTokenState.setAttribute (Location.AN_LOCATION, location.getLocation ());
				tXMLTokenState.setAttribute (MapCell.AN_MAP_CELL_ID, tMapCellID);
				tXMLTokenState.setAttribute (Tokens.AN_TOKEN_INDEX, tTokenIndex);
				aMapCellElement.appendChild (tXMLTokenState);
			}
		}
	}

	@Override
	public boolean canPlaceStation () {
		return (type.canPlaceStation ());
	}

	@Override
	public boolean cityHasOpenStation () {
		boolean tHasOpenStation;
		int tIndex;

		tHasOpenStation = false;
		for (tIndex = 0; tIndex < stationCount; tIndex++) {
			if (hasNoMapTokenAtStation (tIndex)) {
				tHasOpenStation = true;
			}
		}

		return tHasOpenStation;
	}

	public boolean hasNoMapTokenAtStation (int aIndex) {
		return (corpStations [aIndex] == MapToken.NO_MAP_TOKEN);
	}

	public boolean hasMapTokenAtStation (int aIndex) {
		return (corpStations [aIndex] != MapToken.NO_MAP_TOKEN);
	}

	public MapToken getMapTokenAtStation (int aIndex) {
		return corpStations [aIndex];
	}
	
	public TokenCompany getTokenCompanyForStation (int aIndex) {
		TokenCompany tTokenCompany;
		
		tTokenCompany = corpStations [aIndex].getTokenCompany ();
		
		return tTokenCompany;
	}
	
	@Override
	public boolean cityHasAnyStation () {
		boolean tHasAnyStation;
		int tIndex;

		tHasAnyStation = false;
		for (tIndex = 0; tIndex < stationCount; tIndex++) {
			if (hasMapTokenAtStation (tIndex)) {
				tHasAnyStation = true;
			}
		}

		return tHasAnyStation;
	}

	@Override
	public boolean cityHasStation (int aCorpID) {
		boolean tHasStation;
		int tIndex;
		int tPlacedTokenCorpID;

		tHasStation = false;
		for (tIndex = 0; tIndex < stationCount; tIndex++) {
			if (hasMapTokenAtStation (tIndex)) {
				tPlacedTokenCorpID = corpStations [tIndex].getCorporationID ();
				if (tPlacedTokenCorpID == aCorpID) {
					tHasStation = true;
				}
			}
		}

		return tHasStation;
	}

	public boolean cityHasStation (MapToken aMapToken) {
		boolean tHasStation;
		int tCorpID;

		tHasStation = false;
		if (canPlaceStation ()) {
			tCorpID = aMapToken.getCorporationID ();
			tHasStation = cityHasStation (tCorpID);
			if (tHasStation) {
				logger.info ("City Has Station, Found Station for This Corp already on This City.");
			}
		} else {
			logger.info ("City Has Station, tested CanPlaceStation - Failed");
		}

		return tHasStation;
	}

	@Override
	public boolean cityOrTown () {
		return type.cityOrTown ();
	}

	public void clearAllStations () {
		int tIndex;

		if (stationCount > 0) {
			for (tIndex = 0; tIndex < stationCount; tIndex++) {
				setMapTokenAt (tIndex);
			}
		}
	}

	public void setMapTokenAt (int aIndex) {
		corpStations [aIndex] = MapToken.NO_MAP_TOKEN;
	}

	public boolean clearCorporation (TokenCompany aTokenCompany) {
		boolean tCorporationCleared;
		
		tCorporationCleared = cityInfo.clearCorporation (aTokenCompany);
		
		return tCorporationCleared;
	}
	
	public MapToken returnStation (TokenCompany aTokenCompany) {
		int tIndex;
		int tCorporationId;
		boolean tFound;
		MapToken tMapToken;

		tMapToken = MapToken.NO_MAP_TOKEN;
		if (stationCount > 0) {
			tFound = false;
			for (tIndex = 0; tIndex < stationCount; tIndex++) {
				if (hasMapTokenAtStation (tIndex)) {
					tCorporationId = corpStations [tIndex].getCorporationID ();
					if (tCorporationId == aTokenCompany.getID ()) {
						tFound = true;
						tMapToken = corpStations [tIndex];
						aTokenCompany.setTokenUsed (tMapToken, false);
					}
					if (tFound) {
						if ((tIndex + 1) < stationCount) {
							corpStations [tIndex] = corpStations [tIndex + 1];
						} else {
							setMapTokenAt (tIndex);
						}
					}
				}
			}
		}

		return tMapToken;
	}

	public void clearStation (int aCorporationId) {
		int tIndex;
		int tCorporationId;
		boolean tFound;

		if (stationCount > 0) {
			tFound = false;
			for (tIndex = 0; tIndex < stationCount; tIndex++) {
				if (hasMapTokenAtStation (tIndex)) {
					tCorporationId = corpStations [tIndex].getCorporationID ();
					if (tCorporationId == aCorporationId) {
						tFound = true;
					}
					if (tFound) {
						if ((tIndex + 1) < stationCount) {
							corpStations [tIndex] = corpStations [tIndex + 1];
						} else {
							setMapTokenAt (tIndex);
						}
					}
				}
			}
		}
	}

	public MapToken getMapTokenFor (int aCorporationID) {
		MapToken tMapToken;
		int tIndex;
		int tCorporationId;

		tMapToken = MapToken.NO_MAP_TOKEN;
		if (stationCount > 0) {
			for (tIndex = 0; (tIndex < stationCount) && (tMapToken == MapToken.NO_MAP_TOKEN); tIndex++) {
				if (hasMapTokenAtStation (tIndex)) {
					tCorporationId = corpStations [tIndex].getCorporationID ();
					if (tCorporationId == aCorporationID) {
						tMapToken = corpStations [tIndex];
					}
				}
			}
		}

		return tMapToken;
	}

	@Override
	public City clone () {
		int tIndex;
		City tCity;
		CityInfo tCityInfo;
		
		tCity = (City) super.clone ();
		tCity.stationCount = stationCount;
		if (stationCount > 0) {
			tCity.corpStations = new MapToken [stationCount];
			for (tIndex = 0; tIndex < stationCount; tIndex++) {
				tCity.corpStations [tIndex] = corpStations [tIndex];
			}
		}
		tCityInfo = tCity.getCityInfo ();
		tCityInfo.setRevenueCenter (tCity);

		return tCity;
	}

	@Override
	public boolean containingPoint (Point aPoint, Hex aHex, int Xc, int Yc, int aTileOrient) {
		boolean tContainingPoint;
		int X1;
		int Y1;
		int tTempWidth;
		int tWidth;
		Point tDisplace;
		Rectangle tRectangle;
		Location tLocation;

		tContainingPoint = false;
		tTempWidth = aHex.getCityWidth ();
		tLocation = location.rotateLocation (aTileOrient);
		tDisplace = tLocation.calcCenter (aHex);

		X1 = Xc - tTempWidth + tDisplace.x;
		Y1 = Yc - tTempWidth + tDisplace.y;
		tWidth = tTempWidth * 2;
		switch (type.getType ()) {
		case RevenueCenterType.SINGLE_CITY:
		case RevenueCenterType.DESTINATION_CITY:
			tWidth = tTempWidth * 2;
			break;

		case RevenueCenterType.DOUBLE_CITY: /* Double City */
		case RevenueCenterType.TRIPLE_CITY: /* Triple City */
		case RevenueCenterType.QUAD_CITY: /* Four City */
			X1 -= tTempWidth;
			Y1 -= tTempWidth;
			tWidth = tWidth * 2;
			break;

		}
		tRectangle = new Rectangle (X1, Y1, tWidth, tWidth);
		tContainingPoint = tRectangle.contains (aPoint);

		return tContainingPoint;
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tCorpStation;
		TokenCompany tTokenCompany;
		int tIndex;

		tXMLElement = super.createElement (aXMLDocument);
		tXMLElement.setAttribute (AN_NUMBER, stationCount);
		if (stationCount > 0) {
			for (tIndex = 0; tIndex < stationCount; tIndex++) {
				if (hasMapTokenAtStation (tIndex)) {
					tCorpStation = aXMLDocument.createElement (EN_CORPORATE_STATION);
					tTokenCompany = corpStations [tIndex].getWhichCompany ();
					tCorpStation.setAttribute (AN_COMPANY, tTokenCompany.getID ());
					tXMLElement.appendChild (tCorpStation);
				}
			}
		}

		return tXMLElement;
	}

	@Override
	public void draw (Graphics aGraphics, int aXc, int aYc, Hex aHex, boolean aOnTile, Feature2 aSelectedFeature) {
		draw (aGraphics, aXc, aYc, NO_ROTATION, aHex, aOnTile, aSelectedFeature);
	}

	@Override
	public void draw (Graphics aGrapics, int Xc, int Yc, int aTileOrient, Hex aHex, boolean onTile,
			Feature2 aSelectedFeature) {
		Color tCityColor;
		Location tLocation;
		int tType;

		tLocation = location.rotateLocation (aTileOrient);
		tType = type.getType ();
		switch (tType) {
		case RevenueCenterType.NO_REVENUE_CENTER: 		/* No City */
			break;

		case RevenueCenterType.DEAD_END_ONLY_CITY: 		/* Dead End City, DRAW Revenues Only */
		case RevenueCenterType.RUN_THROUGH_CITY: 		/* Run Through City, DRAW Revenues Only */
			drawValue (aGrapics, Xc, Yc, aHex, aTileOrient);
			break;

		case RevenueCenterType.SINGLE_CITY: 				/* Single City */
		case RevenueCenterType.DEAD_END_CITY: 			/* Dead End City (can have station, need to draw) */
			tCityColor = Color.white;
			drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			if (isSingleSelected (tLocation, aSelectedFeature)) {
				drawSelectionMarker (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY);
			}
			break;

		case RevenueCenterType.DESTINATION_CITY: 		/* Destination City (can have station, need to draw) */
			tCityColor = new Color (255, 255, 204);
			drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.DESTINATION_CITY, tCityColor, aTileOrient, true, 0);
			if (isSingleSelected (tLocation, aSelectedFeature)) {
				drawSelectionMarker (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.DESTINATION_CITY);
			}
			break;

		case RevenueCenterType.BYPASS_CITY: 				/* BYPASS Track Single City */
			tCityColor = Color.white;
			drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			break;

		case RevenueCenterType.TWO_CITIES: 				/* Two Separate Cities */
			tCityColor = Color.white;
			drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			if (!onTile) {
				tLocation.rotateLocation180 ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			}
			break;

		case RevenueCenterType.THREE_CITIES: 			/* Three Seperate Cities */
			tCityColor = Color.white;
			drawACity (aGrapics, Xc, Yc, aHex, location, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			if (!onTile) {
				tLocation.rotateLocation2Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation2Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			}
			break;

		case RevenueCenterType.FOUR_CITIES: 				/* Four Seperate Cities */
			tCityColor = Color.white;
			drawACity (aGrapics, Xc, Yc, aHex, location, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			if (!onTile) {
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation2Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			}
			break;

		case RevenueCenterType.FIVE_CITIES: 				/* Five Seperate Cities */
			tCityColor = Color.white;
			drawACity (aGrapics, Xc, Yc, aHex, location, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			if (!onTile) {
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation2Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			}
			break;

		case RevenueCenterType.SIX_CITIES: 				/* Six Seperate Cities */
			tCityColor = Color.white;
			drawACity (aGrapics, Xc, Yc, aHex, location, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			if (!onTile) {
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
				tLocation.rotateLocation1Tick ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.SINGLE_CITY, tCityColor, aTileOrient, true, 0);
			}
			break;

		case RevenueCenterType.DOUBLE_CITY: 				/* Double City */
		case RevenueCenterType.TRIPLE_CITY: 				/* Triple City */
		case RevenueCenterType.QUAD_CITY: 				/* Four City */
			tCityColor = Color.white;
			drawACity (aGrapics, Xc, Yc, aHex, tLocation, tType, tCityColor, aTileOrient, true, 0);
			if (isSingleSelected (tLocation, aSelectedFeature)) {
				drawSelectionMarker (aGrapics, Xc, Yc, aHex, tLocation, tType);
			}
			break;

		case RevenueCenterType.TWO_DOUBLE_CITIES: 		/* Two Double Cities */
			tCityColor = Color.white;
			drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.DOUBLE_CITY, tCityColor, aTileOrient, true, 0);
			if (!onTile) {
				tLocation.rotateLocation180 ();
				drawACity (aGrapics, Xc, Yc, aHex, tLocation, RevenueCenterType.DOUBLE_CITY, tCityColor, aTileOrient, true, 0);
			}
			break;

		case RevenueCenterType.PRIVATE_RAILWAY_POINT: 	/* Private Railway Revenue Location */
			if (cityInfo != CityInfo.NO_CITY_INFO) {
				cityInfo.drawPrivateRailway (aGrapics, Xc, Yc, aHex);
			}
			break;
		}
		drawName (aGrapics, Xc, Yc, aHex);
	}

	public void drawACity (Graphics aGraphics, int Xc, int Yc, Hex aHex, Location aLocation, int aType, Color aCityColor,
			int aTileOrient, boolean drawValue, int aTokenIndex) {
		drawACity (aGraphics, Xc, Yc, aHex, aLocation, aType, aCityColor, aTileOrient, drawValue, Xc, Yc, aTokenIndex);
	}

	public void drawACity (Graphics aGraphics, double Xc, double Yc, Hex aHex, int aLocation, int aType, Color aCityColor,
			int aTileOrient, boolean drawValue, int hexXc, int hexYc, int aTokenIndex) {
		Location tLocation;
		int XCenter;
		int YCenter;
		
		tLocation = new Location (aLocation);
		XCenter = Double.valueOf (Xc).intValue ();
		YCenter = Double.valueOf (Yc).intValue ();
		drawACity (aGraphics, XCenter, YCenter, aHex, tLocation, aType, aCityColor, aTileOrient, drawValue, hexXc, hexYc,
				aTokenIndex);
	}

	public void drawACity (Graphics g, int Xc, int Yc, Hex aHex, Location aLocation, int aType, Color aCityColor,
			int aTileOrient, boolean drawValue, int hexXc, int hexYc, int aTokenIndex) {
		double XCenter1, YCenter1;
		double XCenter2, YCenter2;
		double XCenter3, YCenter3;
		double XCenter4, YCenter4;
		int X1, Y1;
		int X2, Y2;
		int XB1, YB1;
		int XB2, YB2;
		int XB3, YB3;
		int XB4, YB4;
		int XB5, YB5;
		int XB6, YB6;
		int XB7, YB7;
		int XB8, YB8;
		int temp;
		int Xsign;
		int Ysign;
		double C30 = 0.8660254;
		double S30 = 0.5;
		double tCS30;
		double tempC30;
		double tempS30;

		Point tDisplace;
		tDisplace = aLocation.calcCenter (aHex);

		temp = aHex.getCityWidth ();
		tempC30 = temp * C30;
		tempS30 = temp * S30;
		X1 = Xc - temp;
		Y1 = Yc - temp;
		X2 = Xc + temp;
		Y2 = Yc + temp;
		switch (aType) {
		case RevenueCenterType.NO_REVENUE_CENTER:
		case RevenueCenterType.TWO_CITIES:
		case RevenueCenterType.THREE_CITIES:
		case RevenueCenterType.FOUR_CITIES:
		case RevenueCenterType.FIVE_CITIES:
		case RevenueCenterType.SIX_CITIES:
		case RevenueCenterType.TWO_DOUBLE_CITIES:
			break;

		case RevenueCenterType.DESTINATION_CITY:
			temp = Double.valueOf (temp / 1.25).intValue ();
			X1 = Xc - temp;
			Y1 = Yc - temp;
			X2 = Xc + temp;
			Y2 = Yc + temp;
			if (aLocation.getLocation () != Location.CENTER_CITY_LOC) {
				X1 += tDisplace.x;
				X2 += tDisplace.x;
				Y1 += tDisplace.y;
				Y2 += tDisplace.y;
			}
			drawTheCity (g, aCityColor, X1, Y1, X2, Y2, drawValue, aHex, hexXc, hexYc, aTileOrient, aTokenIndex);
			break;

		case RevenueCenterType.SINGLE_CITY:
			if (aLocation.getLocation () != Location.CENTER_CITY_LOC) {
				X1 += tDisplace.x;
				X2 += tDisplace.x;
				Y1 += tDisplace.y;
				Y2 += tDisplace.y;
			}
			drawTheCity (g, aCityColor, X1, Y1, X2, Y2, drawValue, aHex, hexXc, hexYc, aTileOrient, aTokenIndex);
			break;

		case RevenueCenterType.DOUBLE_CITY:
			if (aLocation.getLocation () != Location.CENTER_CITY_LOC) {
				Xsign = 1;
				Ysign = 1;
				switch (aTileOrient) {
				case (0):
					Xsign = 1;
					Ysign = 1;
					break;

				case (1):
					if (Hex.getDirection ()) {
						Xsign = 0;
						Ysign = 2;
					} else {
						Xsign = -1;
						Ysign = 1;
					}
					break;

				case (2):
					if (Hex.getDirection ()) {
						Xsign = -1;
						Ysign = 1;
					} else {
						Xsign = -2;
						Ysign = 0;
					}
					break;

				case (3):
					Xsign = -1;
					Ysign = -1;
					break;

				case (4):
					if (Hex.getDirection ()) {
						Xsign = 0;
						Ysign = -2;
					} else {
						Xsign = 1;
						Ysign = -1;
					}
					break;

				case (5):
					if (Hex.getDirection ()) {
						Ysign = -1;
						Xsign = 1;
					} else {
						Ysign = 0;
						Xsign = 2;
					}
					break;
				}
				X1 += tDisplace.x + temp;
				Y1 += tDisplace.y + temp;
				XCenter1 = X1;
				YCenter1 = Y1;
				if (Hex.getDirection ()) {
					tCS30 = tempC30;
					tempC30 = tempS30;
					tempS30 = tCS30;
				}
				XCenter2 = X1 + 2 * Xsign * tempS30;
				YCenter2 = Y1 + 2 * Ysign * tempC30;
				if (Ysign == 0) {
					XB1 = Double.valueOf (XCenter1).intValue ();
					YB1 = Double.valueOf (YCenter1 - temp).intValue ();
					XB2 = Double.valueOf (XCenter2).intValue ();
					YB2 = YB1;
					XB3 = XB2;
					YB3 = Double.valueOf (YCenter1 + temp).intValue ();
					XB4 = XB1;
					YB4 = YB3;
				} else {
					if (Xsign == 0) {
						XB1 = Double.valueOf (XCenter1 - temp).intValue ();
						YB1 = Double.valueOf (YCenter1).intValue ();
						XB2 = Double.valueOf (XCenter1 + temp).intValue ();
						YB2 = YB1;
						XB3 = XB2;
						YB3 = Double.valueOf (YCenter2).intValue ();
						XB4 = XB1;
						YB4 = YB3;
					} else {
						XB1 = Double.valueOf (XCenter1 - Xsign * tempS30).intValue ();
						YB1 = Double.valueOf (YCenter1 + Ysign * tempC30).intValue ();
						XB2 = Double.valueOf (XCenter2 - Xsign * tempS30).intValue ();
						YB2 = Double.valueOf (YCenter2 + Ysign * tempC30).intValue ();
						XB3 = Double.valueOf (XCenter2 + Xsign * tempS30).intValue ();
						YB3 = Double.valueOf (YCenter2 - Ysign * tempC30).intValue ();
						XB4 = Double.valueOf (XCenter1 + Xsign * tempS30).intValue ();
						YB4 = Double.valueOf (YCenter1 - Ysign * tempC30).intValue ();
					}
				}
			} else {
				if ((aTileOrient == 1) || (aTileOrient == 4)) {
					XB1 = X1;
					YB1 = Y1;
					XB2 = X2;
					YB2 = Y1;
					XB3 = X2;
					YB3 = Y2;
					XB4 = X1;
					YB4 = Y2;
					XCenter1 = X1 + temp;
					YCenter1 = Y1;
					XCenter2 = XCenter1;
					YCenter2 = YCenter1 + temp + temp;
				} else {
					if ((aTileOrient == 0) || (aTileOrient == 3)) {
						Xsign = -1;
						Ysign = -1;
					} else {
						Xsign = 1;
						Ysign = -1;
					}
					XCenter1 = Xc + tDisplace.x + Xsign * tempC30;
					YCenter1 = Yc + tDisplace.y + Ysign * tempS30;
					XCenter2 = Xc + tDisplace.x - Xsign * tempC30;
					YCenter2 = Yc + tDisplace.y - Ysign * tempS30;
					XB1 = Double.valueOf (XCenter1 - Xsign * tempS30).intValue ();
					YB1 = Double.valueOf (YCenter1 + Ysign * tempC30).intValue ();
					XB2 = Double.valueOf (XCenter2 - Xsign * tempS30).intValue ();
					YB2 = Double.valueOf (YCenter2 + Ysign * tempC30).intValue ();
					XB3 = Double.valueOf (XCenter2 + Xsign * tempS30).intValue ();
					YB3 = Double.valueOf (YCenter2 - Ysign * tempC30).intValue ();
					XB4 = Double.valueOf (XCenter1 + Xsign * tempS30).intValue ();
					YB4 = Double.valueOf (YCenter1 - Ysign * tempC30).intValue ();
				}
			}
			drawMultipleCityBox (g, XB1, YB1, XB2, YB2, XB3, YB3, XB4, YB4);
			drawACity (g, XCenter1, YCenter1, aHex, Location.CENTER_CITY_LOC, RevenueCenterType.SINGLE_CITY, aCityColor,
					aTileOrient, false, hexXc, hexYc, 0);
			drawACity (g, XCenter2, YCenter2, aHex, Location.CENTER_CITY_LOC, RevenueCenterType.SINGLE_CITY, aCityColor,
					aTileOrient, true, hexXc, hexYc, 1);
			break;

		case RevenueCenterType.TRIPLE_CITY:
			if ((aTileOrient == 0) || (aTileOrient == 2) || (aTileOrient == 4)) {
				Xsign = -1;
				Ysign = -1;
			} else {
				Xsign = 1;
				Ysign = 1;
			}
			XCenter3 = Xc;
			YCenter3 = Yc - Ysign * temp;
			XCenter1 = XCenter3 + Xsign * 2 * tempS30;
			YCenter1 = YCenter3 + Ysign * 2 * tempC30;
			XCenter2 = XCenter3 - Xsign * 2 * tempS30;
			YCenter2 = YCenter3 + Ysign * 2 * tempC30;
			XB1 = Double.valueOf (XCenter1).intValue ();
			YB1 = Double.valueOf (YCenter1 + Ysign * temp).intValue ();
			XB2 = Double.valueOf (XCenter2).intValue ();
			YB2 = YB1;
			XB3 = Double.valueOf (XCenter2 - Ysign * tempC30).intValue ();
			YB3 = Yc;
			XB4 = Double.valueOf (XCenter3 - Xsign * tempC30).intValue ();
			YB4 = Double.valueOf (YCenter3 - Ysign * tempS30).intValue ();
			XB5 = Double.valueOf (XCenter3 + Xsign * tempC30).intValue ();
			YB5 = YB4;
			XB6 = Double.valueOf (XCenter1 + Xsign * tempC30).intValue ();
			YB6 = YB3;
			drawMultipleCityBox (g, XB1, YB1, XB2, YB2, XB3, YB3, XB4, YB4, XB5, YB5, XB6, YB6);
			drawACity (g, XCenter1, YCenter1, aHex, Location.CENTER_CITY_LOC, RevenueCenterType.SINGLE_CITY, aCityColor,
					aTileOrient, false, hexXc, hexYc, 0);
			drawACity (g, XCenter2, YCenter2, aHex, Location.CENTER_CITY_LOC, RevenueCenterType.SINGLE_CITY, aCityColor,
					aTileOrient, false, hexXc, hexYc, 1);
			drawACity (g, XCenter3, YCenter3, aHex, Location.CENTER_CITY_LOC, RevenueCenterType.SINGLE_CITY, aCityColor,
					aTileOrient, true, hexXc, hexYc, 2);
			break;

		case RevenueCenterType.QUAD_CITY:
			XCenter1 = Xc - temp;
			YCenter1 = Yc - temp;
			XCenter2 = Xc + temp;
			YCenter2 = Yc - temp;
			XCenter3 = Xc - temp;
			YCenter3 = Yc + temp;
			XCenter4 = Xc + temp;
			YCenter4 = Yc + temp;
			XB1 = Double.valueOf (XCenter1).intValue ();
			YB1 = Double.valueOf (YCenter1 - temp).intValue ();
			XB2 = Double.valueOf (XCenter2).intValue ();
			YB2 = YB1;
			XB3 = Double.valueOf (XCenter2 + temp).intValue ();
			YB3 = Double.valueOf (YCenter2).intValue ();
			XB4 = XB3;
			YB4 = Double.valueOf (YCenter4).intValue ();
			XB5 = XB2;
			YB5 = Double.valueOf (YCenter4 + temp).intValue ();
			XB6 = XB1;
			YB6 = YB5;
			XB7 = Double.valueOf (XCenter3 - temp).intValue ();
			YB7 = YB4;
			XB8 = XB7;
			YB8 = YB3;
			drawMultipleCityBox (g, XB1, YB1, XB2, YB2, XB3, YB3, XB4, YB4, XB5, YB5, XB6, YB6, XB7, YB7, XB8, YB8);
			drawACity (g, XCenter1, YCenter1, aHex, Location.CENTER_CITY_LOC, RevenueCenterType.SINGLE_CITY, aCityColor,
					aTileOrient, false, hexXc, hexYc, 0);
			drawACity (g, XCenter2, YCenter2, aHex, Location.CENTER_CITY_LOC, RevenueCenterType.SINGLE_CITY, aCityColor,
					aTileOrient, false, hexXc, hexYc, 1);
			drawACity (g, XCenter3, YCenter3, aHex, Location.CENTER_CITY_LOC, RevenueCenterType.SINGLE_CITY, aCityColor,
					aTileOrient, false, hexXc, hexYc, 2);
			drawACity (g, XCenter4, YCenter4, aHex, Location.CENTER_CITY_LOC, RevenueCenterType.SINGLE_CITY, aCityColor,
					aTileOrient, true, hexXc, hexYc, 3);
			break;
		}
	}

	public void drawMultipleCityBox (Graphics g, int X1, int Y1, int X2, int Y2) {
		int width;
		int height;

		width = X2 - X1;
		height = Y2 - Y1;
		g.setColor (Color.white);
		g.fillRect (X1, Y1, width, height);
		g.setColor (Color.black);
		g.drawRect (X1, Y1, width, height);
	}

	public void drawMultipleCityBox (Graphics g, int X0, int Y0, int X1, int Y1, int X2, int Y2, int X3, int Y3) {
		int xp[];
		int yp[];

		xp = new int [5];
		yp = new int [5];
		xp [0] = X0;
		yp [0] = Y0;
		xp [1] = X1;
		yp [1] = Y1;
		xp [2] = X2;
		yp [2] = Y2;
		xp [3] = X3;
		yp [3] = Y3;
		xp [4] = X0;
		yp [4] = Y0;
		drawMutlipleCityBox (g, xp, yp, 5);
	}

	public void drawMultipleCityBox (Graphics g, int X0, int Y0, int X1, int Y1, int X2, int Y2, int X3, int Y3, int X4,
			int Y4, int X5, int Y5) {
		int xp[];
		int yp[];

		xp = new int [7];
		yp = new int [7];
		xp [0] = X0;
		yp [0] = Y0;
		xp [1] = X1;
		yp [1] = Y1;
		xp [2] = X2;
		yp [2] = Y2;
		xp [3] = X3;
		yp [3] = Y3;
		xp [4] = X4;
		yp [4] = Y4;
		xp [5] = X5;
		yp [5] = Y5;
		xp [6] = X0;
		yp [6] = Y0;
		drawMutlipleCityBox (g, xp, yp, 7);
	}

	public void drawMultipleCityBox (Graphics g, int X0, int Y0, int X1, int Y1, int X2, int Y2, int X3, int Y3, int X4,
			int Y4, int X5, int Y5, int X6, int Y6, int X7, int Y7) {
		int xp[];
		int yp[];

		xp = new int [9];
		yp = new int [9];
		xp [0] = X0;
		yp [0] = Y0;
		xp [1] = X1;
		yp [1] = Y1;
		xp [2] = X2;
		yp [2] = Y2;
		xp [3] = X3;
		yp [3] = Y3;
		xp [4] = X4;
		yp [4] = Y4;
		xp [5] = X5;
		yp [5] = Y5;
		xp [6] = X6;
		yp [6] = Y6;
		xp [7] = X7;
		yp [7] = Y7;
		xp [8] = X0;
		yp [8] = Y0;
		drawMutlipleCityBox (g, xp, yp, 9);
	}

	public void drawMutlipleCityBox (Graphics g, int xp[], int yp[], int npnts) {
		Shape tPreviousClip;
		Polygon tCityFrame;
		Area tNewClip;
		Area tCityFrameClip;

		tCityFrame = new Polygon (xp, yp, npnts);
		tPreviousClip = g.getClip ();
		tNewClip = new Area (tPreviousClip);
		tCityFrameClip = new Area (tCityFrame);
		tNewClip.intersect (tCityFrameClip);
		g.setClip (tNewClip);
		g.setColor (Color.white);
		g.fillPolygon (tCityFrame);
		g.setColor (Color.black);
		g.drawPolygon (tCityFrame);
		g.setClip (tPreviousClip);
	}

	public void drawTheCity (Graphics g, Color aCityColor, int X1, int Y1, int X2, int Y2, boolean drawValue, Hex aHex,
			int Xc, int Yc, int aTileOrientation, int aTokenIndex) {
		int width;
		int height;
		int tCorpID;
		boolean tDrawToken;
		boolean tIsCorporationBase;
		boolean tIsDestination;

		width = X2 - X1;
		height = Y2 - Y1;

		tDrawToken = false;
		if (stationCount > 0) {
			if (aTokenIndex < stationCount) {
				if (hasMapTokenAtStation (aTokenIndex)) {
					tDrawToken = true;
				}
			}
		}
		if (tDrawToken) {
			corpStations [aTokenIndex].drawToken (g, X1, Y1, width, height);
		} else {
			if (aCityColor != null) {
				g.setColor (aCityColor);
				g.fillOval (X1, Y1, width, height);
			}
			tIsDestination = isDestination ();
			if (tIsDestination) {
				drawDestination (g, X1, Y1, width, height);
			} else {
				tIsCorporationBase = isCorporationBase ();
				if (tIsCorporationBase) {
					tCorpID = getHomeCompanyID ();
					if (!cityHasStation (tCorpID)) {
						drawCorporationBase (g, X1, Y1, width, height);
					}
				}
			}
		}

		/* Draw Frame and Revenue Value for City */
		g.setColor (Color.black);
		g.drawOval (X1, Y1, width, height);
		if (drawValue) {
			drawValue (g, Xc, Yc, aHex, aTileOrientation);
		}
	}

	public void drawSelectionMarker (Graphics g, int Xc, int Yc, Hex aHex, Location aLocation, int aRCType) {
		int X1;
		int Y1;
		int X2;
		int Y2;
		int temp;
		int width;
		Point tDisplace;

		temp = aHex.getCityWidth ();
		tDisplace = aLocation.calcCenter (aHex);
		X1 = Xc - temp;
		Y1 = Yc - temp;
		X2 = Xc + temp;
		Y2 = Yc + temp;
		width = temp * 2;

		switch (aRCType) {
		case RevenueCenterType.SINGLE_CITY:
		case RevenueCenterType.DESTINATION_CITY:
			if (aLocation.getLocation () != Location.CENTER_CITY_LOC) {
				X1 += tDisplace.x;
				X2 += tDisplace.x;
				Y1 += tDisplace.y;
				Y2 += tDisplace.y;
			}
			break;

		case RevenueCenterType.DOUBLE_CITY: /* Double City */
		case RevenueCenterType.TRIPLE_CITY: /* Triple City */
		case RevenueCenterType.QUAD_CITY: /* Four City */
			if (aLocation.getLocation () != Location.CENTER_CITY_LOC) {
				X1 += tDisplace.x;
				X2 += tDisplace.x;
				Y1 += tDisplace.y;
				Y2 += tDisplace.y;
			}
			X1 -= temp;
			Y1 -= temp;
			X2 += temp;
			Y2 += temp;
			width = width * 2;
			break;
		}
		g.setColor (Color.ORANGE);
		g.drawRect (X1, Y1, width, width);
		g.drawLine (X1, Y1, X2, Y2);
		g.drawLine (X1, Y2, X2, Y1);
		X1++;
		X2++;
		g.setColor (Color.BLACK);
		g.drawRect (X1, Y1, width, width);
		g.drawLine (X1, Y1, X2, Y2);
		g.drawLine (X1, Y2, X2, Y1);
		X1--;
		X2--;
		Y1++;
		Y2++;
		g.setColor (Color.BLACK);
		g.drawRect (X1, Y1, width, width);
		g.drawLine (X1, Y1, X2, Y2);
		g.drawLine (X1, Y2, X2, Y1);
	}

	public int firstFreeStation () {
		int tFirstFree;
		int tIndex;

		tFirstFree = NOT_VALID_STATION;
		if (canPlaceStation ()) {
			for (tIndex = 0; (tIndex < stationCount) && (tFirstFree == NOT_VALID_STATION); tIndex++) {
				if (hasNoMapTokenAtStation (tIndex)) {
					tFirstFree = tIndex;
				}
			}
		}

		return tFirstFree;
	}

	public int getFreeStationCount () {
		int tFreeCount;
		int tIndex;

		tFreeCount = 0;
		for (tIndex = 0; tIndex < stationCount; tIndex++) {
			if (hasNoMapTokenAtStation (tIndex)) {
				tFreeCount++;
			}
		}

		return tFreeCount;
	}

	public int getMaxStations () {
		return type.getMaxStations ();
	}

	public int getMaxStations (int aRevenueCenterType) {
		RevenueCenterType cityType;

		cityType = new RevenueCenterType (aRevenueCenterType);
		
		return cityType.getMaxStations ();
	}

	public String getNumberToString () {
		return Integer.valueOf (stationCount).toString ();
	}

	@Override
	public int getRevenue (int aPhase) {
		return super.getRevenue (aPhase);
	}

	public int getRevenuePhaseIndex (int aIndex) {
		return revenues.getPhaseIndex (aIndex);
	}

	public int getRevenueValueIndex (int aIndex) {
		return revenues.getValueIndex (aIndex);
	}

	public MapToken getToken (int aStationIndex) {
		MapToken retValue = MapToken.NO_MAP_TOKEN;

		if (canPlaceStation ()) {
			if (stationIndexInRange (aStationIndex)) {
				retValue = corpStations [aStationIndex];
			}
		}

		return (retValue);
	}

	public int getStationCount () {
		return stationCount;
	}

	@Override
	public boolean isDestination () {
		return type.isDestination ();
	}

	@Override
	public void loadBaseStates (XMLNode aMapCellNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (baseParsingRoutine, this);
		tXMLNodeList.parseXMLNodeList (aMapCellNode, EN_CORPORATE_BASE);
	}

	ParsingRoutineI baseParsingRoutine = new ParsingRoutineIO () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode, Object aMetaObject) {
			int tLocation;
			String tAbbrev;
			MapCell tMapCell;
			TokenCompany tTokenCompany;

			City tCity;

			tCity = (City) aMetaObject;
			tAbbrev = aChildNode.getThisAttribute (Corporation.AN_ABBREV);
			tLocation = aChildNode.getThisIntAttribute (Location.AN_LOCATION);
			
			if (location.getLocation () == tLocation) {
				tMapCell = tCity.cityInfo.getMapCell ();
				tTokenCompany = tMapCell.getTokenCompany (tAbbrev);
				if (tTokenCompany == TokenCompany.NO_TOKEN_COMPANY) {
					logger.info ("Did not find a Token Company with abbrev " + tAbbrev);
				} else {
					tCity.setCorporationHome (tTokenCompany);
				}
			}
		}
	};

	@Override
	public void loadStationsStates (XMLNode aMapCellNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (tokenParsingRoutine, this);
		tXMLNodeList.parseXMLNodeList (aMapCellNode, EN_CORPORATE_STATION);
	}

	ParsingRoutineI tokenParsingRoutine = new ParsingRoutineIO () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode, Object aMetaObject) {
			int tLocation;
			int tIndex;
			int tTokenIndex;
			String tAbbrev;
			MapCell tMapCell;
			TokenCompany tTokenCompany;
			MapToken tMapToken;

			City tCity;

			tCity = (City) aMetaObject;
			tAbbrev = aChildNode.getThisAttribute (Corporation.AN_ABBREV);
			tTokenIndex = aChildNode.getThisIntAttribute (Tokens.AN_TOKEN_INDEX);
			tLocation = aChildNode.getThisIntAttribute (Location.AN_LOCATION);
			tIndex = aChildNode.getThisIntAttribute (AN_STATION_INDEX);
			
			if (tCity.canPlaceStation ()) {
				if (location.getLocation () == tLocation) {
					tMapCell = tCity.cityInfo.getMapCell ();
					tTokenCompany = tMapCell.getTokenCompany (tAbbrev);
					if (tTokenCompany == TokenCompany.NO_TOKEN_COMPANY) {
						logger.info ("Did not find a Token Company with abbrev " + tAbbrev);
					} else {
						tMapToken = (MapToken) tTokenCompany.getTokenAt (tTokenIndex);
						tCity.setStation (tIndex, tMapToken);
					}
				}
			}
		}
	};

	public boolean placeStation (MapToken aStation) {
		return placeStation (aStation, cityInfo.getMapCell ());
	}

	public boolean placeStation (MapToken aStation, MapCell aMapCell) {
		int tIndex;
		int tDoMore;
		boolean tPlacedStation;

		tPlacedStation = false;
		tDoMore = -1;
		for (tIndex = 0; (tIndex < stationCount) && (tDoMore == -1); tIndex++) {
			if (hasNoMapTokenAtStation (tIndex)) {
				tPlacedStation = true;
				tDoMore = tIndex;
				corpStations [tIndex] = aStation;
				corpStations [tIndex].setMapCell (aMapCell);
				corpStations [tIndex].setLocation (getLocation ());
			} else if (corpStations [tIndex].isSameCompany (aStation)) {
				/* Oops, already have this company station on this tile/hex */
				tDoMore = tIndex;
			}
		}

		return tPlacedStation;
	}

	@Override
	public void printlog () {
		int tIndex;

		super.printlog ();
		System.out.println ("City size " + stationCount);		// PRINTLOG method
		for (tIndex = 0; tIndex < stationCount; tIndex++) {
			if (hasNoMapTokenAtStation (tIndex)) {
				System.out.println ("City at index " + tIndex + " has no Stations");
			} else {
				corpStations [tIndex].printlog ();
			}
		}
	}

	public boolean setStation (MapToken aStation) {
		int tFirstFree;
		boolean tStationSet;

		tFirstFree = firstFreeStation ();
		if (tFirstFree >= 0) {
			tStationSet = setStation (tFirstFree, aStation);
		} else {
			tStationSet = false;
			logger.error ("No Free Station Found.");
		}

		return tStationSet;
	}

	public boolean setStation (int aStationIndex, MapToken aStation) {
		boolean tStationSet;
		MapCell tMapCell;

		tStationSet = false;
		if (canPlaceStation ()) {
			if (stationIndexInRange (aStationIndex)) {
				if (!mapCellHasStation (aStation)) {
					tMapCell = cityInfo.getMapCell ();
					corpStations [aStationIndex] = aStation;
					corpStations [aStationIndex].placeToken (tMapCell, getLocation ());
					cityInfo.clearCorporationOnMapCell ();
					tStationSet = true;
				} else {
					logger.error ("City Has Station - Placement of new Station Failed");
				}
			} else {
				logger.error ("Station Index " + aStationIndex + " Out of Range");
			}
		} else {
			logger.error ("Can Place Station Failed");
		}

		return tStationSet;
	}

	public void setValues (int aType, int aNumber, int aID, int aLocation, String aName, int aValue) {
		super.setValues (aType, aID, aLocation, aName, aValue);
		setStationCount (aNumber);
	}

	public void setStationCount (int aNumber) {
		int tIndex;

		if (aNumber == 0) {
			aNumber = type.getStationCount ();
		}
		stationCount = aNumber;
		if (aNumber > 0) {
			corpStations = new MapToken [aNumber];
			for (tIndex = 0; tIndex < aNumber; tIndex++) {
				setMapTokenAt (tIndex);
			}
		}
	}

	public boolean stationIndexInRange (int aStationIndex) {
		return ((aStationIndex >= 0) && (aStationIndex < stationCount));
	}

	@Override
	public String getTokenToolTip () {
		String tTokenToolTip;

		tTokenToolTip = GUI.EMPTY_STRING;
		if (stationCount > 0) {
			for (MapToken tMapToken : corpStations) {
				if (tMapToken != MapToken.NO_MAP_TOKEN) {
					if (!(GUI.EMPTY_STRING.equals (tTokenToolTip))) {
						tTokenToolTip += ",";
					}
					tTokenToolTip += tMapToken.getCorporationAbbrev ();
				}
			}
		}

		return tTokenToolTip;
	}

	@Override
	public boolean isOpen () {
		return (getFreeStationCount () > 0);
	}
}
