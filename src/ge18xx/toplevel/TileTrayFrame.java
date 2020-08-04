package ge18xx.toplevel;

//
//  TileTrayFrame.java
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

import java.awt.*;

import javax.swing.*;

public class TileTrayFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	private final int TILE_WIDTH = 92; 		// # of Pixels Wide per Tile
	private final int TILE_HEIGHT = 115; 	// # of Pixels Height per Tile
	TileSet tileSet;
	GameManager gameManager;
	
	public TileTrayFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getActiveGameName ());
		JScrollPane scrollPane;
		int num_rows;
		
		tileSet = new TileSet (this);
        scrollPane = new JScrollPane ();
		scrollPane.setViewportView (tileSet);
		// Width - 92 pixels for each tile, Multiply by # TILES/ROW
		// Height - 115 Pixels per Row
		// Count types of tiles, and divide by TILES/ROW, round up, to get how many rows need to show
		// Should change based upon phase (and tiles available/to be shown)
		num_rows = 4;  // TODO: Need to Calculate this from the TileSet
        scrollPane.setPreferredSize (new Dimension (TILE_WIDTH * TileSet.TILES_PER_ROW, TILE_HEIGHT * num_rows));
		add (scrollPane, BorderLayout.CENTER);
		gameManager = aGameManager;
	}
	
	public boolean addTile (Tile aTile, int aTotalCount) {
		return tileSet.addTile (aTile, aTotalCount);
	}
	
	public void copyTileDefinitions (TileSet aTileDefinitions) {
		tileSet.copyTileDefinitions (aTileDefinitions);
	}
	
	public XMLElement createTileDefinitions (XMLDocument aXMLDocument) {
		return (tileSet.createAllTileDefinitions (aXMLDocument));
	}
	
	public Tile getTile (int aTileNumber) {
		if (tileSet == null) {
			return null;
		} else {
			return tileSet.getTile (aTileNumber);
		}
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
	
	public void setTraySize () {
		tileSet.setTraySize ();
	}
	
	public void setValues (String aSetName) {
		tileSet.setValues (aSetName);
	}

	public boolean isUpgradeAllowed (GameTile aUpgradeGameTile) {
		boolean tUpgradeAllowed = true;
		TileType tTileType = aUpgradeGameTile.getTheTileType ();
		String tTileColor = tTileType.getName ();
		
		System.out.println ("Tile " + aUpgradeGameTile.getTileNumber () + " is Color " + tTileColor);
		tUpgradeAllowed = gameManager.isUpgradeAllowed (tTileColor);
		
		return tUpgradeAllowed;
	}
}
