package ge18xx.company;

import ge18xx.map.Location;
import ge18xx.map.MapCell;

public class MapToken extends Token {
	public static final MapToken NO_MAP_TOKEN = null;
	MapCell mapCell;
	Location location;
	int cost;
	boolean connectedSides[] = new boolean [6];

	public MapToken () {
		super ();
		setLocation (Location.NO_LOC);
		setMapCell (MapCell.NO_MAP_CELL);
		setCost (0);
		setAllConnectedSides (false);
	}

	public MapToken (MapToken aMapToken, int aCost) {
		super (aMapToken);

		setLocation (Location.NO_LOC);
		setMapCell (MapCell.NO_MAP_CELL);
		setCost (aCost);
		setAllConnectedSides (false);
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

	public boolean isConnectedToSide (int aSideIndex) {
		return getConnectedSide (aSideIndex);
	}

	public boolean getConnectedSide (int aSideIndex) {
		boolean tConnectedSide = false;

		if (Location.isValidSide (aSideIndex)) {
			tConnectedSide = connectedSides [aSideIndex];
		}

		return tConnectedSide;
	}

	public void setConnectedSide (int aSideIndex, boolean aConnected) {
		if (Location.isValidSide (aSideIndex)) {
			connectedSides [aSideIndex] = aConnected;
		}
	}

	public void setAllConnectedSides (boolean aConnected) {
		int tSideIndex;

		for (tSideIndex = Location.MIN_SIDE; tSideIndex <= Location.MAX_SIDE; tSideIndex++) {
			setConnectedSide (tSideIndex, aConnected);
		}
	}

	public void placeToken (MapCell aMapCell, Location aLocation) {
		setMapCell (aMapCell);
		setLocation (aLocation);
		setConnectedSides (aMapCell, aLocation);
	}

	public void setConnectedSides (MapCell aMapCell, Location aLocation) {
		int tSideIndex;
		int tTileRotation;
		int tSideIndexRotated;
		Location tSideRotated;
		boolean tConnectedToSide;

		tTileRotation = aMapCell.getTileOrient ();
		for (tSideIndex = Location.MIN_SIDE; tSideIndex <= Location.MAX_SIDE; tSideIndex++) {
			tConnectedToSide = aMapCell.areLocationsConnected (aLocation, tSideIndex);
			tSideRotated = new Location (tSideIndex);
			tSideRotated = tSideRotated.rotateLocation (tTileRotation);
			tSideIndexRotated = tSideRotated.getLocation ();
			setConnectedSide (tSideIndexRotated, tConnectedToSide);
		}
	}

	public String getSides () {
		String tSides = "|";
		int tSideIndex;

		for (tSideIndex = Location.MIN_SIDE; tSideIndex <= Location.MAX_SIDE; tSideIndex++) {
			if (connectedSides [tSideIndex]) {
				tSides += tSideIndex + "|";
			}
		}

		return tSides;
	}

	public void printConnectedSides () {
		int tSideIndex;

		for (tSideIndex = Location.MIN_SIDE; tSideIndex <= Location.MAX_SIDE; tSideIndex++) {
			System.out.print (tSideIndex + " " + connectedSides [tSideIndex] + " | ");
		}
		System.out.println ("");
	}

	@Override
	public void printlog () {
		super.printlog ();
		if (mapCell == MapCell.NO_MAP_CELL) {
			System.err.println ("No Map Cell Specified Yet");
		} else {
			mapCell.printlog ();
		}
		location.printlog ();
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
	
	@Override
	public boolean isAMapToken () {
		return true;
	}

}
