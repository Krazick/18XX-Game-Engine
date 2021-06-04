package ge18xx.company.benefit;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
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
	
	@Override
	public String getNewButtonLabel (PrivateCompany aPrivateCompany) {
		String tNewButtonText;
		
		tNewButtonText = "Place Token on " + aPrivateCompany.getAbbrev () + " Home";
		
		return tNewButtonText;
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		JButton tPlaceTokenButton;
		
		if (shouldConfigure (aPrivateCompany)) {
			System.out.println ("Should Configure for Token Placement");
			tPlaceTokenButton = new JButton (getNewButtonLabel (aPrivateCompany));
			tPlaceTokenButton.setActionCommand (CorporationFrame.PLACE_TOKEN_PRIVATE);
			aButtonRow.add (tPlaceTokenButton);
		}
	}
}
