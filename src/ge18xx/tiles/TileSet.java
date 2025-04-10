package ge18xx.tiles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

//
//  TileSet.java
//  Java_18XX
//
//  Created by Mark Smith on 11/12/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.map.Hex18XX;
import ge18xx.toplevel.TileTrayFrame;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import geUtilities.ParsingRoutine2I;
import geUtilities.ParsingRoutineI;

public class TileSet extends JLabel implements LoadableXMLI, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private static final String NO_CITY_NAME = "";
	private static final String NO_TILE_SET_NAME = "";
	public static final ElementName EN_TILE_MANIFEST = new ElementName ("TileManifest");
	public static final ElementName EN_TILE_DEFINITIONS = new ElementName ("TileDefinitions");
	public static final ElementName EN_TILE_SET = new ElementName ("TileSet");
	public static final ElementName EN_TILE = new ElementName ("Tile");
	public static final ElementName EN_UPGRADE = new ElementName ("Upgrade");
	public static final AttributeName AN_DIRECTION = new AttributeName ("direction");
	public static final AttributeName AN_GAME = new AttributeName ("game");
	public static final AttributeName AN_VARIANT = new AttributeName ("variant");
	public static final AttributeName AN_COLOR = new AttributeName ("color");
	public static final AttributeName AN_NUMBER = new AttributeName ("number");
	public static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	public static final TileSet NO_TILE_SET = null;
	public static final int TILES_PER_ROW = 9;
	List<GameTile> gameTiles = new LinkedList<> ();
	String setName;
	Hex18XX hex;
	boolean showAllTiles; 		// Set true to show all Tiles in Tile Tray
	boolean singleTileSelect; 	// Set true if in mode to select a SINGLE Tile, selecting a different one
								// should unselect ALL and leave only the single tile selected.
	GameTile parsedGameTile;
	TileTrayFrame tileTrayFrame;
	int maxWidth;
	int maxHeight;

	public TileSet (TileTrayFrame aTileTrayFrame) {
		this (NO_TILE_SET_NAME);
		tileTrayFrame = aTileTrayFrame;
		setShowAllTiles (false);
	}

	private void setShowAllTiles (boolean aShowAllTiles) {
		showAllTiles = aShowAllTiles;
	}

	public TileSet (String aSetName) {
		setValues (aSetName);
		setShowAllTiles (false);
	}

	public boolean addTile (Tile aTile, int aTotalCount) {
		GameTile tGameTile;
		boolean tAddedGameTile;

		tGameTile = new GameTile (aTile, aTotalCount);
		tAddedGameTile = gameTiles.add (tGameTile);

		return tAddedGameTile;
	}

	public boolean addTile (int aTileNumber, int aTotalCount, boolean aOverride) {
		GameTile tGameTile;

		tGameTile = new GameTile (aTileNumber, aTotalCount, aOverride);

		return gameTiles.add (tGameTile);
	}

	public void clearAllPlayable () {
		for (GameTile tGameTile : gameTiles) {
			tGameTile.clearPlayable ();
		}
		redrawTileTray ();
	}

	public void clearAllSelected () {
		for (GameTile tGameTile : gameTiles) {
			tGameTile.clearSelected ();
		}
		redrawTileTray ();
	}

	public void redrawTileTray () {
		revalidate ();
		repaint ();
	}

	public void tileTrayFrameToFront () {
		if (tileTrayFrame != TileTrayFrame.NO_TILE_TRAY_FRAME) {
			tileTrayFrame.toTheFront ();
		}
	}

	public void copyTileDefinitions (TileSet aTileDefinitions) {
		int tTileNumber;

		for (GameTile tGameTile : gameTiles) {
			tTileNumber = tGameTile.getTileNumber ();
			addATileDefinition (aTileDefinitions, tTileNumber, tGameTile);
		}
	}

	public void addATileDefinition (TileSet aTileDefinitions, int aTileNumber, GameTile aGameTile) {
		Tile tTile;
		int tTotalCount;
		int tAvailableCount;

		tTile = aTileDefinitions.getTile (aTileNumber);
		if (tTile != Tile.NO_TILE) {
			// Set the Generic Tile Definition to show in Tile Tray
			aGameTile.setTile (tTile);
			tTotalCount = aGameTile.getTotalCount ();
			tAvailableCount = aGameTile.availableCount ();
			// If don't have all tiles needed, add them to the stack.
			if (tAvailableCount < tTotalCount) {
				addNTileClones (aGameTile, tTile, tTotalCount);
			}
		}
	}

	public void addNTileClones (GameTile aGameTile, Tile aTile, int aTotalCount) {
		Tile tTileClone;
		int aIndex;

		for (aIndex = 0; aIndex < aTotalCount; aIndex++) {
			tTileClone = aTile.clone ();
			aGameTile.pushTile (tTileClone);
		}
	}

	public void copyATileFromDefinitions (TileSet aTileDefinitions, int aTileNumber, int aQuantity) {
		Tile tTile;
		GameTile tGameTile;
		int tTotalCount;
		boolean tTileAdded;

		tTile = aTileDefinitions.getTile (aTileNumber);
		if (tTile != Tile.NO_TILE) {
			tGameTile = getGameTileMatching (aTileNumber);
			if (tGameTile != GameTile.NO_GAME_TILE) {
				if (aQuantity > 0) {
					addNTileClones (tGameTile, tTile, aQuantity);
					tGameTile.setUsedCount (0);
					tTotalCount = tGameTile.getTotalCount () + aQuantity;
					tGameTile.setTotalCount (tTotalCount);
				}
			} else {
				if (aQuantity > 0) {
					tTileAdded = addTile (tTile, aQuantity);
					tGameTile = getGameTileMatching (aTileNumber);
					addNTileClones (tGameTile, tTile, aQuantity);
					tGameTile.setUsedCount (0);
					if (tTileAdded) {
						sortGameTiles ();
					}
				}
			}
		}
	}

	private GameTile getGameTileMatching (int aTileNumber) {
		int tTileNumber;
		GameTile tFoundGameTile;

		tFoundGameTile = GameTile.NO_GAME_TILE;
		for (GameTile tGameTile : gameTiles) {
			tTileNumber = tGameTile.getTileNumber ();
			if (tTileNumber == aTileNumber) {
				tFoundGameTile = tGameTile;
			}
		}

		return tFoundGameTile;
	}

	public XMLElement createAllTileDefinitions (XMLDocument aXMLDocument) {
		XMLElement tAllTileDefinitions;
		XMLElement tTileElement;
		Tile tTile;

		tAllTileDefinitions = aXMLDocument.createElement (EN_TILE_DEFINITIONS);
		for (GameTile tGameTile : gameTiles) {
			tTile = tGameTile.getTile ();
			tTileElement = tTile.createElement (aXMLDocument);
			tAllTileDefinitions.appendChild (tTileElement);
		}

		return tAllTileDefinitions;
	}

	public GameTile getGameTile (int aTileNumber) {
		Iterator<GameTile> tIterator = gameTiles.iterator ();
		GameTile tGameTile = GameTile.NO_GAME_TILE;
		boolean tFoundTile = false;

		if (aTileNumber != 0) {
			while (tIterator.hasNext () && !tFoundTile) {
				tGameTile = tIterator.next ();
				if (aTileNumber == tGameTile.getTileNumber ()) {
					tFoundTile = true;
				}
			}
		}

		return tGameTile;
	}

	public GameTile getRotateTileContainingPoint (Point aPoint) {
		GameTile tFoundTile = GameTile.NO_GAME_TILE;

		for (GameTile tGameTile : gameTiles) {
			if (tGameTile.rotateArrowContainingPoint (aPoint, hex)) {
				tFoundTile = tGameTile;
			}
		}

		return tFoundTile;
	}

	public GameTile getSelectedTile () {
		GameTile tFoundTile = GameTile.NO_GAME_TILE;

		for (GameTile tGameTile : gameTiles) {
			if (tGameTile.isSelected ()) {
				tFoundTile = tGameTile;
			}
		}

		return tFoundTile;
	}

	public Tile getTile (int aTileNumber) {
		Iterator<GameTile> tIterator = gameTiles.iterator ();
		GameTile tGameTile;
		boolean tFoundTile = false;
		Tile tTile = Tile.NO_TILE;

		if (aTileNumber != 0) {
			while (tIterator.hasNext () && !tFoundTile) {
				tGameTile = tIterator.next ();
				if (aTileNumber == tGameTile.getTileNumber ()) {
					tTile = tGameTile.getTile ();
					tFoundTile = true;
				}
			}
		}

		return tTile;
	}

	public GameTile getTileContainingPoint (Point aPoint) {
		GameTile tFoundTile = GameTile.NO_GAME_TILE;

		for (GameTile tGameTile : gameTiles) {
			if (tGameTile.containingPoint (aPoint, hex)) {
				tFoundTile = tGameTile;
			}
		}

		return tFoundTile;
	}

	public TileType getTileType () {
		GameTile tGameTile = new GameTile ();

		return tGameTile.getTheTileType ();
	}

	@Override
	public String getTypeName () {
		return "Tile Set";
	}

	ParsingRoutine2I tileManifestParsingRoutine = new ParsingRoutine2I () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			try {
				loadXMLManifest (aChildNode);
			} catch (IOException e) {
				System.err.println ("Exception Caught on Loading XML Manifest");
			}
		}

		@Override
		public void foundItemMatchKey2 (XMLNode aChildNode) {
			Tile tTile;

			tTile = new Tile (aChildNode);
			addTile (tTile, 0);
		}
	};

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNodeList tXMLNodeList;
		XMLNode tXMLTileSetRoot;
		String tRootName;
		String tDirection;

		tXMLTileSetRoot = aXMLDocument.getDocumentNode ();
		tRootName = tXMLTileSetRoot.getNodeName ();
		if (EN_TILE_MANIFEST.equals (tRootName)) {
			tDirection = tXMLTileSetRoot.getThisAttribute (AN_DIRECTION);
			setHex (tDirection);
			tileTrayFrame.setDefaults (tXMLTileSetRoot);
			tileTrayFrame.setDefaultFrameInfo ();
		} else if (EN_TILE_DEFINITIONS.equals (tRootName)) {
//			tBaseColor = tXMLTileSetRoot.getThisAttribute (AN_COLOR);
		}
		tXMLNodeList = new XMLNodeList (tileManifestParsingRoutine);
		tXMLNodeList.parseXMLNodeList (tXMLTileSetRoot, EN_TILE_SET, EN_TILE);
	}

	public void loadXMLManifest (XMLNode aManifestNode) throws IOException {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (tileSetParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aManifestNode, EN_TILE);
	}

	@Override
	public void mouseClicked (MouseEvent e) {
//		handleClick (e);
	}

	public void handleClick (MouseEvent aMouseEvent) {
		Point tPoint = aMouseEvent.getPoint ();
		GameTile tGameTile = getTileContainingPoint (tPoint);
		GameTile tRotateGameTile;
		GameTile tPreviousGameTile;
		boolean tShiftDown;
		
		if (tGameTile != GameTile.NO_GAME_TILE) {
			if (tGameTile.tileAvailable ()) {
				if (singleTileSelect) {
					tPreviousGameTile = getSelectedTile ();
					if (tPreviousGameTile == GameTile.NO_GAME_TILE) {
						toggleSelectedTile (tGameTile);
					} else {
						if (tGameTile != tPreviousGameTile) {
							toggleSelectedTile (tPreviousGameTile);
							toggleSelectedTile (tGameTile);
						}
					}
				} else {
					toggleSelectedTile (tGameTile);
				}
				if (tileTrayFrame.isPlaceTileMode ()) {
					tileTrayFrame.bringMapToFront ();
				}
			}
		} else {
			tRotateGameTile = getRotateTileContainingPoint (tPoint);
			if (tRotateGameTile != GameTile.NO_GAME_TILE) {
				tShiftDown = aMouseEvent.isShiftDown ();

				if (tShiftDown) {
					tRotateGameTile.rotateTileLeft ();
				} else {
					tRotateGameTile.rotateTileRight ();
				}
			}
			tileTrayFrameToFront ();
		}
		redrawTileTray ();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged (MouseEvent aMouseEvent) {
	}

	@Override
	public void mouseEntered (MouseEvent aMouseEvent) {
	}

	@Override
	public void mouseExited (MouseEvent aMouseEvent) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved (MouseEvent aMouseEvent) {
		GameTile tGameTile;

		Point point = aMouseEvent.getPoint ();
		tGameTile = getTileContainingPoint (point);
		if (tGameTile == GameTile.NO_GAME_TILE) {
			setToolTipText ("***");
		} else {
			if (tGameTile.isFixedTile () && !showAllTiles) {
				setToolTipText (NO_CITY_NAME);
			} else {
				setToolTipText (tGameTile.getToolTip ());
			}
		}
	}

	@Override
	public void mousePressed (MouseEvent aMouseEvent) {
	}

	@Override
	public void mouseReleased (MouseEvent aMouseEvent) {
		handleClick (aMouseEvent);
	}

	public boolean showThisTile (Tile aTile) {
		boolean tShowThisTile = true;

		if (!showAllTiles) {
			if (aTile.isFixedTile ()) {
				tShowThisTile = false;
			}
		}

		return tShowThisTile;
	}

	@Override
	public void paintComponent (Graphics aGraphics) {
		int tX;
		int tY;
		int tXoffset;
		int tYoffset;
		int tIndex;
		int tYNumOffset;
		int tWidth;
		int tHeight;
		Tile tTile;

		tXoffset = Double.valueOf (Hex18XX.getWidth () * 2.25).intValue ();
		tYoffset = hex.getYd () * 2 + 25;
		tYNumOffset = hex.getYd () + 17;
		tWidth = Hex18XX.getWidth ();
		tHeight = hex.getYd () + 5;
		tX = tXoffset - tWidth;
		tY = tYoffset - tHeight;
		tIndex = 0;

		for (GameTile tGameTile : gameTiles) {
			tGameTile.setXY (tX, tY);
			tTile = tGameTile.getTile ();
			if (tTile != Tile.NO_TILE) {
				if (showThisTile (tTile)) {
					drawThisTile (aGraphics, tX, tY, tYNumOffset, tWidth, tHeight, tTile, tGameTile);
				}
			}
			tIndex++;
			if (tIndex == TILES_PER_ROW) {
				tX = tXoffset - tWidth;
				tY += tYoffset;
				tIndex = 0;
			} else {
				tX += tXoffset;
			}
		}
	}

	private void drawThisTile (Graphics aGraphics, int aX, int aY, int aYNumOffset,
				int aWidth, int aHeight, Tile aTile, GameTile aGameTile) {
		int tXNum;
		int tYNum;
		int tValueWidth;
		int tTileOrient;
		String tIdLabel;

		setBackgroundForTile (aGraphics, aX, aY, aWidth, aHeight, aGameTile);
		tTileOrient = aGameTile.getTileOrient ();
		aTile.paintComponent (aGraphics, tTileOrient, hex, new Feature2 (), aGameTile.isSelected ());
		hex.drawRotateRightArrow (aGraphics, aX, aY);
		aGameTile.drawSelected (aGraphics, hex);
		tIdLabel = aTile.getNumberToString ();
		tIdLabel = tIdLabel + " [" + aGameTile.getTotalAndAvailable () + "]";
		tValueWidth = aGraphics.getFontMetrics ().stringWidth (tIdLabel);
		tXNum = aX - tValueWidth / 2;
		tYNum = aY + aYNumOffset;
		if (aGameTile.availableCount () > 0) {
			aGraphics.setColor (Color.BLACK);
		} else {
			aGraphics.setColor (Color.RED);
		}
		aGraphics.drawString (tIdLabel, tXNum, tYNum);
	}

	private void setBackgroundForTile (Graphics aGraphics, int aX, int aY, int aWidth, int aHeight, GameTile aGameTile) {
		int tXUpperLeft;
		int tYUpperLeft;

		tXUpperLeft = aX - aWidth;
		tYUpperLeft = aY - aHeight;
		if (tileTrayFrame.isUpgradeAllowed (aGameTile)) {
			if (aGameTile.isPlayable ()) {
				if (aGameTile.availableCount () > 0) {
					aGraphics.setColor (Color.ORANGE);
				} else {
					aGraphics.setColor (Color.LIGHT_GRAY);
				}
				aGraphics.fillRect (tXUpperLeft, tYUpperLeft, aWidth * 2, aHeight * 2);
			}
		} else {
			aGraphics.setColor (Color.GRAY);
			aGraphics.fillRect (tXUpperLeft, tYUpperLeft, aWidth * 2, aHeight * 2);
		}
	}

	public Tile popTile (int aTileNumber) {
		Iterator<GameTile> tIterator = gameTiles.iterator ();
		GameTile tGameTile;
		boolean tFoundTile = false;
		Tile tTile = Tile.NO_TILE;

		if (aTileNumber != 0) {
			while (tIterator.hasNext () && !tFoundTile) {
				tGameTile = tIterator.next ();
				if (aTileNumber == tGameTile.getTileNumber ()) {
					tTile = tGameTile.popTile ();
					tFoundTile = true;
				}
			}
		}

		return tTile;
	}

	public void setHex (boolean aHexDirection) {
		hex = new Hex18XX (aHexDirection);
	}

	public void setHex (String aHexDirection) {
		boolean tHexDirection;

		if (aHexDirection == null) {
			tHexDirection = false;
		} else {
			if (aHexDirection.equals (Hex18XX.DIRECTION_NS)) {
				tHexDirection = false;
			} else {
				if (aHexDirection.equals (Hex18XX.DIRECTION_EW)) {
					tHexDirection = true;
				} else {
					tHexDirection = false;
				}
			}
		}

		setHex (tHexDirection);
	}

	public void setPlayableUpgradeTiles (GameTile aGameTile, String aTileName, String aBaseCityName) {
		int tUpgradeCount;
		int tUpgradeIndex;
		int tToTileNumber;
		int tPlayableCount;
		String tBaseCityName;
		GameTile tUpgradeGameTile;
		Upgrade tUpgrade;
		boolean tNoCityName;

		tPlayableCount = 0;
		tUpgradeCount = aGameTile.getUpgradeCount ();
		if (tUpgradeCount > 0) {
			tBaseCityName = NO_CITY_NAME;
			for (tUpgradeIndex = 0; tUpgradeIndex < tUpgradeCount; tUpgradeIndex++) {
				tUpgrade = aGameTile.getUpgrade (tUpgradeIndex);
				if (tUpgrade != Upgrade.NO_UPGRADE) {
					tBaseCityName = tUpgrade.getBaseCityName ();
					tToTileNumber = tUpgrade.getTileNumber ();
					tUpgradeGameTile = getGameTile (tToTileNumber);
					if (tileTrayFrame.isUpgradeAllowed (tUpgradeGameTile)) {
						if (aBaseCityName.equals (tBaseCityName)) {
							tUpgradeGameTile.setPlayable (true);
							tPlayableCount++;
						}
					}
				}
			}
			if (tBaseCityName == null) {
				tNoCityName = true;
			} else {
				if (NO_CITY_NAME.equals (tBaseCityName)) {
					tNoCityName = true;
				} else {
					tNoCityName = false;
				}
			}
			if (tNoCityName) {
				if (tPlayableCount == 0) {
					if (!TileName.NO_NAME2.equals (aTileName)) {
						for (tUpgradeIndex = 0; tUpgradeIndex < tUpgradeCount; tUpgradeIndex++) {
							tPlayableCount = getPlayableCount (aGameTile, aTileName, tUpgradeIndex, tPlayableCount);
						}
					}
				}
				if (tPlayableCount == 0) {
					for (tUpgradeIndex = 0; tUpgradeIndex < tUpgradeCount; tUpgradeIndex++) {
						tPlayableCount = getPlayableCount (aGameTile, TileName.NO_NAME2, tUpgradeIndex, tPlayableCount);
					}
				}
				if (tPlayableCount == 0) {
					if (tUpgradeCount == 1) {
						tUpgrade = aGameTile.getUpgrade (0);
						if (tUpgrade != Upgrade.NO_UPGRADE) {
							tToTileNumber = tUpgrade.getTileNumber ();
							tUpgradeGameTile = getGameTile (tToTileNumber);
							if (tileTrayFrame.isUpgradeAllowed (tUpgradeGameTile)) {
								tUpgradeGameTile.setPlayable (true);
								tPlayableCount++;
							}
						}
					}
				}
			}
		}
		if (tPlayableCount > 0) {
			redrawTileTray ();
		}
	}

	public int getPlayableCount (GameTile aGameTile, String aMatchTileName, int aUpgradeIndex, int aPlayableCount) {
		int tToTileNumber;
		String tTileName;
		GameTile tUpgradeGameTile;
		Upgrade tUpgrade;

		tUpgrade = aGameTile.getUpgrade (aUpgradeIndex);
		if (tUpgrade != Upgrade.NO_UPGRADE) {
			tToTileNumber = tUpgrade.getTileNumber ();
			tUpgradeGameTile = getGameTile (tToTileNumber);
			tTileName = tUpgradeGameTile.getTileName ();
			if (tileTrayFrame.isUpgradeAllowed (tUpgradeGameTile)) {
				if (aMatchTileName.equals (tTileName)) {
					tUpgradeGameTile.setPlayable (true);
					aPlayableCount++;
				}
			}
		}

		return aPlayableCount;
	}

	public int getAvailableCount (GameTile aGameTile, String aTileName, String aBaseCityName) {
		int tUpgradeCount;
		int tUpgradeIndex;
		int tToTileNumber;
		int tAvailableCount;
		String tTileName;
		String tBaseCityName;
		GameTile tUpgradeGameTile;
		Upgrade tUpgrade;
		boolean tNoCityName;

		tAvailableCount = 0;
		tUpgradeCount = aGameTile.getUpgradeCount ();
		if (tUpgradeCount > 0) {
			tBaseCityName = NO_CITY_NAME;
			for (tUpgradeIndex = 0; tUpgradeIndex < tUpgradeCount; tUpgradeIndex++) {
				tUpgrade = aGameTile.getUpgrade (tUpgradeIndex);
				if (tUpgrade != Upgrade.NO_UPGRADE) {
					tBaseCityName = tUpgrade.getBaseCityName ();
					tToTileNumber = tUpgrade.getTileNumber ();
					tUpgradeGameTile = getGameTile (tToTileNumber);
					if (tileTrayFrame.isUpgradeAllowed (tUpgradeGameTile)) {
						if (aBaseCityName.equals (tBaseCityName)) {
							tAvailableCount += tUpgradeGameTile.getAvailableCount ();
						}
					}
				}
			}
			if (tBaseCityName == null) {
				tNoCityName = true;
			} else {
				if (NO_CITY_NAME.equals (tBaseCityName)) {
					tNoCityName = true;
				} else {
					tNoCityName = false;
				}
			}
			if (tNoCityName) {
				if (tAvailableCount == 0) {
					if (!NO_CITY_NAME.equals (aTileName)) {
						for (tUpgradeIndex = 0; tUpgradeIndex < tUpgradeCount; tUpgradeIndex++) {
							tUpgrade = aGameTile.getUpgrade (tUpgradeIndex);
							if (tUpgrade != Upgrade.NO_UPGRADE) {
								tToTileNumber = tUpgrade.getTileNumber ();
								tUpgradeGameTile = getGameTile (tToTileNumber);
								tTileName = tUpgradeGameTile.getTileName ();
								if (tileTrayFrame.isUpgradeAllowed (tUpgradeGameTile)) {
									if (aTileName.equals (tTileName)) {
										tAvailableCount += tUpgradeGameTile.getAvailableCount ();
									}
								}
							}
						}
					}
				}
				if (tAvailableCount == 0) {
					for (tUpgradeIndex = 0; tUpgradeIndex < tUpgradeCount; tUpgradeIndex++) {
						tUpgrade = aGameTile.getUpgrade (tUpgradeIndex);
						if (tUpgrade != Upgrade.NO_UPGRADE) {
							tToTileNumber = tUpgrade.getTileNumber ();
							tUpgradeGameTile = getGameTile (tToTileNumber);
							tTileName = tUpgradeGameTile.getTileName ();
							if (tileTrayFrame.isUpgradeAllowed (tUpgradeGameTile)) {
								if (NO_CITY_NAME.equals (tTileName)) {
									tAvailableCount += tUpgradeGameTile.getAvailableCount ();
								}
							}
						}
					}
				}
				if (tAvailableCount == 0) {
					if (tUpgradeCount == 1) {
						tUpgrade = aGameTile.getUpgrade (0);
						if (tUpgrade != Upgrade.NO_UPGRADE) {
							tToTileNumber = tUpgrade.getTileNumber ();
							tUpgradeGameTile = getGameTile (tToTileNumber);
							if (tileTrayFrame.isUpgradeAllowed (tUpgradeGameTile)) {
								tAvailableCount += tUpgradeGameTile.getAvailableCount ();
							}
						}
					}
				}
			}
		}
		if (tAvailableCount > 0) {
			redrawTileTray ();
		}

		return tAvailableCount;
	}

	public void setPlayableTiles (int aTileType, int aMapCellTypeCount, String aTileName) {
		int tTileTypeCount;
		int tTileType;
		int tPlayableCount;
		String tTileName;

		tPlayableCount = 0;
		for (GameTile tGameTile : gameTiles) {
			tTileName = tGameTile.getTileName ();
			if (aTileName.equals (tTileName)) {
				tTileType = tGameTile.getTileType ();
				if (aTileType == tTileType) {
					tTileTypeCount = tGameTile.getTypeCount ();
					if (tTileTypeCount == aMapCellTypeCount) {
						tGameTile.setPlayable (true);
						tPlayableCount++;
					}
				}
			}
		}
		if (tPlayableCount == 0) {
			for (GameTile tGameTile : gameTiles) {
				tTileType = tGameTile.getTileType ();
				if (aTileType == tTileType) {
					tTileTypeCount = tGameTile.getTypeCount ();
					if (tTileTypeCount == aMapCellTypeCount) {
						tGameTile.setPlayable (true);
						tPlayableCount++;
					}
				}
			}
		}

		if (tPlayableCount > 0) {
			redrawTileTray ();
		}

	}

	public int getAvailableCount (int aTileType, int aMapCellTypeCount, String aTileName) {
		int tTileTypeCount;
		int tTileType;
		int tAvailableCount;
		String tTileName;

		tAvailableCount = 0;
		for (GameTile tGameTile : gameTiles) {
			tTileName = tGameTile.getTileName ();
			if (aTileName.equals (tTileName)) {
				tTileType = tGameTile.getTileType ();
				if (aTileType == tTileType) {
					tTileTypeCount = tGameTile.getTypeCount ();
					if (tTileTypeCount == aMapCellTypeCount) {
						tAvailableCount += tGameTile.getAvailableCount ();
					}
				}
			}
		}
		if (tAvailableCount == 0) {
			for (GameTile tGameTile : gameTiles) {
				tTileType = tGameTile.getTileType ();
				if (aTileType == tTileType) {
					tTileTypeCount = tGameTile.getTypeCount ();
					if (tTileTypeCount == aMapCellTypeCount) {
						tAvailableCount += tGameTile.getAvailableCount ();
					}
				}
			}
		}

		return tAvailableCount;
	}

	public int getAvailableCount (int aTileType, String aTileName) {
		String tTileName;
		int tTileType;
		int tAvailableCount = 0;

		for (GameTile tGameTile : gameTiles) {
			tTileType = tGameTile.getTileType ();
			if (aTileType == tTileType) {
				tTileName = tGameTile.getTileName ();
				if (tTileName.equals (aTileName)) {
					tAvailableCount += tGameTile.getAvailableCount ();
				}
			}
		}

		return tAvailableCount;
	}

	public void setPlayableTiles (int aTileType, String aTileName) {
		String tTileName;
		int tTileType;
		int tPlayableCount = 0;

		for (GameTile tGameTile : gameTiles) {
			tTileType = tGameTile.getTileType ();
			if (aTileType == tTileType) {
				tTileName = tGameTile.getTileName ();
				if (tTileName.equals (aTileName)) {
					tGameTile.setPlayable (true);
					tPlayableCount++;
				}
			}
		}
		if (tPlayableCount > 0) {
			redrawTileTray ();
		}
	}

	public void setScale (int aHexScale) {
		hex.setScale (aHexScale);
		setTraySize ();
		redrawTileTray ();
	}

	public void setSingleTileSelect (boolean aSelectState) {
		singleTileSelect = aSelectState;
		clearAllSelected ();
	}

	public int getTileCountToShow () {
		int tileCount = 0;

		if (showAllTiles) {
			tileCount = gameTiles.size ();
		} else {
			for (GameTile tGameTile : gameTiles) {
				if (!tGameTile.isFixedTile ()) {
					tileCount++;
				}
			}
		}

		return tileCount;
	}

	public void setValues (String aSetName) {
		gameTiles = new LinkedList<> ();
		setName = aSetName;
		addMouseListener (this);
		addMouseMotionListener (this);
		setSingleTileSelect (false);
	}

	public boolean startBoardTile () {
		if (setName.equals ("Other")) {
			return true;
		} else {
			return false;
		}
	}

	public void toggleSelectedTile (GameTile aGameTile) {
		if (aGameTile.isSelectable ()) {
			aGameTile.toggleSelected ();
		}
		tileTrayFrame.notifyMapFrame ();
	}

	public int calcRowCount () {
		int tRowCount;
		double tTileCount;
		double tRowCountD;

		tTileCount = getTileCountToShow ();
		tRowCountD = Double.valueOf (tTileCount / TILES_PER_ROW);
		tRowCount = (int) Math.ceil (tRowCountD);

		return tRowCount;
	}

	public void setTraySize () {
		int tMaxX;
		int tMaxY;
		int tRowCount;
		Dimension tNewDimension;

		if (hex == Hex18XX.NO_HEX18XX) {
			setHex (Hex18XX.getDirection ());
		}
		tRowCount = calcRowCount ();
		tMaxX = Double.valueOf (Hex18XX.getWidth () * 2.25 * TILES_PER_ROW + 10).intValue ();
		tMaxY = (hex.getYd () * 2 + 25) * tRowCount + 20;
		tNewDimension = new Dimension (tMaxX, tMaxY);
		tileTrayFrame.setScrollPanePSize (tNewDimension);
		setPreferredSize (tNewDimension);
	}

	ParsingRoutineI tileSetParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			XMLNodeList tXMLNodeList;
			Upgrade tUpgrade;
			int tTileNumber;
			int tQuantity;
			boolean tOverride;
			String tChildName;

			tChildName = aChildNode.getNodeName ();
			if (Tile.EN_TILE.equals (tChildName)) {
				tTileNumber = aChildNode.getThisIntAttribute (AN_NUMBER);
				tQuantity = aChildNode.getThisIntAttribute (AN_QUANTITY);
				tOverride = aChildNode.getThisBooleanAttribute (GameTile.AN_OVERRIDE);
				addTile (tTileNumber, tQuantity, tOverride);
				parsedGameTile = getGameTile (tTileNumber);
				tXMLNodeList = new XMLNodeList (tileParsingRoutine);
				tXMLNodeList.parseXMLNodeList (aChildNode, EN_UPGRADE);
			} else if (GameTile.EN_UPGRADE.equals (tChildName)) {
				tUpgrade = new Upgrade (aChildNode);
				if (parsedGameTile != GameTile.NO_GAME_TILE) {
					parsedGameTile.addUpgrade (tUpgrade);
				}
			}
		}
	};

	ParsingRoutineI tileParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			Upgrade tUpgrade;

			tUpgrade = new Upgrade (aChildNode);
			if (parsedGameTile != GameTile.NO_GAME_TILE) {
				parsedGameTile.addUpgrade (tUpgrade);
			}
		}
	};

	public void sortGameTiles () {
		Collections.sort (gameTiles, GameTile.GameTileComparator);
	}
}