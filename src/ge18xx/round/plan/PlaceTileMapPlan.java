package ge18xx.round.plan;

import ge18xx.company.Corporation;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;

public class PlaceTileMapPlan extends MapPlan {
	Tile tileToPlace;
	int tileOrientation;
	
	public PlaceTileMapPlan (String aGameName, String aName) {
		super (Corporation.NO_CORPORATION, aGameName, aName);	
	}
	
	public PlaceTileMapPlan (Corporation aCorporation, String aGameName, String aName) {
		super (aCorporation, aGameName, aName);	
	}

	public PlaceTileMapPlan (MapCell aMapCell, Corporation aCorporation, String aGameName, String aName) {
		super (aMapCell, aCorporation, aGameName, aName);
	}

	public void setTileToPlace (Tile aTile, int aTileOrientation) {
		tileToPlace = aTile;
		tileOrientation = aTileOrientation;
	}
	
	public Tile getTileToPlace () {
		return tileToPlace;
	}
	
	public int getTileOrientation () {
		return tileOrientation;
	}
}
