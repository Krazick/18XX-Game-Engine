package ge18xx.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.NodeList;

import ge18xx.center.Centers;
import ge18xx.center.City;

//
//  HexMap.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.center.CityInfo;
import ge18xx.center.RevenueCenter;
import ge18xx.company.Corporation;
import ge18xx.company.MapToken;
import ge18xx.company.TokenCompany;
import ge18xx.company.TokenInfo.TokenType;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.CloseCompanyAction;
import ge18xx.round.action.ReplaceTokenAction;
import ge18xx.round.action.RotateTileAction;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileName;
import ge18xx.tiles.TileSet;
import ge18xx.tiles.TileType;
import ge18xx.toplevel.MapFrame;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.GUI;

public class HexMap extends JLabel implements LoadableXMLI, MouseListener, MouseMotionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	public static final ElementName EN_MAP = new ElementName ("Map");
	public static final ElementName EN_ROW = new ElementName ("Row");
	public static final AttributeName AN_ROWS = new AttributeName ("rows");
	public static final AttributeName AN_ROW = new AttributeName ("row");
	public static final AttributeName AN_COL = new AttributeName ("col");
	public static final AttributeName AN_COLS = new AttributeName ("cols");
	public static final AttributeName AN_INDEX = new AttributeName ("index");
	public static final AttributeName AN_START_COL = new AttributeName ("startCol");
	public static final AttributeName AN_DEFAULT_TYPE = new AttributeName ("defaultType");
	public static final AttributeName AN_DIRECTION = new AttributeName ("direction");
	public static final AttributeName AN_FILL_COLOR = new AttributeName ("fillColor");
	public static final AttributeName AN_ROW_START = new AttributeName ("rowStart");
	public static final AttributeName AN_COL_START = new AttributeName ("colStart");
	public static final boolean DONT_ADD_ACTION = false;
	public static final boolean DO_ADD_ACTION = true;
	public static final HexMap NO_HEX_MAP = null;
	MapCell map [] [];
	Hex18XX hex;
	TileSet tileSet;
	MapFrame mapFrame;
	SelectableMapCells selectableMapCells;
	MapGraph mapGraph;
	Logger logger;
	boolean selectRevenueCenter;
	boolean selectTrackSegment;
	boolean tilePlaced;
	boolean placeTileMode;
	boolean singleMapCellSelect; // Set true if in mode to select a SINGLE Hex Map Cell, selecting a different
								// should unselect ALL and leave only the single map cell selected.

	public HexMap (MapFrame aMapFrame) {
		setMapFrame (aMapFrame);
		hex = Hex18XX.NO_HEX18XX;
		setTilePlaced (false);
		addMouseListener (this);
		addMouseMotionListener (this);
		setBackground (Color.white);
		setSingleMapCellSelect (false);
		selectableMapCells = new SelectableMapCells ();
		if (mapFrame != XMLFrame.NO_XML_FRAME) {
			logger = mapFrame.getLogger ();
		}
	}

	/**
	 * Retrieve the current phase from the Game Manager and return it
	 * 
	 * @return The current Phase as int value
	 */

	public int getCurrentPhase () {
		return mapFrame.getCurrentPhase ();
	}

	public void setMapFrame (MapFrame aMapFrame) {
		mapFrame = aMapFrame;
	}
	
	public MapFrame getMapFrame () {
		return mapFrame;
	}
	
	// Selectable Map Cell Functions to be callable from elsewhere

	public void fillAllSMC () {
		// TODO Build routine to properly identify all of the MapCells that the
		// current Operating Company can place either a new tile (on empty Map Cell)
		// or upgrade an existing Tile
	}

	public void removeAllSMC () {
		selectableMapCells.removeAll ();
	}

	public void addMapCellSMC (MapCell aMapCell) {
		selectableMapCells.addMapCell (aMapCell);
	}

	public void addMapCellsSMC (String aMapCellIDs) {
		selectableMapCells.addMapCells (this, aMapCellIDs);
	}

	public boolean containsMapCellSMC (MapCell aMapCell) {
		return selectableMapCells.containsMapCell (aMapCell);
	}

	public boolean isSMCEmpty () {
		return selectableMapCells.isEmpty ();
	}

	public boolean mapCellIsInSelectableSMC (MapCell aMapCell) {
		boolean tIsInSelectable;

		if (isSMCEmpty ()) {
			tIsInSelectable = false;
		} else {
			tIsInSelectable = containsMapCellSMC (aMapCell);
		}

		return tIsInSelectable;
	}

	public void addReachableMapCells () {
		// TODO -- Fill the Selectable MapCells with those Map Cells reachable
		// from the Current Operating Company's current set of Tokens. Use the MapGraph
		// To find these MapCells.
	}

	public void CalcGridCenters () {
		int rowIndex;
		int colIndex;
		int Xc;
		int Yc;
		int toggle;
		int temp_2DLR;
		int temp_DUP_dwidth;
		int rowCount;
		int colCount;

		if (Hex.getDirection ()) {
			temp_2DLR = hex.getDisplaceLeftRight () + hex.getDisplaceLeftRight ();
			temp_DUP_dwidth = hex.getDisplaceUpDown () + Hex.getWidth ();
			rowCount = getRowCount ();

			Yc = 0 - temp_DUP_dwidth + hex.getIntDWidth ();
			if (Double.valueOf (rowCount / 2).intValue () * 2 == rowCount) {
				toggle = 0;
			} else {
				toggle = 1;
			}
			for (rowIndex = rowCount - 1; rowIndex >= 0; rowIndex--) {
				if (toggle == 1) {
					Xc = hex.getDisplaceLeftRight () - temp_2DLR;
				} else {
					Xc = 0;
				}
				Yc += temp_DUP_dwidth;
				colCount = getColCount (rowIndex);
				for (colIndex = 0; colIndex < colCount; colIndex++) {
					Xc += temp_2DLR;
					if (map [rowIndex] [colIndex] == MapCell.NO_MAP_CELL) {
						map [rowIndex] [colIndex] = new MapCell (Xc, Yc, this);
					} else {
						map [rowIndex] [colIndex].setXY (Xc, Yc);
					}
				}
				toggle = 1 - toggle;
			}
		} else {
			Xc = 0 - hex.getDisplaceUpDown ();
			toggle = 1;
			temp_2DLR = hex.getDisplaceLeftRight () + hex.getDisplaceLeftRight ();
			temp_DUP_dwidth = hex.getDisplaceUpDown () + Hex.getWidth ();
			rowCount = getRowCount ();

			for (rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Yc = 0 - hex.getDisplaceLeftRight () * toggle;
				Xc += temp_DUP_dwidth;
				colCount = getColCount (rowIndex);
				for (colIndex = 0; colIndex < colCount; colIndex++) {
					Yc += temp_2DLR;
					if (map [rowIndex] [colIndex] == MapCell.NO_MAP_CELL) {
						map [rowIndex] [colIndex] = new MapCell (Xc, Yc, this);
					} else {
						map [rowIndex] [colIndex].setXY (Xc, Yc);
					}
				}
				toggle = 1 - toggle;
			}

		}
	}

	public void clearAllSelected () {
		int rowIndex;
		int colIndex;
		int rowCount;
		int colCount;

		rowCount = getRowCount ();
		for (rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			colCount = getColCount (rowIndex);
			for (colIndex = 0; colIndex < colCount; colIndex++) {
				map [rowIndex] [colIndex].clearSelected ();
			}
		}
		redrawMap ();
		if (tileSet != TileSet.NO_TILE_SET) {
			tileSet.clearAllPlayable ();
		}
	}

	/**
	 * Clear the Specified Train Number from the Entire Map
	 *
	 * @param aTrainNumber The Train Number to clear
	 */
	public void clearTrain (int aTrainNumber) {
		int rowIndex;
		int colIndex;
		int rowCount;
		int colCount;

		rowCount = getRowCount ();
		for (rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			colCount = getColCount (rowIndex);
			for (colIndex = 0; colIndex < colCount; colIndex++) {
				map [rowIndex] [colIndex].clearTrain (aTrainNumber);
				map [rowIndex] [colIndex].clearTrainUsingSides (aTrainNumber);
			}
		}
	}

	/**
	 * Clear All Trains from All of the Map Cells on the Map. Will also Clear the
	 * Sides as well
	 *
	 */
	public void clearAllTrains () {
		int rowIndex;
		int colIndex;
		int rowCount;
		int colCount;

		rowCount = getRowCount ();
		for (rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			colCount = getColCount (rowIndex);
			for (colIndex = 0; colIndex < colCount; colIndex++) {
				map [rowIndex] [colIndex].clearAllTrains ();
				map [rowIndex] [colIndex].clearAllTrainsUsingSides ();
			}
		}
	}

	public void collectNonHomeMapCellIDs (int aCorpID, String aAbbrev, List<String> aHomeMapCellIDs, 
					List<String> aNonHomeMapCellIDs) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		String tMapCellID;
		Location tLocation;
		String tTokenLocation;

		tRowCount = getRowCount ();
		for (tRowIndex = 0; (tRowIndex < tRowCount); tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; (tColIndex < tColCount); tColIndex++) {
				if (map [tRowIndex] [tColIndex].hasStation (aCorpID)) {
					tMapCellID = map [tRowIndex] [tColIndex].getID ();
					tLocation = map [tRowIndex] [tColIndex].getLocationWithStation (aCorpID);
					tTokenLocation = buildTokenLocation (aCorpID, aAbbrev, tMapCellID, tLocation);
					if (! aHomeMapCellIDs.contains (tTokenLocation)) {
						aNonHomeMapCellIDs.add (tTokenLocation);
					}
				}
			}
		}

	}
	
	public String getTokenLocation (String aMapCellID, String aAbbrev, int aCorpID) {
		MapCell tMapCell;
		Location tMapCelllLocation;
		String tTokenLocation;
		
		tMapCell = getMapCellForID (aMapCellID);
		tMapCelllLocation = tMapCell.getLocationWithStation (aCorpID);
		tTokenLocation = buildTokenLocation (aCorpID, aAbbrev, aMapCellID, tMapCelllLocation);
		
		return tTokenLocation;
	}

	public String buildTokenLocation (int aCorpID, String aAbbrev, String aMapCellID, Location aLocation) {
		String tTokenLocation;
		int tLocationInt;
		
		if (aLocation == Location.NO_LOC) {
			tLocationInt = Location.CENTER_CITY_LOC;
		} else {
			tLocationInt = aLocation.getLocation ();
		}
		tTokenLocation = aCorpID + ":" + aAbbrev + ":" + aMapCellID + ":" + tLocationInt;
		
		return tTokenLocation;
	}
	
	public boolean hasStation (int aCorpID) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		boolean tHasStation;

		tHasStation = false;
		tRowCount = getRowCount ();
		for (tRowIndex = 0; (tRowIndex < tRowCount) && !tHasStation; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; (tColIndex < tColCount) && !tHasStation; tColIndex++) {
				if (map [tRowIndex] [tColIndex].hasStation (aCorpID)) {
					tHasStation = true;
				}
			}
		}

		return tHasStation;
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tElement;
		XMLElement tCellElement;
		XMLElement tRowElement;
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		int tMaxColCount;

		tElement = aXMLDocument.createElement (EN_MAP);
		tRowCount = getRowCount ();
		tMaxColCount = getMaxColCount ();
		tElement.setAttribute (AN_ROWS, tRowCount);
		tElement.setAttribute (AN_COLS, tMaxColCount);
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tRowElement = aXMLDocument.createElement (EN_ROW);
			tRowElement.setAttribute (AN_INDEX, tRowIndex);
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				tCellElement = map [tRowIndex] [tColIndex].createElement (aXMLDocument);
				tRowElement.appendChild (tCellElement);
			}
			tElement.appendChild (tRowElement);
		}

		return tElement;
	}

	public boolean putMapTokenDown (TokenCompany aTokenCompany, MapToken aMapToken, TokenType aTokenType, 
					City aCity, MapCell aMapCell, boolean aAddLayTokenAtion) {
		boolean tTokenPlaced;
		
		tTokenPlaced = mapFrame.putMapTokenDown (aTokenCompany, aMapToken, aTokenType, aCity, 
					aMapCell, aAddLayTokenAtion);
		
		return tTokenPlaced;
	}

	public int getColCount (int thisRow) {
		if (map == MapCell.NO_MAP_CELLS) {
			return 0;
		}

		return (map [thisRow].length);
	}

	public int getHexWidth () {
		return (Hex.getWidth ());
	}

	public int getHexHeight () {
		return (hex.getYd () * 2);
	}

	public int getHexYd () {
		return (hex.getYd ());
	}

	private String [] getIDs (String aID) {
		String [] theIDs;
		String [] tempIDs;
		String tempAlpha;
		String tUpperAlpha;
		String tLowerAlpha;
		String tNumerics;
		int tMatchIndex;
		int tIndex;
		int tSize;

		theIDs = null;
		tempIDs = null;
		tempAlpha = null;
		tUpperAlpha = "A:B:C:D:E:F:G:H:I:J:K:L:M:N:O:P:Q:R:S:T:U:V:W:X:Y:Z";
		tLowerAlpha = "a:b:c:d:e:f:g:h:i:j:k:l:m:n:o:p:q:r:s:t:u:v:w:x:y:z:aa:ab:ac:ad:ae:af:ag:ah:ai:aj:ak:al:am:an:ao:ap:aq:ar:as:at:au:av:aw:ax:ay:az";
		tNumerics = "1:2:3:4:5:6:7:8:9:10:11:12:13:14:15:16:17:18:19:20:21:22:23:24:25:26:27:28:29:30";
		if (aID != null) {
			if (aID.equals ("A")) {
				theIDs = tUpperAlpha.split (":");
			} else if (aID.equals ("a")) {
				theIDs = tLowerAlpha.split (":");
			} else if (aID.equals ("1")) {
				theIDs = tNumerics.split (":");
			} else if (aID.equals ("0")) {
				tNumerics = "0:" + tNumerics;
				theIDs = tNumerics.split (":");
			} else if (aID.equals ("zz")) {
				tLowerAlpha = "zz:" + tLowerAlpha;
				theIDs = tLowerAlpha.split (":");
			} else {
				// If not match the first character, try matching against another, and reverse
				// the array sent back.
				tMatchIndex = tUpperAlpha.indexOf (aID) + 1;
				if (tMatchIndex > 0) {
					tempAlpha = tUpperAlpha.substring (0, tMatchIndex);
				} else {
					tMatchIndex = tLowerAlpha.indexOf (aID);
					if (tMatchIndex > 0) {
						tempAlpha = tLowerAlpha.substring (0, tMatchIndex);
					}
				}
				if (tempAlpha != null) {
					tempIDs = tempAlpha.split (":");
					tSize = tempIDs.length;
					theIDs = new String [tSize];
					for (tIndex = 0; tIndex < tSize; tIndex++) {
						theIDs [tIndex] = tempIDs [tSize - tIndex - 1];
					}
				}
			}
		} else {
			theIDs = tLowerAlpha.split (":");
		}

		return theIDs;
	}

	public MapCell getMapCell (int aRow, int aCol) {
		MapCell tMapCell;

		tMapCell = MapCell.NO_MAP_CELL;
		if (inRowRange (aRow)) {
			if (inColRange (aRow, aCol)) {
				tMapCell = map [aRow] [aCol];
			}
		}

		return tMapCell;
	}

	public MapCell getMapCellContainingPoint (Point2D.Double aPoint) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		MapCell tFoundMapCell;

		tFoundMapCell = MapCell.NO_MAP_CELL;
		tRowCount = getRowCount ();
		for (tRowIndex = 0; (tRowIndex < tRowCount) && (tFoundMapCell == MapCell.NO_MAP_CELL); tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; (tColIndex < tColCount) && (tFoundMapCell == MapCell.NO_MAP_CELL); tColIndex++) {
				if (map [tRowIndex] [tColIndex].containingPoint (aPoint, hex)) {
					tFoundMapCell = map [tRowIndex] [tColIndex];
				}
			}
		}

		return tFoundMapCell;
	}

	public XMLElement getMapStateElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLMapCellElement;
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		MapCell tMapCell;

		tXMLElement = aXMLDocument.createElement (EN_MAP);
		tRowCount = getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				tMapCell = map [tRowIndex] [tColIndex];
				if (tMapCell != MapCell.NO_MAP_CELL) {
					tXMLMapCellElement = tMapCell.getMapCellState (aXMLDocument);
					if (tXMLMapCellElement != XMLElement.NO_XML_ELEMENT) {
						tXMLMapCellElement.setAttribute (AN_ROW, tRowIndex);
						tXMLMapCellElement.setAttribute (AN_COL, tColIndex);
						tXMLElement.appendChild (tXMLMapCellElement);
					}
				}
			}
		}

		return tXMLElement;
	}

	public MapCell getMapCellContainingPoint (Point aPoint) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		MapCell tFoundMapCell;
		
		tFoundMapCell = MapCell.NO_MAP_CELL;
		tRowCount = getRowCount ();
		for (tRowIndex = 0; (tRowIndex < tRowCount) && (tFoundMapCell == MapCell.NO_MAP_CELL); tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; (tColIndex < tColCount) && (tFoundMapCell == MapCell.NO_MAP_CELL); tColIndex++) {
				if (map [tRowIndex] [tColIndex].containingPoint (aPoint, hex)) {
					tFoundMapCell = map [tRowIndex] [tColIndex];
				}
			}
		}

		return tFoundMapCell;
	}

	public MapCell getMapCellForID (String aID) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		MapCell tFoundMapCell;
		
		tFoundMapCell = MapCell.NO_MAP_CELL;
		if (aID != null) {
			if (!aID.equals (GUI.EMPTY_STRING)) {
				tRowCount = getRowCount ();
				for (tRowIndex = 0; (tRowIndex < tRowCount) 
					&& (tFoundMapCell == MapCell.NO_MAP_CELL); 
						tRowIndex++) {
					tColCount = getColCount (tRowIndex);
					for (tColIndex = 0; (tColIndex < tColCount)
							&& (tFoundMapCell == MapCell.NO_MAP_CELL); tColIndex++) {
						if (map [tRowIndex] [tColIndex].forID (aID)) {
							tFoundMapCell = map [tRowIndex] [tColIndex];
						}
					}
				}
			}
		}

		return tFoundMapCell;
	}

	public int getMaxColCount () {
		int tIndex;
		int tMaxColCount;
		int tColCount;
		int tRowCount;

		tMaxColCount = getColCount (0);
		if (tMaxColCount > 0) {
			tRowCount = getRowCount ();
			for (tIndex = 1; tIndex < tRowCount; tIndex++) {
				tColCount = getColCount (tIndex);
				if (tColCount > tMaxColCount) {
					tMaxColCount = tColCount;
				}
			}
		}

		return tMaxColCount;
	}

	public int getMaxWidth () {
		int tMaxWidth;
		
		tMaxWidth = getMaxColCount () * getHexWidth ();
		
		return tMaxWidth;
	}

	public int getMaxHeight () {
		int tMaxHeight;
		
		tMaxHeight = getMaxRowCount () * getHexHeight ();
		
		return tMaxHeight;
	}

	public int getMaxRowCount () {
		return (getRowCount ());
	}

	public int getMaxX () {
		int tMaxRow;
		int tMaxCol;
		int tMaxX;
		int tMaxX1;
		int tMaxX2;

		if (map == MapCell.NO_MAP_CELLS) {
			tMaxX = 0;
		} else {
			tMaxRow = getMaxRowCount ();
			if (map [tMaxRow - 1] [0] == MapCell.NO_MAP_CELL) {
				tMaxX = 0;
			} else {
				if (map [0] [0].getMapDirection ()) {
					tMaxCol = getMaxColCount ();
					tMaxX1 = map [0] [tMaxCol - 1].getXCenter () + hex.rightEdgeDisplacement ();
					tMaxX2 = map [0] [tMaxCol - 2].getXCenter () + hex.rightEdgeDisplacement ();
					if (tMaxX1 > tMaxX2) {
						tMaxX = tMaxX1;
					} else {
						tMaxX = tMaxX2;
					}
				} else {
					tMaxX = map [tMaxRow - 1] [0].getXCenter () + hex.rightEdgeDisplacement ();
				}
			}
		}

		return (tMaxX + 3);
	}

	public int getMaxY () {
		int tMaxY;
		int tMaxRow;
		int tMaxCol;

		if (map == MapCell.NO_MAP_CELLS) {
			tMaxY = 0;
		} else {
			tMaxRow = getMaxRowCount ();
			tMaxCol = getMaxColCount ();
			if (map [1] [tMaxCol - 1] == MapCell.NO_MAP_CELL) {
				tMaxY = 0;
			} else {
				if (map [0] [0].getMapDirection ()) {
					tMaxY = map [0] [0].getYCenter () + hex.bottomEdgeDisplacement ();
				} else {
					if (tMaxRow > 0) {
						tMaxY = map [1] [tMaxCol - 1].getYCenter () + hex.bottomEdgeDisplacement ();
					} else {
						tMaxY = map [0] [tMaxCol - 1].getYCenter () + hex.bottomEdgeDisplacement ();
					}
				}
			}
		}

		return (tMaxY + 3);
	}

	public int getMinX () {
		int tMinX;

		tMinX = 0;
		
		return tMinX;
	}

	public int getMinY () {
		int tMinY;

		tMinY = 0;
		
		return tMinY;
	}

	public int getRevenueCenterID (int aRow, int aCol) {
		int tRevenueCenterID;

		tRevenueCenterID = RevenueCenter.NO_ID;
		if (inRowRange (aRow)) {
			if (inColRange (aRow, aCol)) {
				tRevenueCenterID = map [aRow] [aCol].getRevenueCenterID ();
			}
		}

		return tRevenueCenterID;
	}

	public int getRowCount () {
		int tRowCount;
		

		if (map == MapCell.NO_MAP_CELLS) {
			tRowCount = 0;
		} else {
			tRowCount = map.length;
		}

		return tRowCount;
	}

	public TokenCompany getTokenCompany (String aAbbrev) {
		return mapFrame.getTokenCompany (aAbbrev);
	}

	public MapCell getSelectedMapCell () {
		int rowIndex;
		int colIndex;
		int rowCount;
		int colCount;
		MapCell foundMapCell;

		rowCount = getRowCount ();
		foundMapCell = MapCell.NO_MAP_CELL;
		for (rowIndex = 0; (rowIndex < rowCount) && (foundMapCell == MapCell.NO_MAP_CELL); rowIndex++) {
			colCount = getColCount (rowIndex);
			for (colIndex = 0; (colIndex < colCount) && (foundMapCell == MapCell.NO_MAP_CELL); colIndex++) {
				if (map [rowIndex] [colIndex].isSelected ()) {
					foundMapCell = map [rowIndex] [colIndex];
				}
			}
		}

		return foundMapCell;
	}

	public boolean getSelectRevenueCenter () {
		return selectRevenueCenter;
	}

	public boolean getSelectTrackSegment () {
		return selectTrackSegment;
	}

	public Terrain getTerrain () {
		return map [0] [0].getBaseTerrain ();
	}

	public int getTileNumber (int aRow, int aCol) {
		int tTileNumber;

		tTileNumber = 0;
		if (inRowRange (aRow)) {
			if (inColRange (aRow, aCol)) {
				tTileNumber = map [aRow] [aCol].getTileNumber ();
			}
		}

		return tTileNumber;
	}

	@Override
	public String getTypeName () {
		return "Map";
	}

	public void handleSelectRevenueCenter (MapCell aSelectedMapCell, MapCell aPreviousSelectedMapCell, 
						Point aPoint) {
		City tSelectedCity;
		RevenueCenter tSelectedRevenueCenter;

		tSelectedCity = City.NO_CITY;
		tSelectedRevenueCenter = RevenueCenter.NO_CENTER;
		if (aPreviousSelectedMapCell != MapCell.NO_MAP_CELL) {
			aPreviousSelectedMapCell.clearSelected ();
		}
		if (aSelectedMapCell != MapCell.NO_MAP_CELL) {
			if (containsMapCellSMC (aSelectedMapCell)) {
				aSelectedMapCell.handleSelectRevenueCenter (aPoint);
				tSelectedRevenueCenter = aSelectedMapCell.getSelectedRevenueCenter ();
				if (tSelectedRevenueCenter instanceof City) {
					tSelectedCity = (City) tSelectedRevenueCenter;
				}
				if (mapFrame.isSelectRouteMode ()) {
					if (aSelectedMapCell.isTileOnCell ()) {
						mapFrame.handleSelectedRoute (aSelectedMapCell, tSelectedRevenueCenter);
					} else {
						System.err.println ("No Tile, and no Track on Tile - Ignore the Click");
					}
				} else {
					if (tSelectedCity != City.NO_CITY) {
						mapFrame.updatePutTokenButton (tSelectedCity, aSelectedMapCell);
					}
				}
			}
		}

	}

	public void handleSingleMapCellSelect (MapCell aSelectedMapCell, MapCell aPreviousSelectedMapCell, 
							MouseEvent aMouseEvent) {

		if (aPreviousSelectedMapCell == MapCell.NO_MAP_CELL) {
			if (containsMapCellSMC (aSelectedMapCell)) {
				toggleSelectedMapCell (aSelectedMapCell);
			} else {
				if (aSelectedMapCell == MapCell.NO_MAP_CELL) {
					System.err.println ("No Selected Map Cell provided");
				} else {
					System.err.println ("The Map Cell " + aSelectedMapCell.getID () 
							+ " is currently NOT Selectable (1)");
				}
			}
		} else {
			if (aSelectedMapCell == MapCell.NO_MAP_CELL) {
				toggleSelectedMapCell (aSelectedMapCell);
			} else {
				if (aPreviousSelectedMapCell == aSelectedMapCell) {
					if (aSelectedMapCell.isTileOnCell ()) {
						// Tile is on Cell. Check if Locked Orientation.
						if (!aSelectedMapCell.isTileOrientationLocked ()) {
							// Tile Orientation is not locked, time to Rotate in place
							rotateTileInPlace (aSelectedMapCell, DO_ADD_ACTION, aMouseEvent);
						} else {
							// Tile Orientation is locked, Toggle Cell selection
							toggleSelectedMapCell (aSelectedMapCell);
						}
					} else if (containsMapCellSMC (aSelectedMapCell)) {
						// No Tile on Cell, Toggle Selection
						toggleSelectedMapCell (aSelectedMapCell);
					} else {
						System.err.println ("The Map Cell " + aSelectedMapCell.getID () 
								+ " is currently NOT Selectable (2)");
					}
				} else {
					if (containsMapCellSMC (aSelectedMapCell)) {
						if (aSelectedMapCell.isSelectable ()) {
							aPreviousSelectedMapCell.lockTileOrientation ();
							toggleSelectedMapCell (aPreviousSelectedMapCell);	
							toggleSelectedMapCell (aSelectedMapCell);			
						}
					} else {
						System.err.println ("The Map Cell " + aSelectedMapCell.getID () 
								+ " is currently NOT Selectable (3)");
					}
				}
			}
		}
	}

	public void rotateTileInPlace (MapCell aThisMapCell, boolean aAddAction, MouseEvent aMouseEvent) {
		int tCountOfRotations;
		int tPossible;
		int tSteps;
		int tNewOrientation;
		int tPreviousOrientation;
		boolean tShiftDown;
		Tile tTile;
		RotateTileAction tRotateTileAction;
		GameManager tGameManager;
		RoundManager tRoundManager;
		Corporation tOperatingCompany;
		String tTokens;
		String tBases;
		String tOperatingRoundID;

		tCountOfRotations = aThisMapCell.getCountofAllowedRotations ();
		if (tCountOfRotations > 1) {
			tTile = aThisMapCell.getTile ();
			tPossible = aThisMapCell.getTileOrient ();
			tPreviousOrientation = tPossible;
			tShiftDown = aMouseEvent.isShiftDown ();
			tSteps = aThisMapCell.calculateSteps (tPossible, tTile, tShiftDown);
			if (tShiftDown) {
				aThisMapCell.rotateTileLeft (tSteps);
			} else {
				aThisMapCell.rotateTileRight (tSteps);
			}
			if (tTile.getRevenueCenterCount () == 2) {
				// Two Revenue Centers, need to Swap Tokens -- "OO" in 1830 and 1856 to keep on
				// the correct centers -- a bit kludgy
				aThisMapCell.swapTokens ();
			}
			if (aAddAction == DO_ADD_ACTION) {
				tNewOrientation = aThisMapCell.getTileOrient ();
				tGameManager = (GameManager) mapFrame.getGameManager ();
				tRoundManager = tGameManager.getRoundManager ();
				tOperatingCompany = tRoundManager.getOperatingCompany ();
				tOperatingRoundID = tGameManager.getOperatingRoundID ();
				tRotateTileAction = new RotateTileAction (ActorI.ActionStates.OperatingRound,
						tOperatingRoundID, tOperatingCompany);
				tTokens = tTile.getPlacedTokens ();
				tBases = tTile.getCorporationBases ();
				tRotateTileAction.addRotateTileEffect (tOperatingCompany, aThisMapCell, tTile, tNewOrientation,
						tPreviousOrientation, tTokens, tBases, tTokens);
				tRoundManager.addAction (tRotateTileAction);
			}
		} else {
			System.err.println ("Only ONE Allowed Rotations have been identified for the Tile on this MapCell");
		}
	}

	public boolean inColRange (int aRow, int aCol) {
		boolean tInColRange;
		
		tInColRange = false;
		if (inRowRange (aRow)) {
			tInColRange = ((aCol >= 0) && (aCol < getColCount (aRow)));
		}
		
		return tInColRange;
	}

	public boolean inRowRange (int aRow) {
		boolean tInRowRange;
		
		tInRowRange = ((aRow >= 0) && (aRow < getRowCount ()));
		
		return tInRowRange;
	}

	public boolean inRowColRanges (int aRow, int aCol) {
		boolean tInRowColRanges;

		tInRowColRanges = false;
		if (inRowRange (aRow)) {
			if (inColRange (aRow, aCol)) {
				tInRowColRanges = true;
			}
		}

		return tInRowColRanges;
	}

	public boolean isTileOnCell (int aRow, int aCol) {
		boolean tileFound;

		tileFound = false;
		if (inRowColRanges (aRow, aCol)) {
			tileFound = map [aRow] [aCol].isTileOnCell ();
		}

		return tileFound;
	}

	public Tile getTileFromTileSet (int aTileNumber) {
		Tile tTile;
		GameTile tGameTile;

		tTile = Tile.NO_TILE;
		tGameTile = tileSet.getGameTile (aTileNumber);
		if (tGameTile != GameTile.NO_GAME_TILE) {
			tTile = tGameTile.popTile ();
		} else {
			System.err.println ("Did not find the Game Tile with # " + aTileNumber);
		}

		return tTile;
	}

	public void loadMapCellState (XMLNode aMapCellNode) {
		int tCol;
		int tRow;
		int tTileOrientation;
		int tTileNumber;
		int tBenefitValue;
		int tDefaultTileNumber;
		boolean tHasPort;
		boolean tHasCattle;

		tCol = aMapCellNode.getThisIntAttribute (AN_COL);
		tRow = aMapCellNode.getThisIntAttribute (AN_ROW);
		tTileOrientation = aMapCellNode.getThisIntAttribute (MapCell.AN_ORIENTATION);
		tTileNumber = aMapCellNode.getThisIntAttribute (Tile.AN_TILE_NUMBER);
		tHasPort = aMapCellNode.getThisBooleanAttribute (MapCell.AN_PORT_TOKEN);
		tHasCattle = aMapCellNode.getThisBooleanAttribute (MapCell.AN_CATTLE_TOKEN);
		tBenefitValue = aMapCellNode.getThisIntAttribute (MapCell.AN_BENEFIT_VALUE);
		if (inRowRange (tRow)) {
			if (inColRange (tRow, tCol)) {
				if (isTileOnCell (tRow, tCol)) {
					tDefaultTileNumber = map [tRow] [tCol].getTileNumber ();
					if (tDefaultTileNumber == tTileNumber) {
						placeBenefitTokens (tCol, tRow, tBenefitValue, tHasPort, tHasCattle);
					} else {
						placeTileWithState (tCol, tRow, tTileOrientation, tTileNumber, tBenefitValue, 
									tHasPort, tHasCattle);
					}
				} else {
					placeTileWithState (tCol, tRow, tTileOrientation, tTileNumber, tBenefitValue, 
									tHasPort, tHasCattle);
				}
				if (isTileOnCell (tRow, tCol)) {
					map [tRow] [tCol].loadStationsStates (aMapCellNode);
					map [tRow] [tCol].loadBaseStates (aMapCellNode);
				}
			}
		}
	}

	private void placeTileWithState (int aCol, int aRow, int aTileOrientation, int aTileNumber, int aBenefitValue, 
			boolean aHasPort, boolean aHasCattle) {
		Tile tTile;
		Tile tCurrentTile;
		
		tTile = getTileFromTileSet (aTileNumber);
		if (tTile != Tile.NO_TILE) {
			tCurrentTile = map [aRow] [aCol].getTile ();
			map [aRow] [aCol].putTile (tTile, aTileOrientation);
			map [aRow] [aCol].lockTileOrientation ();
			placeBenefitTokens (aCol, aRow, aBenefitValue, aHasPort, aHasCattle);
			restoreTile (tCurrentTile);
		} else {
			System.err.println ("Upgrade: Did not find the Tile with # " + aTileNumber);
		}
	}
	
	private void placeBenefitTokens (int aCol, int aRow, int aBenefitValue, boolean aHasPort, 
					boolean aHasCattle) {		
		if (aHasPort) {
			map [aRow] [aCol].layPortToken ();
		}
		if (aHasCattle) {
			map [aRow] [aCol].layCattleToken ();
		}
		map [aRow] [aCol].setBenefitValue (aBenefitValue);
	}

	private boolean loadXMLRow (XMLNode aRowNode, int aTerrainCost[], int aTerrainType[], int aCols,
			int aDefaultTerrainType, String [] theRowIDs, String [] theColIDs) throws IOException {
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		String tID;
		int tChildrenCount;
		int tRowIndex;
		int tColIndex;
		int index;
		int tOddRow;
		boolean evenRow;
		boolean tGoodLoad;
		MapCell tMapCell;

		tGoodLoad = true;
		tRowIndex = aRowNode.getThisIntAttribute (AN_INDEX, 0);
		tChildren = aRowNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tColIndex = aRowNode.getThisIntAttribute (AN_START_COL, 0);
		if (tColIndex != 0) {
			for (index = 0; index < tColIndex; index++) {
				map [tRowIndex] [index].setEmptyMapCell (aDefaultTerrainType);
			}
		}
		if ((tRowIndex / 2) * 2 == tRowIndex) {
			evenRow = true;
			tOddRow = 0;
		} else {
			evenRow = false;
			tOddRow = 1;
		}

		for (index = 0; (index < tChildrenCount) && tGoodLoad; index++) {
			tChildNode = new XMLNode (tChildren.item (index));
			tChildName = tChildNode.getNodeName ();
			if (MapCell.EN_MAP_CELL.equals (tChildName)) {
				if (tColIndex < aCols) {
					if (map [tRowIndex] [tColIndex].getMapDirection ()) {
						tID = theRowIDs [tRowIndex] + theColIDs [tColIndex * 2 + tOddRow];
					} else {
						tID = theRowIDs [tRowIndex] + theColIDs [tColIndex * 2 + tOddRow];
					}
					map [tRowIndex] [tColIndex].loadXMLCell (tChildNode, aTerrainCost, aTerrainType, tID);
					tMapCell = map [tRowIndex] [tColIndex];
					tMapCell.setOffsetCoordinates (tColIndex, tRowIndex);
					tColIndex++;
				} else {
					tGoodLoad = false;
				}
			}
		}
		if (aCols > tColIndex) {
			for (index = tColIndex; index < aCols; index++) {
				map [tRowIndex] [index].setEmptyMapCell (aDefaultTerrainType);
			}
		}

		for (index = 0; index < aCols; index++) {
			if (index > 0) {
				map [tRowIndex] [index].setNeighbor (0, map [tRowIndex] [index - 1]);
			}
			if (tRowIndex > 0) {
				if (evenRow) {
					map [tRowIndex] [index].setNeighbor (4, map [tRowIndex - 1] [index]);
					if (index > 0) {
						map [tRowIndex] [index].setNeighbor (5, map [tRowIndex - 1] [index - 1]);
					}
				} else {
					map [tRowIndex] [index].setNeighbor (5, map [tRowIndex - 1] [index]);
					if ((index + 1) < aCols) {
						map [tRowIndex] [index].setNeighbor (4, map [tRowIndex - 1] [index + 1]);
					}
				}
			}
		}

		if (!tGoodLoad) {
			System.err.println ("Bad Load on Row [" + tRowIndex + "].");
		}

		return tGoodLoad;
	}

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNode tXMLMapRoot;
		XMLNode tChildNode;
		XMLNode tTCChildNode;
		NodeList tChildren;
		NodeList tTerrainCostsChildren;
		int tCols;
		int tRows;
		int tDefaultTerrainType;
		int tTCTIndex;
		int tChildrenCount;
		int tIndex;
		int tTerrainCostsCount;
		int tTCindex;
		String tDirection;
		String tDefaultType;
		String tChildName;
		String tTCChildName;
		String tTCTypeName;
		String tFillColor = "black";
		String tRowStartID;
		String tColStartID;
		String theRowIDs [];
		String theColIDs [];
		boolean tLoadedRow;
		int tTerrainType [];
		int tTerrainCost [];
		int tDefaultHexSize;
		int tRow;
		int tCol;

		tTerrainType = new int [15];
		tTerrainCost = new int [15];
		tXMLMapRoot = aXMLDocument.getDocumentNode ();
		tCols = tXMLMapRoot.getThisIntAttribute (AN_COLS);
		tRows = tXMLMapRoot.getThisIntAttribute (AN_ROWS);
		tDefaultType = tXMLMapRoot.getThisAttribute (AN_DEFAULT_TYPE);
		if (tDefaultType.equals (GUI.EMPTY_STRING)) {
			tDefaultTerrainType = 0;
		} else {
			tDefaultTerrainType = Terrain.getTypeFromName (tDefaultType);
		}
		tDirection = tXMLMapRoot.getThisAttribute (AN_DIRECTION);
		tFillColor = tXMLMapRoot.getThisAttribute (AN_FILL_COLOR);
		tRowStartID = tXMLMapRoot.getThisAttribute (AN_ROW_START);
		tColStartID = tXMLMapRoot.getThisAttribute (AN_COL_START);

		mapFrame.setDefaults (tXMLMapRoot);

		theRowIDs = getIDs (tRowStartID);
		theColIDs = getIDs (tColStartID);
		buildMapArray (tCols, tRows);
		tRow = 0;
		tCol = 0;
		setMapCell (tRow, tCol, tDirection);
		map [0] [0].setTerrainFillColor (tFillColor);
		hex = new Hex18XX (map [0] [0].getMapDirection ());
		tDefaultHexSize = mapFrame.getDefaultHexScale ();
		if (tDefaultHexSize == 0) {
			tDefaultHexSize = 8;
		}
		setHexScale (tDefaultHexSize);

		CalcGridCenters ();

		tChildren = tXMLMapRoot.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tTCTIndex = 0;
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (Terrain.EN_TERRAIN_COSTS.equals (tChildName)) {
				tTerrainCostsChildren = tChildNode.getChildNodes ();
				tTerrainCostsCount = tTerrainCostsChildren.getLength ();
				for (tTCindex = 0; tTCindex < tTerrainCostsCount; tTCindex++) {
					tTCChildNode = new XMLNode (tTerrainCostsChildren.item (tTCindex));
					tTCChildName = tTCChildNode.getNodeName ();
					if (Terrain.EN_TERRAIN.equals (tTCChildName)) {
						tTerrainCost [tTCTIndex] = tTCChildNode.getThisIntAttribute (Terrain.AN_COST);
						tTCTypeName = tTCChildNode.getThisAttribute (Terrain.AN_TYPE);
						tTerrainType [tTCTIndex] = Terrain.getTypeFromName (tTCTypeName);
						tTCTIndex++;
					}
				}
			} else if (EN_ROW.equals (tChildName)) {
				tLoadedRow = loadXMLRow (tChildNode, tTerrainCost, tTerrainType, tCols, tDefaultTerrainType,
								theRowIDs, theColIDs);
				if (!tLoadedRow) {
					System.err.println ("Found too many columns to Load on Row.");
				}
			}
		}

		setSingleMapCellSelect (false);
		setMapSize ();
		mapFrame.setDefaultFrameInfo ();
	}

	public void setMapCell (int aRow, int aCol, String aDirection) {
		MapCell tMapCell;
		
		tMapCell = new MapCell (this, aDirection);
		tMapCell.setOffsetCoordinates (aCol, aRow);
		setMapCell (aRow, aCol, tMapCell);
	}
	
	public void setMapCell (int aRow, int aCol, MapCell aMapCell) {
		map [aRow] [aCol] = aMapCell;
	}

	public void buildMapArray (int aCols, int aRows) {
		map = new MapCell [aRows] [aCols];
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged (MouseEvent arg0) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved (MouseEvent aMouseEvent) {
		MapCell tMapCell;

		Point tPoint = aMouseEvent.getPoint ();
		tMapCell = getMapCellContainingPoint (tPoint);
		if (tMapCell == MapCell.NO_MAP_CELL) {
			setToolTipText ("***");
		} else {
			setToolTipText (tMapCell.getToolTip ());
		}
	}

	@Override
	public void mouseClicked (MouseEvent e) {
	}

	public void redrawMap () {
		revalidate ();
		repaint ();
	}

	@Override
	public void mouseEntered (MouseEvent e) {
	}

	@Override
	public void mouseExited (MouseEvent e) {
	}

	@Override
	public void mousePressed (MouseEvent e) {
	}

	@Override
	public void mouseReleased (MouseEvent aMouseEvent) {
		handleClick (aMouseEvent);
	}

	public void handleClick (MouseEvent aMouseEvent) {
		Point tPoint;
		MapCell tSelectedMapCell;
		MapCell tPreviousSelectedMapCell;
		boolean tShiftDown;
		
		tPoint = aMouseEvent.getPoint ();

		tShiftDown = aMouseEvent.isShiftDown ();
		tSelectedMapCell = getMapCellContainingPoint (tPoint);
		tPreviousSelectedMapCell = getSelectedMapCell ();
		if (singleMapCellSelect) {
			handleSingleMapCellSelect (tSelectedMapCell, tPreviousSelectedMapCell, aMouseEvent);
		} else {
			if (tShiftDown && mapFrame.isSelectRouteMode ()) {
				mapFrame.handleRemoveRouteSegment (tSelectedMapCell);
			} else {
				if (selectRevenueCenter) {
					handleSelectRevenueCenter (tSelectedMapCell, tPreviousSelectedMapCell, tPoint);
				} else {
					if (tPreviousSelectedMapCell == tSelectedMapCell) {
						toggleSelectedMapCell (tSelectedMapCell);
					} else {
						toggleSelectedMapCell (tPreviousSelectedMapCell);
						toggleSelectedMapCell (tSelectedMapCell);
					}
				}
			}
		}

		redrawMap ();
	}

	@Override
	public void paintComponent (Graphics aGraphics) {
		int tRowIndex;
		int tColIndex;
		int tRowCount;
		int tColCount;
		

		tRowCount = getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				map [tRowIndex] [tColIndex].paintComponent (aGraphics, hex);
				if (mapFrame.isATestGame ()) {
					map [tRowIndex] [tColIndex].paintRowCol (aGraphics, hex, tRowIndex, tColIndex);
				}
			}
		}
	}

	public void putTile (int aRow, int aCol, Tile aTile) {
		if (inRowRange (aRow)) {
			if (inColRange (aRow, aCol)) {
				map [aRow] [aCol].placeTile (tileSet, aTile);
			}
		}
	}

	public void putStartingTile (int aRow, int aCol, Tile aTile) {
		if (inRowRange (aRow)) {
			if (inColRange (aRow, aCol)) {
				map [aRow] [aCol].placeTile (tileSet, aTile);
				map [aRow] [aCol].setStartingTile ();
			}
		}
	}

	public void setCityInfoXX (int aRow, int aCol, CityInfo aCityInfo) {
		if (inRowRange (aRow)) {
			if (inColRange (aRow, aCol)) {
				map [aRow] [aCol].setCityInfo (aCityInfo);
			}
		}
	}

	public void setMapSize () {
		int tMaxX;
		int tMaxY;
		Dimension tMaxSize;

		tMaxX = getMaxX ();
		tMaxY = getMaxY ();
		tMaxSize = new Dimension (tMaxX, tMaxY);
		setPreferredSize (tMaxSize);
	}

	public void setPlaceTileMode (boolean aMode) {
		placeTileMode = aMode;
	}

	public boolean isPlaceTileMode () {
		return placeTileMode;
	}

	public void setSingleMapCellSelect (boolean aSelectState) {
		singleMapCellSelect = aSelectState;
		clearAllSelected ();
	}

	public void setSelectRevenueCenter (boolean aSelectState) {
		selectRevenueCenter = aSelectState;
	}

	public void setSelectTrackSegment (boolean aSelectState) {
		selectTrackSegment = aSelectState;
	}

	public void setTileSet (TileSet aTileSet) {
		tileSet = aTileSet;
	}

	public int getHexScale () {
		return Hex.getScale ();
	}

	public void setHexScale (int aScale) {
		hex.setScale (aScale);
		if (map != MapCell.NO_MAP_CELLS) {
			CalcGridCenters ();
			setMapSize ();
			redrawMap ();
		}
		tileSet.setScale (aScale);
	}

	/** Listen to the slider. */
	@Override
	public void stateChanged (ChangeEvent aEvent) {
		JSlider tSource;
		int tHexScale;
		
		tSource = (JSlider) aEvent.getSource ();
		if (!tSource.getValueIsAdjusting ()) {
			tHexScale = tSource.getValue ();
			setHexScale (tHexScale);
		}
	}

	public void toggleSelectedMapCell (MapCell aSelectedMapCell) {
		if (!wasTilePlaced ()) { // If in Tile Place Mode, and a Tile was put down, but not Exited, do nothing
			if (aSelectedMapCell != MapCell.NO_MAP_CELL) {
				if (aSelectedMapCell.isSelectable ()) {
					aSelectedMapCell.toggleSelected ();
					if (placeTileMode) {
						if (aSelectedMapCell.isSelected ()) {
							setPlayableTiles (aSelectedMapCell);
						} else {
							tileSet.clearAllPlayable ();
						}
					}
				}
			}
			tileSet.clearAllSelected ();
			mapFrame.updatePutTileButton ();
		}
		updateCorporationFrame ();
	}

	public Corporation getOperatingCompany () {
		GameManager tGameManager;
		Corporation tOperatingCompany;
		
		tGameManager = (GameManager) mapFrame.getGameManager ();
		tOperatingCompany = tGameManager.getOperatingCompany ();
		
		return tOperatingCompany;
	}
	
	public void updateCorporationFrame () {
		Corporation tOperatingCompany;
		MapCell tSelectedMapCell;
		
		tOperatingCompany = getOperatingCompany ();
		tSelectedMapCell = getSelectedMapCell ();
		if (tOperatingCompany != Corporation.NO_CORPORATION) {
			if (! isPlaceTileMode ()) {
				tOperatingCompany.updateCorporationFrame (tSelectedMapCell);
			}
		}
	}
	
	public void setPlayableTiles (MapCell aSelectedMapCell) {
		int tMapCellTypeCount;
		int tTileNumber;
		String tTileName;
		String tBaseTileName;
		String tBaseCityName;
		Tile tTile;
		GameTile tGameTile;

		tTileName = getTileName (aSelectedMapCell);
		tBaseCityName = getBaseCityName (aSelectedMapCell);
		if (aSelectedMapCell.isTileOnCell ()) {
			tTile = aSelectedMapCell.getTile ();
			tTileNumber = tTile.getNumber ();
			tGameTile = tileSet.getGameTile (tTileNumber);
			tileSet.setPlayableUpgradeTiles (tGameTile, tTileName, tBaseCityName);
		} else {
			tBaseTileName = aSelectedMapCell.getName ();
			tMapCellTypeCount = aSelectedMapCell.getTypeCount ();
			if (TileName.OO_NAME.equals (tTileName)) {
				tileSet.setPlayableTiles (TileType.GREEN, tTileName);
			} else {
				if (TileName.NO_NAME2.equals (tTileName)) {
					tileSet.setPlayableTiles (TileType.YELLOW, tMapCellTypeCount, tBaseTileName);
				} else {
					tileSet.setPlayableTiles (TileType.YELLOW, tMapCellTypeCount, tTileName);
				}
			}
		}
		tileSet.tileTrayFrameToFront ();
	}

	public String getBaseCityName (MapCell aMapCell) {
		String tBaseCityName;

		tBaseCityName = aMapCell.getCityName ();
		if (tBaseCityName == Centers.NO_CITY_NAME) {
			tBaseCityName = "";
		}

		return tBaseCityName;
	}

	public String getTileName (MapCell aMapCell) {
		String tTileName;

		tTileName = aMapCell.getName ();
		if (tTileName == GUI.NULL_STRING) {
			tTileName = GUI.EMPTY_STRING;
		}

		return tTileName;
	}

	public boolean isTileAvailableForMapCell (MapCell aMapCell) {
		boolean tIsTileAvailableForMapCell;
		Tile tTile;
		int tMapCellTypeCount;
		int tTileNumber;
		String tTileName;
		GameTile tGameTile;
		int tAvailableCount;
		String tBaseTileName;
		String tBaseCityName;

		tIsTileAvailableForMapCell = true;
		tAvailableCount = 0;
		tTileName = getTileName (aMapCell);
		tBaseCityName = getBaseCityName (aMapCell);
		if (aMapCell.isTileOnCell ()) {
			tTile = aMapCell.getTile ();
			tTileNumber = tTile.getNumber ();
			tGameTile = tileSet.getGameTile (tTileNumber);
			tAvailableCount = tileSet.getAvailableCount (tGameTile, tTileName, tBaseCityName);
		} else {
			tBaseTileName = aMapCell.getName ();
			tMapCellTypeCount = aMapCell.getTypeCount ();
			if (TileName.OO_NAME.equals (tTileName)) {
				tAvailableCount = tileSet.getAvailableCount (TileType.GREEN, tTileName);
			} else {
				if (TileName.NO_NAME2.equals (tTileName)) {
					tAvailableCount = tileSet.getAvailableCount (TileType.YELLOW, tMapCellTypeCount,
									tBaseTileName);
				} else {
					tAvailableCount = tileSet.getAvailableCount (TileType.YELLOW, tMapCellTypeCount, tTileName);
				}
			}
		}

		if (tAvailableCount == 0) {
			tIsTileAvailableForMapCell = false;
		}

		return tIsTileAvailableForMapCell;
	}

	public void toggleSelectedRevenueCenter (RevenueCenter aSelectedRevenueCenter, MapCell aMapCell) {
		City tSelectedCity;

		tSelectedCity = City.NO_CITY;
		if (aSelectedRevenueCenter != RevenueCenter.NO_CENTER) {
			aSelectedRevenueCenter.toggleSelected (0);
			if (aSelectedRevenueCenter instanceof City) {
				tSelectedCity = (City) aSelectedRevenueCenter;
			}
		}
		mapFrame.updatePutTokenButton (tSelectedCity, aMapCell);
	}

	public void putTileDown () {
		boolean tTilePlaced;
		MapCell tSelectedMapCell;

		tSelectedMapCell = getSelectedMapCell ();
		if (tSelectedMapCell == MapCell.NO_MAP_CELL) {
			System.err.println("Put Tile Down Button Selected, no Map Cell Selected from Frame");
		} else {
			removeAllSMC ();
			tTilePlaced = tSelectedMapCell.putTileDown (tileSet);
			setTilePlaced (tTilePlaced);
			selectableMapCells.addMapCell (tSelectedMapCell);
		}
	}

	public void setTilePlaced (boolean aTilePlaced) {
		tilePlaced = aTilePlaced;
	}

	public boolean wasTilePlaced () {
		return tilePlaced;
	}

	public void lockPlacedTile () {
		MapCell tSelectedMapCell;

		tSelectedMapCell = getSelectedMapCell ();
		if (tSelectedMapCell == MapCell.NO_MAP_CELL) {
			System.err.println ("ERROR-- Trying to Lock Tile Orientation, can't Find Selected MapCell.");
		} else {
			tSelectedMapCell.lockTileOrientation ();
		}
	}
	
	public void upgradeTile (MapCell aCurrentMapCell, Tile aNewTile) {
		aCurrentMapCell.upgradeTile (tileSet, aNewTile);
		redrawMap ();
	}

	public void restoreTile (Tile aCurrentTile) {
		GameTile tCurrentGameTile;
		int tCurrentTileNumber;

		if (aCurrentTile != Tile.NO_TILE) {
			tCurrentTileNumber = aCurrentTile.getNumber ();
			// Remove Tile from Map Cell, Clear all City Info and Stations, and place it
			// back on TileSet
			tCurrentGameTile = tileSet.getGameTile (tCurrentTileNumber);
			aCurrentTile.clearAll ();
			tCurrentGameTile.pushTile (aCurrentTile);
		}
	}

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {

	}

	public Corporation getCorporationByID (int aCorporationID) {
		return mapFrame.getCorporationByID (aCorporationID);
	}

	public Corporation getCorporation (String aCorporationAbbrev) {
		return mapFrame.getCorporation (aCorporationAbbrev);
	}

	public void removeAllMapTokens (TokenCompany aTokenCompany, CloseCompanyAction aCloseCompanyAction) {
		int tRowCount;
		int tRowIndex;
		int tColCount;
		int tColIndex;
		MapCell tMapCell;

		mapGraph = new MapGraph ();

		// Scan the rest of the Map for more Bases, and add.
		// Want to start with the Home Bases by default for Graph
		tRowCount = getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				tMapCell =  map [tRowIndex] [tColIndex];
				if (tMapCell.getRevenueCenterCount () > 0) {
					tMapCell.removeMapTokens (aTokenCompany, aCloseCompanyAction);
				}
			}
		}

	}
	
