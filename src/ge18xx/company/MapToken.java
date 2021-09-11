package ge18xx.company;

import ge18xx.map.Location;
import ge18xx.map.MapCell;

public class MapToken extends Token {
	public static final MapToken NO_MAP_TOKEN = null;
	MapCell mapCell;
	Location location;
	int cost;
	
	public MapToken () {
		super ();
		setLocation (Location.NO_LOC);
		setMapCell (MapCell.NO_MAP_CELL);
		setCost (0);
	}
	
	public MapToken (MapToken aMapToken, int aCost) {
		super ();

		setLocation (Location.NO_LOC);
		setMapCell (MapCell.NO_MAP_CELL);
		setCost (aCost);
	}

	public MapCell getMapCell () {
		return mapCell;
	}
	
	public String getMapCellID () {
		String tMapCellID = MapCell.NO_ID;
		
		if (MapCell.NO_MAP_CELL != mapCell) {
			tMapCellID = mapCell.getCellID ();
		}
		
		return tMapCellID;
	}
	
	public Location getLocation () {
		return location;
	}

	public void setCost (int aCost) {
		cost = aCost;
	}
	
	public int getCost () {
		return cost;
	}
	
	public void setMapCell (MapCell aMapCell) {
		mapCell = aMapCell;
	}
	
	public void setLocation (Location aLocation) {
		location = aLocation;
	}
	
	public void placeToken (MapCell aMapCell, Location aLocation) {
		setMapCell (aMapCell);
		setLocation (aLocation);
	}
	
	public void printlog () {
		super.printlog ();
		if (mapCell == MapCell.NO_MAP_CELL) {
			System.err.println ("No Map Cell Specified Yet");
		} else {
			mapCell.printlog ();
		}
		location.printlog();
	}
	
	public boolean tokenPlaced () {
		if (mapCell == MapCell.NO_MAP_CELL) {
			return false;
		} else if (location == Location.NO_LOC) {
			return false;
		} else {
			return true;
		}
	}
}
