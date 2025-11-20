package ge18xx.round.plan;

import ge18xx.company.Corporation;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;

public class PlaceMapTilePlan extends MapPlan {
	Tile tile;
	int tileOrient;
	
	public PlaceMapTilePlan (String aName) {
		this (aName, Corporation.NO_CORPORATION);
	}

	public PlaceMapTilePlan (String aName, Corporation aCorporation) {
		this (aName, aCorporation, MapCell.NO_MAP_CELL);
	}

	public PlaceMapTilePlan (String aName, Corporation aCorporation, MapCell aMapCell) {
		super (aName, aCorporation, aMapCell);
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

}
