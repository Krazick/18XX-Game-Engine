package ge18xx.map;

//
//  Terrain.java
//  Java_18XX
//
//  Created by Mark Smith on 11/12/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.tiles.Feature;
import ge18xx.toplevel.LoadableXMLI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

import java.awt.Graphics;
import java.awt.Color;

import java.io.IOException;

import org.w3c.dom.NodeList;

public class Terrain extends Feature implements LoadableXMLI {
	public static final ElementName EN_TERRAIN = new ElementName ("Terrain");
	public static final ElementName EN_TERRAIN_COSTS = new ElementName ("TerrainCosts");
	public static final ElementName EN_TERRAIN_FEATURES = new ElementName ("TerrainFeatures");
	public static final AttributeName AN_BASE = new AttributeName ("base");
	public static final AttributeName AN_COLOR = new AttributeName ("color");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_TYPE = new AttributeName ("type");
	public static final AttributeName AN_COST = new AttributeName ("cost");
	public static final AttributeName AN_OPTIONAL = new AttributeName ("optional");
	public static final AttributeName AN_CATEGORY = new AttributeName ("category");
	public static final Terrain NO_TERRAINX = null;
    static final Terrain NO_TERRAIN_FEATURE = null;
    static final int NO_TERRAIN = 0;
	static final int NO_COST = 0;
	
    static final int CLEAR = 1;
    static final int OCEAN = 2;
    static final int DELTA = 3;
    static final int OFF_BOARD_RED = 4;
    static final int OFF_BOARD_GRAY = 5;
    static final int OFF_BOARD_BLACK = 6;
    static final int OFF_BOARD_GREEN = 7;

    static final int THICK_BORDER = 9;
    static final int RIVER = 10;
    static final int MULTIPLE_RIVER = 11;
    static final int MAJOR_RIVER = 12;
    static final int HILL = 13;
    static final int MOUNTAIN = 14;
    static final int HIMALAYA = 15;
    static final int PASS = 16;
    static final int SWAMP = 17;
    static final int LAKE = 18;
	static final int PORT = 19;
	static final int SMALL_RIVER = 20;
	static final int LARGE_RIVER = 21;
	static final int SHALLOW_COAST = 22;
	static final int COAST = 23;
	static final int DEEP_COAST = 24;
    static final int DESERT = 25;
    static final int END_ROUTE = 26;
	static final int MIN_TERRAIN = NO_TERRAIN;
	static final int MAX_TERRAIN = END_ROUTE;
	static final String NAMES [] = {"NO TERRAIN", "Clear", "Ocean", "Delta", "Off Board Red", "Off Board Gray", 
		"Off Board Black", "Off Board Green", "", "Thick Border", "River", "Multiple River", "Major River", "Hill", 
		"Mountain", "Himalya", "Pass", "Swamp", "Lake", "Port", "Small River", "Large River", "Shallow Coast", 
		"Coast", "Deep Coast", "Desert", "End Route"};
    static Color [] colors = null;
	
	int terrain;
	int cost;
	
	public Terrain () {
		this (NO_TERRAIN);
	}
	
	public Terrain (int aTerrain) {
		this (aTerrain, NO_COST, Location.NO_LOCATION);
	}
	
	public Terrain (int aTerrain, int aCost, int aLocation) {
		super (aLocation);
		setValues (aTerrain, aCost);
	}
	
	public Terrain (int aTerrain, int aCost, Location aLocation) {
		super (aLocation);
		setValues (aTerrain, aCost);
	}
	
	public Terrain (XMLNode aNode) {
		String tTerrainName;
		int tTerrain, tCost, tLocation;
		
		tTerrainName = aNode.getThisAttribute (AN_TYPE);
		if (tTerrainName != null) {
			tTerrain = getTypeFromName (tTerrainName);
		} else {
			tTerrain = NO_TERRAIN;
		}
		tCost = aNode.getThisIntAttribute (AN_COST);
		tLocation = aNode.getThisIntAttribute (Location.AN_LOCATION, Location.CENTER_CITY_LOC);
		if (tTerrain != NO_TERRAIN) {
			setLocation (tLocation);
			setValues (tTerrain, tCost);
		} else {
			setLocation (Location.NO_LOCATION);
			setValues (NO_TERRAIN, NO_COST);
		}
	}
	
	@Override
	public boolean bleedThroughAll () {
		if ((terrain < RIVER) || (terrain == PORT) || (terrain == END_ROUTE)) {
			return true;
		} else {
			return false;
		}
	}
	
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tElement;
		
