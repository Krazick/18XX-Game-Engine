package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.map.MapCell;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class CattlePlacementBenefit extends MapBenefit {
	final static AttributeName AN_TOKEN_TYPE = new AttributeName ("tokenType");
	final static AttributeName AN_TOKEN_PLACEMENT = new AttributeName ("tokenPlacement");
	final static AttributeName AN_TOKEN_BONUS = new AttributeName ("tokenBonus");
	public final static String NAME = "Cattle Placement";
	String tokenType;
	boolean tokenPlacement;
	int tokenBonus;

	public CattlePlacementBenefit (XMLNode aXMLNode) {
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

	@Override
	public void setTokenType (String aTokenType) {
		tokenType = aTokenType;
	}

	public void setTokenPlacement (boolean aTokenPlacement) {
		tokenPlacement = aTokenPlacement;
	}

	public void setTokenBonus (int aTokenBonus) {
		tokenBonus = aTokenBonus;
	}

	@Override
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
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;

		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.PLACE_CATTLE_TOKEN.equals (tActionCommand)) {
			handlePlaceCattleToken ();
		}
	}
	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		JButton tPlaceCattleTokenButton;

		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			if (!hasButton ()) {
				tPlaceCattleTokenButton = new JButton (getNewButtonLabel ());
				setButton (tPlaceCattleTokenButton);
				setButtonPanel (aButtonRow);
				tPlaceCattleTokenButton.setActionCommand (CorporationFrame.PLACE_CATTLE_TOKEN);
				tPlaceCattleTokenButton.addActionListener (this);
				aButtonRow.add (tPlaceCattleTokenButton);
			}
			updateButton ();
		}
	}

	private void handlePlaceCattleToken () {
		MapCell tSelectedMapCell;
		boolean tCanHoldCattleToken;
		ShareCompany tOwningCompany;

		tOwningCompany = (ShareCompany) getOwningCompany ();
		capturePreviousBenefitInUse (tOwningCompany, this);

		tSelectedMapCell = getSelectedMapCell ();
		if (tSelectedMapCell != MapCell.NO_MAP_CELL) {
			tCanHoldCattleToken = tSelectedMapCell.canHoldCattleToken ();
			if (tCanHoldCattleToken) {
				setMapCellID (tSelectedMapCell);
				placeBenefitToken (tSelectedMapCell, tokenType, this, tokenBonus);
			}
		}
	}

	@Override
	public String getNewButtonLabel () {
		String tNewButtonText;

		tNewButtonText = "Place " + tokenType + " Token on Map";

		return tNewButtonText;
	}

	@Override
	public void updateButton () {
		TrainCompany tOwningCompany;
		Benefit tBenefitInUse;
		String tBenefitInUseName;
		MapCell tSelectedMapCell;
		boolean tCanHoldPortToken;

		tOwningCompany = getOwningCompany ();
		tBenefitInUse = tOwningCompany.getBenefitInUse ();
		tBenefitInUseName = tBenefitInUse.getName ();
		tSelectedMapCell = getSelectedMapCell ();
		if (tSelectedMapCell != MapCell.NO_MAP_CELL) {
			tCanHoldPortToken = tSelectedMapCell.canHoldPortToken ();
			if (tCanHoldPortToken) {
				if ((tBenefitInUse.isRealBenefit ()) && (!NAME.equals (tBenefitInUseName))) {
					disableButton ();
					setToolTip ("Another Benefit is currently in Use");
				} else {
					enableButton ();
					setToolTip ("Ready for " + tokenType + " Token Placement");
				}
			} else {
				disableButton ();
				setToolTip ("The Selected Map Cell cannot hold a " + tokenType + " Token");				
			}
		} else {
			disableButton ();
			setToolTip ("No Selected Map Cell");
		}
	}
	
	@Override
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		String tBenefitText;
		
		tBenefitText = "Cattle Token Placement on " + getMapCellID ();		
		tBenefitLabel = new JLabel (tBenefitText);
		
		return tBenefitLabel;
	}
}
