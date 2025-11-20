package ge18xx.round.plan;

import ge18xx.company.Corporation;
import ge18xx.map.MapCell;

public class MapPlan extends CorporationPlan {
	MapCell mapCell;
	
	public MapPlan (String aName) {
		this (aName, Corporation.NO_CORPORATION);
	}

	public MapPlan (String aName, Corporation aCorporation) {
		this (aName, aCorporation, MapCell.NO_MAP_CELL);
	}

	public MapPlan (String aName, Corporation aCorporation, MapCell aMapCell) {
		super (aName, aCorporation);
		setMapCell (aMapCell);
	}
	
	public void setMapCell (MapCell aMapCellID) {
		mapCell = aMapCellID;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
}
