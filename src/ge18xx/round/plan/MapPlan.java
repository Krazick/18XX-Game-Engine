package ge18xx.round.plan;

import ge18xx.company.Corporation;
import ge18xx.map.MapCell;

public class MapPlan extends CorporationPlan {
	MapCell mapCell;
	
	public MapPlan (String aGameName, String aName) {
		this (aGameName, aName, Corporation.NO_CORPORATION);
	}

	public MapPlan (String aGameName, String aName, Corporation aCorporation) {
		this (aGameName, aName, aCorporation, MapCell.NO_MAP_CELL);
	}

	public MapPlan (String aGameName, String aName, Corporation aCorporation, 
				MapCell aMapCell) {
		super (aGameName, aName, aCorporation);
		setMapCell (aMapCell);
	}
	
	public void setMapCell (MapCell aMapCellID) {
		mapCell = aMapCellID;
	}
	
	public MapCell getMapCell () {
		return mapCell;
	}
}
