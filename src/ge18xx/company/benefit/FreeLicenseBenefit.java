package ge18xx.company.benefit;

import java.util.Arrays;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.License;
import ge18xx.company.ShareCompany;
import ge18xx.map.Location;
import ge18xx.round.action.Action;
import ge18xx.round.action.effects.AddLicenseEffect;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLNode;

public class FreeLicenseBenefit extends PassiveEffectBenefit {
	public final static String NAME = "Free License";
	int corporationID;
	int licenseCost;
	String [] mapCellIDs;
	License.LicenseTypes licenseType;
	int value;
	AddLicenseEffect addLicenseEffect;

	public FreeLicenseBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		String tMapCellIDs;
		String tTokenType;
		int tCorporationID;
		int tLicenseValue;
		int tLicenseCost;
		License.LicenseTypes tLicenseType;

		tCorporationID = aXMLNode.getThisIntAttribute (Corporation.AN_ID);
		tLicenseCost = aXMLNode.getThisIntAttribute (LicenseBenefit.AN_LICENSE_COST);
		tMapCellIDs = aXMLNode.getThisAttribute (LicenseBenefit.AN_MAP_CELL);
		tLicenseValue = aXMLNode.getThisIntAttribute (LicenseBenefit.AN_LICENSE_VALUE);
		tTokenType = aXMLNode.getThisAttribute (MapBenefit.AN_TOKEN_TYPE);
		tLicenseType = License.getTypeFromName (tTokenType);
		
		setCorporationID (tCorporationID);
		setLicenseCost (tLicenseCost);
		setMapCellIDs (tMapCellIDs);
		setLicenseValue (tLicenseValue);
		setLicenseType (tLicenseType);

		setName (NAME);
	}

	public void setLicenseType (License.LicenseTypes aLicenseType) {
		licenseType = aLicenseType;
	}
	
	public void setCorporationID (int aCorporationID) {
		corporationID = aCorporationID;
	}

	public void setLicenseCost (int aLicenseCost) {
		licenseCost = aLicenseCost;
	}

	public void setMapCellIDs (String aMapCellIDs) {
		mapCellIDs = aMapCellIDs.split (",");
	}

	public int getMapCellIDCount () {
		return mapCellIDs.length;
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

	public String getMapCellID (int aIndex) {
		String tMapCellID;
		String tMapCellIDAndLocation;
		String [] tItems;
		
		tMapCellID = GUI.NULL_STRING;
		if (aIndex < mapCellIDs.length) {
			tMapCellIDAndLocation = mapCellIDs [aIndex];
			tItems = tMapCellIDAndLocation.split (":");
			tMapCellID = tItems [0];
		}
		
		return tMapCellID;
	}

	public int getLocationInt (int aIndex) {
		int tLocationInt;
		String tLocation;
		String tMapCellIDAndLocation;
		String [] tItems;
		
		tLocation = GUI.NULL_STRING;
		tLocationInt = Location.NO_LOCATION;
		if (aIndex < mapCellIDs.length) {
			tMapCellIDAndLocation = mapCellIDs [aIndex];
			tItems = tMapCellIDAndLocation.split (":");
			tLocation = tItems [1];
			tLocationInt = Integer.parseInt (tLocation);
		}
		
		return tLocationInt;
	}
	
	public String [] getMapCellIDs () {
		return mapCellIDs;
	}
	
	public String getAllMapCellIDs () {
		String tAllMapCellIDs;
		
		tAllMapCellIDs = Arrays.toString (mapCellIDs);
		
		return tAllMapCellIDs;
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
		tLicense = new License (tLicenseName, licenseCost, value);
		tLicense.setMapCellIDs (getAllMapCellIDs ());
		tLicense.setType (licenseType);
		
		return tLicense;
	}
	
	@Override
	public void handlePassive (ShareCompany aShareCompany, Action aAction) {
		License tLicense;
		
		tLicense = getLicense ();
		addLicense (aShareCompany, tLicense);
		setUsed (true);
		aAction.addEffect (addLicenseEffect);
	}

	@Override
	public void addLicense (ShareCompany aOwningCompany, License aLicense) {
		Bank tBank;
		
		aOwningCompany.addLicense (aLicense);
		tBank = aOwningCompany.getBank ();
		addLicenseEffect = new AddLicenseEffect (tBank, aOwningCompany, 0, aLicense);
	}
}
