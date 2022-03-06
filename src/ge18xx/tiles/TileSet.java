package ge18xx.tiles;

//
//  TileSet.java
//  Java_18XX
//
//  Created by Mark Smith on 11/12/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.map.Hex;
import ge18xx.toplevel.LoadableXMLI;
import ge18xx.toplevel.TileTrayFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutine2I;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.io.IOException;

import javax.swing.JLabel;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class TileSet extends JLabel implements LoadableXMLI, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private static final String NO_CITY_NAME = "";
	private static final String NO_TILE_SET_NAME = "";
	public static final TileSet NO_TILE_SET = null;
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
	public static final int TILES_PER_ROW = 9;
	List<GameTile> gameTiles = new LinkedList<GameTile> ();
	String setName;
	Hex hex;
	boolean showAllTiles;		// Set true to show all Tiles in Tile Tray
	boolean singleTileSelect;	// Set true if in mode to select a SINGLE Tile, selecting a different one
								// should unselect ALL and leave only the single tile selected.
	GameTile parsedGameTile;
	TileTrayFrame tileTrayFrame;
	
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
	
	public boolean addTile (int aTileNumber, int aTotalCount) {
		return gameTiles.add (new GameTile (aTileNumber, aTotalCount));
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
		Tile tTile;
		Tile tTileClone;
		int tTileNumber;
		int tTotalCount;
		int tAvailableCount;
		int tIndex;
		
		for (GameTile tGameTile : gameTiles) {
			tTileNumber = tGameTile.getTileNumber ();
			tTile = aTileDefinitions.getTile (tTileNumber);
			if (tTile != Tile.NO_TILE) {
				// Set the Generic Tile Definition to show in Tile Tray
				tGameTile.setTile (tTile);
				tTotalCount = tGameTile.totalCount ();
				tAvailableCount = tGameTile.availableCount ();
				// If don't have all tiles needed, add them to the stack.
				if (tAvailableCount < tTotalCount) {
					for (tIndex = 0; tIndex < tTotalCount; tIndex++) {
						tTileClone = tTile.clone ();
						tGameTile.pushTile (tTileClone);
					}
				}
			}
		}
	}
	
	public XMLElement createAllTileDefinitions (XMLDocument aXMLDocument) {
		XMLElement allTileDefinitions;
		XMLElement tTileElement;
		Tile tTile;
		
		allTileDefinitions = aXMLDocument.createElement (EN_TILE_DEFINITIONS);
		for (GameTile tGameTile : gameTiles) {
			tTile = tGameTile.getTile ();
			tTileElement = tTile.createElement (aXMLDocument);
			allTileDefinitions.appendChild (tTileElement);
		}
		
		return allTileDefinitions;
	}
	
	public GameTile getGameTile (int aTileNumber) {
		Iterator<GameTile> iter = gameTiles.iterator ();
		GameTile tGameTile = GameTile.NO_GAME_TILE;
		boolean tFoundTile = false;
		
		if (aTileNumber != 0) {
			while (iter.hasNext () && !tFoundTile) {
				tGameTile = (GameTile) iter.next ();
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
		Iterator<GameTile> iter = gameTiles.iterator ();
		GameTile tGameTile;
		boolean tFoundTile = false;
		Tile tTile = Tile.NO_TILE;
		
		if (aTileNumber != 0) {
			while (iter.hasNext () && !tFoundTile) {
				tGameTile = (GameTile) iter.next ();
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
		
		tXMLTileSetRoot = aXMLDocument.getDocumentElement ();
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
		
		if (tGameTile != null) {
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
				tileTrayFrame.bringMapToFront ();
			}
		} else {
			tRotateGameTile = getRotateTileContainingPoint (tPoint);
			if (tRotateGameTile != null) {
				tRotateGameTile.rotateTileRight ();
			}
			tileTrayFrameToFront ();
		}
		redrawTileTray ();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged (MouseEvent arg0) {}
	
    @Override
	public void mouseEntered (MouseEvent e) {}
	
    @Override
	public void mouseExited (MouseEvent e) {}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved (MouseEvent arg0) {
		GameTile tGameTile;
		
		Point point = arg0.getPoint();
		tGameTile = getTileContainingPoint (point);
		if (tGameTile == GameTile.NO_GAME_TILE) {
			setToolTipText ("***");
		} else {
			if (tGameTile.isFixedTile () && ! showAllTiles) {
				setToolTipText (NO_CITY_NAME);
			} else {
				setToolTipText (tGameTile.getToolTip ());
			}
		}
	}
	
    @Override
	public void mousePressed (MouseEvent e) {}
	
    @Override
	public void mouseReleased (MouseEvent e) {
    	handleClick (e);
    }
	
    public boolean showThisTile (Tile tTile) {
    	boolean tShowThisTile = true;
    	
    	if (! showAllTiles) {
    		if (tTile.isFixedTile ()) {
    			tShowThisTile = false;
    		}
    	}
    	
    	return tShowThisTile;
    }
    
	@Override
	public void paintComponent (Graphics g) {
		int X, Y, Xoffset, Yoffset, index, XNum, YNum, YNumOffset;
		int valueWidth;
		int tWidth, tHeight;
		Tile tTile;
		int tTileOrient;
		String idLabel;
		
		Xoffset = new Double (Hex.getWidth () * 2.25).intValue ();
		Yoffset = hex.getYd () * 2 + 25;
		YNumOffset = hex.getYd () + 17;
		tWidth = Hex.getWidth ();
		tHeight = hex.getYd () + 5;
		X = Xoffset - tWidth;
		Y = Yoffset - tHeight;
		index = 0;
		for (GameTile tGameTile : gameTiles) {
			tGameTile.setXY (X, Y);
			tTile = tGameTile.getTile ();
			if (tTile != Tile.NO_TILE) {
				if (showThisTile (tTile)) {
					setBackgroundForTile (g, X, Y, tWidth, tHeight, tGameTile);
					tTileOrient = tGameTile.getTileOrient ();
					tTile.paintComponent (g, tTileOrient, hex, new Feature2 ());
					hex.drawRotateRightArrow (g, X, Y);
					tGameTile.drawSelected (g, hex);
					idLabel = tTile.getNumberToString ();
					idLabel = idLabel + " [" + tGameTile.getTotalAndAvailable () + "]";
					valueWidth = g.getFontMetrics ().stringWidth (idLabel);
					XNum = X - valueWidth/2;
					YNum = Y + YNumOffset;
					if (tGameTile.availableCount () > 0) {
						g.setColor (Color.BLACK);
					} else {
						g.setColor (Color.RED);
					}
					g.drawString (idLabel, XNum, YNum);
				}
			}
			index++;
			if (index == TILES_PER_ROW) {
				X = Xoffset - tWidth;
				Y += Yoffset;
				index = 0;
			} else {
				X += Xoffset;
			}
		}
	}

	private void setBackgroundForTile (Graphics g, int X, int Y, int aWidth, int aHeight, GameTile aGameTile) {
		int XUpperLeft;
		int YUpperLeft;
		XUpperLeft = X - aWidth;
		YUpperLeft = Y - aHeight;

		if (tileTrayFrame.isUpgradeAllowed (aGameTile)) {
			if (aGameTile.isPlayable ()) {
				if (aGameTile.availableCount () > 0) {
					g.setColor (Color.ORANGE);
				} else {
					g.setColor (Color.LIGHT_GRAY);
				}
				g.fillRect (XUpperLeft, YUpperLeft, aWidth * 2, aHeight * 2);
			}
		} else {
			g.setColor (Color.GRAY);
			g.fillRect (XUpperLeft, YUpperLeft, aWidth * 2, aHeight * 2);
		}
	}
	
	public Tile popTile (int aTileNumber) {
		Iterator<GameTile> iter = gameTiles.iterator ();
		GameTile tGameTile;
		boolean tFoundTile = false;
		Tile tTile = Tile.NO_TILE;
		
		if (aTileNumber != 0) {
			while (iter.hasNext () && !tFoundTile) {
				tGameTile = (GameTile) iter.next ();
				if (aTileNumber == tGameTile.getTileNumber ()) {
					tTile = tGameTile.popTile ();
					tFoundTile = true;
				}
			}
		}
		
		return tTile;
	}
	
	public void setHex (boolean aHexDirection) {
		hex = new Hex (aHexDirection);
	}
	
	public void setHex (String aHexDirection) {
		boolean tHexDirection;
		
		if (aHexDirection == null) {
			tHexDirection = false;
		} else {
			if (aHexDirection.equals (Hex.DIRECTION_NS)) {
				tHexDirection = false;
			} else {
				if (aHexDirection.equals (Hex.DIRECTION_EW)) {
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
				if (tUpgrade !=  Upgrade.NO_UPGRADE) {
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
					if (! TileName.NO_NAME2.equals (aTileName)) {
						for (tUpgradeIndex = 0; tUpgradeIndex < tUpgradeCount; tUpgradeIndex++) {
							tPlayableCount = getPlayableCount (aGameTile, aTileName, 
											tUpgradeIndex, tPlayableCount);
						}
					}
				}
				if (tPlayableCount == 0) {
					for (tUpgradeIndex = 0; tUpgradeIndex < tUpgradeCount; tUpgradeIndex++) {
						tPlayableCount = getPlayableCount (aGameTile, TileName.NO_NAME2, 
								tUpgradeIndex, tPlayableCount);
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

	public int getPlayableCount (GameTile aGameTile, String aMatchTileName, int aUpgradeIndex, 
			int aPlayableCount) {
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
					if (! NO_CITY_NAME.equals (aTileName)) {
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
								if (NO_CITY_NAME.equals (tTileName))  {
									tAvailableCount += tUpgradeGameTile.getAvailableCount ();
								}
							}
						}
					}
				}
				if (tAvailableCount == 0) {
					if (tUpgradeCount == 1) {
						tUpgrade = aGameTile.getUpgrade (0);
						if (tUpgrade != null) {
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
				if (! tGameTile.isFixedTile ()) {
					tileCount++;
				}
			}
		}
		
		return tileCount;
	}
	
	public void setTraySize () {
		int maxX, maxY, tileCount, rowCount;
		Dimension tNewDimension;
		
		if (hex == Hex.NO_HEX) {
			setHex (Hex.getDirection ());
		}
		tileCount = getTileCountToShow ();
		rowCount = new Double (tileCount / TILES_PER_ROW).intValue () + 1;
		maxX = new Double (Hex.getWidth () * 2.25 * TILES_PER_ROW + 10).intValue ();
		maxY = (hex.getYd () * 2 + 20) * rowCount;
		tNewDimension = new Dimension (maxX, maxY);
		tileTrayFrame.setScrollPanePSize (tNewDimension);
	}
	
	public void setValues (String aSetName) {
		gameTiles = new LinkedList<GameTile> ();
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
	
	ParsingRoutineI tileSetParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			XMLNodeList tXMLNodeList;
			Upgrade tUpgrade;
			int tTileNumber;
			int tQuantity;
			String tChildName;
			
			tChildName = aChildNode.getNodeName ();
			if (Tile.EN_TILE.equals (tChildName)) {
				tTileNumber = aChildNode.getThisIntAttribute (AN_NUMBER);
				tQuantity = aChildNode.getThisIntAttribute (AN_QUANTITY);
				addTile (tTileNumber, tQuantity);
				parsedGameTile = getGameTile (tTileNumber);
				tXMLNodeList = new XMLNodeList (tileParsingRoutine);
				tXMLNodeList.parseXMLNodeList (aChildNode, EN_UPGRADE);
			} else if (GameTile.EN_UPGRADE.equals (tChildName)) {
				tUpgrade  = new Upgrade (aChildNode);
				if (parsedGameTile != GameTile.NO_GAME_TILE) {
					parsedGameTile.addUpgrade (tUpgrade);
				}
			}
		}
	};
	
	ParsingRoutineI tileParsingRoutine  = new ParsingRoutineI ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			Upgrade tUpgrade;
			
			tUpgrade  = new Upgrade (aChildNode);
			if (parsedGameTile != GameTile.NO_GAME_TILE) {
				parsedGameTile.addUpgrade (tUpgrade);
			}
		}
	};
	
	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
	}
}