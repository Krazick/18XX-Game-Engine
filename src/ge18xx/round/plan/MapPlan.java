package ge18xx.round.plan;

import ge18xx.company.Corporation;
import ge18xx.map.MapCell;

public class MapPlan extends CorporationPlan {
	public static final MapPlan NO_MAP_PLAN = null;
	MapCell mapCell;
	
	
	public MapPlan (String aGameName, String aName) {
		this (aGameName, aName, Corporation.NO_CORPORATION);
	}
	
	public MapPlan (String aGameName, String aName, Corporation aCorporation) {
		super (aGameName, aName, aCorporation);
		setMapCell (MapCell.NO_MAP_CELL);
	}
	
	public MapPlan (String aGameName, String aName, Corporation aCorporation, MapCell aMapCell) {
		super (aGameName, aName, aCorporation);
		setMapCell (aMapCell);
	}

	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}

	public MapCell getMapCell () {
		return mapCell;
	}
	
	public String getMapCellID () {
		return mapCell.getCellID ();
	}
}