package ge18xx.company;

import java.util.ArrayList;

import ge18xx.bank.Bank;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class License extends Coupon {
	public static final ElementName EN_LICENSES = new ElementName ("Licenses");
	public static final ElementName EN_LICENSE = new ElementName ("License");
	public static final AttributeName AN_LICENSE = new AttributeName ("license");
	public static final AttributeName AN_BENEFIT_VALUE = new AttributeName ("benefitValue");
	public static final AttributeName AN_LICENSE_NAME = new AttributeName ("licenseName");
	public static final AttributeName AN_MAP_CELL_IDS = new AttributeName ("mapCellIDs");
	public static final AttributeName AN_LICENSE_TYPE = new AttributeName ("type");
	public static final License NO_LICENSE = null;
	public static final ArrayList<License> NO_LICENSES = null;
	public static final String NO_NAME = "";
	public static final String NO_TYPE_NAME = "No Type";
	public static final String NO_MAP_CELL_IDS = "";
	int benefitValue;
	boolean isPortLicense;
	boolean isBridgeLicense;
	boolean isTunnelLicense;
	boolean isCattleLicense;
	boolean isCoalLicense;
	boolean isRiverLicense;
	String mapCellIDs;
	LicenseTypes type;
	public enum LicenseTypes { 
		NO_TYPE ("No Type"), 
		PORT ("Port"), 
		OPEN_PORT ("Open Port"), 
		CLOSED_PORT ("Closed Port"), 
		BRIDGE ("Bridge"), 
		TUNNEL ("Tunnel"), 
		CATTLE ("Cattle"), 
		COAL ("Coal"),
		RIVER ("River");
		
		private String enumString;
	
		LicenseTypes (String aEnumString) {
			enumString = aEnumString;
		}
	
		@Override
		public String toString () {
			return enumString;
		}

	};
	
	public License () {
		this (NO_TYPE_NAME, NO_VALUE, NO_VALUE);
	}
	
	public License (String aName, int aBenefitValue) {
		this (aName, NO_VALUE, aBenefitValue);
	}
	
	public License (String aName, int aPrice, int aBenefitValue) {
		super (aName, aPrice);
		
		LicenseTypes tLicenseType;
		
		tLicenseType = getTypeFromName (aName);
		setValues (tLicenseType, aPrice, aBenefitValue);
	}
	
	public License (LicenseTypes aType, int aPrice, int aBenefitValue) {
		super (aType.toString (), aPrice);
		setValues (aType, aPrice, aBenefitValue);
	}
	
	public void setValues (LicenseTypes aType, int aPrice, int aBenefitValue) {
		boolean tIsPortLicense;
		
		setType (aType);
		setBenefitValue (aBenefitValue);
		
		if ((aType == LicenseTypes.PORT) || 
			(aType == LicenseTypes.OPEN_PORT) ||
			(aType == LicenseTypes.CLOSED_PORT)) {
			tIsPortLicense = true;
		} else {
			tIsPortLicense = false;
		}
		setIsPortLicense (tIsPortLicense);
		setMapCellIDs (NO_MAP_CELL_IDS);
	}

	public License (XMLNode aXMLNode) {
		super (aXMLNode);
		int tBenefitValue;
		String tMapCellIDs;
		String tTypeName;
		boolean tIsPortLicense;
		
		tIsPortLicense = aXMLNode.getThisBooleanAttribute (PortLicense.AN_PORT_LICENSE);
		tTypeName = aXMLNode.getThisAttribute (AN_LICENSE_TYPE, "Port");
		tBenefitValue = aXMLNode.getThisIntAttribute (AN_BENEFIT_VALUE, NO_VALUE);
		tMapCellIDs = aXMLNode.getThisAttribute (AN_MAP_CELL_IDS);
		setBenefitValue (tBenefitValue);
		setIsPortLicense (tIsPortLicense);
		setMapCellIDs (tMapCellIDs);
		setTypeFromName (tTypeName);
	}

	public static LicenseTypes getTypeFromName (String aTypeName) {
		String tTypeName;
		LicenseTypes tFoundType;
		
		tFoundType = LicenseTypes.NO_TYPE;
		for (LicenseTypes tType : LicenseTypes.values ()) {
			tTypeName = tType.toString ();
			if (aTypeName.equals (tTypeName)) {
				tFoundType = tType;
			}
		}
		
		return tFoundType;
	}
	
	public void setTypeFromName (String aTypeName) {
		LicenseTypes tFoundType;
		
		tFoundType = getTypeFromName (aTypeName);
		
		switch (tFoundType) {
			case NO_TYPE:
				break;
			case PORT:
				setIsPortLicense (true);
				break;
			case OPEN_PORT:
				setIsPortLicense (true);
				break;
			case CLOSED_PORT:
				setIsPortLicense (true);
				break;
			case BRIDGE:
				setIsBridgeLicense (true);
				break;
			case TUNNEL:
				setIsTunnelLicense (true);
				break;
			case CATTLE:
				setIsCattleLicense (true);
				break;
			case COAL:
				setIsCoalLicense (true);
				break;
			case RIVER:
				setIsRiverLicense (true);
			default:
				break;
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
	
	public void setIsCoalLicense (boolean aIsCoalLicense) {
		isCoalLicense = aIsCoalLicense;
		if (isCoalLicense) {
			setType (LicenseTypes.COAL);
		}
	}
	
	public void setIsRiverLicense (boolean aIsRiverLicense) {
		isRiverLicense = aIsRiverLicense;
		if (isCoalLicense) {
			setType (LicenseTypes.RIVER);
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
	
	public String getLicenseName () {
		String tLicenseName;
		
		tLicenseName = getName () + " License";
		
		return tLicenseName;
	}
	
	public String getLicenseLabel () {
		String tLicenseLabel;
		int tPrice;
		
		tLicenseLabel = getLicenseName ();
		tPrice = getPrice ();
		if (tPrice > 0) {
			tLicenseLabel += " Price " + Bank.formatCash (tPrice);
		}
		
		return tLicenseLabel;
	}
	
	@Override
	public void addAttributes (XMLElement aXMLElement) {
		super.addAttributes (aXMLElement);
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
