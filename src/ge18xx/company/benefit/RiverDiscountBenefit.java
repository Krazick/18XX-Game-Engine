package ge18xx.company.benefit;

import javax.swing.JLabel;

import geUtilities.GUI;
import geUtilities.XMLNode;

public class RiverDiscountBenefit extends Benefit {

	public RiverDiscountBenefit () {
		// TODO Auto-generated constructor stub
	}

	public RiverDiscountBenefit (XMLNode aXMLNode) {
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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		
		tBenefitLabel = GUI.NO_LABEL;
		
		return tBenefitLabel;
	}
}
