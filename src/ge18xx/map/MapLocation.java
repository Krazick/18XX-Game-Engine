package ge18xx.map;

//
//  MapLocation.java
//  Java_18XX
//
//  Created by Mark Smith on 8/9/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class MapLocation {
	int row;
	int col;
	Location location;

	public MapLocation () {
		this (0, 0, null);
	}

	public MapLocation (int aRow, int aCol) {
		this (aRow, aCol, new Location (Location.CENTER_CITY_LOC));
	}

	public MapLocation (int aRow, int aCol, int aLocation) {
		this (aRow, aCol, new Location (aLocation));
	}

	public MapLocation (int aRow, int aCol, Location aLocation) {
		row = aRow;
		col = aCol;
		location = aLocation;
	}

	public int getRow () {
		return row;
	}

	public int getCol () {
		return col;
	}

	public Location getLocation () {
		return location;
	}
}
