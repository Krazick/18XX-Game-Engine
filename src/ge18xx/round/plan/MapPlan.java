package ge18xx.round.plan;

import ge18xx.company.Corporation;
import ge18xx.map.MapCell;

public class MapPlan extends CorporationPlan {
	MapCell mapCell;
	
	// Get copy of RootHexMap (Super Class of HexMap that holds the MapCell Array
	
	public MapPlan (Corporation aCorporation, String aGameName, String aName) {
		super (aCorporation, aGameName, aName);
		setMapCell (MapCell.NO_MAP_CELL);
	}
	
	public MapPlan (MapCell aMapCell, Corporation aCorporation, String aGameName, String aName) {
		super (aCorporation, aGameName, aName);
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
