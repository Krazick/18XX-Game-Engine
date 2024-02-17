package ge18xx.tiles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

//
//  TileName.java
//  Game_18XX
//
//  Created by Mark Smith on 9/16/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.map.Hex;
import ge18xx.map.Location;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class TileName extends Feature implements Cloneable {
	public static final ElementName EN_TILE_NAME = new ElementName ("TileName");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_LOCATION = new AttributeName ("location");
	public static final String OO_NAME = "OO";
	public static final String NY_NAME = "NY";
	public static final TileName NO_TILE_NAME = null;
	public static String NO_NAME = null;
	public static String NO_NAME2 = "";
	String name;

	public TileName () {
		this (NO_NAME);
	}

	public TileName (String aName) {
		this (aName, Location.NO_LOCATION);
	}

	public TileName (String aName, int aLocation) {
		setValues (aName, aLocation);
	}

	public TileName (XMLNode aNode) {
		String tName;
		int tLocation;

		tName = aNode.getThisAttribute (AN_NAME);
		tLocation = aNode.getThisIntAttribute (AN_LOCATION, Location.CENTER_CITY_LOC);
		setValues (tName, tLocation);
	}

	@Override
	public TileName clone () {
		TileName tTileName = (TileName) super.clone ();
		tTileName.name = name;

		return tTileName;
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tElement;

		if (name == NO_NAME) {
			tElement = XMLElement.NO_XML_ELEMENT;
		} else if (name.equals (NO_NAME2)) {
			tElement = XMLElement.NO_XML_ELEMENT;
		} else {
			tElement = aXMLDocument.createElement (EN_TILE_NAME);
			tElement.setAttribute (AN_NAME, name);
			if (!isNoLocation ()) {
				tElement.setAttribute (AN_LOCATION, getLocationToString ());
			}
		}

		return tElement;
	}

	public void draw (Graphics g, int X, int Y, Hex aHex) {
		draw (g, X, Y, 0, aHex);
	}

	public void draw (Graphics aGraphics, int aX, int aY, int aTileOrient, Hex aHex) {
		int tWidth;
		int tHeight;
		int tX1;
		int tY1;
		Location tLocation;
		Point tPoint;
		Font tnewFont, tCurrentFont;

		if (!(name.equals (NO_NAME))) {
			if (!(name.equals (NO_NAME2))) {
				tCurrentFont = aGraphics.getFont ();
				tnewFont = new Font ("Dialog", Font.PLAIN, 10);
				aGraphics.setFont (tnewFont);
				tWidth = aGraphics.getFontMetrics ().stringWidth (name);
				tHeight = aGraphics.getFontMetrics ().getHeight ();
				if (location.isNoLocation ()) {
					tX1 = aX - tWidth / 2;
					tY1 = aY + tHeight / 2;
				} else {
					tLocation = location.rotateLocation (aTileOrient);
					tPoint = tLocation.calcCenter (aHex);
					tX1 = aX + tPoint.x - tWidth / 2;
					tY1 = aY + tPoint.y + tHeight / 2;
				}

				aGraphics.setColor (Color.black);
				aGraphics.drawString (name, tX1, tY1);
				aGraphics.setFont (tCurrentFont);
			}
		}
	}

	public String getName () {
		return name;
	}

	public boolean isNYTile () {
		boolean tIsNYTile;

		tIsNYTile = false;
		if (NY_NAME.equals (name)) {
			tIsNYTile = true;
		}

		return tIsNYTile;
	}

	public boolean isOOTile () {
		boolean tIsOOTile;

		tIsOOTile = false;
		if (OO_NAME.equals (name)) {
			tIsOOTile = true;
		}

		return tIsOOTile;
	}

	@Override
	public void printlog () {
		System.out.println ("Tile Name " + name);		// PRINTLOG
		super.printlog ();
	}

	public void setValues (String aName, int aLocation) {
		setLocation (aLocation);
		name = aName;
	}
}
