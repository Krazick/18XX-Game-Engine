package ge18xx.company;

import ge18xx.map.HexMap;
import ge18xx.map.Location;
import ge18xx.map.MapCell;

public class MapToken extends Token {
	static final MapCell NOT_PLACED = null;
	static final Location NO_LOCATION = null;
	MapCell mapCell;
	Location location;
	int cost;
	
	public MapToken () {
		super ();
		setLocation (NO_LOCATION);
		setMapCell (NOT_PLACED);
		setCost (0);
	}
	
	public MapToken (MapToken aMapToken, int aCost) {
		super ();

		setLocation (NO_LOCATION);
		setMapCell (NOT_PLACED);
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
		if (mapCell == HexMap.NO_MAP_CELL) {
			System.out.println ("No Map Cell Specified Yet");
		} else {
			mapCell.printlog ();
		}
		location.printlog();
	}
	
	public boolean tokenPlaced () {
		if (mapCell == NOT_PLACED) {
			return false;
		} else if (location == NO_LOCATION) {
			return false;
		} else {
			return true;
		}
	}
}
