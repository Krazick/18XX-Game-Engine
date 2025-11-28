package ge18xx.round.plan;

import java.util.LinkedList;
import java.util.List;

import ge18xx.company.Corporation;
import ge18xx.map.GameMap;
import ge18xx.map.MapCell;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;

public class PlaceMapTilePlan extends MapPlan {
	Tile tile;
	int tileOrient;
	List<GameTile> gameTiles = new LinkedList<GameTile> ();
	
	public PlaceMapTilePlan (String aPlayerName, String aGameName, String aName) {
		this (aPlayerName, aGameName, aName, Corporation.NO_CORPORATION);
	}

	public PlaceMapTilePlan (String aPlayerName, String aGameName, String aName, 
			Corporation aCorporation) {
		this (aPlayerName, aGameName, aName, aCorporation, MapCell.NO_MAP_CELL);
	}

	public PlaceMapTilePlan (String aPlayerName, String aGameName, String aName, 
			Corporation aCorporation, MapCell aMapCell) {
		super (aPlayerName, aGameName, aName, aCorporation, aMapCell);
		setTileAndOrientation (Tile.NO_TILE, MapCell.NO_TILE_ORIENTATION);
	}

	public void setTileAndOrientation (Tile aTile, int aTileOrient) {
		setTile (aTile);
		setTileOrient (aTileOrient);
	}
	
	public void setTile (Tile aTile) {
		tile = aTile;
	}

	public Tile getTile () {
		return tile;
	}

	public void setTileOrient (int aTileOrient) {
		tileOrient = aTileOrient;
	}

	public int getTileOrient () {
		return tileOrient;
	}

	public void setPlayableTiles (GameMap aPlanningMap) {
		aPlanningMap.setPlayableTiles (mapCell);
		gameTiles = aPlanningMap.getPlayableGameTiles ();
		System.out.println ("There are " + gameTiles.size () + " PlayableTiles");
		aPlanningMap.clearPlayableTiles ();
	}
	
	public int playableTilesCount () {
		return gameTiles.size ();
	}
	
	public GameTile getPlayableTileAt (int aIndex) {
		GameTile tGameTile;
			
		tGameTile = gameTiles.get (aIndex);
		
		return tGameTile;
	}
}
