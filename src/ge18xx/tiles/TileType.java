package ge18xx.tiles;

//
//  TileType.java
//  Java_18XX
//
//  Created by Mark Smith on 11/12/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.toplevel.LoadableXMLI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import java.awt.Color;

import java.io.IOException;

public class TileType implements Cloneable, LoadableXMLI {
	public static final ElementName EN_TILE_TYPE = new ElementName ("TileType");
	public static final ElementName EN_TILE_TYPES = new ElementName ("TileTypes");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_COLOR = new AttributeName ("color");
	public static final int NO_TYPE = 0;
	public static final int YELLOW = 1;
	public static final int GREEN = 2;
	public static final int GREY = 3;
	public static final int BROWN = 4;
	public static final int RED_OFFBOARD = 5;
	public static final int RED_BROWN = 6;
	public static final int OCEAN_FERRY = 7;
	public static final int OCEAN = 8;
	public static final int PURPLE = 9;
	public static final int MIN_TYPE = NO_TYPE;
	public static final int MAX_TYPE = PURPLE;
	public static String NAMES [] = {"NO TYPE", "Yellow", "Green", "Grey", "Brown", "Red Off Board", "Red-Brown", "Ocean Ferry", "Ocean", "Purple"};
	static Color [] colors = null;
	
	int type;
	boolean fixed;
	
	public TileType () {
		this (NO_TYPE, false);
	}
	
	public TileType (int aType, boolean aFixed) {
		setColors ();
		if ((aType >= MIN_TYPE) && (aType <= MAX_TYPE)) {
			type = aType;
		} else {
			type = NO_TYPE;
		}
		setFixed (aFixed);
	}
	
	public boolean canDeadEndTrack () {
		boolean tCanDeadEndTrack = false;
		
		switch (type) {
			case YELLOW:
			case GREEN:
			case BROWN:
				tCanDeadEndTrack = true;
				break;
		}
		
		return tCanDeadEndTrack;
	}
	
	@Override
	public TileType clone () {
		try {
			TileType tTileType = (TileType) super.clone ();
			tTileType.type = type;
			tTileType.fixed = fixed;
			
			return tTileType;
		} catch (CloneNotSupportedException e) {
			throw new Error ("TileType.clone Not Supported Exception");
		}
	}
	
	public Color getColor () {
		return colors [type];
	}
	
	public String getName () {
		return NAMES [type];
	}
	
	public boolean canUpgradeTo (TileType aTileType) {
		boolean tCanUpgradeTo = false;
		
		if (type == YELLOW) {
			if (aTileType.getType () == GREEN) {
				tCanUpgradeTo = true;
			}
		} else if (type == GREEN) {
			if (aTileType.getType () == BROWN) {
				tCanUpgradeTo = true;
			}
		} else if (type == BROWN) {
			if (aTileType.getType () == GREY) {
				tCanUpgradeTo = true;
			}
		}
		
		return tCanUpgradeTo;
	}
	
	public Color getRevenueColor () {
		Color tRevenueColor = Color.black;
		
		switch (type) {
			case YELLOW:
			case GREY:
			case OCEAN:
			case GREEN:
				tRevenueColor = Color.black;
				break;
				
            default:
				tRevenueColor = Color.white;
				break;
        }
		
		return (tRevenueColor);
	}

	public int getType () {
		return type;
	}
	
	@Override
	public String getTypeName () {
		return "Tile Type";
	}
	
	public static int getTypeFromName (String aName) {
		int index;
		int thisType = NO_TYPE;
		
		for (index = MIN_TYPE; index < MAX_TYPE; index++) {
			if (aName.equals (NAMES [index])) {
				thisType = index;
			}
		}
		
		return thisType;
	}
	
	public void setFixed (boolean aFixed) {
		fixed = aFixed;
	}
	
	public boolean isFixed () {
		return fixed;
	}
	
	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNodeList tXMLNodeList;
		XMLNode XMLMapRoot;
		
		XMLMapRoot = aXMLDocument.getDocumentElement ();
		tXMLNodeList = new XMLNodeList (tileTypesParsingRoutine);
		tXMLNodeList.parseXMLNodeList (XMLMapRoot, EN_TILE_TYPES);
	}
	
	public void printlog () {
		System.out.println ("Tile Type value " + type + " Name is " + NAMES [type]);
	}
	
	private void setColors () {
		if (colors == null) {
			int tColorCount = (MAX_TYPE - MIN_TYPE) + 1;
			setStaticColors (tColorCount);
		}
	}
	
	private static void setStaticColors (int aColorCount) {
		colors = new Color [aColorCount];
		colors [0] = Color.lightGray;
		colors [1] = Color.yellow;
		colors [2] = Color.green;
		colors [3] = new Color (150, 150, 150);
		colors [4] = new Color (139, 69, 19);
		colors [5] = new Color (153, 0, 0);
		colors [6] = new Color (204, 51, 51);
		colors [7] = new Color (153, 204, 255);
		colors [8] = new Color (153, 204, 255);
	}
	
	ParsingRoutineI tileTypesParsingRoutine  = new ParsingRoutineI ()  {
		XMLNodeList tXMLNodeList;
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			tXMLNodeList = new XMLNodeList (tileTypeParsingRoutine);
			tXMLNodeList.parseXMLNodeList (aChildNode, EN_TILE_TYPE);
		}
	};
	
	ParsingRoutineI tileTypeParsingRoutine  = new ParsingRoutineI ()  {
		int tID;
		int tRed;
		int tGreen;
		int tBlue;
		String tColorValues;
		String [] tSplit;

		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			tID = aChildNode.getThisIntAttribute (AN_ID);
			tColorValues = aChildNode.getThisAttribute (AN_COLOR);
			tSplit = tColorValues.split (",");
			tRed = Integer.parseInt (tSplit [0]);
			tGreen = Integer.parseInt (tSplit [1]);
			tBlue = Integer.parseInt (tSplit [2]);
			colors [tID] = new Color (tRed, tGreen, tBlue);
		}
	};

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
	}

	public boolean isSameType (int aTileType) {
		boolean tIsSameType = false;
		
		if (type == aTileType) {
			tIsSameType = true;
		}
		
		return tIsSameType;
	}
}
