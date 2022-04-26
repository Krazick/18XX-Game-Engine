package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class PortPlacementBenefit extends MapBenefit {
	final static AttributeName AN_TOKEN_TYPE = new AttributeName ("tokenType");
	final static AttributeName AN_TOKEN_PLACEMENT = new AttributeName ("tokenPlacement");
	final static AttributeName AN_TOKEN_BONUS = new AttributeName ("tokenBonus");
	public final static String NAME = "Port Placement";
	String tokenType;
	boolean tokenPlacement;
	int tokenBonus;
	
	public PortPlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		String tTokenType;
		boolean tTokenPlacement;
		int tTokenBonus;
		
		tTokenType = aXMLNode.getThisAttribute (AN_TOKEN_TYPE);
		tTokenPlacement = aXMLNode.getThisBooleanAttribute (AN_TOKEN_PLACEMENT);
		tTokenBonus = aXMLNode.getThisIntAttribute (AN_TOKEN_BONUS);
		setTokenType (tTokenType);
		setTokenPlacement (tTokenPlacement);
		setTokenBonus (tTokenBonus);
		setName (NAME);
	}

	public void setTokenType (String aTokenType) {
		tokenType = aTokenType;
	}
	
	public void setTokenPlacement (boolean aTokenPlacement) {
		tokenPlacement = aTokenPlacement;
	}
	
	public void setTokenBonus (int aTokenBonus) {
		tokenBonus = aTokenBonus;
	}
	
	public String getTokenType () {
		return tokenType;
	}
	
	public boolean getTokenPlacement () {
		return tokenPlacement;
	}
	
	public int getTokenBonus () {
		return tokenBonus;
	}

	@Override
	public boolean realBenefit () {
		return true;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;

		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.PLACE_TOKEN_PRIVATE.equals (tActionCommand)) {
			handlePlacePortToken  ();
		}
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		JButton tPlacePortTokenButton;

		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			if (! hasButton ()) {
				tPlacePortTokenButton = new JButton (getNewButtonLabel ());
				setButton (tPlacePortTokenButton);
				setButtonPanel (aButtonRow);
				tPlacePortTokenButton.setActionCommand (CorporationFrame.PLACE_TOKEN_PRIVATE);
				tPlacePortTokenButton.addActionListener (this);
				aButtonRow.add (tPlacePortTokenButton);
			}
			updateButton ();
		}
	}

	private void handlePlacePortToken () {
		System.out.println ("Ready to place Port Token");
	}
	
	@Override
	public String getNewButtonLabel () {
		String tNewButtonText;

		tNewButtonText = "Place Port Token on Map";

		return tNewButtonText;
	}
	
	@Override
	public void updateButton () {
		ShareCompany tOwningCompany;
		Benefit tBenefitInUse;
		String tBenefitInUseName;

		tOwningCompany = getOwningCompany ();
		tBenefitInUse = tOwningCompany.getBenefitInUse ();
		tBenefitInUseName = tBenefitInUse.getName ();
		if ((tBenefitInUse.realBenefit ()) && (! NAME.equals (tBenefitInUseName))) {
			disableButton ();
			setToolTip ("Another Benefit is currently in Use");
		} else if (! hasTile ()) {
			disableButton ();
			setToolTip ("No Tile on the MapCell, can't place Port Token");
		} else {
			enableButton ();
			setToolTip ("All Good");
		}
	}

}
