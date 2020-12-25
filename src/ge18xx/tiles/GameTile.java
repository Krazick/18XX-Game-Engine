package ge18xx.tiles;

//
//  GameTiles.java
//  Java_18XX
//
//  Created by Mark Smith on 11/12/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//


import ge18xx.map.Hex;
import ge18xx.utilities.ElementName;

import java.awt.Graphics;
import java.awt.geom.*;
import java.awt.Point;

import java.util.List;
import java.util.LinkedList;

public class GameTile {
	public static final ElementName EN_UPGRADE = new ElementName ("Upgrade");
	public static final Upgrade NO_UPGRADE = null;
	public static final GameTile NO_GAME_TILE = null;
	Tile tile;
	List<Tile> tiles;
	List<Upgrade> upgrades;
	int tileOrient;
	int tileNumber;
	int totalCount;
	int usedCount;
	int XCenter;
	int YCenter;
	boolean selected;
	boolean playable;
	
	public GameTile () {
		this (new Tile (), 0);
	}
	
	public GameTile (Tile aTile, int aTotalCount) {
		setValues (aTile, aTile.getNumber (), aTotalCount, 0);
	}
	
	public GameTile (int aTileNumber, int aTotalCount) {
		setValues (null, aTileNumber, aTotalCount, 0);
	}
	
	public boolean addUpgrade (Upgrade aUpgrade) {
		return upgrades.add (aUpgrade);
	}
	
	public int availableCount () {
		return (totalCount - usedCount);
	}
	
	public void clearPlayable () {
		setPlayable (false);
	}
	
	public void clearSelected () {
		selected = false;
	}
	
	public boolean containingPoint (Point2D.Double aPoint, Hex aHex) {
		return aHex.contains (aPoint, XCenter, YCenter);
	}
	
	public boolean containingPoint (Point aPoint, Hex aHex) {
		return aHex.contains (aPoint, XCenter, YCenter);
	}
	
	public void drawSelected (Graphics g, Hex aHex) {
		if (selected) {
			aHex.paintSelected (g, XCenter, YCenter);
		}
	}
	
	public Tile getTile () {
		return (tile);
	}
	
	public String getTileName () {
		return (tile.getName ());
	}
	
	public int getTileNumber () {
		if (tile != null) {
			return (tile.getNumber ());
		} else {
			return tileNumber;
		}
	}
	
	public int getTileOrient () {
		return tileOrient;
	}
	
	public int getTileType () {
		return tile.getTileType ();
	}
	
	public TileType getTheTileType () {
		return tile.getTheTileType ();
	}
	
	public String getToolTip () {
		String tTip;
		
		tTip = "<html>";
		tTip += tile.getToolTip ();
		tTip += "Total/Available: " + getTotalAndAvailable () + "<br>Tile Orientation: " + tileOrient ;
		
		tTip += "</html>";
		
		return tTip;
	}
	
	public String getTotalAndAvailable () {
		String tTotal = new Integer (totalCount).toString ();
		String tAvailable = new Integer (availableCount ()).toString ();
		
		return (tAvailable + " of " + tTotal);
	}
	
	public int getTypeCount () {
		return tile.getTypeCount ();
	}
	
	public Upgrade getUpgrade (int aIndex) {
		Upgrade tUpgrade;
		
		tUpgrade = upgrades.get (aIndex);
		
		return tUpgrade;
	}
	
	public int getUpgradeCount () {
		return upgrades.size ();
	}
	
	public Upgrade getUpgradeTo (int aTileNumber) {
		Upgrade tUpgrade;
		int tIndex;
		int tTileNumber;
		boolean tFoundUpgrade;
		
		tFoundUpgrade = false;
		tUpgrade = null;
		for (tIndex = 0; (tIndex < upgrades.size ()) && (! tFoundUpgrade); tIndex++) {
			tUpgrade = getUpgrade (tIndex);
			tTileNumber = tUpgrade.getTileNumber ();
			if (tTileNumber == aTileNumber) {
				tFoundUpgrade = true;
			}
		}
		
		return tUpgrade;
	}
	
	public boolean isFixedTile () {
		return tile.isFixedTile ();
	}
	
	public boolean isPlayable () {
		return playable;
	}
	
	public boolean isSelected () {
		return selected;
	}
	
	public boolean isSelectable () {
		return true;
	}
	
	public boolean isTileCreated () {
		return (! (tile == null));
	}
	
	public Tile popTile () {
		Tile tTile;
		
		if (tileAvailable ()) {
			if (tiles != null) {
				if (tiles.size () > 0) {
					tTile = tiles.remove (0);
					usedCount++;
				} else {
					tTile = null;
				}
			} else {
				tTile = null;
			}
		} else {
			tTile = null;
		}
		
		return tTile;
	}
	
	public void pushTile (Tile aTile) {
		if (tiles != null) {
			tiles.add (aTile);
			usedCount--;
		}
	}
	
	public boolean rotateArrowContainingPoint (Point aPoint, Hex aHex) {
		return aHex.rotateArrowContainingPoint (aPoint, XCenter, YCenter);
	}
	
	public void rotateTileLeft () {
		tileOrient = (tileOrient - 1) % 6;
	}
	
	public void rotateTileRight () {
		tileOrient = (tileOrient + 1) % 6;
	}
	
	public void setPlayable (boolean aPlayable) {
		playable = aPlayable;
	}
	
	public void setTile (Tile aTile) {
		tile = aTile;
	}
	
	public void setValues (Tile aTile, int aTileNumber, int aTotalCount, int aUsedCount) {
		tiles = new LinkedList<Tile> ();
		upgrades = new LinkedList<Upgrade> ();
		setTile (aTile);
		tileNumber = aTileNumber;
		totalCount = aTotalCount;
		// Initially all the tiles used up, need to push them on the stack, 
		// and decrement usedCount as they are pushed onto the stack.
		usedCount = aTotalCount;
		clearSelected ();
		tileOrient = 0;
		setPlayable (false);
	}
	
	public void setXY (int X, int Y) {
		XCenter = X;
		YCenter = Y;
		tile.setXY (X, Y);
	}
	
	public boolean tileAvailable () {
		return (usedCount < totalCount);
	}
	
	public void toggleSelected () {
		selected = !selected;
	}
	
	public int totalCount () {
		return totalCount;
	}

	public int getAvailableCount() {
		return availableCount ();
	}
}