// Map Graph Functions
	
	public void buildMapGraph (TokenCompany aTokenCompany) {
		buildMapGraph ();
		collectSelectableCells (aTokenCompany);
	}

	public void collectSelectableCells (TokenCompany aTokenCompany) {
		List<Vertex> tBaseVertexes;
		List<MapCell> tEmptyMapCells;

		printMapGraphInfo ("Full MapGraph ", mapGraph.getVertexes ());
		tBaseVertexes = mapGraph.getVertexesWithToken (aTokenCompany);
		tEmptyMapCells = mapGraph.getEmptyMapCellsWithCompany (tBaseVertexes);
		for (MapCell tMapCell : tEmptyMapCells) {
			selectableMapCells.addMapCell (tMapCell);
			mapFrame.repaint ();
		}
	}

	public void printMapGraphInfo (String aTitle, List<Vertex> aMapGraph) {
		System.out.println (aTitle + aMapGraph.size ());		// PRINTLOG

		for (Vertex tVertex : aMapGraph) {
			tVertex.printInfo ();
		}
	}

	public boolean graphContainsMapCell (MapCell aMapCell) {
		boolean tGraphContainsMapCell;
		
		tGraphContainsMapCell = false;
		if (mapGraph != MapGraph.NO_MAP_GRAPH) {
			tGraphContainsMapCell = mapGraph.containsMapCell (aMapCell);
		}
		
		return tGraphContainsMapCell;
	}
	
	public void breadthFirstSearch (String aHomeVertexID) {
		mapGraph.breadthFirstSearch (aHomeVertexID) ;
	}
	
	public boolean foundInBFS (String aMapCellID) {
		return mapGraph.foundInBFS (aMapCellID);
	}
	
	public void buildMapGraph () {
		int tRowCount;
		int tRowIndex;
		int tColCount;
		int tColIndex;
		MapCell tMapCell;

		mapGraph = new MapGraph ();

		// Scan the rest of the Map for more Bases, and add.
		// Want to start with the Home Bases by default for Graph
		tRowCount = getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tColCount = getColCount (tRowIndex);
			for (tColIndex = 0; tColIndex < tColCount; tColIndex++) {
				tMapCell =  map [tRowIndex] [tColIndex];
				tMapCell.fillMapGraph (mapGraph);
			}
		}
	}
	
	public String getCompanyAbbrev (String aHomeMapCellID) {
		String tCompanyAbbrev;
		String tMapCellInfo [];
		
		tMapCellInfo = aHomeMapCellID.split (":");
		tCompanyAbbrev = tMapCellInfo [1];
		
		return tCompanyAbbrev;
	}
	
	public MapCell getMapCellByInfo (String aHomeMapCellInfo) {
		MapCell tMapCell;
		String tMapCellID;
		String tMapCellInfo [];
		
		tMapCellInfo = aHomeMapCellInfo.split (":");
		tMapCellID = tMapCellInfo [2];
		tMapCell = this.getMapCellForID (tMapCellID);
		
		return tMapCell;
	}

	public void replaceMapToken (String aMapCellID, MapToken aNewMapToken, TokenCompany aFoldingCompany, 
			ReplaceTokenAction aReplaceTokenAction) {
		MapCell tMapCell;
		String tMapCellID;
		String tMapCellInfo [];
		
		tMapCellInfo = aMapCellID.split (":");
		tMapCellID = tMapCellInfo [2];
		tMapCell = getMapCellForID (tMapCellID);
		tMapCell.replaceMapToken (tMapCellInfo, aNewMapToken, aFoldingCompany, aReplaceTokenAction);
	}
}
