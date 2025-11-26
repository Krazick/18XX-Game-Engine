package ge18xx.round.plan;

import java.awt.Rectangle;

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
	
	public Rectangle buildSelectedViewArea () {
		int tXc;
		int tYc;
		Rectangle tSelectedViewArea;
		
		if (mapCell != MapCell.NO_MAP_CELL) {
			tXc = mapCell.getXCenter ();
			tYc = mapCell.getYCenter ();
			tSelectedViewArea = new Rectangle (tXc - 150, tYc - 150, 300, 300);
		} else {
			tSelectedViewArea = new Rectangle (0, 0, 300, 300);
		}
		
		return tSelectedViewArea;
	}
}