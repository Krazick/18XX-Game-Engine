package ge18xx.company;

import java.util.ArrayList;

import ge18xx.bank.Bank;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class License extends Coupon {
	public static final ElementName EN_LICENSES = new ElementName ("Licenses");
	public static final ElementName EN_LICENSE = new ElementName ("License");
	public static final AttributeName AN_LICENSE = new AttributeName ("license");
	public static final AttributeName AN_BENEFIT_VALUE = new AttributeName ("benefitValue");
	public static final AttributeName AN_LICENSE_NAME = new AttributeName ("licenseName");
	public static final AttributeName AN_LICENSE_PRICE = new AttributeName ("price");
	public static final AttributeName AN_MAP_CELL_IDS = new AttributeName ("mapCellIDs");
	public static final AttributeName AN_LICENSE_TYPE = new AttributeName ("type");
	public static final License NO_LICENSE = null;
	public static final ArrayList<License> NO_LICENSES = null;
	public static final int NO_VALUE = 0;
	public enum LicenseTypes { NO_TYPE, PORT, BRIDGE, TUNNEL, CATTLE };
	int benefitValue;
	boolean isPortLicense;
	boolean isBridgeLicense;
	boolean isTunnelLicense;
	boolean isCattleLicense;
	String mapCellIDs;
	LicenseTypes type;
	
	public License (String aName, int aBenefitValue) {
		this (aName, NO_VALUE, aBenefitValue);
	}
	
	public License (String aName, int aPrice, int aBenefitValue) {
		super (aName, aPrice);
		setBenefitValue (aBenefitValue);
		setIsPortLicense (false);
		setType (LicenseTypes.NO_TYPE);
		setMapCellIDs ("");
	}

	public License (XMLNode aXMLNode) {
		super (aXMLNode);
		int tBenefitValue;
		String tLicenseName;
		String tMapCellIDs;
		String tTypeName;
		boolean tIsPortLicense;
		
		tIsPortLicense = aXMLNode.getThisBooleanAttribute (PortLicense.AN_PORT_LICENSE);
		tTypeName = aXMLNode.getThisAttribute (AN_LICENSE_TYPE, "Port");
		tBenefitValue = aXMLNode.getThisIntAttribute (AN_BENEFIT_VALUE, NO_VALUE);
		tLicenseName = aXMLNode.getThisAttribute (AN_LICENSE_NAME);
		tMapCellIDs = aXMLNode.getThisAttribute (AN_MAP_CELL_IDS);
		setBenefitValue (tBenefitValue);
		setName (tLicenseName);
		setIsPortLicense (tIsPortLicense);
		setMapCellIDs (tMapCellIDs);
		setTypeFromName (tTypeName);
	}

	public void setTypeFromName (String aTypeName) {
		String tTypeName;
		LicenseTypes tFoundType;
		
		tFoundType = LicenseTypes.NO_TYPE;
		for (LicenseTypes tType : LicenseTypes.values ()) {
			tTypeName = tType.toString ();
			if (aTypeName.equals (tTypeName)) {
				tFoundType = tType;
			}
		}
		setType (tFoundType);
	}
	
	public void setType (LicenseTypes aType) {
		type = aType;
	}
	
	public void setIsPortLicense (boolean aIsPortLicense) {
		isPortLicense = aIsPortLicense;
		if (isPortLicense) {
			setType (LicenseTypes.PORT);
		}
	}
	
	public void setIsBridgeLicense (boolean aIsBridgeLicense) {
		isBridgeLicense = aIsBridgeLicense;
		if (isBridgeLicense) {
			setType (LicenseTypes.BRIDGE);
		}
	}
	
	public void setIsTunnelLicense (boolean aIsTunnelLicense) {
		isTunnelLicense = aIsTunnelLicense;
		if (isTunnelLicense) {
			setType (LicenseTypes.TUNNEL);
		}
	}
	
	public void setIsCattleLicense (boolean aIsCattleLicense) {
		isCattleLicense = aIsCattleLicense;
		if (isCattleLicense) {
			setType (LicenseTypes.CATTLE);
		}
	}
	
	public void setBenefitValue (int aBenefitValue) {
		benefitValue = aBenefitValue;
	}
	
	public void setMapCellIDs (String aMapCellIDs) {
		mapCellIDs = aMapCellIDs;
	}
	
	public int getBenefitValue () {
		return benefitValue;
	}
	
	public int getPortValue () {
		return NO_VALUE;
	}
	
	public String getMapCellIDs () {
		return mapCellIDs;
	}
	
	public boolean isLicenseOfType (LicenseTypes aType) {
		boolean tIsLicenseOfType;
		
		if (aType == type) {
			tIsLicenseOfType = true;
		} else {
			tIsLicenseOfType = false;
		}
		
		return tIsLicenseOfType;
	}
	
	public boolean isPortLicense () {
		return isPortLicense;
	}
	
	public boolean isBridgeLicense () {
		return isBridgeLicense;
	}
	
	public boolean isTunnelLicense () {
		return isTunnelLicense;
	}
	
	public boolean isCattleLicense () {
		return isCattleLicense;
	}
	
	public String getLicenseLabel () {
		String tLicenseLabel;
		int tPrice;
		
		tLicenseLabel = getName () + " License";
		tPrice = getPrice ();
		if (tPrice > 0) {
			tLicenseLabel += " Price " + Bank.formatCash (tPrice);
		}
		
		return tLicenseLabel;
	}
	
	public void addAttributes (XMLElement aXMLElement) {
		aXMLElement.setAttribute (AN_LICENSE_NAME, getName ());
		aXMLElement.setAttribute (AN_BENEFIT_VALUE, getBenefitValue ());
		aXMLElement.setAttribute (AN_MAP_CELL_IDS, getMapCellIDs ());
		aXMLElement.setAttribute (AN_LICENSE_TYPE, type.toString ());
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tLicenseElement;
		
		tLicenseElement = aXMLDocument.createElement (EN_LICENSE);

		return tLicenseElement;
	}
}
