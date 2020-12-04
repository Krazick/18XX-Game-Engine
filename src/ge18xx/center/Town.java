package ge18xx.center;

//
//  Town.java
//  Java_18XX
//
//  Created by Mark Smith on 12/29/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//


import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.tiles.Feature2;
import ge18xx.tiles.TileType;
import ge18xx.utilities.XMLNode;

import java.awt.*;

public class Town extends RevenueCenter {
	public Town () {
		this (RevenueCenter.NO_VALUE, NO_ID, Location.NO_LOCATION, NO_NAME, RevenueCenterType.NO_REVENUE_CENTER, new TileType ());
	}
	
	public Town (XMLNode aNode) {
		super (aNode);
	}
	
	public Town (Town aTown) {
		this (aTown.type.getType (), aTown.id, aTown.location.getLocation (), aTown.name, aTown.getRevenue (Revenue.ALL_PHASES), aTown.getTileType ());
		setRevenueLocation (aTown.revenues.getLocation ());
	}
	
	public Town (int aType, int aID, int aValue, TileType aTileType) {
		this (aType, aID, Location.NO_LOCATION, NO_NAME, aValue, aTileType);
	}
	
	public Town (int aType, int aID, int aLocation, int aValue, TileType aTileType) {
		this (aType, aID, aLocation, NO_NAME, aValue, aTileType);
	}
	
	public Town (int aType, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
		super (aType, aID, aLocation, aName, aValue, aTileType);
	}

	public Town (RevenueCenterType aRCType, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
		super (aRCType, aID, aLocation, aName, aValue, aTileType);
	}
	
	public boolean cityOrTown () {
		return (true);
	}
	
	public boolean containingPoint (Point aPoint, Hex aHex, int Xc, int Yc, int aTileOrient) {
		boolean tContainingPoint = false;
		int X1, Y1, X2, Y2;
		int townTemp = aHex.getCityWidth ()/3;
		int width;
		int height;
		Point tDisplace;
		Rectangle tRectangle;
		
		tDisplace = location.calcCenter (aHex);
		
		X1 = Xc - townTemp + tDisplace.x;
		X2 = Xc + townTemp + tDisplace.x;
		Y1 = Yc - townTemp + tDisplace.y;
		Y2 = Yc + townTemp + tDisplace.y;
		width = X2 - X1;
		height = Y2 - Y1;
		tRectangle = new Rectangle (X1, Y1, width, height);
		tContainingPoint = tRectangle.contains (aPoint);
		
		return tContainingPoint;
	}
		
	public void draw (Graphics g, int Xc, int Yc, int aTileOrient, Hex aHex, boolean onTile, Feature2 aSelectedFeature) {
		int X1, Y1, X2, Y2;
		int X3, Y3;
		int width3, height3;
		int townTemp = aHex.getCityWidth ()/3;
		int width;
		int height;
		Point tDisplace;
		Color aCityColor;
		
		aCityColor = Color.black;
		tDisplace = location.calcCenter (aHex);

		X1 = Xc - townTemp + tDisplace.x;
		X2 = Xc + townTemp + tDisplace.x;
		Y1 = Yc - townTemp + tDisplace.y;
		Y2 = Yc + townTemp + tDisplace.y;
		width = X2 - X1;
		height = Y2 - Y1;
		if (onTile) {
			X3 = X1 - 1;
			Y3 = Y1 - 1;
			width3 = width + 2;
			height3 = height + 2;
			g.setColor (Color.white);
			g.fillOval (X3, Y3, width3, height3);
		}
		g.setColor (aCityColor);
		g.fillOval (X1, Y1, width, height);
		if (isSingleSelected (location, aSelectedFeature)) {
			X3 = X1 - 3;
			Y3 = Y1 - 3;
			g.setColor (Color.ORANGE);
			g.drawRect (X3, Y3, width + 6, height + 6);
			g.drawLine (X3, Y3, X3 + width + 6, Y3 + height + 6);
			g.drawLine (X3 + width + 6, Y3, X3, Y3 + height + 6);
		}
		g.setColor (Color.black);
		drawValue (g, Xc, Yc, aHex, aTileOrient);
	}
	
	@Override
	public boolean isOpen() {
		return true;
	}

}
