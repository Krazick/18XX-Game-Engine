package ge18xx.company.benefit;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class TilePlacementBenefit extends MapBenefit {
	boolean extraTilePlacement;
	final static AttributeName AN_EXTRA = new AttributeName ("extra");

	public TilePlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		boolean tExtraTilePlacement;
		
		tExtraTilePlacement = aXMLNode.getThisBooleanAttribute (AN_EXTRA);
		setExtraTilePlacement (tExtraTilePlacement);
	}
	
	private void setExtraTilePlacement (boolean aExtraTilePlacement) {
		extraTilePlacement = aExtraTilePlacement;
	}
}
