package ge18xx.company.benefit;

import javax.swing.JLabel;

import geUtilities.GUI;
import geUtilities.xml.XMLNode;

public class RiverDiscountBenefit extends Benefit {
	public static final String NAME = "River Discount";
	
	public RiverDiscountBenefit () {
		setName (NAME);
	}

	public RiverDiscountBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
	}

	@Override
	public int getCost () {
		return 0;
	}

	@Override
	public String getNewButtonLabel () {
		return GUI.EMPTY_STRING;
	}
	
	@Override
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		
		tBenefitLabel = GUI.NO_LABEL;
		
		return tBenefitLabel;
	}
}
