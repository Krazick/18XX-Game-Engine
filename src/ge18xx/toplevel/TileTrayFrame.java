package ge18xx.toplevel;

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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class TileTrayFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
//	private final int TILE_WIDTH = 92; 		// # of Pixels Wide per Tile
//	private final int TILE_HEIGHT = 115; 	// # of Pixels Height per Tile
	public static final String BASE_TITLE = "Tile Tray";
	public static final TileTrayFrame NO_TILE_TRAY_FRAME = null;
	TileSet tileSet;
	GameManager gameManager;
	JScrollPane scrollPane;
	
	public TileTrayFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getActiveGameName ());
		
		buildTileTrayScrollPanel ();
		
		// Width - 92 pixels for each tile, Multiply by # TILES/ROW
		// Height - 115 Pixels per Row
		// Count types of tiles, and divide by TILES/ROW, round up, to get how many rows need to show
		// Should change based upon phase (and tiles available/to be shown)
//		tNumRows = 4;  
//		tWidth = TILE_WIDTH * TileSet.TILES_PER_ROW;
//		tHeight = TILE_HEIGHT * tNumRows;
//		tDimension = new Dimension (tWidth, tHeight);
//		
//		setScrollPanePSize (tDimension);
		
		gameManager = aGameManager;
	}
	
	private void buildTileTrayScrollPanel () {
		tileSet = new TileSet (this);
        scrollPane = new JScrollPane ();
		scrollPane.setViewportView (tileSet);
		scrollPane.setHorizontalScrollBarPolicy(
			    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add (scrollPane, BorderLayout.CENTER);
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
		if (tileSet == TileSet.NO_TILE_SET) {
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
	
	public void bringMapToFront () {
		gameManager.bringMapToFront ();
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
		
		tUpgradeAllowed = gameManager.isUpgradeAllowed (tTileColor);
		
		return tUpgradeAllowed;
	}

	public void setScrollPanePSize(Dimension tNewDimension) {
		scrollPane.setPreferredSize (tNewDimension);
        scrollPane.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
 	}
}
