package ge18xx.company.benefit;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationFrame;
import ge18xx.company.License;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyLicenseAction;
import ge18xx.round.action.effects.AddLicenseEffect;
import ge18xx.round.action.effects.CashTransferEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLNode;

public class LicenseBenefit extends Benefit {
	public final static String NAME = "License";
	final static AttributeName AN_LICENSE_COST = new AttributeName ("licenseCost");
	final static AttributeName AN_MAP_CELL = new AttributeName ("mapCell");
	final static AttributeName AN_LICENSE_VALUE = new AttributeName ("value");
	int licenseCost;
	String [] mapCellIDs;
	int value;
	License.LicenseTypes licenseType;

	public LicenseBenefit (XMLNode aXMLNode) {
		super (aXMLNode);

		String tMapCellIDs;
		String tTokenType;
		int tLicenseValue;
		int tLicenseCost;
		License.LicenseTypes tLicenseType;

		tLicenseCost = aXMLNode.getThisIntAttribute (AN_LICENSE_COST);
		tMapCellIDs = aXMLNode.getThisAttribute (AN_MAP_CELL, GUI.EMPTY_STRING);
		tLicenseValue = aXMLNode.getThisIntAttribute (AN_LICENSE_VALUE);
		tTokenType = aXMLNode.getThisAttribute (MapBenefit.AN_TOKEN_TYPE);
		tLicenseType = License.getTypeFromName (tTokenType);
		setLicenseCost (tLicenseCost);
		setMapCellIDs (tMapCellIDs);
		setLicenseValue (tLicenseValue);
		setLicenseType (tLicenseType);

		setName (NAME);
	}

	public void setLicenseType (License.LicenseTypes aLicenseType) {
		licenseType = aLicenseType;
	}

	public void setLicenseCost (int aLicenseCost) {
		licenseCost = aLicenseCost;
	}

	public void setMapCellIDs (String aMapCellIDs) {
//		mapCellIDs = aMapCellIDs;
		mapCellIDs = aMapCellIDs.split (",");
	}

	public void setLicenseValue (int aLicenseValue) {
		value = aLicenseValue;
	}

	public int getLicenseCost () {
		return licenseCost;
	}

	public int getLicenseValue () {
		return value;
	}

	@Override
	public int getCost () {
		return licenseCost;
	}

	@Override
	public boolean isRealBenefit () {
		return true;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;

		tActionCommand = aEvent.getActionCommand ();
		if (CorporationFrame.BUY_LICENSE.equals (tActionCommand)) {
			handleBuyLicense ();
		}
	}

	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		JButton tBuyLicenseButton;
		Corporation tCompany;
		boolean tAddButton;
		
