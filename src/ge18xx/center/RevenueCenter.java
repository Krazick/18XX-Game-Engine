package ge18xx.center;

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
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.awt.Graphics;
import java.awt.Point;

import org.w3c.dom.NodeList;

public abstract class RevenueCenter extends Feature implements Cloneable {
	public static final ElementName EN_REVENUE_CENTER = new ElementName ("RevenueCenter");
	public static final ElementName EN_CORPORATE_BASE = new ElementName ("CorporationBase");
	public static final AttributeName AN_TYPE = new AttributeName ("type");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final RevenueCenter NO_CENTER = null;
	public static final int NO_ID = 0;
	static final int NO_ROTATION = 0;
	static final int NO_VALUE = 0;
	static final int STATION_NOT_POSSIBLE = -1;
	static final int MAX_TRAIN_COUNT = 6;
	static final String NO_NAME = "";
	int id;
	String name;
	Revenues revenues;
	RevenueCenterType type; 
	CityInfo cityInfo;
	boolean selectedForTrain [] = new boolean [MAX_TRAIN_COUNT];
	TileType tileType;

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
	
	public RevenueCenter (RevenueCenterType aRCType, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
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
		String tRevenueCenterTypeName, tRevenueCenterName;
		int tID, tLocation;
		int tChildrenCount, tChildrenIndex;
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
		boolean tHasAnyStation = false;
		
		tHasAnyStation = cityInfo.isCorporationBase ();

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
	
	public void clearCityInfoCorporation () {
		cityInfo.clearCorporation ();
	}
	
	public void clearCityInfoCorporation (Corporation aCorporation) {
		cityInfo.clearCorporation (aCorporation);
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
		RevenueCenter tRevenueCenter = (RevenueCenter) super.clone ();
		tRevenueCenter.id = id;
		tRevenueCenter.name = name;
		tRevenueCenter.revenues = revenues;
		tRevenueCenter.type = type.clone ();
		tRevenueCenter.cityInfo = cityInfo;
		for (int tTrainIndex = 0; tTrainIndex < MAX_TRAIN_COUNT; tTrainIndex++) {
			tRevenueCenter.selectedForTrain [tTrainIndex] = selectedForTrain [tTrainIndex];
		}
	
		return tRevenueCenter;
	}
	
	public boolean containingPoint (Point aPoint, Hex aHex, int XCenter, int YCenter, int aTileOrient) {
		boolean tContainingPoint = false;
		
		return tContainingPoint;
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
			if (tRevenueElement != null) {
				tXMLElement.appendChild (tRevenueElement);
			}
		}
		
		return tXMLElement;
	}
	
	public void draw (Graphics g, int Xc, int Yc, Hex aHex, boolean onTile, Feature2 aSelectedFeature) {
		draw (g, Xc, Yc, NO_ROTATION, aHex, onTile, aSelectedFeature);
		drawName (g, Xc, Yc, aHex);
	}
	
	public void drawCorporationBase (Graphics g, int X1, int Y1, int width, int height) {
		if (isCorporationBase ()) {
			if (type.isDestination ()) {
				cityInfo.drawCorporationBase (g, X1, Y1, width, height, false);
			} else {
				cityInfo.drawCorporationBase (g, X1, Y1, width, height, true);
			}
		}
	}
	
	public void drawName (Graphics g, int Xc, int Yc, Hex aHex) {
		if (cityInfo != CityInfo.NO_CITY_INFO) {
//			cityInfo.drawName (g, Xc, Yc, aHex);
		}
	}
	
	public void drawValue (Graphics g, int Xc, int Yc, Hex aHex, int aTileOrient) {
		revenues.draw (g, Xc, Yc, aHex, aTileOrient, tileType);
	}
	
	public String getCIName () {
		String tCityInfoName = NO_NAME;
		
		if (cityInfo != CityInfo.NO_CITY_INFO) {
			tCityInfoName = cityInfo.getName ();
		}
		
		return tCityInfoName;
	}
	
	public CityInfo getCityInfo () {
		return cityInfo;
	}
	
	public String getCityName () {
		if (cityInfo != CityInfo.NO_CITY_INFO) {
			return cityInfo.getName ();
		} else {
			return null;
		}
	}
	
