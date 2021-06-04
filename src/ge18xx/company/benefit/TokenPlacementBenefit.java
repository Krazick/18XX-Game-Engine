package ge18xx.company.benefit;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class TokenPlacementBenefit extends MapBenefit {
	boolean extraTokenPlacement;
	final static AttributeName AN_EXTRA = new AttributeName ("extra");

	public TokenPlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		boolean tExtraTokenPlacement;
		
		tExtraTokenPlacement = aXMLNode.getThisBooleanAttribute (AN_EXTRA);
		setExtraTokenPlacement (tExtraTokenPlacement);
	}
	
	private void setExtraTokenPlacement (boolean aExtraTokenPlacement) {
		extraTokenPlacement = aExtraTokenPlacement;
	}

}
