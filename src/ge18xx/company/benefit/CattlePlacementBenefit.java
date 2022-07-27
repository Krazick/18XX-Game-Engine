package ge18xx.company.benefit;

import ge18xx.utilities.XMLNode;

public class CattlePlacementBenefit extends Benefit {

	public CattlePlacementBenefit () {
		// TODO Auto-generated constructor stub
	}

	public CattlePlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCost () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getNewButtonLabel () {
		return "Place Cattle Token";
	}
}
