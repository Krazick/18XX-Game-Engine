package ge18xx.round.plan;

import ge18xx.company.Corporation;
import ge18xx.map.MapCell;
import ge18xx.tiles.Tile;

public class PlaceTileMapPlan extends MapPlan {
	Tile tileToPlace;
	int tileOrientation;
	
	public PlaceTileMapPlan (String aGameName, String aName) {
		super (aGameName, aName, Corporation.NO_CORPORATION);	
	}
	
	public PlaceTileMapPlan (String aGameName, String aName, Corporation aCorporation) {
		super (aGameName, aName, aCorporation);	
	}

	public PlaceTileMapPlan (String aGameName, String aName, Corporation aCorporation, MapCell aMapCell) {
		super (aGameName, aName, aCorporation, aMapCell);
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
