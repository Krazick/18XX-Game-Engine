package ge18xx.tiles;

import java.awt.Color;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.io.IOException;

import geUtilities.xml.LoadableXMLI;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.ParsingRoutineI;
import geUtilities.XMLDocument;
import geUtilities.XMLNode;
import geUtilities.XMLNodeList;

public class TileType implements Cloneable, LoadableXMLI {
	public static final ElementName EN_TILE_TYPE = new ElementName ("TileType");
	public static final ElementName EN_TILE_TYPES = new ElementName ("TileTypes");
	public static final AttributeName AN_ID = new AttributeName ("id");
	public static final AttributeName AN_COLOR = new AttributeName ("color");
	public static final TileType NO_TILE_TYPE = null;
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
		setPaints ();
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

	public static Paint getPaint (int aType) {
		return getPaint (aType, false);
	}

	public static Paint getPaint (int aType, boolean aHighlight) {
		Paint tPaint;

		if (aHighlight) {
			tPaint = paints [aType] [1];
		} else {
			tPaint = paints [aType] [0];
		}

		return tPaint;
	}

	public Paint getPaint (boolean aIsSelectable) {
		Paint tPaint;

		tPaint = getPaint (type, aIsSelectable);

		return tPaint;
	}

	public Paint getPaint () {
		return getPaint (false);
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
		Color tRevenueColor;

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
		int tIndex;
		int tType;

		tType = NO_TYPE;
		for (tIndex = MIN_TYPE; tIndex < MAX_TYPE; tIndex++) {
			if (aName.equals (NAMES [tIndex])) {
				tType = tIndex;
			}
		}

		return tType;
	}

	public static boolean validName (String aName) {
		int tFoundType;
		boolean tValidName;

		tFoundType = getTypeFromName (aName);
		if (tFoundType != NO_TYPE) {
			tValidName = true;
		} else {
			tValidName = false;
		}

		return tValidName;
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
		System.out.println ("Tile Type value " + type + " Name is " + NAMES [type]);	// PRINTLOG
	}

	private void setPaints () {
		int tPaintCount;

		if (paints == null) {
			tPaintCount = (MAX_TYPE - MIN_TYPE) + 1;
			setStaticPaints (tPaintCount);
		}
	}

	private static void setStaticPaints (int aPaintCount) {
		TexturePaint tTexturePaint;
		int tGridSize;
//		int tAlpha;
//		Color tTransparent;

		tGridSize = 5;
//		tAlpha = 75;
		paints = new Paint [aPaintCount] [2];
		paints [NO_TYPE] [0] = Color.lightGray;
		paints [YELLOW] [0]  = Color.yellow;
		paints [GREEN] [0]  = Color.green;
		paints [GREY] [0]  = new Color (150, 150, 150);
		paints [BROWN] [0]  = new Color (139, 69, 19);
		paints [RED_OFFBOARD] [0]  = new Color (153, 0, 0);
		paints [RED_BROWN] [0]  = new Color (204, 51, 51);
		paints [OCEAN_FERRY] [0]  = new Color (153, 204, 255);
		paints [OCEAN] [0]  = new Color (153, 204, 255);
		paints [PURPLE] [0]  = new Color (140, 49, 224);
		paints [CLEAR] [0]  = new Color (204, 255, 204);		// CLEAR

		// Highlight Section
		paints [NO_TYPE] [1] = Color.lightGray;
		tTexturePaint = XMLNode.createTexture (Color.yellow, Color.lightGray, tGridSize);	// YELLOW_HIGHLIGHT
//		tTransparent = GUI.makeTransparent (Color.yellow, tAlpha);
		paints [YELLOW] [1] = tTexturePaint;
		tTexturePaint = XMLNode.createTexture (Color.green, Color.lightGray, tGridSize);	// GREEN HIGHLIGHT
		paints [GREEN] [1] = tTexturePaint;
		tTexturePaint = XMLNode.createTexture (new Color (150, 150, 150), Color.white, tGridSize);		// GREY_HIGHLIGHT
		paints [GREY] [1] = tTexturePaint;
		tTexturePaint = XMLNode.createTexture (new Color (139, 69, 19), Color.white, tGridSize);		// BROWN_HIGHLIGHT
		paints [BROWN] [1] = tTexturePaint;
		tTexturePaint = XMLNode.createTexture (new Color (153, 0, 0), Color.white, tGridSize);		// BROWN_HIGHLIGHT
		paints [RED_OFFBOARD] [1] = tTexturePaint;
		paints [RED_BROWN] [1]  = new Color (204, 51, 51);
		paints [OCEAN_FERRY] [1]  = new Color (153, 204, 255);
		paints [OCEAN] [1]  = new Color (153, 204, 255);
		paints [PURPLE] [1]  = new Color (140, 49, 224);
		tTexturePaint = XMLNode.createTexture (new Color (204, 255, 204), Color.lightGray, tGridSize);	// CLEAR_HIGHLIGHT
		paints [CLEAR] [1] = tTexturePaint;
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

		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			Color tColor;
			int tID;

			tID = aChildNode.getThisIntAttribute (AN_ID);
			tColor = aChildNode.getThisColorAttribute (AN_COLOR);
			paints [tID] [0] = tColor;
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

	public int compareType (TileType aType) {
		int tTypeDiff;
		int tType;

		tType = aType.getType ();
		tTypeDiff = type - tType;

		return tTypeDiff;
	}
}
