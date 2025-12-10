package ge18xx.round.plan.condition;

import ge18xx.map.MapCell;

public class NoTileOnMapCell extends Condition {
	public static final String NAME = "No Tile on MapCell";
	MapCell mapCell;		// Must be MapCell on the Live Map, not the Planning Map
	
	public NoTileOnMapCell (MapCell aMapCell) {
		this (NAME, aMapCell);
	}

	public NoTileOnMapCell (String aName, MapCell aMapCell) {
		super (aName);
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
	
	@Override
	public String getReport () {
		String tReport;
		
		tReport = super.getReport () + "(" + mapCell.getID () + ")";
		tReport = appendStatus (tReport);
		
		return tReport;
	}
}
