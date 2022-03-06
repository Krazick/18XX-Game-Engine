package ge18xx.center;

//
//  CityInfo.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.company.Corporation;
import ge18xx.company.Token;
import ge18xx.company.TokenCompany;
import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

public class CityInfo implements Cloneable {
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_TYPE = new AttributeName ("type");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_CORP_BASE = new AttributeName ("corpBase");
	public static final ElementName EN_CITY_INFO = new ElementName ("CityInfo");
	public static final CityInfo NO_CITY_INFO = null;
	public static final String NO_NAME = "";
	int id, type, mapX, mapY;
	String name;
	Location nameLocation;
	RevenueCenter center;
	MapCell mapCell;
	Corporation corporation; // Corporation Home Base
	
	public CityInfo () {
		this (0, NO_NAME, null, RevenueCenterType.NO_REVENUE_CENTER);
	}
	
	public CityInfo (int aID, String aName, Location aNameLocation, int aType) {
		id = aID;
		name = aName;
		nameLocation = aNameLocation;
		type = aType;
		clearCorporation ();
		clearMapCell ();
		clearRevenueCenter ();
	}
	
	public CityInfo (XMLNode aChildNode) {
		int tLocation;
		String tCorporationAbbrev;
		
		clearCorporation ();
		clearMapCell ();
		clearRevenueCenter ();
		id = aChildNode.getThisIntAttribute (AN_ID);
		name = aChildNode.getThisAttribute (AN_NAME);
		type = aChildNode.getThisIntAttribute (AN_TYPE);
		tCorporationAbbrev = aChildNode.getThisAttribute (AN_CORP_BASE);
		setCorporation (tCorporationAbbrev);
		tLocation = aChildNode.getThisIntAttribute (Location.AN_LOCATION, Location.CENTER_CITY_LOC);
		nameLocation = new Location (tLocation);
	}
	
	public void clearCorporation () {
		corporation = Corporation.NO_CORPORATION;
	}
	
	public void clearCorporation (Corporation aCorporation) {
		if (corporation == aCorporation) {
			clearCorporation ();
		}
	}
	
	public void clearCorporationOnMapCell () {
		if (mapCell != MapCell.NO_MAP_CELL) {
			mapCell.clearCorporation ();
		}
	}
	
	public void clearMapCell () {
		mapCell = MapCell.NO_MAP_CELL;
	}
	
	public void clearRevenueCenter () {
		center = RevenueCenter.NO_CENTER;
	}
	
	@Override
	public CityInfo clone () {
		CityInfo tCityInfo = CityInfo.NO_CITY_INFO;;
		
		try {
			tCityInfo = (CityInfo) super.clone ();
			
			tCityInfo.id = id;
			tCityInfo.name = name;
			tCityInfo.nameLocation = nameLocation;
			tCityInfo.type = type;
			tCityInfo.center = center;
			tCityInfo.mapCell = mapCell;
			tCityInfo.corporation = corporation;
		} catch (CloneNotSupportedException e) {
			System.err.println ("City Info Clone, Clone Not Supported");
		}
		
		return tCityInfo;
	}
	
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		
		tXMLElement = aXMLDocument.createElement (EN_CITY_INFO);
		tXMLElement.setAttribute (AN_NAME, name);
		tXMLElement.setAttribute (AN_ID, id);
		tXMLElement.setAttribute (AN_TYPE, type);
		tXMLElement.setAttribute (AN_CORP_BASE, getCorporationAbbrev ());
		
