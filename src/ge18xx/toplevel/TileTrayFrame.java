package ge18xx.toplevel;

import java.awt.BorderLayout;

//  Game_18XX
//
//  Created by Mark Smith on 9/3/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.game.GameManager;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import ge18xx.tiles.TileType;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

public class TileTrayFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
//	private final int TILE_WIDTH = 92; 		// # of Pixels Wide per Tile
//	private final int TILE_HEIGHT = 115; 	// # of Pixels Height per Tile
	public static final String BASE_TITLE = "Tile Tray";
	public static final XMLFrame NO_TILE_TRAY_FRAME = null;
	TileSet tileSet;
	GameManager gameManager;

	public TileTrayFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getActiveGameName ());

		buildTileTrayScrollPanel ();

		// Width - 92 pixels for each tile, Multiply by # TILES/ROW
		// Height - 115 Pixels per Row
		// Count types of tiles, and divide by TILES/ROW, round up, to get how many rows
		// need to show

		gameManager = aGameManager;
	}

	private void buildTileTrayScrollPanel () {
		tileSet = new TileSet (this);
		buildScrollPane (tileSet, BorderLayout.CENTER);
	}

	private boolean validTileSet () {
		return (tileSet != TileSet.NO_TILE_SET);
	}

	public boolean addTile (Tile aTile, int aTotalCount) {
		boolean tTileAdded;

		tTileAdded = false;
		if (validTileSet ()) {
			tTileAdded = tileSet.addTile (aTile, aTotalCount);
		}

		return tTileAdded;
	}

	public void copyTileDefinitions (TileSet aTileDefinitions) {
		if (validTileSet ()) {
			tileSet.copyTileDefinitions (aTileDefinitions);
		}
	}

	public XMLElement createTileDefinitions (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = XMLElement.NO_XML_ELEMENT;
		if (validTileSet ()) {
			tXMLElement = tileSet.createAllTileDefinitions (aXMLDocument);
		}

		return tXMLElement;
	}

	public Tile getTile (int aTileNumber) {
		Tile tTile;

		if (validTileSet ()) {
			tTile = tileSet.getTile (aTileNumber);
		} else {
			tTile = Tile.NO_TILE;
		}

		return tTile;
	}

	public TileSet getTileSet () {
		return tileSet;
	}

	public TileType getTileType () {
		return tileSet.getTileType ();
	}

	public void notifyMapFrame () {
		gameManager.notifyMapFrame ();
	}

	public boolean isPlaceTileMode () {
		return gameManager.isPlaceTileMode ();
	}

	public void bringMapToFront () {
		gameManager.bringMapToFront ();
	}

	public void setValues (String aSetName) {
		tileSet.setValues (aSetName);
	}

	public void loadTileTrayFrame (String aXMLTileTrayName) {
		try {
			loadXML (aXMLTileTrayName, tileSet);
		} catch (Exception eException) {
			System.err.println ("Exception thrown " + eException.getMessage ());
			eException.printStackTrace ();
		}
	}

	/**
	 * Determine if the specified GameTile is currently allowed to be placed on the Map, based upon the
	 * current Phase of the Game and the Tile Color
	 *
	 * @param aGameTile The Tile to test if allowed to be placed
	 *
	 * @return TRUE if the current Game Phase allows this tile Type Color can be placed.
	 *
	 */
	public boolean isUpgradeAllowed (GameTile aGameTile) {
		boolean tUpgradeAllowed = true;
		String tTileColor;

		tTileColor = aGameTile.getTileColor ();
		tUpgradeAllowed = gameManager.isUpgradeAllowed (tTileColor);

		return tUpgradeAllowed;
	}

	public void copyATileFromDefinitions (TileSet tTDTileSet, int aTileNumber, int aQuantity) {
		if (validTileSet ()) {
			tileSet.copyATileFromDefinitions (tTDTileSet, aTileNumber, aQuantity);
		}
	}
}
