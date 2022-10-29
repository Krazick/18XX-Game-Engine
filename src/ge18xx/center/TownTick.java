package ge18xx.center;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

//
//  TownTick.java
//  Java_18XX
//
//  Created by Mark Smith on 12/30/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.tiles.Feature2;
import ge18xx.tiles.TileType;
import ge18xx.utilities.XMLNode;

public class TownTick extends Town {

	public TownTick () {
		this (RevenueCenterType.NO_REVENUE_CENTER, NO_ID, Location.NO_LOCATION, NO_NAME, RevenueCenter.NO_VALUE,
				new TileType ());
	}

	public TownTick (XMLNode aNode) {
		super (aNode);
	}

	public TownTick (TownTick aTownTick) {
		this (aTownTick.type.getType (), aTownTick.id, aTownTick.location.getLocation (), aTownTick.name,
				aTownTick.getRevenue (Revenue.ALL_PHASES), aTownTick.getTileType ());
		setRevenueLocation (aTownTick.revenues.getLocation ());
	}

	public TownTick (int aType, int aID, int aValue, TileType aTileType) {
		this (aType, aID, Location.NO_LOCATION, NO_NAME, aValue, aTileType);
	}

	public TownTick (int aType, int aID, int aLocation, int aValue, TileType aTileType) {
		this (aType, aID, aLocation, NO_NAME, aValue, aTileType);
	}

	public TownTick (int aType, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
		super (aType, aID, aLocation, aName, aValue, aTileType);
	}

	public TownTick (RevenueCenterType aRCType, int aID, int aLocation, String aName, int aValue, TileType aTileType) {
		super (aRCType, aID, aLocation, aName, aValue, aTileType);
	}

	@Override
	public boolean cityOrTown () {
		return (true);
	}

	@Override
	public boolean containingPoint (Point aPoint, Hex aHex, int Xc, int Yc, int aTileOrient) {
		boolean tContainingPoint = false;
		int X1, Y1, X2, Y2, Xd, Yd, width, height;
		int temp = aHex.getCityWidth ();
		Location tLocation;
		Point tDisplace;
		Rectangle tRectangle;

		tLocation = location.rotateLocation (aTileOrient);
		tDisplace = tLocation.calcCenter (aHex);
		Xd = Xc + tDisplace.x;
		Yd = Yc + tDisplace.y;
		X1 = Xd - temp;
		Y1 = Yd - temp;
		X2 = Xd + temp;
		Y2 = Yd + temp;
		width = X2 - X1;
		height = Y2 - Y1;
		tRectangle = new Rectangle (X1, Y1, width, height);
		tContainingPoint = tRectangle.contains (aPoint);

		return tContainingPoint;
	}

