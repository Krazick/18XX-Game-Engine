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
	public static final License NO_LICENSE = null;
	public static final ArrayList<License> NO_LICENSES = null;
	public static final int NO_VALUE = 0;
	int benefitValue;
	boolean isPortLicense;
	
	public License (String aName, int aPrice, int aBenefitValue) {
		super (aName, aPrice);
		setBenefitValue (aBenefitValue);
		setIsPortLicense (false);
	}

	public License (XMLNode aXMLNode) {
		super (aXMLNode);
		int tBenefitValue;
		String tLicenseName;
		boolean tIsPortLicense;
		
		tIsPortLicense = aXMLNode.getThisBooleanAttribute (PortLicense.AN_PORT_LICENSE);
		tBenefitValue = aXMLNode.getThisIntAttribute (AN_BENEFIT_VALUE, NO_VALUE);
		tLicenseName = aXMLNode.getThisAttribute (AN_LICENSE_NAME);
		setBenefitValue (tBenefitValue);
		setName (tLicenseName);
		setIsPortLicense (tIsPortLicense);
	}


	public void setIsPortLicense (boolean aIsPortLicense) {
		isPortLicense = aIsPortLicense;
	}
	
	public void setBenefitValue (int aBenefitValue) {
		benefitValue = aBenefitValue;
	}
	
	public int getBenefitValue () {
		return benefitValue;
	}
	
	public int getPortValue () {
		return NO_VALUE;
	}
	
	public boolean isPortLicense () {
		return isPortLicense;
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
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tLicenseElement;
		
		tLicenseElement = aXMLDocument.createElement (EN_LICENSE);

		return tLicenseElement;
	}
}
