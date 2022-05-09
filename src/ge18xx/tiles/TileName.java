package ge18xx.tiles;

//
//  TileName.java
//  Game_18XX
//
//  Created by Mark Smith on 9/16/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

public class TileName extends Feature implements Cloneable {
	public final static ElementName EN_TILE_NAME = new ElementName ("TileName");
	public final static AttributeName AN_NAME = new AttributeName ("name");
	public final static AttributeName AN_LOCATION = new AttributeName ("location");
	public final static String OO_NAME = "OO";
	public final static String NY_NAME = "NY";
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

	public void draw (Graphics g, int X, int Y, int aTileOrient, Hex aHex) {
		int width, height, X1, Y1;
		Location tLocation;
		Point xy;
		Font tnewFont, tCurrentFont;

		if (!(name.equals (NO_NAME))) {
			if (!(name.equals (NO_NAME2))) {
				tCurrentFont = g.getFont ();
				tnewFont = new Font ("Dialog", Font.PLAIN, 10);
				g.setFont (tnewFont);
				width = g.getFontMetrics ().stringWidth (name);
				height = g.getFontMetrics ().getHeight ();
				if (location.isNoLocation ()) {
					X1 = X - width / 2;
					Y1 = Y + height / 2;
				} else {
					tLocation = location.rotateLocation (aTileOrient);
					xy = tLocation.calcCenter (aHex);
					X1 = X + xy.x - width / 2;
					Y1 = Y + xy.y + height / 2;
				}

				g.setColor (Color.black);
				g.drawString (name, X1, Y1);
				g.setFont (tCurrentFont);
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
		System.out.println ("Tile Name " + name);
		super.printlog ();
	}

	public void setValues (String aName, int aLocation) {
		setLocation (aLocation);
		name = aName;
	}
}
