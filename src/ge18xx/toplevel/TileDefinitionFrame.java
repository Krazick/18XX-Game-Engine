package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;

//
//  TileDefinitionFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 12/2/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.tiles.TileSet;

public class TileDefinitionFrame extends XMLFrame {
	public static final String BASE_TITLE = "Tile Definition";
	public static final String TILE_SUFFIX_NAME = " Tile Definitions.xml";
	public static final String TILE_DIRECTORY_NAME = "Tile XML Data/";
	private static final long serialVersionUID = 1L;
	String allTileSetNames[] = { "Yellow", "Green", "Brown", "Grey", "Other" };
	TileSet tileSet;

	public TileDefinitionFrame (String aFrameName, TileTrayFrame aTileTrayFrame, String aGameName) {
		super (aFrameName, aGameName);
		JScrollPane scrollPane;

		tileSet = new TileSet (aTileTrayFrame);
		scrollPane = new JScrollPane ();
		scrollPane.setViewportView (tileSet);
		scrollPane.setPreferredSize (new Dimension (300, 300));
		add (scrollPane, BorderLayout.CENTER);
	}

	public String [] getAllTileSetNames () {
		return allTileSetNames;
	}

	public TileSet getTileSet () {
		return tileSet;
	}

	public void loadAllTileDefinitions (String aBaseDirName, TileTrayFrame aTileTrayFrame) {
		TileSet tTDTileSet;
		
		for (String tTileSetName : allTileSetNames) {
			tTDTileSet = loadATileDefinitionSet (aBaseDirName, tTileSetName);
			aTileTrayFrame.copyTileDefinitions (tTDTileSet);
		}
	}

	public TileSet loadATileDefinitionSet (String aBaseDirName, String aTileSetName) {
		String tXMLTDFileName;
		
		tXMLTDFileName = aBaseDirName + TILE_DIRECTORY_NAME + aTileSetName + TILE_SUFFIX_NAME;
		try {
			loadXML (tXMLTDFileName, tileSet);
		} catch (Exception eException) {
			System.err.println ("Exception thrown " + eException.getMessage ());
			eException.printStackTrace ();
		}
		
		return tileSet;
	}

	public void loadATileFromASet (String aBaseDirName, TileTrayFrame aTileTrayFrame, 
									int aTileNumber, String aTileSetName, int aQuantity) {
		TileSet tTDTileSet;
		
		tTDTileSet = loadATileDefinitionSet (aBaseDirName, aTileSetName);
		aTileTrayFrame.copyATileFromDefinitions (tTDTileSet, aTileNumber, aQuantity);
	}
}