package ge18xx.map;

import java.util.LinkedList;
import java.util.List;

public class SelectableMapCells {
	List<MapCell> selectableMapCells;

	public SelectableMapCells () {
		selectableMapCells = new LinkedList<> ();
	}

	public void removeAll () {
		selectableMapCells.removeAll (selectableMapCells);
	}

	public void addMapCell (MapCell aMapCell) {
		selectableMapCells.add (aMapCell);
	}

	public void addMapCells (HexMap aHexMap, String aMapCellIDs) {
		String tMapCellIDs [];
		MapCell tMapCell;
		int tMapCellCount;
		int tMapCellIndex;

		tMapCellIDs = aMapCellIDs.split (", ?");
		tMapCellCount = tMapCellIDs.length;
		if (tMapCellCount > 0) {
			for (tMapCellIndex = 0; tMapCellIndex < tMapCellCount; tMapCellIndex++) {
				tMapCell = aHexMap.getMapCellForID (tMapCellIDs [tMapCellIndex]);
				if (tMapCell != MapCell.NO_MAP_CELL) {
					addMapCell (tMapCell);
				}
			}
		}
	}

	public boolean containsMapCell (MapCell aMapCell) {
		boolean tContainsMapCell;

		if (isEmpty ()) {
			tContainsMapCell = true;
		} else {
			tContainsMapCell = selectableMapCells.contains (aMapCell);
		}

		return tContainsMapCell;
	}

	public boolean isEmpty () {
		return selectableMapCells.isEmpty ();
	}
}
