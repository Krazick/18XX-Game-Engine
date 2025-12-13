package ge18xx.round.plan;

import java.awt.Rectangle;

import ge18xx.company.Corporation;
import ge18xx.map.GameMap;
import ge18xx.map.MapCell;

public class MapPlan extends CorporationPlan {
	public static final MapPlan NO_MAP_PLAN = null;
	public static final String NO_MAP_CELL = "No Selected MapCell";
	MapCell mapCell;
	MapCell planningMapCell;
	GameMap planningMap;

	public MapPlan (String aPlayerName, String aGameName, String aName) {
		this (aPlayerName, aGameName, aName, Corporation.NO_CORPORATION);
	}
	
	public MapPlan (String aPlayerName, String aGameName, String aName, Corporation aCorporation) {
		super (aPlayerName, aGameName, aName, aCorporation);
		setMapCell (MapCell.NO_MAP_CELL);
	}
	
	public MapPlan (String aPlayerName, String aGameName, String aName, Corporation aCorporation, MapCell aMapCell) {
		super (aPlayerName, aGameName, aName, aCorporation);
		setMapCell (aMapCell);
	}

	public void setPlanningMap (GameMap aPlannngMap) {
		planningMap = aPlannngMap;
	}

	public GameMap getPlanningMap () {
		return planningMap;
	}

	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}

	public MapCell getMapCell () {
		return mapCell;
	}

	public void setPlanningMapCell (MapCell aPlanningMapCell) {
		planningMapCell = aPlanningMapCell;
	}

	public MapCell getPlanningMapCell () {
		return planningMapCell;
	}
	
	public String getMapCellID () {
		return mapCell.getCellID ();
	}
	
	public int getMapCellXc () {
		return mapCell.getXCenter ();
	}
	
	public int getMapCellYc () {
		return mapCell.getYCenter ();
	}
	
	public String getMapCellCoords () {
		String tMapCellCoords;
		
		tMapCellCoords = NO_MAP_CELL;
		if (mapCell != MapCell.NO_MAP_CELL) {
			tMapCellCoords = "[" + getMapCellXc () + ", " + getMapCellYc () + "]";
		}
		
		return tMapCellCoords;
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