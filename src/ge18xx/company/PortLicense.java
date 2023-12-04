package ge18xx.company;

import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;

public class PortLicense extends License {
	public static final AttributeName AN_PORT_LICENSE = new AttributeName ("port");

	public PortLicense (String aName, int aBenefitValue) {
		super (aName, NO_VALUE, aBenefitValue);
		setIsPortLicense (true);
	}
	
	@Override
	public int getPortValue () {
		return getBenefitValue ();
	}
	
	@Override
	public void addAttributes (XMLElement aLicenseElement) {
		super.addAttributes (aLicenseElement);
		aLicenseElement.setAttribute (AN_PORT_LICENSE, isPortLicense ());
	}
	
	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tLicenseElement;
		
		tLicenseElement = super.createElement (aXMLDocument);
		addAttributes (tLicenseElement);
		
		return tLicenseElement;
	}
}
