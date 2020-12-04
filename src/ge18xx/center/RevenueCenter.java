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

import java.awt.*;

import org.w3c.dom.*;

public abstract class RevenueCenter extends Feature implements Cloneable {
	public static final ElementName EN_REVENUE_CENTER = new ElementName ("RevenueCenter");
	public static final AttributeName AN_TYPE = new AttributeName ("type");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_ID = new AttributeName ("id");
	static final int NO_ROTATION = 0;
	public static final int NO_ID = 0;
	static final int NO_VALUE = 0;
	static final int STATION_NOT_POSSIBLE = -1;
	static final String NO_NAME = "";
	int id;
	String name;
	Revenues revenues;
	RevenueCenterType type; 
	CityInfo cityInfo;
	boolean selectedForTrain [] = new boolean [4];
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
		if (tRevenueCenterTypeName == null) {
			tRevenueCenterType = null;
		} else {
			tRevenueCenterType = new RevenueCenterType (tRevenueCenterTypeName);
		}
		tRevenueCenterName = aNode.getThisAttribute (AN_NAME);
		tID = aNode.getThisIntAttribute (AN_ID, NO_ID);
		tLocation = aNode.getThisIntAttribute (Location.AN_LOCATION, Location.CENTER_CITY_LOC);
		if (tRevenueCenterType != null) {
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
	
	public boolean cityHasAnyStation () {
		return false;
	}
	
	public boolean cityHasStation (Token aToken) {
		return false;
	}
	
	public void clearAllSelected () {
		int tIndex;
		
		for (tIndex = 0; tIndex < 4; tIndex++) {
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
	
	public RevenueCenter clone () {
		RevenueCenter tRevenueCenter = (RevenueCenter) super.clone ();
		tRevenueCenter.id = id;
		tRevenueCenter.name = name;
		tRevenueCenter.revenues = revenues;
		tRevenueCenter.type = type.clone ();
		tRevenueCenter.cityInfo = cityInfo;
		tRevenueCenter.selectedForTrain [0] = selectedForTrain [0];
		tRevenueCenter.selectedForTrain [1] = selectedForTrain [1];
		tRevenueCenter.selectedForTrain [2] = selectedForTrain [2];
		tRevenueCenter.selectedForTrain [3] = selectedForTrain [3];
	
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
		if (revenues != null) {
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
		if (cityInfo != null) {
//			cityInfo.drawName (g, Xc, Yc, aHex);
		}
	}
	
	public void drawValue (Graphics g, int Xc, int Yc, Hex aHex, int aTileOrient) {
		revenues.draw (g, Xc, Yc, aHex, aTileOrient, tileType);
	}
	
	public String getCIName () {
		String tCityInfoName = NO_NAME;
		
		if (cityInfo != null) {
			tCityInfoName = cityInfo.getName ();
		}
		
		return tCityInfoName;
	}
	
	public CityInfo getCityInfo () {
		return cityInfo;
	}
	
	public String getCityName () {
		if (cityInfo != null) {
			return cityInfo.getName ();
		} else {
			return null;
		}
	}
	
	public int getCenterCount () {
		return (type.getCenterCount ());
	}
	
	public TokenCompany getTokenCorporation () {
		if (cityInfo == null) {
			return null;
		} else {
			return cityInfo.getTokenCorporation ();
		}
	}
	
	public Corporation getCorporation () {
		if (cityInfo == null) {
			return null;
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
		return (revenues.getValue ());
	}
	
	public Revenues getRevenues () {
		return revenues;
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
		return (cityInfo != null);
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
	
	public void printlog () {
		System.out.println ("Revenue Center id " + id + " Name " + name);
		super.printlog ();
		cityInfo.printCityInfo();
	}
	
	public void setCityInfo (CityInfo aCityInfo) {
		cityInfo = aCityInfo.clone ();
	}
	
	public void setCorporation (Corporation aCorporation) {
		if (cityInfo == null) {
			cityInfo = new CityInfo ();
		}
		cityInfo.setCorporation (aCorporation, this);
	}
	
	public void setMapCell (MapCell aMapCell) {
		if (cityInfo == null) {
			cityInfo = new CityInfo ();
		}
		cityInfo.setMapCell (aMapCell);
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
	
	// ABSTRACT Methods that should be overloaded by the sub-classes
	public abstract boolean cityOrTown ();
	public abstract void draw (Graphics g, int Xc, int Yc, int aTileOrient, Hex aHex, boolean onTile, Feature2 aSelectedFeature);

	public String getTokenToolTip () {
		return "";
	}
	
	@Override
	public boolean isOpen() {
		
		return false;
	}

}
