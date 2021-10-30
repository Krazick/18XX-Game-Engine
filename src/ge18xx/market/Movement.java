package ge18xx.market;

//
//  Movement.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

class Movement {
	public final static Movement NO_MOVEMENT = null;
	int rowAdjust, colAdjust;
	final static AttributeName AN_ROW_ADJUST = new AttributeName ("rowAdjust");
	final static AttributeName AN_COL_ADJUST = new AttributeName ("colAdjust");
	
	public Movement () {
		this (0, 0);
	}
	
	public Movement (int aRowAdjust, int aColAdjust) {
		setValues (aRowAdjust, aColAdjust);
	}
	
	public Movement (XMLNode aChildNode) {
		this (aChildNode.getThisIntAttribute (AN_ROW_ADJUST),
				aChildNode.getThisIntAttribute (AN_COL_ADJUST));
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
		if ((aMovement.getRowAdjustment () == rowAdjust) && 
			(aMovement.getColAdjustment () == colAdjust)) {
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