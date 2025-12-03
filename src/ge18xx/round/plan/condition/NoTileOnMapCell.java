package ge18xx.round.plan.condition;

import ge18xx.map.MapCell;

public class NoTileOnMapCell extends Condition {
	public static final String NAME = "No Tile on MapCell";
	MapCell mapCell;
	
	public NoTileOnMapCell (MapCell aMapCell) {
		this (NAME, aMapCell);
	}

	public NoTileOnMapCell (String aName, MapCell aMapCell) {
		super (NAME);
		setMapCell (aMapCell);		
	}

	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
	
	@Override
	public boolean meets () {
		boolean tMeets;
		
		if (mapCell != MapCell.NO_MAP_CELL) {
			if (mapCell.isTileOnCell ()) {
				tMeets = FAILS;
			} else {
				tMeets = MEETS;
			}
		} else {
			tMeets = FAILS;
		}
		
		return tMeets;
	}
}
