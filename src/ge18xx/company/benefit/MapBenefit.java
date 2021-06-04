package ge18xx.company.benefit;

import ge18xx.company.PrivateCompany;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class MapBenefit extends Benefit {
	final static AttributeName AN_MAPCELL = new AttributeName ("mapCell");
	final static AttributeName AN_COST = new AttributeName ("cost");
	final static AttributeName AN_SAME_TURN = new AttributeName ("sameTurn");
	String mapCellID;
	int cost;
	boolean sameTurn;
	
	public MapBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		String tMapCellID;
		int tCost;
		boolean tSameTurn;
		
		tMapCellID = aXMLNode.getThisAttribute (AN_MAPCELL);
		tCost = aXMLNode.getThisIntAttribute (AN_COST);
		tSameTurn = aXMLNode.getThisBooleanAttribute (AN_SAME_TURN);
		setMapCellID (tMapCellID);
		setCost (tCost);
		setSameTurn (tSameTurn);
	}
	
	public void setSameTurn (boolean aSameTurn) {
		sameTurn = aSameTurn;
	}
	
	public void setMapCellID (String aMapCellID) {
		mapCellID = aMapCellID;
	}

	public void setCost (int aCost) {
		cost = aCost;
	}
	
	public String getMapCellID () {
		return mapCellID;
	}
	
	public int getCost () {
		return cost;
	}
	
	public boolean getSameTurn () {
		return sameTurn;
	}

	public String getNewButtonLabel(PrivateCompany aPrivateCompany) {
		return null;
	}
}
