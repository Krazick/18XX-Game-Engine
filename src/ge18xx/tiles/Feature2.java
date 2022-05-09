package ge18xx.tiles;

//
//  Feature2.java
//  Game_18XX
//
//  Created by Mark Smith on 2/17/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//
// Feature that appears on a hex that has two locations associated with it (Track, Private Railway)

import ge18xx.map.Hex;
import ge18xx.map.Location;

import java.awt.Point;

public class Feature2 extends Feature {
	public static final Feature2 NO_FEATURE2 = null;
	Location location2;

	public Feature2 () {
		super (Location.NO_LOCATION);
		setLocation2 (Location.NO_LOCATION);
	}

	public Feature2 (Location aLocation1, Location aLocation2) {
		setLocation (aLocation1);
		setLocation2 (aLocation2);
	}

	public Feature2 (int aLocation1, int aLocation2) {
		setLocation (new Location (aLocation1));
		setLocation2 (new Location (aLocation2));
	}

	public boolean bothLocationsSet () {
		boolean tBothLocationsSet;

		if (isNoLocation ()) {
			tBothLocationsSet = false;
		} else {
			if (isNoLocation2 ()) {
				tBothLocationsSet = false;
			} else {
				tBothLocationsSet = true;
			}
		}

		return tBothLocationsSet;
	}

	public Point calcCenter2 (Hex aHex) {
		return location2.calcCenter (aHex);
	}

	public Location getLocation2 () {
		return location2;
	}

	public int getLocation2ToInt () {
		return location2.getLocation ();
	}

	public boolean isAtLocation2 (Location aLocation) {
		return (location2.getLocation () == aLocation.getLocation ());
	}

	public boolean isCenterLocation2 () {
		return location2.isCenterLocation ();
	}

	public boolean isDeadEnd2 () {
		return location2.isDeadEnd ();
	}

	public boolean isNoLocation2 () {
		if (location2 == Location.NO_LOC) {
			return true;
		} else {
			return location2.isNoLocation ();
		}
	}

	@Override
	public void printlog () {
		super.printlog ();
		location2.printlog ();
	}

	public void setLocation2 (Location aLocation2) {
		location2 = aLocation2;
	}

	public void setLocation2 (int aLocation2) {
		location2 = new Location (aLocation2);
	}
}