	@Override
	public void draw (Graphics g, int Xc, int Yc, int aTileOrient, Hex aHex, boolean onTile,
			Feature2 aSelectedFeature) {
		int X1, Y1, X2, Y2, Xd, Yd, X3, Y3, width, height;
		int temp = aHex.getCityWidth ();
		Point tDisplace;
		int tTrackWidth = (int) (aHex.getTrackWidth () * 1.5);
		int maxXDisplacement;
		int minXDisplacement;
		int maxYDisplacement;
		int minYDisplacement;
		int tickSlant;
		Location tLocation;
		Color aCityColor;
		Graphics2D g2d = (Graphics2D) g;

		if (Hex.getDirection ()) {
			maxXDisplacement = (int) (-tTrackWidth * 0.866025);
			minXDisplacement = (int) (-tTrackWidth * 0.5);
			maxYDisplacement = (int) (tTrackWidth * 0.5);
			minYDisplacement = (int) (tTrackWidth * 0.866025);
		} else {
			maxXDisplacement = (int) (tTrackWidth * 0.5);
			minXDisplacement = (int) (tTrackWidth * 0.866025);
			maxYDisplacement = (int) (tTrackWidth * 0.866025);
			minYDisplacement = (int) (tTrackWidth * 0.5);
		}

		aCityColor = Color.black;
		tLocation = location.rotateLocation (aTileOrient);
		tDisplace = tLocation.calcCenter (aHex);
		Xd = Xc + tDisplace.x;
		Yd = Yc + tDisplace.y;
		X1 = Xd - temp;
		Y1 = Yd - temp;
		X2 = Xd + temp;
		Y2 = Yd + temp;
		tickSlant = aTileOrient;
		if (tLocation.isCenterLocation ()) {

		} else if (tLocation.isCityHexCorner ()) {
			tickSlant = (tLocation.getLocation () - 11) % 6;
		} else if (tLocation.isCityFarHexSide ()) {
			tickSlant = (aTileOrient + 0) % 6 + 6;
			tickSlant = (tLocation.getLocation () - 17) % 6 + 6;
		} else if (tLocation.isCityHexSide ()) {
			tickSlant = tLocation.getLocation () - 6;
		} else if (tLocation.isCityFarHexCornerLeft ()) {
			tickSlant = (tLocation.getLocation () - 22) % 6 + 0;
		} else if (location.isCityFarHexCornerLeft ()) {
			tickSlant = (tLocation.getLocation () - 28) % 6 + 0;
		}
		switch (tickSlant) {
		case (0):
		case (3):
			if (Hex.getDirection ()) {
				X1 = Xd;
				X2 = Xd;
				Y1 = Yd - tTrackWidth;
				Y2 = Yd + tTrackWidth;
			} else {
				X1 = Xd - tTrackWidth;
				X2 = Xd + tTrackWidth;
				Y1 = Yd;
				Y2 = Yd;
			}
			break;

		case (1):
		case (4):
			X1 = Xd - maxXDisplacement;
			X2 = Xd + maxXDisplacement;
			Y1 = Yd - maxYDisplacement;
			Y2 = Yd + maxYDisplacement;
			break;

		case (2):
		case (5):
			X1 = Xd + maxXDisplacement;
			X2 = Xd - maxXDisplacement;
			Y1 = Yd - maxYDisplacement;
			Y2 = Yd + maxYDisplacement;
			break;

		case (7):
		case (10):
			if (Hex.getDirection ()) {
				X1 = Xd - tTrackWidth;
				X2 = Xd + tTrackWidth;
				Y1 = Yd;
				Y2 = Yd;
			} else {
				X1 = Xd;
				X2 = Xd;
				Y1 = Yd - tTrackWidth;
				Y2 = Yd + tTrackWidth;
			}
			break;

		case (6):
		case (9):
			X1 = Xd - minXDisplacement;
			X2 = Xd + minXDisplacement;
			Y1 = Yd - minYDisplacement;
			Y2 = Yd + minYDisplacement;
			break;

		case (8):
		case (11):
			X1 = Xd + minXDisplacement;
			X2 = Xd - minXDisplacement;
			Y1 = Yd - minYDisplacement;
			Y2 = Yd + minYDisplacement;
			break;
		}
		Stroke tCurrentStroke = g2d.getStroke ();
		BasicStroke tTrackStroke = new BasicStroke (tTrackWidth);

		g2d.setStroke (tTrackStroke);
		g.setColor (aCityColor);
		g.drawLine (X1, Y1, X2, Y2);
		g2d.setStroke (tCurrentStroke);
		drawValue (g, Xc, Yc, aHex, aTileOrient);

		g.setColor (Color.green);
		g.drawLine (Xd - 1, Yd, Xd + 1, Yd);
		g.drawLine (Xd, Yd - 1, Xd, Yd + 1);

		if (isSingleSelected (tLocation, aSelectedFeature)) {
			if (X2 < X1) {
				X3 = X2;
				X2 = X1;
				X1 = X3;
			}
			if (Y2 < Y1) {
				Y3 = Y2;
				Y2 = Y1;
				Y1 = Y3;
			}
			width = X2 - X1;
			height = Y2 - Y1;
			X3 = X1 - 3;
			Y3 = Y1 - 3;
			g.setColor (Color.ORANGE);
			g.drawRect (X3, Y3, width + 6, height + 6);
			g.drawLine (X3, Y3, X3 + width + 6, Y3 + height + 6);
			g.drawLine (X3 + width + 6, Y3, X3, Y3 + height + 6);
		}
		g.setColor (Color.black);
	}
}
