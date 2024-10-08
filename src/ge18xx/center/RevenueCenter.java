package ge18xx.center;

import java.awt.Graphics;
import java.awt.Point;

import org.w3c.dom.NodeList;

//
//  RevenueCenter.java
//  Java_18XX
//
//  Created by Mark Smith on 12/29/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.company.Corporation;
import ge18xx.company.Token;
import ge18xx.company.TokenCompany;
import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.tiles.Feature;
import ge18xx.tiles.Feature2;
import ge18xx.tiles.TileType;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public abstract class RevenueCenter extends Feature implements Cloneable {
	public static final ElementName EN_REVENUE_CENTER = new ElementName ("RevenueCenter");
	public static final ElementName EN_CORPORATE_BASE = new ElementName ("CorporationBase");
	public static final AttributeName AN_TYPE = new AttributeName ("type");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final RevenueCenter NO_CENTER = null;
	public static final String NO_NAME = GUI.EMPTY_STRING;
	public static final int NO_ID = 0;
	public static final int NO_ROTATION = 0;
	public static final int NO_VALUE = 0;
	public static final int STATION_NOT_POSSIBLE = -1;
	public static final int MAX_TRAIN_COUNT = 6;
	int id;
	String name;
	Revenues revenues;
	RevenueCenterType type;
	CityInfo cityInfo;
	TileType tileType;
	boolean selectedForTrain [] = new boolean [MAX_TRAIN_COUNT];
	boolean temporary;

	public RevenueCenter () {
		TileType noTileType;

		noTileType = new TileType ();

		setValues (RevenueCenterType.NO_REVENUE_CENTER, NO_ID, Location.NO_LOCATION, NO_NAME, NO_VALUE);
		setTileType (noTileType);
	}

	public RevenueCenter (int aType, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
		setValues (aType, aID, aLocation, aName, aValue);
		setTileType (aTileType);
	}

	public RevenueCenter (RevenueCenterType aRCType, int aID, int aLocation, String aName, 
							int aValue, TileType aTileType) {
		setValues (aRCType, aID, aLocation, aName, aValue);
		setTileType (aTileType);
	}

	public RevenueCenter (RevenueCenter aRC) {
		int tRevenueCount;

		tRevenueCount = aRC.getRevenueCount ();
		if (tRevenueCount == 1) {
			setValues (aRC.type, aRC.id, aRC.getLocationToInt (), aRC.name, aRC.getRevenue (Revenue.ALL_PHASES));
		} else {
			setValues (aRC.type, aRC.id, aRC.getLocationToInt (), aRC.name, aRC.getRevenue (1));
		}
		setRevenueLocation (aRC.revenues.getLocation ());
		setTileType (aRC.getTileType ());
	}

	public RevenueCenter (XMLNode aNode) {
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		String tRevenueCenterTypeName;
		String tRevenueCenterName;
		int tID;
		int tLocation;
		int tChildrenCount;
		int tChildrenIndex;
		RevenueCenterType tRevenueCenterType;

		tRevenueCenterTypeName = aNode.getThisAttribute (AN_TYPE);
		if (tRevenueCenterTypeName == RevenueCenterType.NO_REVENUE_CENTER_TYPE_NAME) {
			tRevenueCenterType = RevenueCenterType.NO_REVENUE_CENTER_TYPE;
		} else {
			tRevenueCenterType = new RevenueCenterType (tRevenueCenterTypeName);
		}
		tRevenueCenterName = aNode.getThisAttribute (AN_NAME);
		tID = aNode.getThisIntAttribute (AN_ID, NO_ID);
		tLocation = aNode.getThisIntAttribute (Location.AN_LOCATION, Location.CENTER_CITY_LOC);
		if (tRevenueCenterType != RevenueCenterType.NO_REVENUE_CENTER_TYPE) {
			setValues (tRevenueCenterType, tID, tLocation, tRevenueCenterName, NO_VALUE);
		} else {
			setValues (RevenueCenterType.NO_REVENUE_CENTER, NO_ID, Location.NO_LOCATION, NO_NAME, NO_VALUE);
		}
		tChildren = aNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		if (tChildrenCount > 0) {
			for (tChildrenIndex = 0; tChildrenIndex < tChildrenCount; tChildrenIndex++) {
				tChildNode = new XMLNode (tChildren.item (tChildrenIndex));
				tChildName = tChildNode.getNodeName ();
				if (Revenues.EN_REVENUE.equals (tChildName)) {
					revenues.load (tChildNode);
				}
			}
		}
	}

	public void addRevenue (int aValue, int aPhase) {
		revenues.addRevenue (aValue, aPhase);
	}

	public void appendTokensState (XMLDocument aXMLDocument, XMLElement aMapCellElement) {
		/* Revenue Center will never have a Token to Append */
	}

	@Override
	public boolean bleedThroughJustStarting () {
		return false;
	}

	public boolean canPlaceStation () {
		return (false);
	}

	public boolean cityHasStation (int aCorpID) {
		return false;
	}

	public void setTemporary (boolean aTemporary) {
		temporary = aTemporary;
	}
	
	public boolean isTemporary () {
		return temporary;
	}
	
	public void appendCorporationBase (XMLDocument aXMLDocument, XMLElement aMapCellElement) {
		XMLElement tCorporationBaseElement;
		String tCorporationAbbrev;

		tCorporationBaseElement = aXMLDocument.createElement (EN_CORPORATE_BASE);
		tCorporationAbbrev = cityInfo.getCorporationAbbrev ();
		if (tCorporationAbbrev != Corporation.NO_ABBREV) {
			if (tCorporationAbbrev.length () > 0) {
				tCorporationBaseElement.setAttribute (Corporation.AN_ABBREV, tCorporationAbbrev);
				tCorporationBaseElement.setAttribute (AN_LOCATION, location.getLocation ());
				aMapCellElement.appendChild (tCorporationBaseElement);
			}
		}
	}

	public boolean hasAnyCorporationBase () {
		boolean tHasAnyStation;

		if (validCityInfo ()) {
			tHasAnyStation = cityInfo.isCorporationBase ();
		} else {
			tHasAnyStation = false;
		}

		return tHasAnyStation;
	}

	public boolean cityHasAnyStation () {
		return false;
	}

	public boolean cityHasStation (Token aToken) {
		return false;
	}

	public void clearAllSelected () {
		int tIndex;

		for (tIndex = 0; tIndex < MAX_TRAIN_COUNT; tIndex++) {
			setSelected (false, tIndex);
		}
	}

	public boolean clearCityInfoCorporation (Corporation aCorporation) {
		boolean tClearedCorporation;

		tClearedCorporation = cityInfo.clearCorporation (aCorporation);
		
		return tClearedCorporation;
	}

	public void clearCityInfoMapCell () {
		cityInfo.clearMapCell ();
	}

	public void clearCityInfoRevenueCenter () {
		cityInfo.clearRevenueCenter ();
	}

	public void clearCorporation () {
		cityInfo.clearCorporation ();
	}

	@Override
	public RevenueCenter clone () {
		RevenueCenter tRevenueCenter;
		
		tRevenueCenter = (RevenueCenter) super.clone ();
		tRevenueCenter.id = id;
		tRevenueCenter.name = name;
		tRevenueCenter.revenues = revenues.clone ();
		tRevenueCenter.type = type.clone ();
		tRevenueCenter.cityInfo = cityInfo.clone ();
		for (int tTrainIndex = 0; tTrainIndex < MAX_TRAIN_COUNT; tTrainIndex++) {
			tRevenueCenter.selectedForTrain [tTrainIndex] = selectedForTrain [tTrainIndex];
		}

		return tRevenueCenter;
	}

	public boolean containingPoint (Point aPoint, Hex aHex, int XCenter, int YCenter, int aTileOrient) {
		return false;
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tRevenueElement;

		tXMLElement = aXMLDocument.createElement (EN_REVENUE_CENTER);
		tXMLElement.setAttribute (AN_TYPE, type.getName ());
		tXMLElement.setAttribute (AN_ID, id);
		tXMLElement.setAttribute (Location.AN_LOCATION, getLocationToString ());
		tXMLElement.setAttribute (AN_NAME, name);
		if (revenues != Revenues.NO_REVENUES) {
			tRevenueElement = revenues.createElement (aXMLDocument);
			if (tRevenueElement != XMLElement.NO_XML_ELEMENT) {
				tXMLElement.appendChild (tRevenueElement);
			}
		}

		return tXMLElement;
	}

	public void draw (Graphics aGraphics, int aXc, int aYc, Hex aHex, boolean onTile, Feature2 aSelectedFeature) {
		draw (aGraphics, aXc, aYc, NO_ROTATION, aHex, onTile, aSelectedFeature);
		drawName (aGraphics, aXc, aYc, aHex);
	}

	public void drawDestination (Graphics aGraphics, int aX1, int aY1, int aWidth, int aHeight) {
		cityInfo.drawDestination (aGraphics, aX1, aY1, aWidth, aHeight, false);
	}
	
	public void drawCorporationBase (Graphics aGraphics, int aX1, int aY1, int aWidth, int aHeight) {
		cityInfo.drawCorporationBase (aGraphics, aX1, aY1, aWidth, aHeight, true);
	}

	public void drawName (Graphics aGraphics, int aXc, int aYc, Hex aHex) {
		if (validCityInfo ()) {
//			cityInfo.drawName (aGraphics, aXc, aYc, aHex);
		}
	}

	public void drawValue (Graphics aGraphics, int aXC, int aYC, Hex aHex, int aTileOrient) {
		if (revenues.isValidLocation ()) {
			revenues.draw (aGraphics, aXC, aYC, aHex, aTileOrient, tileType);
		}
	}

	public String getCIName () {
		String tCityInfoName;

		if (validCityInfo ()) {
			tCityInfoName = cityInfo.getName ();
		} else {
			tCityInfoName = NO_NAME;
		}

		return tCityInfoName;
	}

	public CityInfo getCityInfo () {
		return cityInfo;
	}

	public String getCityName () {
		String tCityName;

		if (validCityInfo ()) {
			tCityName = cityInfo.getName ();
		} else {
			tCityName = null;
		}

		return tCityName;
	}

	public int getCenterCount () {
		return type.getCenterCount ();
	}

	protected boolean validCityInfo () {
		return (cityInfo != CityInfo.NO_CITY_INFO);
	}

	public TokenCompany getBaseCorporation () {
		TokenCompany tBaseCompany;

		if (validCityInfo ()) {
			tBaseCompany = cityInfo.getBaseCompany ();
		} else {
			tBaseCompany = TokenCompany.NO_TOKEN_COMPANY;
		}

		return tBaseCompany;
	}

	public Corporation getCorporation () {
		Corporation tCorporation;

		if (validCityInfo ()) {
			tCorporation =  cityInfo.getCorporation ();
		} else {
			tCorporation = Corporation.NO_CORPORATION;
		}

		return tCorporation;
	}

	public String getDestCompanyAbbrev () {
		String tAbbrev;

		tAbbrev = GUI.NULL_STRING;
		if (isCorporationBase ()) {
			if (isDestination ()) {
				tAbbrev = cityInfo.getCorporationAbbrev ();
			}
		}

		return tAbbrev;
	}

	public String getHomeCompanyAbbrev () {
		String tAbbrev;

		tAbbrev = Corporation.NO_ABBREV;
		if (isCorporationBase ()) {
			if (! isDestination ()) {
				tAbbrev = cityInfo.getCorporationAbbrev ();
			}
		}

		return tAbbrev;
	}

	public int getHomeCompanyID () {
		int tCompanyID = 0;

		if (isCorporationBase ()) {
			tCompanyID = cityInfo.getCorporationID ();
		}

		return tCompanyID;
	}

	public int getID () {
		return id;
	}

	public String getIDToString () {
		return (Integer.valueOf (id).toString ());
	}

	public String getName () {
		return name;
	}

	public int getRevenueLocation () {
		return revenues.getLocationToInt ();
	}

	public int getRevenue (int aPhase) {
		int tTotalRevenue;
		int tBonusRevenue;
		int tBaseRevenue;
		
		tBaseRevenue = revenues.getValue (aPhase);
		tBonusRevenue = getBonusRevenue ();
		tTotalRevenue = tBaseRevenue + tBonusRevenue;
		
		return tTotalRevenue;
	}

	public int getBonusRevenue () {
		int tBonusRevenue;
		
		tBonusRevenue = 0;
		tBonusRevenue = cityInfo.getBonusRevenue ();
		
		return tBonusRevenue;
	}
	
	public Revenues getRevenues () {
		return revenues;
	}

	public String getRevenueToString (int aPhase) {
		int tRevenue;
		
		tRevenue = revenues.getValue (aPhase);
		
		return (Integer.valueOf (tRevenue).toString ());
	}

	public String getRevenueToString () {
		int tRevenue;
		
		tRevenue = revenues.getValue ();
		
		return (Integer.valueOf(tRevenue).toString ());
	}

	public int getRevenueCount () {
		return revenues.getRevenueCount ();
	}

	public boolean getSelected (int aIndex) {
		return selectedForTrain [aIndex];
	}

	public TileType getTileType () {
		return tileType;
	}

	public RevenueCenterType getRevenueCenterType () {
		return type;
	}

	public int getTypeToInt () {
		return type.getType ();
	}

	public boolean isCity () {
		return type.isCity ();
	}
	
	public boolean isARunThroughCity () {
		return type.isARunThroughCity ();
	}
	
	public boolean isCorporationBase () {
		boolean tIsCorporationBase;

		if (validCityInfo ()) {
			tIsCorporationBase = cityInfo.isCorporationBase ();
		} else {
			tIsCorporationBase = false;
		}

		return tIsCorporationBase;
	}

	public boolean isDestination () {
		return type.isDestination ();
	}

	public boolean isTown () {
		return type.isTown ();
	}

	public boolean isDotTown () {
		return type.isDotTown ();
	}

	public boolean isSingleSelected (Location aLocation, Feature2 aSelectedFeature) {
		Location tFeatureLocation;
		int tLocationInt;
		int tFeatureLocationInt;
		boolean tIsSingleSelected;

		if (aSelectedFeature.isNoLocation ()) {
			tIsSingleSelected = false;
		} else {
			if (aSelectedFeature.isNoLocation2 ()) {
				if (aLocation.isNoLocation ()) {
					tIsSingleSelected = false;
				} else {
					tFeatureLocation = aSelectedFeature.getLocation ();
					tLocationInt = aLocation.getLocation ();
					tFeatureLocationInt = tFeatureLocation.getLocation ();
					if (tLocationInt == tFeatureLocationInt) {
						tIsSingleSelected = true;
					} else {
						tIsSingleSelected = false;
					}
				}
			} else {
				tIsSingleSelected = false;
			}
		}

		return tIsSingleSelected;
	}

	public void loadBaseStates (XMLNode aMapCellNode) {
		/* Should not get here... can only load Stations onto City Revenue Center */
	}

	public void loadStationsStates (XMLNode aMapCellNode) {
		/* Should not get here... can only load Stations onto City Revenue Center */
	}

	public boolean mapCellHasStation (Token aToken) {
		return cityInfo.mapCellHasStation (aToken);
	}

	@Override
	public void printlog () {
		System.out.println ("Revenue Center id " + id + " Name " + name);	// PRINTLOG method
		super.printlog ();
		cityInfo.printCityInfo ();
	}

	/**
	 * This method should be the normal way to set the City Info when upgrading a Tile, to copy each City's specific
	 * data, like the Corporation Bases so in situations where a single MapCell has two different Cities, with different
	 * Corporation Bases to be promoted properly. 1830 NYC, and 1856 Toronto are examples.
	 * 
	 * @param aCityInfo The City info to Clone into this Revenue Center's City Info object.
	 * 
	 */
	public void setCityInfo (CityInfo aCityInfo) {
		// Need to Clone this City Info, rather than simply save it... really should have calling routine clone it and pass the clone in
		// Otherwise for 1830, the NYC Tile get's it home Base for NYNH put in the wrong spot.
		
		cityInfo = aCityInfo.clone ();
	}

	/**
	 * This method should only be used in JUnit Test Cases to set the City Info to what is passed in, 
	 * and not Clone it. Cloning the City Info allows two Cities on the Same Tile, with different Homes
	 * Like NYC in 1830, and Toronto in 1856 to maintain different Corporation Homes in each City.
	 * 
	 * @param aCityInfo The City Info to copy into this Revenue Center's City Info object.
	 * 
	 */
	public void setNoCloneCityInfo (CityInfo aCityInfo) {
		cityInfo = aCityInfo;
	}
	
	public boolean setCorporationHome (Corporation aCorporation) {
		boolean tCorporationHomeSet;
		
		tCorporationHomeSet = true;
		setupCityInfo ();
		cityInfo.setCorporationHome (aCorporation, this);
		
		return tCorporationHomeSet;
	}

	public boolean removeHome (Corporation aCorporation) {
		boolean tHomeRemoved;
		
		tHomeRemoved = cityInfo.clearCorporation (aCorporation);;
		
		return tHomeRemoved;
	}
	
	public void setMapCell (MapCell aMapCell) {
		setupCityInfo ();
		cityInfo.setMapCell (aMapCell);
	}

	public void setupCityInfo () {
		if (! validCityInfo ()) {
			cityInfo = new CityInfo ();
		}
	}

	public void setRevenueLocation (int aLocation) {
		revenues.setLocation (aLocation);
	}

	public void setRevenueLocation (Location aLocation) {
		revenues.setLocation (aLocation);
	}

	public void setRevenues (Revenues aRevenues) {
		revenues = aRevenues;
	}

	public void setSelected (boolean aSelectedState, int aIndex) {
		selectedForTrain [aIndex] = aSelectedState;
	}

	public boolean setStation (Token aStation) {
		return false;
	}

	public boolean setStation (int aIndex, Token aStation) {
		return false;
	}

	public void setTileType (TileType aTileType) {
		tileType = aTileType;
	}

	public void setValues (int aType, int aID, int aLocation, String aName, Revenues aRevenues) {
		setValues (new RevenueCenterType (aType), aID, aLocation, aName, aRevenues);
	}

	public void setValues (int aType, int aID, int aLocation, String aName, int aValue) {
		setValues (new RevenueCenterType (aType), aID, aLocation, aName, aValue, Revenue.ALL_PHASES);
	}

	public void setValues (int aType, int aID, int aLocation, String aName, int aValue, int aPhase) {
		setValues (new RevenueCenterType (aType), aID, aLocation, aName, aValue, aPhase);
	}

	public void setValues (RevenueCenterType aType, int aID, int aLocation, String aName, int aValue) {
		setValues (aType, aID, aLocation, aName, aValue, Revenue.ALL_PHASES);
	}

	public void setValues (RevenueCenterType aType, int aID, int aLocation, String aName, int aValue, int aPhase) {
		Revenues tRevenues;

		tRevenues = new Revenues (aValue, Location.CENTER_CITY_LOC, aPhase);
		setValues (aType, aID, aLocation, aName, tRevenues);
	}

	public void setValues (RevenueCenterType aType, int aID, int aLocation, String aName, Revenues aRevenues) {
		revenues = new Revenues (aRevenues);
		type = aType;
		name = aName;
		setLocation (aLocation);
		id = aID;
		cityInfo = new CityInfo ();
		cityInfo.setRevenueCenter (this);
		clearAllSelected ();
	}

	public void toggleSelected (int aIndex) {
		setSelected (!selectedForTrain [aIndex], aIndex);
	}

	public String getToolTip () {
		String tToolTip;

		tToolTip = GUI.EMPTY_STRING;
		if (type.isPrivateRailway ()) {
			tToolTip = "Private Railway: " + name + "<br>";
		}

		return tToolTip;
	}

	public String getTokenToolTip () {
		return GUI.EMPTY_STRING;
	}

	@Override
	public boolean isOpen () {
		return false;
	}

	public XMLElement getElement (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (aElementName);
		tXMLElement.setAttribute (Location.AN_LOCATION, getLocationToInt ());

		return tXMLElement;
	}

	public boolean withBaseForCorp (Corporation aCorporation) {
		boolean tBaseForCorp;

		tBaseForCorp = cityInfo.withBaseForCorp (aCorporation);

		return tBaseForCorp;
	}

	// ABSTRACT Methods that should be overloaded by the sub-classes
	public abstract boolean cityOrTown ();

	public abstract void draw (Graphics aGraphics, int aXc, int aYc, int aTileOrient, Hex aHex, boolean aOnTile,
			Feature2 aSelectedFeature);

	public abstract boolean cityHasOpenStation ();
}
