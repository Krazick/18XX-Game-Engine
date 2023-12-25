package ge18xx.company;

import ge18xx.map.Location;
import ge18xx.map.MapCell;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;

public class MapToken extends Token {
	public static final MapToken NO_MAP_TOKEN = null;
	public static final String NO_SIDES = null;
	public static final String DIVIDER = "|";
	final static AttributeName AN_CONNECTED_SIDES = new AttributeName ("connectedSides");
	MapCell mapCell;
	Location location;
	int cost;
	boolean connectedSides [] = new boolean [6];

	public MapToken () {
		super ();
		setLocation (Location.NO_LOC);
		setMapCell (MapCell.NO_MAP_CELL);
		setCost (0);
		setAllConnectedSides (false);
	}

	public MapToken (MapToken aMapToken, int aCost, TokenInfo.TokenType aType) {
		super (aMapToken, aType);

		setLocation (Location.NO_LOC);
		setMapCell (MapCell.NO_MAP_CELL);
		setCost (aCost);
		setAllConnectedSides (false);
	}
	
	@Override
	public XMLElement getTokenElement (XMLDocument aXMLDocument) {
		XMLElement tTokenElement;
		String tConnectedSides;
		
		tTokenElement = super.getTokenElement (aXMLDocument);
		fillTokenElement (tTokenElement);
		tTokenElement.setAttribute (MapCell.AN_MAP_CELL_ID, mapCell.getCellID ());
		tTokenElement.setAttribute (Location.AN_LOCATION, location.getLocation ());
		tConnectedSides = getSides ();
		if (tConnectedSides != MapToken.NO_SIDES) {
			tTokenElement.setAttribute (AN_CONNECTED_SIDES, tConnectedSides);
		}

		return tTokenElement;
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

	public void copyConnectedSides (boolean aConnectedSides []) {
		int tSideIndex;
		
		for (tSideIndex = Location.MIN_SIDE; tSideIndex <= Location.MAX_SIDE; tSideIndex++) {
			setConnectedSide (tSideIndex, aConnectedSides [tSideIndex]);
		}

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
		String tSides = NO_SIDES;
		int tSideIndex;

		for (tSideIndex = Location.MIN_SIDE; tSideIndex <= Location.MAX_SIDE; tSideIndex++) {
			if (connectedSides [tSideIndex]) {
				if (tSides == NO_SIDES) {
					tSides = DIVIDER;
				}
				tSides += tSideIndex + DIVIDER;
			}
		}

		return tSides;
	}

	public void printConnectedSides () {
		int tSideIndex;

		for (tSideIndex = Location.MIN_SIDE; tSideIndex <= Location.MAX_SIDE; tSideIndex++) {
			System.out.print (tSideIndex + " " + connectedSides [tSideIndex] + " " + DIVIDER + " ");
		}
		System.out.println ("");		// PRINTLOG
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
