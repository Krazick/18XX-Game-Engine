package ge18xx.round.plan;

import java.util.LinkedList;
import java.util.List;

import ge18xx.company.Corporation;
import ge18xx.map.GameMap;
import ge18xx.map.MapCell;
import ge18xx.tiles.GameTile;
import ge18xx.tiles.Tile;
import ge18xx.tiles.TileSet;
import geUtilities.GUI;

public class PlaceMapTilePlan extends MapPlan {
	Tile tile;
	int tileOrient;
	List<GameTile> gameTiles = new LinkedList<GameTile> ();
	private int previousOrientation;
	private String previousTokens;
	private String previousBases;
	
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

	public void setGameTiles (List<GameTile> aGameTiles) {
		gameTiles = aGameTiles;
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

	// Collect the set of Playable Tiles, without regards to the Current Phase
	public void setPlayableTiles (GameMap aPlanningMap) {
		aPlanningMap.setPlayableTiles (mapCell, false);
		gameTiles = aPlanningMap.getPlayableGameTiles ();
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
	
	public void putTileDownOnMap () {
		boolean tTilePlaced;
		PlanTileSet tPlanTileSet;
		TileSet tFullTileSet;
		GameTile tSelectedTile;
		Tile tNewTile;
		Tile tPreviousTile;

		System.out.println ("Ready to Putdown Tile on Planning Map");
		tPreviousTile = planningMapCell.getTile ();
		if (tPreviousTile != Tile.NO_TILE) {
			previousOrientation = planningMapCell.getTileOrient ();
			previousTokens = tPreviousTile.getPlacedTokens ();
			previousBases = tPreviousTile.getCorporationBases ();
		} else {
			previousOrientation = 0;
			previousTokens = GUI.EMPTY_STRING;
			previousBases = GUI.EMPTY_STRING;
		}
		tFullTileSet = planFrame.getFullTileSet ();
		
		tPlanTileSet = planFrame.getPlanTileSet ();
		tSelectedTile = tPlanTileSet.getSelectedTile ();
		tTilePlaced = planningMapCell.putThisTileDown (tFullTileSet, tSelectedTile, MapCell.NO_ROTATION);
		tNewTile = planningMapCell.getTile ();
		setTile (tNewTile);
		planFrame.setTilePlaced (tTilePlaced);
		tPlanTileSet.clearAllSelected ();
		planFrame.update ();
	}
	
	public void pickupTile () {
		PlanTileSet tPlanTileSet;
		TileSet tFullTileSet;
		int tTileNumber;
		GameTile tPreviousGameTile;
		
		System.out.println ("Ready to Pickup Tile on Planning Map");
		tPlanTileSet = planFrame.getPlanTileSet ();
		tFullTileSet = planFrame.getFullTileSet ();
		if (tile == Tile.NO_TILE) {
			planningMapCell.removeTile ();

			
		} else {
			tTileNumber = tile.getNumber ();
			tPreviousGameTile = tFullTileSet.getGameTile (tTileNumber);
			planningMapCell.putThisTileDown (tFullTileSet, tPreviousGameTile, previousOrientation);
		}
		planFrame.setTilePlaced (false);
		tPlanTileSet.clearAllSelected ();
		planFrame.update ();
	}
	
	public void rotateTile () {
		GameMap tPlanningMap;
		int tPossible;
		
		tPlanningMap = planFrame.getPlanningMap ();
		tPossible = planningMapCell.getTileOrient ();

		System.out.println ("Ready to Rotate Tile on Planning Map");
		tPlanningMap.rotateTileInPlace (planningMapCell, tPossible, false, tile);
		planFrame.update ();
	}
}
