package ge18xx.center;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;

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
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class CityInfo implements Cloneable {
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_TYPE = new AttributeName ("type");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_CORP_BASE = new AttributeName ("corpBase");
	public static final ElementName EN_CITY_INFO = new ElementName ("CityInfo");
	public static final CityInfo NO_CITY_INFO = null;
	public static final String NO_NAME = "";
	int id;
	int type;
	int mapX;
	int mapY;
	String name;
	Location nameLocation;
	RevenueCenter center;
	MapCell mapCell;
	Corporation corporation; // Corporation Home Base

	/**
	 *
	 */
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

	public void copyCityInfo (CityInfo aCityInfo) {
		id = aCityInfo.getID ();
		name = aCityInfo.getName ();
		nameLocation = aCityInfo.getNameLocation ();
		type = aCityInfo.getType ();
		corporation = aCityInfo.getCorporation ();
		mapCell = aCityInfo.getMapCell ();
		center = aCityInfo.getRevenueCenter ();
	}
	
	public void clearCorporation () {
		corporation = Corporation.NO_CORPORATION;
	}

	public boolean clearCorporation (Corporation aCorporation) {
		boolean tCorporationCleared;
		
		if (corporation == aCorporation) {
			clearCorporation ();
			tCorporationCleared = true;
		} else {
			tCorporationCleared = false;
		}
		
		return tCorporationCleared;
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
		CityInfo tCityInfo = CityInfo.NO_CITY_INFO;


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

	public void drawDestination (Graphics g, int X1, int Y1, int aWidth, int aHeight, boolean aHome) {
		TokenCompany tTokenCompany;
		int tDestinationCompanyID;
		
		tDestinationCompanyID = mapCell.getDestinationCorpID ();
		tTokenCompany = mapCell.getTokenCompanyByID (tDestinationCompanyID);
		if (tTokenCompany != Corporation.NO_CORPORATION) {
			tTokenCompany.drawBase (g, X1, Y1, aWidth, aHeight, aHome);
		}
	}
	
	public void drawCorporationBase (Graphics g, int X1, int Y1, int aWidth, int aHeight, boolean aHome) {
		TokenCompany tTokenCompany;

		if (corporation != Corporation.NO_CORPORATION) {
			if (!mapCell.hasStation (corporation.getID ())) {
				if (corporation.isATokenCompany ()) {
					tTokenCompany = (TokenCompany) corporation;
					tTokenCompany.drawBase (g, X1, Y1, aWidth, aHeight, aHome);
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
		X1 = Xc - width / 2;
		Y1 = Yc + height / 2;
		g.setColor (Color.black);
		g.drawString (aLabel, X1, Y1);
		g.setFont (tCurrentFont);
	}

	public void drawName (Graphics g, int Xc, int Yc, Hex aHex) {
		int X1, Y1;
		Point xy;

		if (name != null) {
			if (!(name.equals (NO_NAME))) {
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
		Graphics2D g2d = (Graphics2D) g;
		Point tPoint1;
		Point tPoint2;
		Location tLocation1;
		Location tLocation2;
		int tX1, tY1, tX2, tY2;
		int tXLabel, tYLabel;
		int tPointTemp = aHex.getCityWidth () / 3;
		int tWidthHeight;
		Paint tTerrainFillPaint;
		String tLabel;

		if (corporation != Corporation.NO_CORPORATION) {
			tLocation1 = corporation.getHomeLocation1 ();
			tLocation2 = corporation.getHomeLocation2 ();
			if ((tLocation1 != Location.NO_LOC) && (tLocation2 != Location.NO_LOC)) {
				tPoint1 = tLocation1.calcCenter (aHex);
				tPoint2 = tLocation2.calcCenter (aHex);
				tX1 = Xc + tPoint1.x;
				tY1 = Yc + tPoint1.y;
				tX2 = Xc + tPoint2.x;
				tY2 = Yc + tPoint2.y;
				g2d.drawLine (tX1, tY1, tX2, tY2);
				tWidthHeight = tPointTemp * 2;
				if (mapCell != MapCell.NO_MAP_CELL) {
					tTerrainFillPaint = mapCell.getBaseTerrainFillPaint ();
					g2d.setPaint (tTerrainFillPaint);
					g2d.fillOval (tX1 - tPointTemp, tY1 - tPointTemp, tWidthHeight, tWidthHeight);
					g2d.fillOval (tX2 - tPointTemp, tY2 - tPointTemp, tWidthHeight, tWidthHeight);
				}
				g2d.setColor (Color.black);
				g2d.drawOval (tX1 - tPointTemp, tY1 - tPointTemp, tWidthHeight, tWidthHeight);
				g2d.drawOval (tX2 - tPointTemp, tY2 - tPointTemp, tWidthHeight, tWidthHeight);
				tXLabel = (tX1 + tX2) / 2;
				tYLabel = (tY1 + tY2) / 2;
				tLabel = getCorporationAbbrev ();
				tLabel = corporation.getAbbrev ();
				drawLabel (g, tXLabel, tYLabel, tLabel);
			}
		}
	}

	public int fieldCount () {
		return 4;
	}

	public TokenCompany getBaseCompany () {
		TokenCompany tBaseCompany;

		tBaseCompany = TokenCompany.NO_TOKEN_COMPANY;
		if (corporation != Corporation.NO_CORPORATION) {
			if (!mapCell.hasStation (corporation.getID ())) {
				try {
					tBaseCompany = (TokenCompany) corporation;
				} catch (ClassCastException e) {

				}
			}
		}

		return tBaseCompany;
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

	public int getBonusRevenue () {
		int tBonusRevenue;
		
		tBonusRevenue = 0;
		if (mapCell != MapCell.NO_MAP_CELL) {
			tBonusRevenue = mapCell.getBonusRevenue ();
		}
		
		return tBonusRevenue;
	}
	
	public int getID () {
		return id;
	}

	public String getIDToString () {
		return (Integer.valueOf (id).toString ());
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
		return (Integer.valueOf (type).toString ());
	}

	public boolean isCorporationBase () {
		boolean tIsCorporationBase;

		tIsCorporationBase = false;
		if (center != RevenueCenter.NO_CENTER) {
			if (! center.isDestination ()) {
				if (corporation != Corporation.NO_CORPORATION) {
					if ((corporation.isAShareCompany () || corporation.isAMinorCompany ())) {
						tIsCorporationBase = true;
					}
				}
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
			System.out.println ("No Corporation Base");		// PRINTLOG method
		} else {
			System.out.println ("Base for " + corporation.getAbbrev () + " Corporation");
		}
	}

	public void setCorporation (String aCorporationAbbrev) {
		Corporation tCorporation;

		if (aCorporationAbbrev != null) {
			tCorporation = mapCell.getCorporation (aCorporationAbbrev);
			setCorporation (tCorporation);
		}
	}

	public void setCorporation (Corporation aCorporation) {
		corporation = aCorporation;
	}

	public void setCorporationHome (Corporation aCorporation, RevenueCenter aRevenueCenter) {
		center = aRevenueCenter;
		if (!center.isDestination ()) {
			corporation = aCorporation;
		}
	}

	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}

	public void setRevenueCenter (RevenueCenter aRevenueCenter) {
		center = aRevenueCenter;
	}

	public boolean withBaseForCorp (Corporation aCorporation) {
		boolean tWithBaseForCorp;

		tWithBaseForCorp = false;
		if (corporation == aCorporation) {
			tWithBaseForCorp = true;
		}

		return tWithBaseForCorp;
	}
}