		if (terrain != NO_TERRAIN) {
			tElement = aXMLDocument.createElement (EN_TERRAIN);
			tElement.setAttribute (AN_COST, getName ());
			if (cost > 0) {
				tElement.setAttribute (AN_COST, cost);
			}
			if (! isNoLocation ()) {
				tElement.setAttribute (Location.AN_LOCATION, getLocationToString ());
			}
		} else {
			tElement = null;
		}
		
		return tElement;
	}
	
	public void draw (Graphics g, int X, int Y, Hex aHex, Color aFillColor) {
		switch (terrain) {
        	case NO_TERRAIN:
        		break;
        			
        	case RIVER:		/* River */
				aHex.drawRiver (g, X, Y, getColor ());
        		break;
        	
        	case MULTIPLE_RIVER:		/* Multiple River */
				aHex.drawMultipleRiver (g, X, Y, getColor ());
        		break;
        	
        	case MAJOR_RIVER:		/* Major River */
				aHex.drawMajorRiver (g, X, Y, getColor ());
        		break;
        	
        	case SMALL_RIVER:		/* Small River */
				aHex.drawSmallRiver (g, X, Y, getColor ());
        		break;
				
        	case LARGE_RIVER:		/* Large River */
				aHex.drawLargeRiver (g, X, Y, getColor ());
        		break;
					
			case SHALLOW_COAST:		/* Shallow Coastline */
				aHex.drawShallowCoast (g, X, Y, getColor ());
				break;
					
			case COAST:				/* Coastline */
				aHex.drawCoast (g, X, Y, getColor ());
				break;
						
			case DEEP_COAST:		/* Deep Coastline */
				aHex.drawDeepCoast (g, X, Y, getColor ());
				break;
							
			case HILL:		/* Hill */
        		aHex.drawHill (g, X, Y, aFillColor);
        		break;
        			
        	case MOUNTAIN:		/* Mountain */
        		aHex.drawMountain (g, X, Y, aFillColor);
        		break;
        	
        	case HIMALAYA:		/* Mountain */
        		aHex.drawHimalaya (g, X, Y, aFillColor);
        		break;
        	
        	case PASS:		/* Mountain Pass */
        		break;
        	
        	case SWAMP:		/* Swamp */
        		break;
				
        	case LAKE:		/* Lake */
				break;
			
			case PORT:		/* Port, Draw an Anchor */
				aHex.drawPort (g, X, Y, getColor ());
				break;
			
			case DESERT:	/* Desert, Draw a Cactus */
				break;
				
			case END_ROUTE:
				aHex.drawOctagon (g, X, Y, Color.RED);
        }
	}
	
	public boolean drawBorder () {
		boolean tDrawBorder = true;
		
		switch (terrain) {
			
			case OCEAN:		
            case OFF_BOARD_RED:	
            case OFF_BOARD_GRAY:	
            case OFF_BOARD_BLACK:	
            case OFF_BOARD_GREEN:	
            	tDrawBorder = false;
				break;
        }
		
		return (tDrawBorder);
	}
	
	public Color getColor () {
		return colors [terrain];
	}
	
	public int getCost () {
		return cost;
	}
	
	public String getCostToString () {
		return (new Integer (cost).toString ());
	}
	
	public String getCategory (XMLNode aNode) {
		return aNode.getThisAttribute (AN_CATEGORY);
	}
	
	public String getName () {
		return NAMES [terrain];
	}
	
	public int getTerrain () {
		return terrain;
	}
	
	@Override
	public String getTypeName () {
		return EN_TERRAIN.getString ();
	}
	
	public static int getTypeFromName (String aName) {
		int index;
		int thisType = NO_TERRAIN;
		
		for (index = MIN_TERRAIN; index <= MAX_TERRAIN; index++) {
			if (aName.equals (NAMES [index])) {
				thisType = index;
			}
		}
		
		return thisType;
	}
	
	public boolean isMountainous () {
		// True if Hill, Mountain or Himalaya
		return ((terrain == HILL) || (terrain == MOUNTAIN) || (terrain == HIMALAYA));
	}
	
	public boolean isPort () {
		return (terrain == PORT);
	}
	
	public boolean isRiver () {
		boolean tIsRiver = false;
		
		switch (terrain) {
		
			case RIVER:		
	       	case MULTIPLE_RIVER:	/* Multiple River */
	       	case MAJOR_RIVER:		/* Major River */
	        case SMALL_RIVER:		/* Small River */
	       	case LARGE_RIVER:		/* Large River */
				tIsRiver = true;
				break;
		}
		
		return tIsRiver;
	}
	
	public boolean isSelectable () {
		boolean tIsSelectable = true;
		
		switch (terrain) {
		
			case OFF_BOARD_BLACK:		
			case OFF_BOARD_GREEN:	
			case OCEAN:		
			case OFF_BOARD_GRAY:
				tIsSelectable = false;
				break;
		}
		
		if ((terrain <= NO_TERRAIN) || (terrain > MAX_TERRAIN)) {
			tIsSelectable = false;			
		}
		
		return tIsSelectable;
	}
	
	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNode XMLMapRoot;
		XMLNode tChildNode;
		XMLNode tChildNode1;
		NodeList tChildren;
		NodeList tChildren1;
		String tChildName, tChildName1;
		int tChildrenCount;
		int tChildrenCount1;
		int tIndex;
		int tIndex1;
		int tID;
		int tRed;
		int tGreen;
		int tBlue;
		String tColorValues;
		String [] tSplit;
		
		XMLMapRoot = aXMLDocument.getDocumentElement ();
		tChildren = XMLMapRoot.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (EN_TERRAIN_FEATURES.equals (tChildName)) {
				tChildren1 = tChildNode.getChildNodes ();
				tChildrenCount1 = tChildren1.getLength ();
				for (tIndex1 = 0; tIndex1 < tChildrenCount1; tIndex1++) {
					tChildNode1 = new XMLNode (tChildren1.item (tIndex1));
					tChildName1 = tChildNode1.getNodeName ();
					if (EN_TERRAIN.equals (tChildName1)) {
						tID = tChildNode1.getThisIntAttribute (AN_ID);
						tColorValues = tChildNode1.getThisAttribute (AN_COLOR);
						tSplit = tColorValues.split (",");
						tRed = Integer.parseInt (tSplit [0]);
						tGreen = Integer.parseInt (tSplit [1]);
						tBlue = Integer.parseInt (tSplit [2]);
						colors [tID] = new Color (tRed, tGreen, tBlue);
					}
				}
			}
		}
	}
	
	private void setColors () {
		if (colors == null) {
			int tColorCount = (MAX_TERRAIN - MIN_TERRAIN) + 1;
			setStaticColors (tColorCount);
		}
	}
	
	private static void setStaticColors (int aColorCount) {
		colors = new Color [aColorCount];
		colors [NO_TERRAIN] = Color.black;
		colors [CLEAR] = new Color (218, 227, 131);
		colors [OCEAN] = new Color (165, 204, 236);
		colors [DELTA] = Color.orange;
		colors [OFF_BOARD_RED] = new Color (233, 39, 34);
		colors [OFF_BOARD_GRAY] = Color.gray;
		colors [OFF_BOARD_BLACK] = new Color (102, 204, 102);
		colors [OFF_BOARD_GREEN] = new Color (100, 166, 80);
		colors [THICK_BORDER] = Color.yellow;
		colors [RIVER] = Color.blue;
		colors [MULTIPLE_RIVER] = Color.blue;
		colors [MAJOR_RIVER] = Color.blue;
		colors [HILL] = Color.lightGray;
		colors [MOUNTAIN] = Color.lightGray;
		colors [HIMALAYA] = Color.lightGray;
		colors [PASS] = Color.lightGray;
		colors [SWAMP] = Color.lightGray;
		colors [LAKE] = Color.blue;
		colors [PORT] = Color.black;
		colors [SMALL_RIVER] = Color.blue;
		colors [LARGE_RIVER] = Color.blue;
		colors [SHALLOW_COAST] = Color.blue;
		colors [COAST] = Color.blue;
		colors [DEEP_COAST] = Color.blue;
		colors [DESERT] = Color.black;
	}
	
	public void setCost (int aCost) {
		cost = aCost;
	}
	
	public void setValues (int aTerrain, int aCost) {
		setColors ();
		if ((aTerrain >= MIN_TERRAIN) && (aTerrain <= MAX_TERRAIN)) {
			terrain = aTerrain;
			setCost (aCost);
		} else {
			terrain = NO_TERRAIN;
			setCost (NO_COST);
		}
	}

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
		// Terrain has no Node List to parse -- not required to be completed.
		System.err.println ("Terrain Class Found Item Match Key 1 -- SHOULD NOT HAPPEN");
	}
}
