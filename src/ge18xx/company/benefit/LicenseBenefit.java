package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.CorporationFrame;
import ge18xx.company.License;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.effects.AddLicenseEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class LicenseBenefit extends Benefit {
	public final static String NAME = "License";
	final static AttributeName AN_LICENSE_COST = new AttributeName ("licenseCost");
	final static AttributeName AN_MAP_CELL = new AttributeName ("mapCell");
	final static AttributeName AN_LICENSE_VALUE = new AttributeName ("value");
	int licenseCost;
	String mapCellIDs;
	int value;

	public LicenseBenefit (XMLNode aXMLNode) {
		super (aXMLNode);

		String tMapCellIDs;
		int tLicenseValue;
		int tLicenseCost;

		tLicenseCost = aXMLNode.getThisIntAttribute (AN_LICENSE_COST);
		tMapCellIDs = aXMLNode.getThisAttribute (AN_MAP_CELL);
		tLicenseValue = aXMLNode.getThisIntAttribute (AN_LICENSE_VALUE);
		setLicenseCost (tLicenseCost);
		setMapCellIDs (tMapCellIDs);
		setLicenseValue (tLicenseValue);

		setName (NAME);
	}

	public void setLicenseCost (int aLicenseCost) {
		licenseCost = aLicenseCost;
	}

	public void setMapCellIDs (String aMapCellIDs) {
		mapCellIDs = aMapCellIDs;
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
		ShareCompany tOwningShareCompany;
		PortfolioHolderI tOwner;
		boolean tAddButton;
		String tLicenseName;
		
		super.configure (aPrivateCompany, aButtonRow);
		tAddButton = true;
		if (shouldConfigure ()) {
			tOwner = aPrivateCompany.getOwner ();
			if (tOwner.isAShareCompany ()) {
				tOwningShareCompany = (ShareCompany) tOwner;
				tLicenseName = buildLicenseName ();
				if (tOwningShareCompany.hasLicense (tLicenseName)) {
					tAddButton = false;
				}
			}
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

	private void handleBuyLicense () {
		System.out.println ("Ready to Buy License for " + privateCompany.getAbbrev ());
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
			tOwningCompany = getOwningCompany ();
			tBenefitInUse = tOwningCompany.getBenefitInUse ();
			tBenefitInUseName = tBenefitInUse.getName ();
			if ((tBenefitInUse.isRealBenefit ()) && (!NAME.equals (tBenefitInUseName))) {
				disableButton ();
				setToolTip ("Another Benefit is currently in Use");
			} else {
				enableButton ();
				setToolTip ("All Good");
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
		tAddLicenseEffect = new AddLicenseEffect (tBank, aOwningCompany, 0, aLicense);
		addAdditionalEffect (tAddLicenseEffect);
	}

	public String buildLicenseName () {
		String tLicenseName;
		
		tLicenseName = privateCompany.getAbbrev () + " License";
	
		return tLicenseName;
	}
}
