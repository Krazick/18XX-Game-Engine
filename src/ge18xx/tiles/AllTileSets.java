package ge18xx.tiles;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JLabel;

//
//  AllTileSets.java
//  Game_18XX
//
//  Created by Mark Smith on 9/3/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class AllTileSets extends JLabel {
	private static final long serialVersionUID = 1L;
	Set<TileSet> gameTileSets;

	public AllTileSets () {
		gameTileSets = new HashSet<> ();
	}

	public boolean addTileSet (String aSetName) {
		return gameTileSets.add (new TileSet (aSetName));
	}

	public boolean addTile (Tile tile, int aTotalCount) {
		String tTileTypeName = tile.getTypeName ();
		Iterator<TileSet> iter = gameTileSets.iterator ();
		TileSet tTileSet;
		String tSetName;
		boolean retValue = false;

		while (iter.hasNext () && !retValue) {
			tTileSet = iter.next ();
			tSetName = tTileSet.getName ();
			if (tSetName.equals (tTileTypeName)) {
				retValue = tTileSet.addTile (tile, aTotalCount);
			}
		}

		return retValue;
	}

	public Tile getTile (int aTileNumber) {
		Iterator<TileSet> iter = gameTileSets.iterator ();
		TileSet tTileSet;
		Tile tTile = Tile.NO_TILE;
		boolean tFoundTile = false;

		if (aTileNumber != 0) {
			while (iter.hasNext () && !tFoundTile) {
				tTileSet = iter.next ();
				tTile = tTileSet.getTile (aTileNumber);
				if (tTile != Tile.NO_TILE) {
					tFoundTile = true;
				}
			}
		}

		return tTile;
	}

	public void setTraySize () {

	}
}
