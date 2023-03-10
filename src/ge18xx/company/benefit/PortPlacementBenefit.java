package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.CorporationFrame;
import ge18xx.company.License;
import ge18xx.company.PortLicense;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.LayBenefitTokenAction;
import ge18xx.round.action.effects.AddLicenseEffect;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class PortPlacementBenefit extends MapBenefit {
	final static AttributeName AN_TOKEN_PLACEMENT = new AttributeName ("tokenPlacement");
	final static AttributeName AN_TOKEN_BONUS = new AttributeName ("tokenBonus");
	public final static String NAME = "Port Placement";
	boolean tokenPlacement;
	int tokenBonus;
	AddLicenseEffect addLicenseEffect;

	public PortPlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);

		boolean tTokenPlacement;
		int tTokenBonus;

		tTokenPlacement = aXMLNode.getThisBooleanAttribute (AN_TOKEN_PLACEMENT);
		tTokenBonus = aXMLNode.getThisIntAttribute (AN_TOKEN_BONUS);
		setTokenPlacement (tTokenPlacement);
		setTokenBonus (tTokenBonus);
		setName (NAME);
	}

	public void setTokenPlacement (boolean aTokenPlacement) {
		tokenPlacement = aTokenPlacement;
	}

	public void setTokenBonus (int aTokenBonus) {
		tokenBonus = aTokenBonus;
	}

	public boolean getTokenPlacement () {
		return tokenPlacement;
	}

	public int getTokenBonus () {
		return tokenBonus;
	}

	@Override
	public boolean isRealBenefit () {
		return true;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;

		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.PLACE_PORT_TOKEN.equals (tActionCommand)) {
			handlePlacePortToken ();
		}
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		JButton tPlacePortTokenButton;

		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			if (!hasButton ()) {
				tPlacePortTokenButton = new JButton (getNewButtonLabel ());
				setButton (tPlacePortTokenButton);
				setButtonPanel (aButtonRow);
				tPlacePortTokenButton.setActionCommand (CorporationFrame.PLACE_PORT_TOKEN);
				tPlacePortTokenButton.addActionListener (this);
				aButtonRow.add (tPlacePortTokenButton);
			}
			updateButton ();
		}
	}

	private void handlePlacePortToken () {
		MapCell tSelectedMapCell;
		boolean tCanHoldPortToken;
		ShareCompany tOwningCompany;
		PortLicense tPortLicense;
		String tLicenseName;
		LayBenefitTokenAction tLayBenefitTokenAction;
		GameManager tGameManager;
		
		tOwningCompany = getOwningCompany ();
		capturePreviousBenefitInUse (tOwningCompany, this);

		tSelectedMapCell = getSelectedMapCell ();
		if (tSelectedMapCell != MapCell.NO_MAP_CELL) {
			tCanHoldPortToken = tSelectedMapCell.canHoldPortToken ();
			if (tCanHoldPortToken) {
				setMapCellID (tSelectedMapCell);
				tLicenseName = buildLicenseName ();
				tPortLicense = new PortLicense (tLicenseName, getTokenBonus ());
				tPortLicense.setMapCellIDs (mapCellID);
				addLicense (tOwningCompany, tPortLicense);
				tLayBenefitTokenAction = placeBenefitToken (tSelectedMapCell, tokenType, this, tokenBonus);
				tLayBenefitTokenAction.addAddLicenseEffect (addLicenseEffect);
				tGameManager = tOwningCompany.getGameManager ();
				tGameManager.addAction (tLayBenefitTokenAction);
			}
		}
	}

	public String buildLicenseName () {
		String tLicenseName;
		
		tLicenseName = privateCompany.getAbbrev () + " Port";
	
		return tLicenseName;
	}

	public void addLicense (ShareCompany aOwningCompany, License aLicense) {
		Bank tBank;
		
		aOwningCompany.addLicense (aLicense);
		tBank = aOwningCompany.getBank ();
		addLicenseEffect = new AddLicenseEffect (tBank, aOwningCompany, 0, aLicense);
	}

	@Override
	public String getNewButtonLabel () {
		String tNewButtonText;

		tNewButtonText = "Place " + tokenType + " Token on Map";

		return tNewButtonText;
	}

	@Override
	public void updateButton () {
		ShareCompany tOwningCompany;
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
}
