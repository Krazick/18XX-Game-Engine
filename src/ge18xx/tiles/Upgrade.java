package ge18xx.tiles;

//
//  Upgrade.java
//  Game_18XX
//
//  Created by Mark Smith on 3/23/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import ge18xx.map.Location;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLNode;

import org.w3c.dom.NodeList;

public class Upgrade {
	public static final ElementName EN_REVENUE_CENTER = new ElementName ("RevenueCenter");
	public static final ElementName EN_BASE_CITY_NAME = new ElementName ("BaseCityName");
	public static final AttributeName AN_TO_NUMBER = new AttributeName ("toNumber");
	public static final AttributeName AN_ROTATIONS = new AttributeName ("rotations");
	public static final AttributeName AN_ROTATION = new AttributeName ("rotation");
	public static final AttributeName AN_TO = new AttributeName ("to");
	public static final AttributeName AN_FROM = new AttributeName ("from");
	public static final AttributeName AN_VALUES = new AttributeName ("values");
	public static final Upgrade NO_UPGRADE = null;
	static final int ANY_ROTATION = -1;
	int toNumber;
	int toRotations [];
	int RCfrom [];
	int RCto [];
	int RCrotation [];
	String baseCityName;
	
	public Upgrade () {
	}
	
	public Upgrade (XMLNode aNode) {
		String [] tSplit = null;
		String tRotations;
		NodeList tChildren;
		XMLNode tChildNode;
		String tChildName;
		int tIndex;
		int tChildrenCount;
		int tUpgradeCount, tUpgradeIndex;
		
		toNumber = aNode.getThisIntAttribute (AN_TO_NUMBER);
		tRotations = aNode.getThisAttribute (AN_ROTATIONS);
		tSplit = tRotations.split(",");
		toRotations = new int [tSplit.length];
		for (tIndex = 0; tIndex < tSplit.length; tIndex++) {
			toRotations [tIndex] = Integer.parseInt (tSplit [tIndex]);
		}
		tChildren = aNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		if (tChildrenCount > 0) {
			tUpgradeCount = tChildrenCount/2;
			tUpgradeIndex = 0;
			RCfrom = new int [tUpgradeCount];
			RCto = new int [tUpgradeCount];
			RCrotation = new int [tUpgradeCount];
			for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
				tChildNode = new XMLNode (tChildren.item (tIndex));
				tChildName = tChildNode.getNodeName ();
				if (EN_REVENUE_CENTER.equals (tChildName)) {
					RCto [tUpgradeIndex] = tChildNode.getThisIntAttribute (AN_TO);
					RCfrom [tUpgradeIndex] = tChildNode.getThisIntAttribute (AN_FROM);
					RCrotation [tUpgradeIndex] = tChildNode.getThisIntAttribute (AN_ROTATION, ANY_ROTATION);
					tUpgradeIndex++;
				}
				if (EN_BASE_CITY_NAME.equals (tChildName)) {
					baseCityName = tChildNode.getThisAttribute (AN_VALUES);
				}
			}
		} else {
			RCto = null;
			RCfrom = null;
		}
	}
	
	public String getBaseCityName () {
		return baseCityName;
	}
	
	public Location getToFromLocation (Location aFromLocation, int aRotation) {
		int tFromLocation, tIndex;
		Location tToLocation;
		
		tToLocation = null;
		tFromLocation = aFromLocation.getLocation ();
		for (tIndex = 0; (tIndex < RCfrom.length) && (tToLocation == Location.NO_LOC); tIndex++) {
			if (RCfrom [tIndex] == tFromLocation) {
				tToLocation = new Location (RCto [tIndex]);
			}
		}
		
		return tToLocation;
	}
	
	public int getToFromIndex (int aFromLocation, int aRotation) {
		int tIndex, tToIndex;
		
		tToIndex = Location.CENTER_CITY_LOC;
		for (tIndex = 0; tIndex < RCfrom.length; tIndex++) {
			if ((RCfrom [tIndex] == aFromLocation) && (RCrotation [tIndex] == aRotation)) {
				tToIndex = RCto [tIndex];
			}
		}
		
		return tToIndex;
	}
	
	public int getFromIndex (int aIndex) {
		int tFrom = 0;
		
		if (RCfrom != null) {
			if (aIndex > RCfrom.length) {
				tFrom = RCfrom [aIndex];
			}
		}
		
		return tFrom;
	}
	
	public int getRotation (int aIndex) {
		int tRotation = 0;
		
		if (toRotations != null) {
			if (aIndex < toRotations.length) {
				tRotation = toRotations [aIndex];
			}
		}
		
		return tRotation;
	}
	
	public int getRotationCount () {
		return toRotations.length;
	}
	
	public int getTileNumber () {
		return toNumber;
	}
	
	public int getToIndex (int aIndex) {
		int tTo = 0;
		
		if (RCto != null) {
			if (aIndex > RCto.length) {
				tTo = RCto [aIndex];
			}
		}
		
		return tTo;
	}
}
