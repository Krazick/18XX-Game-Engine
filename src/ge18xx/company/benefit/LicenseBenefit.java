package ge18xx.company.benefit;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class LicenseBenefit extends Benefit {
	public final static String NAME = "License";
	final static AttributeName AN_LICENSE = new AttributeName ("license");
	final static AttributeName AN_LICENSE_COST = new AttributeName ("licenseCost");
	final static AttributeName AN_MAP_CELL = new AttributeName ("mapCell");
	final static AttributeName AN_LICENSE_VALUE = new AttributeName ("value");
	boolean license;
	int licenseCost;
	String mapCellIDs;
	int value;

	public LicenseBenefit (XMLNode aXMLNode) {
		super (aXMLNode);

		String tMapCellIDs;
		boolean tLicense;
		int tLicenseValue;
		int tLicenseCost;

		tLicense = aXMLNode.getThisBooleanAttribute (AN_LICENSE);
		tLicenseCost = aXMLNode.getThisIntAttribute (AN_LICENSE_COST);
		tMapCellIDs = aXMLNode.getThisAttribute (AN_MAP_CELL);
		tLicenseValue = aXMLNode.getThisIntAttribute (AN_LICENSE_VALUE);
		setLicense (tLicense);
		setLicenseCost (tLicenseCost);
		setMapCellIDs (tMapCellIDs);
		setLicenseValue (tLicenseValue);

		setName (NAME);
	}

	public void setLicense (boolean aLicense) {
		license = aLicense;
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
	public boolean realBenefit () {
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

		super.configure (aPrivateCompany, aButtonRow);
		if (shouldConfigure ()) {
			if (!hasButton ()) {
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

		tNewButtonText = "Buy License from " + privateCompany.getAbbrev ();

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
		if ((tBenefitInUse.realBenefit ()) && (!NAME.equals (tBenefitInUseName))) {
			disableButton ();
			setToolTip ("Another Benefit is currently in Use");
		} else {
			enableButton ();
			setToolTip ("All Good");
		}
	}

}
