package ge18xx.player;

import ge18xx.center.City;
import ge18xx.company.ShareCompany;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

public class ContractLine {
	public static final ContractLine NO_CONTRACT_LINE = null;
	public static final ElementName EN_CONTRACT_LINE = new ElementName ("ContractLine");
	public static final ElementName EN_CONTRACT_LINES = new ElementName ("ContractLines");
	public static final AttributeName AN_CITY_NAME = new AttributeName ("cityName");
	public static final AttributeName AN_SHARE_COMPANY_ID = new AttributeName ("shareCompanyID");
	public static final AttributeName AN_CONNECTED = new AttributeName ("connected");
	public static final AttributeName AN_BOND = new AttributeName ("bond");

	ShareCompany shareCompany;
	City city;
	boolean connected;
	int bond;
	
	public ContractLine (City aCity, ShareCompany aShareCompany, int aBond) {
		setCity (aCity);
		setShareCompany (aShareCompany);
		setBond (aBond);
		setConnected (false);
	}

	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLContractLineElement;
		
		tXMLContractLineElement = aXMLDocument.createElement (EN_CONTRACT_LINE);
		if (city == City.NO_CITY) {
			tXMLContractLineElement.setAttribute (AN_CITY_NAME, GUI.EMPTY_STRING);			
		} else {
			tXMLContractLineElement.setAttribute (AN_CITY_NAME, city.getCityName ());
		}
		if (shareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tXMLContractLineElement.setAttribute (AN_SHARE_COMPANY_ID, GUI.EMPTY_STRING);
		} else {
			tXMLContractLineElement.setAttribute (AN_SHARE_COMPANY_ID, shareCompany.getID ());
		}
		tXMLContractLineElement.setAttribute (AN_CONNECTED, connected);
		tXMLContractLineElement.setAttribute (AN_BOND, bond);

		return tXMLContractLineElement;
	}

	public boolean isValidContractLine () {
		boolean tIsValidContractLine;
		
		tIsValidContractLine = true;
		if (city == City.NO_CITY) {
			tIsValidContractLine = false;
		}
		if (shareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tIsValidContractLine = false;
		}
		if (bond <= 0) {
			tIsValidContractLine = false;
		}
		
		return tIsValidContractLine;
	}

	public String getAllReasonsContractLineInvalid () {
		String tAllReasonsContractLineInvalid;
		
		tAllReasonsContractLineInvalid = GUI.EMPTY_STRING;
		if (city == City.NO_CITY) {
			tAllReasonsContractLineInvalid += "No City is specified\n";
		}
		if (shareCompany == ShareCompany.NO_SHARE_COMPANY) {
			tAllReasonsContractLineInvalid += "No Share Company is specified\n";
		}
		if (bond <= 0) {
			tAllReasonsContractLineInvalid += "Bond Value is <= zero (0)\n";
		}
		
		return tAllReasonsContractLineInvalid;
	}

	private void setConnected (boolean aConnected) {
		connected = aConnected;
	}
	
	private void setBond (int aBond) {
		bond = aBond;
	}

	private void setShareCompany (ShareCompany aShareCompany) {
		shareCompany = aShareCompany;
	}

	private void setCity (City aCity) {
		city = aCity;
	}

	public int getBond () {
		return bond;
	}

	public String getCityName () {
		String tCityName;
		
		tCityName = GUI.EMPTY_STRING;
		
		if (city != City.NO_CITY) {
			tCityName = city.getCityName ();
		}
		
		return tCityName;
	}
	
	public ShareCompany getShareCompany () {
		return shareCompany;
	}
	
	public City getCity () {
		return city;
	}
	
	public boolean isConnected () {
		return connected;
	}

	public boolean isDeltaTerrain () {
		boolean tIsDeltaTerrain;
		
		if (city == City.NO_CITY) {
			tIsDeltaTerrain = false;
		} else {
			tIsDeltaTerrain = city.isDeltaTerrain ();
		}
		
		return tIsDeltaTerrain;
	}
	
	// New Methods to add
	// ParseContractLine -- Will parse the XML from the Save Game File
	// GenerateActionEffects -- Will generate the Action with Effects XML of the ContractBid
	// ParseActionEffects -- Will parse the Action with Effects XML of the ContractBid
}
