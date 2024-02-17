package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;

import ge18xx.game.GameManager;

//
//  TileDefinitionFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 12/2/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.tiles.TileSet;

import geUtilities.xml.XMLFrame;
import geUtilities.XMLDocument;

public class TileDefinitionFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	public static final String BASE_TITLE = "Tile Definition";
	public static final String TILE_SUFFIX_NAME = "-Tile-Definitions.xml";
	public static final String TILE_DIRECTORY_NAME = "Tile XML Data/";
	public static final String TILE_URL_BASE = "Tiles";
	String allTileSetNames[] = { "Yellow", "Green", "Brown", "Grey", "Other" };
	TileSet tileSet;

	public TileDefinitionFrame (String aFrameName, TileTrayFrame aTileTrayFrame, GameManager aGameManager) {
		super (aFrameName, aGameManager);
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

	public void loadAllTileDefinitions (String aURLBase, TileTrayFrame aTileTrayFrame) {
		TileSet tTDTileSet;
		XMLDocument tXMLDocument;
		
		for (String tTileSetName : allTileSetNames) {
			tXMLDocument = readXMLfromURL (aURLBase, tTileSetName);
			tTDTileSet = loadATileDefinitionSet (tXMLDocument);
			aTileTrayFrame.copyTileDefinitions (tTDTileSet);
		}
	}

	public TileSet loadATileDefinitionSet (XMLDocument aXMLDocument) {
		try {
			loadXML (aXMLDocument, tileSet);
		} catch (Exception eException) {
			System.err.println ("Exception thrown " + eException.getMessage ());
			eException.printStackTrace ();
		}

		return tileSet;
	}

	public void loadATileFromASet (String aURLBase, String aBaseDirName, TileTrayFrame aTileTrayFrame,
			int aTileNumber, String aTileSetName, int aQuantity) {
		TileSet tTDTileSet;
		XMLDocument tXMLDocument;

		tXMLDocument = readXMLfromURL (aURLBase, aTileSetName);
		tTDTileSet = loadATileDefinitionSet (tXMLDocument);
		aTileTrayFrame.copyATileFromDefinitions (tTDTileSet, aTileNumber, aQuantity);
	}
	
	public void loadATileFromASet (String aBaseDirName, TileTrayFrame aTileTrayFrame,
									int aTileNumber, String aTileSetName, int aQuantity) {
		TileSet tTDTileSet;

		tTDTileSet = loadATileDefinitionSet (aBaseDirName, aTileSetName);
		aTileTrayFrame.copyATileFromDefinitions (tTDTileSet, aTileNumber, aQuantity);
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
	
	public XMLDocument readXMLfromURL (String aURLBase, String tType) {
		XMLDocument tXMLDocument;
		String tFullURL;
		
		tFullURL = constructFullURL (aURLBase, tType);
		tXMLDocument = new XMLDocument (tFullURL);
		
		return tXMLDocument;
	}

	private String constructFullURL (String aURLBase, String aType) {
		String tFullURL;
		
		tFullURL = aURLBase + TILE_URL_BASE + "/" + aType + TILE_SUFFIX_NAME;
		
		return tFullURL;
	}
}