	public int getCenterCount () {
		return (type.getCenterCount ());
	}
	
	public TokenCompany getTokenCorporation () {
		if (cityInfo == CityInfo.NO_CITY_INFO) {
			return TokenCompany.NO_TOKEN_COMPANY;
		} else {
			return cityInfo.getTokenCorporation ();
		}
	}
	
	public Corporation getCorporation () {
		if (cityInfo == CityInfo.NO_CITY_INFO) {
			return Corporation.NO_CORPORATION;
		} else {
			return cityInfo.getCorporation ();
		}
	}
	
	public String getDestCompanyAbbrev () {
		String tAbbrev = null;
		
		if (isCorporationBase ()) {
			if (type.isDestination ()) {
				tAbbrev = cityInfo.getCorporationAbbrev ();
			}
		}
		
		return tAbbrev;
	}
	
	public String getHomeCompanyAbbrev () {
		String tAbbrev = null;
		
		if (isCorporationBase ()) {
			if (! type.isDestination ()) {
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
		return (new Integer (id).toString ());
	}
	
	public String getName () {
		return name;
	}
	
	public int getRevenueLocation () {
		return revenues.getLocationToInt ();
	}
	
	public int getRevenue (int aPhase) {
		return (revenues.getValue (aPhase));
	}
	
	public Revenues getRevenues () {
		return revenues;
	}
	
	public String getRevenueToString (int aPhase) {
		return (new Integer (revenues.getValue (aPhase)).toString ());
	}
	
	public String getRevenueToString () {
		return (new Integer (revenues.getValue ()).toString ());
	}
	
	public int getRevenueCount () {
		return (revenues.getRevenueCount ());
	}
	
	public boolean getSelected (int aIndex) {
		return selectedForTrain [aIndex];
	}
	
	public TileType getTileType () {
		return tileType;
	}
	
	public RevenueCenterType getRevenueCenterType () {
		return (type);
	}
	
	public int getTypeToInt () {
		return (type.getType ());
	}
		
	public boolean isCity () {
		return type.isCity ();
	}
	
	public boolean isCityInfoAvailable () {
		return (cityInfo != CityInfo.NO_CITY_INFO);
	}
	
	public boolean isCorporationBase () {
		boolean tIsCorporationBase;
		
		if (isCityInfoAvailable ()) {
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
	
	public void loadStationsStates (XMLNode aMapCellNode) {
		/* Should not get here... can only load Stations onto City Revenue Center */
	}

	public boolean mapCellHasStation (Token aToken) {
		return cityInfo.mapCellHasStation (aToken);
	}
	
	@Override
	public void printlog () {
		System.out.println ("Revenue Center id " + id + " Name " + name);
		super.printlog ();
		cityInfo.printCityInfo();
	}
	
	public void setCityInfo (CityInfo aCityInfo) {
		cityInfo = aCityInfo.clone ();
	}
	
	public void setCorporation (Corporation aCorporation) {
		setupCityInfo ();
		cityInfo.setCorporation (aCorporation, this);
	}
	
	public void setMapCell (MapCell aMapCell) {
		setupCityInfo ();
		cityInfo.setMapCell (aMapCell);
	}
	
	public void setupCityInfo () {
		if (cityInfo == CityInfo.NO_CITY_INFO) {
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
		return (false);
	}
	
	public boolean setStation (int aIndex, Token aStation) {
		return (false);
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
		setSelected (! selectedForTrain [aIndex], aIndex);
	}

	public String getToolTip () {
		String tToolTip = "";
		
		if (type.isPrivateRailway ()) {
			tToolTip = "Private Railway: " + name + "<br>";
		}

		return tToolTip;
	}
	
	public String getTokenToolTip () {
		return "";
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
		boolean tBaseForCorp = false;
		
		tBaseForCorp = cityInfo.withBaseForCorp (aCorporation);
		
		return tBaseForCorp;
	}

	// ABSTRACT Methods that should be overloaded by the sub-classes
	public abstract boolean cityOrTown ();
	public abstract void draw (Graphics g, int Xc, int Yc, int aTileOrient, Hex aHex, boolean onTile, Feature2 aSelectedFeature);
	public abstract boolean cityHasOpenStation ();
}
