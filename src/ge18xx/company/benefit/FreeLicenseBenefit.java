package ge18xx.company.benefit;

import ge18xx.company.Corporation;
import ge18xx.company.License;
import ge18xx.company.ShareCompany;
import ge18xx.round.action.effects.Effect;
import ge18xx.utilities.XMLNode;

public class FreeLicenseBenefit extends Benefit {
	public final static String NAME = "Free License";
	int corporationID;
	int licenseCost;
	String mapCellIDs;
	int value;

	public FreeLicenseBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		String tMapCellIDs;
		int tCorporationID;
		int tLicenseValue;
		int tLicenseCost;

		tCorporationID = aXMLNode.getThisIntAttribute (Corporation.AN_ID);
		tLicenseCost = aXMLNode.getThisIntAttribute (LicenseBenefit.AN_LICENSE_COST);
		tMapCellIDs = aXMLNode.getThisAttribute (LicenseBenefit.AN_MAP_CELL);
		tLicenseValue = aXMLNode.getThisIntAttribute (LicenseBenefit.AN_LICENSE_VALUE);
		setCorporationID (tCorporationID);
		setLicenseCost (tLicenseCost);
		setMapCellIDs (tMapCellIDs);
		setLicenseValue (tLicenseValue);

		setName (NAME);
	}

	public void setCorporationID (int aCorporationID) {
		corporationID = aCorporationID;
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

	public int getCorporationID () {
		return corporationID;
	}
	
	public int getLicenseCost () {
		return licenseCost;
	}

	public int getLicenseValue () {
		return value;
	}

	@Override
	public int getCost () {
		return 0;
	}

	@Override
	public String getNewButtonLabel () {
		return null;
	}
	
	public License getLicense () {
		License tLicense;
		String tLicenseName;
		
		tLicenseName = buildLicenseName ();
		tLicense = new License (tLicenseName, value);
		tLicense.setMapCellIDs (mapCellIDs);
		
		return tLicense;
	}
	
	@Override
	public Effect handlePassive (ShareCompany aShareCompany) {
		License tLicense;
		
		tLicense = getLicense ();
		addLicense (aShareCompany, tLicense);
		setUsed (true);
		
		return addLicenseEffect;
	}
}