		super.configure (aPrivateCompany, aButtonRow);
		tAddButton = true;
		if (shouldConfigure ()) {
			if (allActors) {
				tCompany = getOperatingCompany ();
			} else {
				tCompany = aPrivateCompany.getOwningCompany ();
			}
			tAddButton = ! companyHasLicense (tCompany);
			if (!hasButton () && tAddButton) {
				tBuyLicenseButton = new JButton (getNewButtonLabel ());
				setButton (tBuyLicenseButton);
				setButtonPanel (aButtonRow);
				tBuyLicenseButton.setActionCommand (CorporationFrame.BUY_LICENSE);
				tBuyLicenseButton.addActionListener (this);
				aButtonRow.add (tBuyLicenseButton);
			}
			updateButton ();
		}
	}

	private boolean companyHasLicense (Corporation aCompany) {
		ShareCompany tShareCompany;
		boolean tCompanyHasLicense;
		String tLicenseName;
		
		tCompanyHasLicense = false;
		if (aCompany != Corporation.NO_CORPORATION) {
			if (aCompany.isAShareCompany ()) {
				tShareCompany = (ShareCompany) aCompany;
				tLicenseName = buildLicenseName ();
				if (tShareCompany.hasLicense (tLicenseName)) {
					tCompanyHasLicense = true;
				}
			}
		}

		return tCompanyHasLicense;
	}
	
	private void handleBuyLicense () {
		License tLicense;
		ShareCompany tShareCompany;
		ShareCompany tOwningCompany;
		CashTransferEffect tCashTransferEffect;
		BuyLicenseAction tBuyLicenseAction;
		String tOperatingRoundID;
		GameManager tGameManager;
		
		tShareCompany = getOperatingCompany ();
		tOwningCompany =  (ShareCompany) getOwningCompany ();
		tLicense = getLicense ();
		addLicense (tOwningCompany, tShareCompany, tLicense);
		tCashTransferEffect = new CashTransferEffect (tShareCompany, tOwningCompany, licenseCost);
		tShareCompany.addCash (-licenseCost);
		tOwningCompany.addCash (licenseCost);
		addAdditionalEffect (tCashTransferEffect);
		tOperatingRoundID = tShareCompany.getOperatingRoundID ();
		tBuyLicenseAction = new BuyLicenseAction (ActorI.ActionStates.OperatingRound, 
										tOperatingRoundID, tShareCompany);
		addAdditionalEffects (tBuyLicenseAction);
		tGameManager = tShareCompany.getGameManager ();
		tGameManager.addAction (tBuyLicenseAction);
		tShareCompany.updateInfo ();
	}

	@Override
	public String getNewButtonLabel () {
		String tNewButtonText;
		String tOwnerAbbrev;

		tOwnerAbbrev = privateCompany.getOwnerName ();
		tNewButtonText = "Buy " + privateCompany.getAbbrev () + " License from " + tOwnerAbbrev;

		return tNewButtonText;
	}

	@Override
	public void updateButton () {
		ShareCompany tOwningCompany;
		Benefit tBenefitInUse;
		String tBenefitInUseName;

		if (buttonConfigured ()) {
			tOwningCompany = getOperatingCompany ();
			if (tOwningCompany != ShareCompany.NO_SHARE_COMPANY) {
				tBenefitInUse = tOwningCompany.getBenefitInUse ();
				tBenefitInUseName = tBenefitInUse.getName ();
				if ((tBenefitInUse.isRealBenefit ()) && (!NAME.equals (tBenefitInUseName))) {
					disableButton ();
					setToolTip ("Another Benefit is currently in Use");
				} else if (tOwningCompany.getTreasury () < licenseCost) {
					disableButton ();
					setToolTip ("Company does not have the license cost of " + 
								Bank.formatCash (licenseCost) + " available.");
				} else {
					enableButton ();
					setToolTip ("All Good");
				}
			} else {
				disableButton ();
				setToolTip ("No Owning Company Found");				
			}
		}
	}

	public Effect handlePassive (ShareCompany aShareCompany) {
		return Effect.NO_EFFECT;
	}

	public void addLicense (ShareCompany aOwningCompany, License aLicense) {
		Bank tBank;
		AddLicenseEffect tAddLicenseEffect;

		aOwningCompany.addLicense (aLicense);
		tBank = aOwningCompany.getBank ();
		tAddLicenseEffect = new AddLicenseEffect (tBank, aOwningCompany, aLicense);
		addAdditionalEffect (tAddLicenseEffect);
	}

	public void addLicense (ShareCompany aOwningCompany, ShareCompany aBuyingCompany, License aLicense) {
		AddLicenseEffect tAddLicenseEffect;

		aBuyingCompany.addLicense (aLicense);
		tAddLicenseEffect = new AddLicenseEffect (aOwningCompany, aBuyingCompany, aLicense);
		addAdditionalEffect (tAddLicenseEffect);
	}

	public String buildLicenseName (License.LicenseTypes aType) {
		String tLicenseName;
		
		tLicenseName = privateCompany.getAbbrev () + " " + aType.toString ();
	
		return tLicenseName;
	}

	public String buildLicenseName () {
		String tLicenseName;
		
		tLicenseName = buildLicenseName (licenseType);
	
		return tLicenseName;
	}
	
	public License getLicense () {
		License tLicense;
		String tLicenseName;
		
		tLicenseName = buildLicenseName (licenseType);
		tLicense = new License (tLicenseName, licenseCost, value);
		tLicense.setMapCellIDs (getAllMapCellIDs ());
		tLicense.setTypeFromName (licenseType.toString ());
		
		return tLicense;
	}
	
	public String getAllMapCellIDs () {
		String tAllMapCellIDs;
		
		tAllMapCellIDs = Arrays.toString (mapCellIDs);
		
		return tAllMapCellIDs;
	}
	
	@Override
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		
		tBenefitLabel = GUI.NO_LABEL;
		
		return tBenefitLabel;
	}
}
