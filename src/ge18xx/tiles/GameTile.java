package ge18xx.tiles;

//
//  GameTiles.java
//  Java_18XX
//
//  Created by Mark Smith on 11/12/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.map.Hex;
import ge18xx.phase.PhaseInfo;
import ge18xx.utilities.ElementName;

import java.awt.Graphics;
import java.awt.Point;

import java.awt.geom.Point2D;

import java.util.List;
import java.util.Comparator;
import java.util.LinkedList;

public class GameTile {
	public static final ElementName EN_UPGRADE = new ElementName ("Upgrade");
	public static final GameTile NO_GAME_TILE = null;
	public static final List<Tile> NO_TILES = null;
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
		if (tile != Tile.NO_TILE) {
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

	/**
	 * Retrieve the Tile Color as a String and return it.
	 * 
	 * @return the String that specifies the Tile Color.
	 * 
	 */
	public String getTileColor () {
		TileType tTileType;
		String tTileColor;
		
		tTileType = getTheTileType ();
		tTileColor = tTileType.getName ();
		
		return tTileColor;
	}
	
	public String getToolTip () {
		String tTip;

		tTip = "<html>";
		tTip += tile.getToolTip (PhaseInfo.NO_NAME);
		tTip += "Total/Available: " + getTotalAndAvailable () + "<br>Tile Orientation: " + tileOrient;

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
		tUpgrade = Upgrade.NO_UPGRADE;
		for (tIndex = 0; (tIndex < upgrades.size ()) && (!tFoundUpgrade); tIndex++) {
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
		return (!(tile == Tile.NO_TILE));
	}

	public Tile popTile () {
		Tile tTile;

		if (tileAvailable ()) {
			if (tiles != NO_TILES) {
				if (tiles.size () > 0) {
					tTile = tiles.remove (0);
					usedCount++;
				} else {
					tTile = Tile.NO_TILE;
				}
			} else {
				tTile = Tile.NO_TILE;
			}
		} else {
			tTile = Tile.NO_TILE;
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
		setTileNumber (aTileNumber);
		setTotalCount (aTotalCount);
		// Initially all the tiles used up, need to push them on the stack,
		// and decrement usedCount as they are pushed onto the stack.
		setUsedCount (aTotalCount);
		clearSelected ();
		setTileOrient (0);
		setPlayable (false);
	}

	public void setXY (int aX, int aY) {
		XCenter = aX;
		YCenter = aY;
		tile.setXY (aX, aY);
	}

	public boolean tileAvailable () {
		return (usedCount < totalCount);
	}

	public void toggleSelected () {
		selected = !selected;
	}

	public void setTileNumber (int aTileNumber) {
		tileNumber = aTileNumber;
	}

	public void setTileOrient (int aTileOrient) {
		tileOrient = aTileOrient;
	}

	public void setTotalCount (int aTotalCount) {
		totalCount = aTotalCount;
	}
	
	public void setUsedCount (int aUsedCount) {
		usedCount = aUsedCount;
	}
	
	public int getTotalCount () {
		return totalCount;
	}

	public int getAvailableCount () {
		return availableCount ();
	}
	
	public static Comparator<GameTile> GameTileComparator = new Comparator<GameTile> () {
		@Override
		public int compare (GameTile aGameTile1, GameTile aGameTile2) {
			int tGameTileOrder;
			int tTileTypeOrder;
			int tTileNumberOrder;
			Tile tTile1;
			Tile tTile2;
			
			tTile1 = aGameTile1.getTile ();
			tTile2 = aGameTile2.getTile ();
			if (tTile1.isFixedTile ()) {
				tGameTileOrder = 1;
			} if (tTile2.isFixedTile ()) {
				tGameTileOrder = -1;
			} else {
				tTileTypeOrder = tTile1.compareType (tTile2);
				if (tTileTypeOrder == 0) {
					tTileNumberOrder = tTile1.compareNumber (tTile2);
					tGameTileOrder = tTileNumberOrder;
				} else {
					tGameTileOrder = tTileTypeOrder;
				}
			}
			
			return tGameTileOrder;
		}
	};
}
