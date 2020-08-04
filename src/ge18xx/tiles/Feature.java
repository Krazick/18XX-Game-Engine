package ge18xx.tiles;

//
//  Feature.java
//  Game_18XX
//
//  Created by Mark Smith on 9/16/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//
//  Feature that appears on a hex, like Revenue Centers, Terrains, Names, etc.
//  Features have one specific Location within the Hex, unlike track segments that have a enter and exit point.
//  Features will be an object within the Map Cells and the Tiles.


import ge18xx.map.Hex;
import ge18xx.map.Location;

import java.awt.Point;

public class Feature implements Cloneable {
	protected Location location;
	
	public Feature () {
		setLocation (Location.NO_LOCATION);
	}
	
	public Feature (Location aLocation) {
		setLocation (aLocation);
	}
	
	public Feature (int aLocation) {
		setLocation (new Location (aLocation));
	}
	
	public boolean bleedThroughAll () {
		return false;
	}
	
	public boolean bleedThroughJustStarting () {
		return true;
	}
	
	public Point calcCenter (Hex aHex) {
		return location.calcCenter (aHex);
	}
	
	public Feature clone () {
		try {
			Feature tFeature = (Feature) super.clone ();
			tFeature.location = (Location) location.clone ();
			
			return tFeature;
		} catch (CloneNotSupportedException e) {
			throw new Error ("Feature.clone Not Supported Exception");
		}
	}
	
	public Location getLocation () {
		return location;
	}
	
	public int getLocationToInt () {
		return location.getLocation ();
	}
	
	public String getLocationToString () {
		return location.toString ();
	}
	
	public boolean isAtLocation (Location aLocation) {
		return (location.getLocation () == aLocation.getLocation ());
	}

	public boolean isCenterLocation () {
		return location.isCenterLocation ();
	}
	
	public boolean isDeadEnd () {
		return location.isDeadEnd ();
	}
	
	public boolean isNoLocation () {
		if (location == null) {
			return true;
		} else {
			return location.isNoLocation ();
		}
	}
	
	public void printlog () {
		location.printlog ();
	}
	
	public void setLocation (Location aLocation) {
		location = aLocation;
	}
	
	public void setLocation (int aLocation) {
		location = new Location (aLocation);
	}
}
