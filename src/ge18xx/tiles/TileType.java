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
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
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
	public static final int CLEAR = 10;
	public static final int MIN_TYPE = NO_TYPE;
	public static final int MAX_TYPE = CLEAR;
	public static final int HIGHLIGHT_ADDITION = 10;
	public static String NAMES[] = { "NO TYPE", "Yellow", "Green", "Grey", "Brown", 
			"Red Off Board", "Red-Brown", "Ocean Ferry", "Ocean", "Purple", "Clear"};
	static Paint [] [] paints = null;

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

	public static Paint getColor (int aType) {
		return getColor (aType, false);
	}
	
	public static Paint getColor (int aType, boolean aHighlight) {
		Paint tPaint;
		
		if (aHighlight) {
			tPaint = paints [aType] [1];
		} else {
			tPaint = paints [aType] [0];
		}
		
		return tPaint;
	}
	
	public Paint getColor (boolean aIsSelected) {
		Paint tPaint;
		
		tPaint = getColor (type, aIsSelected);
		
		return tPaint;
	}
	
	public Paint getColor () {
		return getColor (false);
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

		XMLMapRoot = aXMLDocument.getDocumentNode ();
		tXMLNodeList = new XMLNodeList (tileTypesParsingRoutine);
		tXMLNodeList.parseXMLNodeList (XMLMapRoot, EN_TILE_TYPES);
	}

	public void printlog () {
		System.out.println ("Tile Type value " + type + " Name is " + NAMES [type]);
	}

	private void setColors () {
		int tPaintCount;
		
		if (paints == null) {
			tPaintCount = (MAX_TYPE - MIN_TYPE) + 1;
			setStaticColors (tPaintCount);
		}
	}

	private static void setStaticColors (int aPaintCount) {
		TexturePaint tTexturePaint;
		
		paints = new Paint [aPaintCount] [2];
		paints [0] [0] = Color.lightGray;
		paints [1] [0]  = Color.yellow;
		paints [2] [0]  = Color.green;
		paints [3] [0]  = new Color (150, 150, 150);
		paints [4] [0]  = new Color (139, 69, 19);
		paints [5] [0]  = new Color (153, 0, 0);
		paints [6] [0]  = new Color (204, 51, 51);
		paints [7] [0]  = new Color (153, 204, 255);
		paints [8] [0]  = new Color (153, 204, 255);
		paints [9] [0]  = new Color (140, 49, 224);
		paints [10] [0]  = new Color (204, 255, 204);		// CLEAR
		
		// Highlight Section
		paints [0] [1] = Color.lightGray;
		tTexturePaint = createTexture (Color.yellow, Color.lightGray);	// YELLOW_HIGHLIGHT
		paints [1] [1] = tTexturePaint;
		tTexturePaint = createTexture (Color.green, Color.lightGray);	// GREEN HIGHLIGHT
		paints [2] [1] = tTexturePaint;
		tTexturePaint = createTexture (new Color (150, 150, 150), Color.white);		// GREY_HIGHLIGHT
		paints [3] [1] = tTexturePaint;
		tTexturePaint = createTexture (new Color (139, 69, 19), Color.white);		// BROWN_HIGHLIGHT
		paints [4] [1] = tTexturePaint;
		tTexturePaint = createTexture (new Color (153, 0, 0), Color.white);		// BROWN_HIGHLIGHT
		paints [5] [1] = tTexturePaint;
		paints [6] [1]  = new Color (204, 51, 51);
		paints [7] [1]  = new Color (153, 204, 255);
		paints [8] [1]  = new Color (153, 204, 255);
		paints [9] [1]  = new Color (140, 49, 224);
		tTexturePaint = createTexture (new Color (204, 255, 204), Color.lightGray);	// CLEAR_HIGHLIGHT
		paints [10] [1] = tTexturePaint;
	}

	public static TexturePaint createTexture (Color aBaseColor, Color aHighlightColor) {
		TexturePaint tTexturePaint;
		BufferedImage tBufferenedImage = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		Graphics2D tBufferedGraphic = tBufferenedImage.createGraphics();
		Rectangle tRectangle;
		
		tBufferedGraphic.setColor (aHighlightColor);
		tBufferedGraphic.fillRect (0, 0, 5, 5);
		tBufferedGraphic.setColor (aBaseColor);
		tBufferedGraphic.fillOval (0, 0, 5, 5);
		tRectangle = new Rectangle (0, 0, 5, 5);
		tTexturePaint = new TexturePaint (tBufferenedImage, tRectangle);
		
		return tTexturePaint;
	}

	ParsingRoutineI tileTypesParsingRoutine = new ParsingRoutineI () {
		XMLNodeList tXMLNodeList;

		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			tXMLNodeList = new XMLNodeList (tileTypeParsingRoutine);
			tXMLNodeList.parseXMLNodeList (aChildNode, EN_TILE_TYPE);
		}
	};

	ParsingRoutineI tileTypeParsingRoutine = new ParsingRoutineI () {
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
			paints [tID] [0] = new Color (tRed, tGreen, tBlue);
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
