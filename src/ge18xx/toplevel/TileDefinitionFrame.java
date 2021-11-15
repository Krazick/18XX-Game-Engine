package ge18xx.toplevel;

//
//  TileDefinitionFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 12/2/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.tiles.TileSet;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;

public class TileDefinitionFrame extends XMLFrame {
	public static final String BASE_TITLE = "Tile Definition";
	private static final long serialVersionUID = 1L;
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
	
	public TileSet getTileSet () {
		return tileSet;
	}
}