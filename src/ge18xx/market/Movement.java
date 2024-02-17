package ge18xx.market;

//
//  Movement.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

class Movement {
	public static final Movement NO_MOVEMENT = null;
	public static final AttributeName AN_ROW_ADJUST = new AttributeName ("rowAdjust");
	public static final AttributeName AN_COL_ADJUST = new AttributeName ("colAdjust");
	int rowAdjust;
	int colAdjust;

	public Movement () {
		this (0, 0);
	}

	public Movement (int aRowAdjust, int aColAdjust) {
		setValues (aRowAdjust, aColAdjust);
	}

	public Movement (XMLNode aChildNode) {
		int tRowAdjust;
		int tColAdjust;
		
		tRowAdjust = aChildNode.getThisIntAttribute (AN_ROW_ADJUST);
		tColAdjust = aChildNode.getThisIntAttribute (AN_COL_ADJUST);
		setValues (tRowAdjust, tColAdjust);
	}

	public XMLElement createElement (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (aElementName);
		tXMLElement.setAttribute (AN_ROW_ADJUST, rowAdjust);
		tXMLElement.setAttribute (AN_COL_ADJUST, colAdjust);

		return tXMLElement;
	}

	public void setValues (int aRowAdjust, int aColAdjust) {
		rowAdjust = aRowAdjust;
		colAdjust = aColAdjust;
	}

	public boolean equals (Movement aMovement) {
		if ((aMovement.getRowAdjustment () == rowAdjust) && (aMovement.getColAdjustment () == colAdjust)) {
			return true;
		} else {
			return false;
		}
	}

	public int getColAdjustment () {
		return colAdjust;
	}

	public int getRowAdjustment () {
		return rowAdjust;
	}

	public int getMoveNeighbor () {
		int neighbor;

		neighbor = MarketCell.NEIGHBOR_NONE;
		if ((rowAdjust == 1) && (colAdjust == 0)) {
			neighbor = MarketCell.NEIGHBOR_DOWN;
		}
		if ((rowAdjust == 0) && (colAdjust == 1)) {
			neighbor = MarketCell.NEIGHBOR_RIGHT;
		}
		if ((rowAdjust == 0) && (colAdjust == -1)) {
			neighbor = MarketCell.NEIGHBOR_LEFT;
		}
		if ((rowAdjust == -1) && (colAdjust == 0)) {
			neighbor = MarketCell.NEIGHBOR_UP;
		}
		if ((rowAdjust == 1) && (colAdjust == 1)) {
			neighbor = MarketCell.NEIGHBOR_DOWN_RIGHT;
		}

		return neighbor;
	}
}