		return tXMLElement;
	}
	
	public void drawCorporationBase (Graphics g, int X1, int Y1, int aWidth, int aHeight, boolean aHome) {
		TokenCompany tTokenCompany;
		
		if (corporation != Corporation.NO_CORPORATION) {
			if (! mapCell.hasStation (corporation.getID ())) {
				try {
					tTokenCompany = (TokenCompany) corporation;
					tTokenCompany.drawBase (g, X1, Y1, aWidth, aHeight, aHome);
				} catch (ClassCastException e) {
					
				}
			}
		}
	}
	
	private void drawLabel (Graphics g, int Xc, int Yc, String aLabel) {
		int width, height, X1, Y1;
		Font tCurrentFont, tNewFont;
		
		tCurrentFont = g.getFont ();
		tNewFont = new Font ("Dialog", Font.PLAIN, 10);
		g.setFont (tNewFont);
		width = g.getFontMetrics ().stringWidth (aLabel);
		height = g.getFontMetrics ().getHeight ();
		X1 = Xc - width/2;
		Y1 = Yc + height/2;
		g.setColor (Color.black);
		g.drawString (aLabel, X1, Y1);
		g.setFont (tCurrentFont);
	}
	
	public void drawName (Graphics g, int Xc, int Yc, Hex aHex) {
		int X1, Y1;
		Point xy;
		
		if (name != null) {
			if (! (name.equals (NO_NAME))) {
				if (nameLocation.isNoLocation ()) {
					X1 = Xc;
					Y1 = Yc;
				} else {
					xy = nameLocation.calcCenter (aHex);
					X1 = Xc + xy.x;
					Y1 = Yc + xy.y;
				}
				drawLabel (g, X1, Y1, name);
			}
		}
	}
	
	public void drawPrivateRailway (Graphics g, int Xc, int Yc, Hex aHex) {
		Point tPoint1;
		Point tPoint2;
		Location tLocation1;
		Location tLocation2;
		int tX1, tY1, tX2, tY2;
		int tXLabel, tYLabel;
		int tPointTemp = aHex.getCityWidth ()/3;
		int tWidthHeight;
		Color tTerrainFillColor;
		String tLabel;
		
		if (corporation != Corporation.NO_CORPORATION) {
			tLocation1 = corporation.getHomeLocation1 ();
			tLocation2 = corporation.getHomeLocation2 ();
			tPoint1 = tLocation1.calcCenter (aHex);
			tPoint2 = tLocation2.calcCenter (aHex);
			tX1 = Xc + tPoint1.x;
			tY1 = Yc + tPoint1.y;
			tX2 = Xc + tPoint2.x;
			tY2 = Yc + tPoint2.y;
			g.drawLine (tX1, tY1, tX2, tY2);
			tWidthHeight = tPointTemp * 2;
			if (mapCell != MapCell.NO_MAP_CELL) {
				tTerrainFillColor = mapCell.getBaseTerrainFillColor ();
				g.setColor (tTerrainFillColor);
				g.fillOval (tX1 - tPointTemp, tY1 - tPointTemp, tWidthHeight, tWidthHeight);
				g.fillOval (tX2 - tPointTemp, tY2 - tPointTemp, tWidthHeight, tWidthHeight);
			}
			g.setColor (Color.black);
			g.drawOval (tX1 - tPointTemp, tY1 - tPointTemp, tWidthHeight, tWidthHeight);
			g.drawOval (tX2 - tPointTemp, tY2 - tPointTemp, tWidthHeight, tWidthHeight);
			tXLabel = (tX1 + tX2)/2;
			tYLabel = (tY1 + tY2)/2;
			tLabel = getCorporationAbbrev ();
			tLabel = corporation.getAbbrev ();
			drawLabel (g, tXLabel, tYLabel, tLabel);
		}
	}
	
	public int fieldCount () {
		return 4;
	}
	
	public TokenCompany getTokenCorporation () {
		TokenCompany tTokenCompany;
		
		tTokenCompany = TokenCompany.NO_TOKEN_COMPANY;
		if (corporation != Corporation.NO_CORPORATION) {
			if (! mapCell.hasStation (corporation.getID ())) {
				try {
					tTokenCompany = (TokenCompany) corporation;
				} catch (ClassCastException e) {
					
				}
			}
		}
		
		return tTokenCompany;
	}
	
	public Corporation getCorporation () {
		return corporation;
	}
	
	public String getCorporationAbbrev () {
		if (isCorporationBase ()) {
			return corporation.getAbbrev ();
		} else {
			return "";
		}
	}
	
	public int getCorporationID () {
		if (isCorporationBase ()) {
			return corporation.getID ();
		} else {
			return Corporation.NO_ID;
		}
	}
	
	public int getID () {
		return id;
	}
	
	public String getIDToString () {
		return (new Integer (id).toString ());
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
	
	public String getMapCellID () {
		if (mapCell == MapCell.NO_MAP_CELL) {
			return "NOT YET";
		} else {
			return mapCell.getID ();
		}
	}
	
	public String getName () {
		return name;
	}
	
	public Location getNameLocation () {
		return nameLocation;
	}
	
	public RevenueCenter getRevenueCenter () {
		return center;
	}
	
	public int getType () {
		return type;
	}
	
	public String getTypeToString () {
		return (new Integer (type).toString ());
	}
	
	public boolean isCorporationBase () {
		boolean tIsCorporationBase = false;
		
		if (corporation != Corporation.NO_CORPORATION) {
			if (corporation.isShareCompany ()) {
				tIsCorporationBase = true;
			}
		}
		
		return tIsCorporationBase;
	}
	
	public boolean mapCellHasStation (Token aToken) {
		if (mapCell != MapCell.NO_MAP_CELL) {
			return mapCell.hasStation (aToken);
		} else {
			return false;
		}
	}
	
	public void printCityInfo () {
		System.out.println ("City ID " + id + ", Type " + type + ", Name [" + 
					name + "] on Map Cell " + getMapCellID ());
		if (corporation == Corporation.NO_CORPORATION) {
			System.out.println ("No Corporation Base");
		} else {
			System.out.println ("Base for " + corporation.getAbbrev () + " Corporation");
		}
	}
	
	public void setCorporation (String aCorporationAbbrev) {
		Corporation tCorporation;
		
		if (aCorporationAbbrev != Corporation.NO_ABBREV) {
			tCorporation = mapCell.getCorporation (aCorporationAbbrev);
			setCorporation (tCorporation);
		}
	}
	
	public void setCorporation (Corporation aCorporation) {
		corporation = aCorporation;
	}
	
	public void setCorporation (Corporation aCorporation, RevenueCenter aRevenueCenter) {
		corporation = aCorporation;
		center = aRevenueCenter;
	}
	
	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}
	
	public void setRevenueCenter (RevenueCenter aRevenueCenter) {
		center = aRevenueCenter;
	}
	
	public boolean withBaseForCorp (Corporation aCorporation) {
		boolean tWithBaseForCorp = false;
		
		if (corporation == aCorporation) {
			tWithBaseForCorp = true;
		}
		
		return tWithBaseForCorp;
	}
}
